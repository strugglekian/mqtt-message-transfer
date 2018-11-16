/**
 * @author kf7688
 * @date 2018/11/1
 * @version 1.0
 */
package com.mqtt.transfer.service.impl;

import com.mqtt.transfer.exception.MqttTransferException;
import com.mqtt.transfer.pojo.GatewayTemplate;
import com.mqtt.transfer.repository.GatewayTemplateRepository;
import com.mqtt.transfer.service.GatewayTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service("gatewayTemplateServiceImpl")
public class GatewayTemplateServiceImpl implements GatewayTemplateService {

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
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
        if(StringUtils.isEmpty(gatewayName)){
            gatewayTemplateList = gatewayTemplateRepository.findByFirmName(firmName,pageable).getContent();
            log.info("集合大小："+gatewayTemplateList.size());
        }else {
            gatewayTemplateList = gatewayTemplateRepository.findByFirmNameAndGatewayNameLike(firmName, gatewayName, pageable).getContent();
            log.info("模糊查询集合大小：" + gatewayTemplateList.size());
        }
        return gatewayTemplateList;
    }

}
