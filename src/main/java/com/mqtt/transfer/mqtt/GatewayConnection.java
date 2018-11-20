/**
 * @author kf7688
 * @date 2018/11/3
 * @version 1.0
 */
package com.mqtt.transfer.mqtt;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class GatewayConnection implements DeviceTopic {
    private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    private static final Set<String> topicSet = new HashSet<>();

    String[] topicsSubscribe;
    String[] topicPublish;

    private String onlineSub;
    private String onlinePub;
    private String offlineSub;
    private String offlinePub;
    private String restartSub;
    private String restartPub;
    private String loginSub;
    private String loginPub;
    private String logoutSub;
    private String logoutPub;
    private MqttConnectOptions connectOptions;

    private List<String> allTopics = new ArrayList<>();
    private String[] topicArrays;

    private MqttClient mqttClient;

    private MqttClientInfo mqttClientInfo;

    @Autowired
    public GatewayConnection(MqttClientInfo mqttClientInfo) {
        try {
            this.mqttClientInfo = mqttClientInfo;
            log.info("============== 这里只能调用一次 =============" + mqttClientInfo.getUrl());
            mqttClient = new MqttClient(mqttClientInfo.getUrl(), mqttClientInfo.getClientId(), new MemoryPersistence());
            connectOptions = new MqttConnectOptions();

            //TODO 重连什么时候触发？？
            connectOptions.setAutomaticReconnect(mqttClientInfo.getAutomaticReconnect());
            connectOptions.setKeepAliveInterval(mqttClientInfo.getKeepAliveInterval());

            //如果是true，那么清理所有离线消息，即QoS1或者2的所有未接收内容.默认为true
            connectOptions.setCleanSession(mqttClientInfo.getCleanSession());

            //TODO 如何预留SSL连接

            connectOptions.setUserName(mqttClientInfo.getUsername());
            connectOptions.setPassword(mqttClientInfo.getPassword().toCharArray());
            mqttClient.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
            log.info("服务器连接不上？");
        }
    }

    //TODO 自定义topic怎么办？？

    //TODO 微服务重启需要查询数据库
    @PostConstruct
    public void getAlltopics() {
        allTopics.add("111");
        allTopics.add("222");
        String[] allTopicArrays =  allTopics.toArray(new String[0]);
        log.info(allTopicArrays[0] + ",========," + allTopicArrays[1]);
        try {
            mqttClient.subscribe(allTopicArrays);
            online("test", "test");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void online(String productKey, String deviceId) {
        String[] topicsSubscribe;
        if ("test".equals(productKey) && "test".equals(deviceId)) { //只是为了回调函数生效
            topicsSubscribe = new String[]{"test"};
        } else {
            log.info("alltopics:" + allTopics.size());
            topicsSubscribe = getTopics(productKey, deviceId, TopicEnum.SUBSCRIBE);
            Collections.addAll(topicSet, topicsSubscribe);
        }
        try {
            log.info("数组大小：" + topicsSubscribe.length + "; 集合大小：" + topicSet.size());
            mqttClient.subscribe(topicsSubscribe);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.info("服务器连接不上：" + fm.format(new Date()));
                    //服务器重启
                    int count = 1;
                    while (!mqttClient.isConnected()) {
                        log.info("服务器第"+count+"次重连：" + fm.format(new Date()));
                        try {
                            mqttClient.connect();
                            log.info("重连服务器成功：" + fm.format(new Date()));
                            mqttClient.subscribe(topicSet.toArray(new String[0]));
                            count = 1;
                        } catch (MqttException e) {
                            e.printStackTrace();
                            log.info("服务器连接异常：" + fm.format(new Date()));
                            count++;
                            try {
                                TimeUnit.SECONDS.sleep(10);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                /**
                 * 收到消息以后，转发给解析服务;暂时reply的topic还没有用上
                 * @param topic  网关的topic
                 * @param message  网关的消息
                 * @throws Exception
                 */
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    log.info("订阅topic:" + topic + "===透传");
                    if (topic.equals(topicsSubscribe[0])) {
                        log.info("网关上线：" + new String(message.getPayload()));
                        executorService.execute(() -> publish("/mqttServer" + topicsSubscribe[0], message));
                    } else if (topic.equals(topicsSubscribe[1])) {
                        log.info("网关下线：" + new String(message.getPayload()));
                        executorService.execute(() -> {
                            publish("/mqttServer" + topicsSubscribe[1], message);
                        });
                    } else if (topic.equals(topicsSubscribe[2])) {
                        log.info("网关重启：" + new String(message.getPayload()));
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                publish("/mqttsErver" + topicsSubscribe[2], message);
                            }
                        });
                    } else if (topic.equals(topicsSubscribe[3])) {
                        log.info("子设备上线：" + new String(message.getPayload()));
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                publish("/mqttServer" + topicsSubscribe[3], message);
                            }
                        });
                    } else if (topic.equals(topicsSubscribe[4])) {
                        log.info("子设备下线：" + new String(message.getPayload()));
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                publish("/mqttServer" + topicsSubscribe[4], message);
                            }
                        });
                    } else {
                        log.info("异常topic");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private void publish(String topic, MqttMessage mqttMessage) {
//        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(0);
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        try {
            mqttTopic.publish(mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            while (true) {
                try {
                    mqttClient.close();
                    mqttClient = new MqttClient(mqttClientInfo.getUrl(), mqttClientInfo.getClientId(), new MemoryPersistence());
                    mqttClient.connect(connectOptions);
                    mqttClient.publish(topic, mqttMessage);
                    break;
                } catch (MqttException e1) {
                    e1.printStackTrace();
                    log.info("发布数据失败，重新连接,等待一分钟，并且关闭客户端！");
                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }

        }
    }

    public void unsubscribeAll(String productKey, String deviceId) {
        //TODO 需要查询数据库
        log.info("onlineSub:" + onlineSub + ";  offlineSub:" + offlineSub + ";  restartSub:" + restartSub + "; loginSub:" + loginSub + "; logoutSub:" + logoutSub);
        try {
            mqttClient.unsubscribe(getTopics(productKey, deviceId, TopicEnum.SUBSCRIBE));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private String[] getTopics(String gatewayProductKey, String gatewayDeviceId, TopicEnum topicType) {
        //TODO 考虑到自定义topic,不能拼接，需要从数据库中获取,每个topic是不是需要指定类型：发布还是订阅？？
        onlineSub = "/iot/" + gatewayProductKey + "/" + gatewayDeviceId + "/online";
        onlinePub = "/iot/" + gatewayProductKey + "/" + gatewayDeviceId + "/online_reply";
        offlineSub = "/iot/" + gatewayProductKey + "/" + gatewayDeviceId + "/offline";
        offlinePub = "/iot/" + gatewayProductKey + "/" + gatewayDeviceId + "/offline_reply";
        restartSub = "/iot/" + gatewayProductKey + "/" + gatewayDeviceId + "/restart";
        restartPub = "/iot/" + gatewayProductKey + "/" + gatewayDeviceId + "/restart_reply";
        loginSub = "/ext/session/" + gatewayProductKey + "/" + gatewayDeviceId + "/combine/login";
        loginPub = "/ext/session/" + gatewayProductKey + "/" + gatewayDeviceId + "/combine/login_reply";
        logoutSub = "/ext/session/" + gatewayProductKey + "/" + gatewayDeviceId + "/combine/logut";
        logoutPub = "/ext/session/" + gatewayProductKey + "/" + gatewayDeviceId + "/combine/logut_reply";

        switch (topicType) {
            case PUBLISH:
                return new String[]{onlinePub, offlinePub, restartPub, loginPub, logoutPub};
            case SUBSCRIBE:
                return new String[]{onlineSub, offlineSub, restartSub, loginSub, logoutSub};
            default:
                return new String[]{"error"};
        }
    }


    private enum TopicEnum {
        PUBLISH, SUBSCRIBE
    }
}
