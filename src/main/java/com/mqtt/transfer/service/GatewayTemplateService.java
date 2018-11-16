/**
 * @author kf7688
 * @date 2018/11/1
 * @version 1.0
 */
package com.mqtt.transfer.service;

import com.mqtt.transfer.pojo.GatewayTemplate;

import java.util.List;


public interface GatewayTemplateService {
    void createGatewayTemplate(GatewayTemplate gatewayTemplate);

    List<GatewayTemplate> showGatewayTemplate(int pageNum, int pageSize, String firmName, String gatewayName);
}
