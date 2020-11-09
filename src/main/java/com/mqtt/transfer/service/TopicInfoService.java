/**
 * @Author kkf7688
 * @Data 2018/11/21
 * @Version 1.0
 */

package com.mqtt.transfer.service;

import com.mqtt.transfer.pojo.TopicInfo;

import java.util.List;

public interface TopicInfoService {

    void createTopicInfo(String productKey,String productSecret,String gatewayName,String firmName);

    void addCustomTopic(TopicInfo topicInfo);

    List<TopicInfo> getAllTopic(String firmName,String gatewayName);

}
