/**
 * @author kf7688
 * @date 2018/11/1
 * @version 1.0
 */
package com.mqtt.transfer.service.impl;

import com.mqtt.transfer.exception.ErrorCode;
import com.mqtt.transfer.exception.MqttTransferException;
import com.mqtt.transfer.pojo.GatewayTemplate;
import com.mqtt.transfer.repository.GatewayTemplateRepository;
import com.mqtt.transfer.service.GatewayTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service("gatewayTemplateServiceImpl")
public class GatewayTemplateServiceImpl implements GatewayTemplateService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GatewayTemplateRepository gatewayTemplateRepository;
    private List<GatewayTemplate> gatewayTemplateList;

    @Override
    public void createGatewayTemplate(GatewayTemplate gatewayTemplate) {
        GatewayTemplate gatewayTemp = gatewayTemplateRepository.findByGatewayNameAndFirmName(gatewayTemplate.getGatewayName(),gatewayTemplate.getFirmName());
        if(null != gatewayTemp){
            throw new MqttTransferException("该网关型号已经存在");
        }
        gatewayTemplateRepository.insert(gatewayTemplate);
    }

    @Override
    public List<GatewayTemplate> showGatewayTemplate(int pageNum, int pageSize, String firmName,String gatewayName) {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(pageNum-1,pageSize,sort);
        if(StringUtils.isEmpty(gatewayName)){
            gatewayTemplateList = gatewayTemplateRepository.findByFirmName(firmName,pageable).getContent();
            log.info("集合大小："+gatewayTemplateList.size());
        }else {
            gatewayTemplateList = gatewayTemplateRepository.findByFirmNameAndGatewayNameLike(firmName, gatewayName, pageable).getContent();
            log.info("模糊查询集合大小：" + gatewayTemplateList.size());
        }
        return gatewayTemplateList;
    }

    @Override
    public void deleteGatewayTemplate(String firmName, String gatewayName) {
        //TODO 查询网关模板下面是否有子设备
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("firmName",firmName);
            requestBody.put("gatewayName",gatewayName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object responseResult = restTemplate.postForObject("http",requestBody,Object.class);
        if(StringUtils.isEmpty(responseResult)){
            throw new MqttTransferException(ErrorCode.EXISTED_DEVICE,"网关下存在子设备，无法删除");
        }
        gatewayTemplateRepository.deleteGatewayTemplateByFirmNameAndGatewayName(firmName,gatewayName);
    }
}
