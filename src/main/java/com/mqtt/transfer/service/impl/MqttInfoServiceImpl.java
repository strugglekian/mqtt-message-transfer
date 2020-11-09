/**
 * @Author kkf7688
 * @Data 2018/11/21
 * @Version 1.0
 */

package com.mqtt.transfer.service.impl;


import com.mqtt.transfer.pojo.MqttInfo;
import com.mqtt.transfer.repository.MqttInfoRepository;
import com.mqtt.transfer.service.MqttInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("mqttInfoServiceImpl")
public class MqttInfoServiceImpl implements MqttInfoService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MqttInfoRepository mqttInfoRepository;

    @Override
    public void createMqttInfo(MqttInfo mqttInfo) {
        mqttInfoRepository.insert(mqttInfo);
    }

    @Override
    public MqttInfo showMqttInfo(String firmName, String gatewayName) {
        return mqttInfoRepository.findByFirmNameAndGatewayName(firmName, gatewayName);
    }

    @Override
    public MqttInfo refreshMqttInfo(MqttInfo mqttInfo) {
        //TODO 通过productKey,productSecret重新向老王获取新的，设置mqttInfo参数，Id不变相当于更新操作
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayName", mqttInfo.getGatewayName());
            jsonObject.put("firmName",mqttInfo.getFirmName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        restTemplate.postForObject("http://", jsonObject, Object.class);

        MqttInfo newMqttInfo = mqttInfoRepository.save(mqttInfo);
        return newMqttInfo;
    }

}
