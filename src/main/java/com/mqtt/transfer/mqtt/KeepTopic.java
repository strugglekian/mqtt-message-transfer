/**
 * @Author kkf7688
 * @Data 2018/11/20
 * @Version 1.0
 */

package com.mqtt.transfer.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeepTopic {
    private MqttConnectOptions connectOptions;
    private MqttClient mqttClient;

    public KeepTopic() {
        log.info("============== 这里只能调用一次 =============");

        try {
            mqttClient = new MqttClient("tcp://192.168.142.128:1883", "kangzhenyu", new MemoryPersistence());

            connectOptions = new MqttConnectOptions();

            //TODO 重连什么时候触发？？
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setKeepAliveInterval(30);

            //如果是true，那么清理所有离线消息，即QoS1或者2的所有未接收内容.默认为true
            connectOptions.setCleanSession(false);

            //TODO 如何预留SSL连接

            mqttClient.connect(connectOptions);

            String[] onOffLine = {"$SYS/brokers/emq@127.0.0.1/clients/f168f24e93da424595e4bf61e75cb96a/connected",
                    "$SYS/brokers/emq@127.0.0.1/clients/f168f24e93da424595e4bf61e75cb96a/disconnected", "$SYS/brokers"};
            mqttClient.subscribe(onOffLine);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                    log.info("lianjie异常============、、、、、、、、、、、");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    log.info("dddddddddddddddddddd");
                    if (onOffLine[0].equals(s)) {
                        log.info("客户端上线");
                        log.info(new String(mqttMessage.getPayload()));
                    }
                    if (onOffLine[1].equals(s)) {
                        log.info("客户端下线：");
                        log.info(new String(mqttMessage.getPayload()));
                    }
                    if (onOffLine[2].equals(s)) {
                        log.info("集群节点列表:");
                        log.info(new String(mqttMessage.getPayload()));

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void sysTopic() {
        try {
            String[] onOffLine = {"$SYS/brokers/emq@127.0.0.1/9c39756736b24b7991c74be689597a40/connected",
                    "$SYS/brokers/emq@127.0.0.1/9c39756736b24b7991c74be689597a40/disconnected", "$SYS/brokers", "$SYS/brokers/emq@127.0.0.1/version"};
            mqttClient.subscribe(onOffLine);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                    log.info("lianjie异常============、、、、、、、、、、、");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    log.info("dddddddddddddddddddd");
                    if (onOffLine[0].equals(s)) {
                        log.info(new String(mqttMessage.getPayload()));
                    }
                    if (onOffLine[1].equals(s)) {
                        log.info(new String(mqttMessage.getPayload()));
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
