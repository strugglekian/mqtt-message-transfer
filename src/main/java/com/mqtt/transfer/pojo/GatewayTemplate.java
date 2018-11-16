package com.mqtt.transfer.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "gatewayTemplate")
public class GatewayTemplate {

    @Id
    private String id;
    private String firmName;
    private String gatewayName;
    private String gatewayLink;
    private String gatewayType;
    private Integer gatewayTypeLength;
    private String authMethod;


}