/**
 * @author kf7688
 * @date 2018/11/14
 * @version 1.0
 */
package com.mqtt.transfer.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "as.mqtt")
public class MqttClientInfo {
    private String url;
    private String username;
    private String password;
    private String clientId;
    private Boolean automaticReconnect;
    private Integer keepAliveInterval;
    private Boolean cleanSession;
}
