/**
 * @author kf7688
 * @date 2018/11/12
 * @version 1.0
 */
package com.mqtt.transfer.controller;

import com.mqtt.transfer.mqtt.GatewayConnection;
import com.mqtt.transfer.pojo.MqttInfo;
import com.mqtt.transfer.util.ReplyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class TopicController {

    private final GatewayConnection gatewayConnection;

    @Autowired
    public TopicController(GatewayConnection gatewayConnection) {
        this.gatewayConnection = gatewayConnection;
    }

    @PostMapping(value = "/topic/gateway",produces = "application/json;charset=utf-8")
    public ReplyResult deviceTopic(@RequestBody MqttInfo mqttInfo){
        log.info("PostMapping:/topic/gatewayTemplate:"+ mqttInfo);
        gatewayConnection.online(mqttInfo.getProductKey(), mqttInfo.getProductSecret(),null);
        return ReplyResult.ok();
    }

    @DeleteMapping(value = "/topic/gateway",produces = "application/json;charset=utf-8")
    public ReplyResult unsubscribe(@RequestBody MqttInfo mqttInfo){
        log.info("DeleteMapping:/topic/gatewayTemplate:"+ mqttInfo);
        gatewayConnection.unsubscribeAll(mqttInfo.getProductKey(),mqttInfo.getProductSecret());
        return ReplyResult.ok();
    }

}
