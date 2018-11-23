/**
 * @Author kkf7688
 * @Data 2018/11/21
 * @Version 1.0
 */

package com.mqtt.transfer.controller;

import com.mqtt.transfer.pojo.MqttInfo;
import com.mqtt.transfer.service.MqttInfoService;
import com.mqtt.transfer.util.ReplyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttInfoController {

    private final MqttInfoService mqttInfoService;

    @Autowired
    public MqttInfoController(@Qualifier("mqttInfoServiceImpl") MqttInfoService mqttInfoService) {
        this.mqttInfoService = mqttInfoService;
    }

    @PostMapping(value = "/mqtt/information", produces = "application/json;charset=utf-8")
    public ReplyResult createMqttInfo(@RequestBody MqttInfo mqttInfo) {
        mqttInfoService.createMqttInfo(mqttInfo);
        return ReplyResult.ok();
    }

    @PatchMapping(value = "/mqtt/information", produces = "application/json;charset-8")
    public ReplyResult freshMqttInfo(@RequestBody MqttInfo mqttInfo) {
        mqttInfoService.refreshMqttInfo(mqttInfo);
        return ReplyResult.ok();
    }


}
