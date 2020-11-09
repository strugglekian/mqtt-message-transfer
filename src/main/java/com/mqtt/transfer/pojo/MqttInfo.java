/**
 * @author kf7688
 * @date 2018/11/13
 * @version 1.0
 */
package com.mqtt.transfer.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "mqttInfo")
public class MqttInfo {
    @Id
    private String id;
    private String authMethod;
    private String url;
    private String productKey;
    private String productSecret;
    private String clientId;
    private String gatewayName;
    private String firmName;
}
