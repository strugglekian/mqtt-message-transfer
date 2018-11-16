/**
 * @author kf7688
 * @date 2018/11/8
 * @version 1.0
 */
package com.mqtt.transfer.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaBean {

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaBean(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
