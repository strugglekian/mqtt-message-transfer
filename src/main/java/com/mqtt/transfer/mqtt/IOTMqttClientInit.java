/**
 * @author kf7688
 * @date 2018/11/1
 * @version 1.0
 */
package com.mqtt.transfer.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
public class IOTMqttClientInit {

    private MqttClient mqttClient;

    private MqttClientInfo mqttClientInfo;

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public IOTMqttClientInit(MqttClientInfo mqttClientInfo) {
        try {
            log.info("============== 这里只能调用一次 =============");
            mqttClient = new MqttClient(mqttClientInfo.getUrl(), mqttClientInfo.getClientId(), new MemoryPersistence());
            MqttConnectOptions connectOptions = new MqttConnectOptions();

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

}


