package com.mqtt.transfer.service;

import com.mqtt.transfer.pojo.MqttInfo;

public interface MqttInfoService {

    void createMqttInfo(MqttInfo mqttInfo);

    MqttInfo showMqttInfo(String firmName, String gatewayName);

    MqttInfo refreshMqttInfo(MqttInfo mqttInfo);


}
