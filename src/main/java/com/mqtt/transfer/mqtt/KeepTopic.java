/**
 * @Author kkf7688
 * @Data 2018/11/20
 * @Version 1.0
 */

package com.mqtt.transfer.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeepTopic {
    private MqttConnectOptions connectOptions;
    private MqttClient mqttClient;

    public KeepTopic() {
        log.info("=====测试系统topic========");
        try {
            mqttClient = new MqttClient("tcp://localhost:1883", "kangzhenyu", new MemoryPersistence());
            connectOptions = new MqttConnectOptions();
            //TODO 重连什么时候触发？？
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setKeepAliveInterval(30);

            //如果是true，那么清理所有离线消息，即QoS1或者2的所有未接收内容.默认为true
            connectOptions.setCleanSession(true);

            //TODO 如何预留SSL连接

            mqttClient.connect(connectOptions);

            String[] onOffLine = {"$SYS/brokers/emq@127.0.0.1/clients/9c39756736b24b7991c74be689597a40/connected",
                    "$SYS/brokers/emq@127.0.0.1/clients/9c39756736b24b7991c74be689597a40/disconnected",
                    "$SYS/brokers/emq@127.0.0.1/clients/paho1538203353250000000/connected",
                    "$SYS/brokers/emq@127.0.0.1/clients/paho1538203353250000000/disconnected",
                    "$SYS/brokers", "$SYS/brokers/emq@127.0.0.1/version"};
            mqttClient.subscribe(onOffLine);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    log.info("lianjie异常============、、、、、、、、、、、");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    if (onOffLine[0].equals(s)) {
                        log.info("上线：" + new String(mqttMessage.getPayload()));
                    }
                    if (onOffLine[1].equals(s)) {
                        log.info("下线：" + new String(mqttMessage.getPayload()));
                    }
                    if (onOffLine[2].equals(s)) {
                        log.info("paho上线：" + new String(mqttMessage.getPayload()));
                    }
                    if (onOffLine[3].equals(s)) {
                        log.info("paho下线：" + new String(mqttMessage.getPayload()));
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
