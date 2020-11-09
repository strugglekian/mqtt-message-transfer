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
@Document(collection = "topicInfo")
public class TopicInfo {

    @Id
    private String id;
    private String topicName;
    private String topicDescribe;
    private String topicOperation;
    private String gatewayName;
    private String firmName;

    public  TopicInfo(){}

    public TopicInfo(String topicName, String topicDescribe, String topicOperation, String gatewayName, String firmName) {
        this.topicName = topicName;
        this.topicDescribe = topicDescribe;
        this.topicOperation = topicOperation;
        this.gatewayName = gatewayName;
        this.firmName = firmName;
    }
}
