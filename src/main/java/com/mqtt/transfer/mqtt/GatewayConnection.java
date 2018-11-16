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

    @Autowired
    private MqttClientInfo mqttClientInfo;

    private List<String> allTopics = new ArrayList<>();
    private String[] topicArrays;

    private IOTMqttClientInit iotMqttClientInit;

    @Autowired
    public GatewayConnection(IOTMqttClientInit iotMqttClientInit) {
        this.iotMqttClientInit = iotMqttClientInit;
    }

   //TODO 自定义topic怎么办？？

    //TODO 微服务重启需要查询数据库
    @PostConstruct
    public void getAlltopics() {
        log.info("================="+mqttClientInfo.getUrl());
        allTopics.add("111");
        allTopics.add("222");
        topicArrays = new String[allTopics.size()];
        String[] allTopicArrays = (String[]) allTopics.toArray(topicArrays);
        log.info(allTopicArrays[0] + ",========," + allTopicArrays[1]);
        try {
            iotMqttClientInit.getMqttClient().subscribe(allTopicArrays);
            online("test", "test");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void online(String productKey, String deviceId) {
        String[] topicsSubscribe;
        if ("test".equals(productKey) && "test".equals(deviceId)) {
            topicsSubscribe = new String[]{"test"};
        } else {
            log.info("alltopics:" + allTopics.size());
            topicsSubscribe = initTopic(productKey, deviceId, TopicType.SUBSCRIBE_TOPIC);
            Collections.addAll(topicSet, topicsSubscribe);
        }
        try {
            log.info("数组大小：" + topicsSubscribe.length + "; 集合大小：" + topicSet.size());
            iotMqttClientInit.getMqttClient().subscribe(topicsSubscribe);
            iotMqttClientInit.getMqttClient().setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //服务器重启
                    while (!iotMqttClientInit.getMqttClient().isConnected()) {
                        log.info("服务器连接不上：" + fm.format(new Date()));
                        try {
                            iotMqttClientInit.getMqttClient().connect();
                            iotMqttClientInit.getMqttClient().subscribe(topicSet.toArray(new String[topicSet.size()]));
                        } catch (MqttException e) {
                            e.printStackTrace();
                            try {
                                TimeUnit.SECONDS.sleep(10);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            continue;
                        }
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    log.info("订阅topic:" + topic);
                    if (topic.equals(topicsSubscribe[0])) {
                        log.info("网关上线：" + new String(message.getPayload()));
                    } else if (topic.equals(topicsSubscribe[1])) {
                        log.info("网关下线：" + new String(message.getPayload()));
                    } else if (topic.equals(topicsSubscribe[2])) {
                        log.info("网关重启：" + new String(message.getPayload()));
                    } else if (topic.equals(topicsSubscribe[3])) {
                        log.info("子设备上线：" + new String(message.getPayload()));
                    } else if (topic.equals(topicsSubscribe[4])) {
                        log.info("子设备下线：" + new String(message.getPayload()));
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

    public void publish(String topic, String message) {
        MqttClient mqttClient = null;
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(0);
        MqttTopic mqttTopic = iotMqttClientInit.getMqttClient().getTopic(topic);
        try {
            mqttTopic.publish(mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            while (true){
                try {
                    mqttClient = new MqttClient(mqttClientInfo.getUrl(),mqttClientInfo.getClientId()+"abcdefg",new MemoryPersistence());
                    MqttConnectOptions connectOptions = new MqttConnectOptions();
                    connectOptions.setAutomaticReconnect(mqttClientInfo.getAutomaticReconnect());
                    connectOptions.setKeepAliveInterval(mqttClientInfo.getKeepAliveInterval());
                    connectOptions.setCleanSession(mqttClientInfo.getCleanSession());
                    //TODO 如何预留SSL连接
                    connectOptions.setUserName(mqttClientInfo.getUsername());
                    connectOptions.setPassword(mqttClientInfo.getPassword().toCharArray());
                    mqttClient.connect(connectOptions);
                    mqttClient.publish(topic,mqttMessage);
                    break;
                } catch (MqttException e1) {
                    e1.printStackTrace();
                    log.info("发布数据失败，重新连接");
                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    mqttClient.close(true);
                    continue;
                }
            }

        }
    }

    public void unsubscribeAll(String productKey, String deviceId) {
        log.info("onlineSub:" + onlineSub + ";  offlineSub:" + offlineSub + ";  restartSub:" + restartSub + "; loginSub:" + loginSub + "; logoutSub:" + logoutSub);
        try {
            iotMqttClientInit.getMqttClient().unsubscribe(initTopic(productKey, deviceId, TopicType.SUBSCRIBE_TOPIC));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private String[] initTopic(String productKey, String deviceId, String topicType) {
        //TODO 考虑到自定义topic,不能拼接，需要从数据库中获取,每个topic是不是需要指定类型：发布还是订阅？？
        onlineSub = "/iot/" + productKey + "/" + deviceId + "/online";
        onlinePub = "/iot/" + productKey + "/" + deviceId + "/online_reply";
        offlineSub = "/iot/" + productKey + "/" + deviceId + "/offline";
        offlinePub = "/iot/" + productKey + "/" + deviceId + "/offline_reply";
        restartSub = "/iot/" + productKey + "/" + deviceId + "/restart";
        restartPub = "/iot/" + productKey + "/" + deviceId + "/restart_reply";
        loginSub = "/ext/session/" + productKey + "/" + deviceId + "/combine/login";
        loginPub = "/ext/session/" + productKey + "/" + deviceId + "/combine/login_reply";
        logoutSub = "/ext/session/" + productKey + "/" + deviceId + "/combine/logut";
        logoutPub = "/ext/session/" + productKey + "/" + deviceId + "/combine/logut_reply";
        if (TopicType.ALL_TOPIC.equals(topicType)) {
            return new String[]{onlineSub, offlineSub, restartSub, loginSub, logoutSub, onlinePub, offlinePub, restartPub, loginPub, logoutPub};
        } else if (TopicType.PUBLISH_TOPIC.equals(topicType)) {
            return new String[]{onlinePub, offlinePub, restartPub, loginPub, logoutPub};
        } else if (TopicType.SUBSCRIBE_TOPIC.equals(topicType)) {
            return new String[]{onlineSub, offlineSub, restartSub, loginSub, logoutSub};
        }
        return new String[]{"error"};
    }

    private class TopicType {
        private static final String ALL_TOPIC = "allTopic";
        private static final String PUBLISH_TOPIC = "publishTopic";
        private static final String SUBSCRIBE_TOPIC = "subscribeTopic";
    }

}
