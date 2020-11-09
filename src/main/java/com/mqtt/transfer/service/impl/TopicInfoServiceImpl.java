/**
 * @Author kkf7688
 * @Data 2018/11/21
 * @Version 1.0
 */

package com.mqtt.transfer.service.impl;

import com.mqtt.transfer.pojo.TopicInfo;
import com.mqtt.transfer.repository.TopicInfoRepository;
import com.mqtt.transfer.service.TopicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("topicInfoServiceImpl")
public class TopicInfoServiceImpl implements TopicInfoService {

    private final TopicInfoRepository topicInfoRepository;

    @Autowired
    public TopicInfoServiceImpl(TopicInfoRepository topicInfoRepository) {
        this.topicInfoRepository = topicInfoRepository;
    }

    @Override
    public void createTopicInfo(String productKey, String productSecret, String gatewayName, String firmName) {
        String onlineSub = "/iot/" + productKey + "/" + productSecret + "/online";
        String offlineSub = "/iot/" + productKey + "/" + productSecret + "/offline";
        String restartSub = "/iot/" + productKey + "/" + productSecret + "/restart";
        String loginSub = "/ext/session/" + productKey + "/" + productSecret + "/combine/login";
        String logoutSub = "/ext/session/" + productKey + "/" + productSecret + "/combine/logut";
        List<TopicInfo> mqttInfoList = new ArrayList<>();
        mqttInfoList.add(new TopicInfo(onlineSub, "网关上线", "设备发布", gatewayName, firmName));
        mqttInfoList.add(new TopicInfo(offlineSub, "网关下线", "设备发布", gatewayName, firmName));
        mqttInfoList.add(new TopicInfo(restartSub, "网关重启", "设备订阅", gatewayName, firmName));
        mqttInfoList.add(new TopicInfo(loginSub, "子设备上线", "设备发布", gatewayName, firmName));
        mqttInfoList.add(new TopicInfo(logoutSub, "子设备下线", "设备发布", gatewayName, firmName));
        topicInfoRepository.insert(mqttInfoList);
    }

    @Override
    public void addCustomTopic(TopicInfo topicInfo) {

    }

    @Override
    public List<TopicInfo> getAllTopic(String firmName, String gatewayName) {
        return topicInfoRepository.getByFirmNameAndGatewayName(firmName, gatewayName);
    }
}
