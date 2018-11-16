/**
 * @author kf7688
 * @date 2018/11/14
 * @version 1.0
 */
package com.mqtt.transfer.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KeepAliveConnection {

    public KeepAliveConnection() {
        try {
            MqttClient mqttClient = new MqttClient("tcp://localhost:1883", "kangzhenyu", new MemoryPersistence());
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setWill("close", "last message".getBytes(), 2, true);
            mqttClient.connect();
            mqttClient.publish("$SYS/brokers", new MqttMessage("dfffffffff".getBytes()));
            String[] topics = {
                    "$SYS/brokers/emq@127.0.0.1/clients/kangzhenyu/connected", "$SYS/brokers/emq@127.0.0.1/clients/h3c_iot_mqtt_server/connected",
                    "$SYS/brokers/emq@127.0.0.1/clients/48a1b69dc1f246dd961a14ee1c979506/connected", "$SYS/brokers"
            };
            mqttClient.subscribe(topics);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.info("mqtt断开了连接");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    if (topic.equals("$SYS/brokers/emq@127.0.0.1/clients/kangzhenyu/connected")) {
//                        log.info("$SYS/brokers/emq@127.0.0.1/clients/kangzhenyu/connected:" + new String(message.getPayload()));
//                    } else if (topic.equals("$SYS/brokers/emq@127.0.0.1/clients/h3c_iot_mqtt_server/connected")) {
//                        log.info("$SYS/brokers/emq@127.0.0.1/clients/h3c_iot_mqtt_server/connected:" + new String(message.getPayload()));
//                    } else if ("$SYS/brokers/emq@127.0.0.1/clients/48a1b69dc1f246dd961a14ee1c979506/connected".equals(topic)) {
//                        log.info("$SYS/brokers/emq@127.0.0.1/clients/48a1b69dc1f246dd961a14ee1c979506/connected:" + new String(message.getPayload()));
//                    } else if ("$SYS/brokers".equals(topic)) {
//                        log.info("$SYS/brokers" + new String(message.getPayload()));
//                    } else if ("close".equals(topic)) {
//                        log.info("close:" + new String(message.getPayload()));
//                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
