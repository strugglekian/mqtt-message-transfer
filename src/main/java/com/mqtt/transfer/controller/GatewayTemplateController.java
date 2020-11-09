/**
 * @author kf7688
 * @date 2018/11/1
 * @version 1.0
 */
package com.mqtt.transfer.controller;

import com.mqtt.transfer.exception.ErrorCode;
import com.mqtt.transfer.exception.MqttTransferException;
import com.mqtt.transfer.pojo.GatewayTemplate;
import com.mqtt.transfer.repository.GatewayTemplateRepository;
import com.mqtt.transfer.service.GatewayTemplateService;
import com.mqtt.transfer.util.PageResult;
import com.mqtt.transfer.util.ReplyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class GatewayTemplateController {

    @Autowired
    @Qualifier("gatewayTemplateServiceImpl")
    private GatewayTemplateService gatewayTemplateService;

    @Autowired
    private GatewayTemplateRepository gatewayTemplateRepository;

    private List<GatewayTemplate> gatewayTemplateList;

    /**
     * 创建网关模板
     *
     * @param gatewayTemplate
     * @return
     */
    @PostMapping(value = "/gateway", produces = "application/json;utf-8")
    public ReplyResult createTemplate(@RequestBody GatewayTemplate gatewayTemplate) {
        log.info("PostMapping:/gateway" + gatewayTemplate);
        try {
            gatewayTemplateService.createGatewayTemplate(gatewayTemplate);
        } catch (MqttTransferException e) {
            return ReplyResult.build(ErrorCode.DUPLICATE_NAME, e.getMessage());
        }
        return ReplyResult.ok();
    }

    /**
     * 模糊查询和展示所有列表公用一个接口
     *
     * @param gatewayName query里面带上该参数表示模糊查询,不带参数展示所有
     * @param pageNum     第几页
     * @param pageSize    每页显示的数量
     * @return
     */
    @GetMapping(value = "/gateway")
    public PageResult showGatewayTemplate(@RequestParam(required = false) String gatewayName, String firmName, int pageNum, int pageSize) {
        log.info("GetMapping:/gateway========>>>" + "gatewayName:" + gatewayName + "; firmName:" + firmName + "; pageNum:" + pageNum + "; pageSize:" + pageSize);
        //TODO 通配符转意，删除最后一页要向前跳转一页
        Long totalCount;
        if (StringUtils.isEmpty(gatewayName)) {
            totalCount = gatewayTemplateRepository.countByFirmName(firmName);
            log.info("totalCount:" + totalCount);
        } else {
            gatewayName.replaceAll(".", "\\.");
            totalCount = gatewayTemplateRepository.countByFirmNameAndGatewayNameLike(firmName, gatewayName);
            log.info("模糊查询totalCount:" + totalCount);
        }

        if ((pageNum - 1) * pageSize <= totalCount) {
            gatewayTemplateList = gatewayTemplateService.showGatewayTemplate(pageNum - 1, pageSize, firmName, gatewayName);
        } else if (pageNum * pageSize >= (totalCount + pageSize)) {
            gatewayTemplateList = gatewayTemplateService.showGatewayTemplate(1, pageSize, firmName, gatewayName);
        } else {
            gatewayTemplateList = gatewayTemplateService.showGatewayTemplate(pageNum, pageSize, firmName, gatewayName);
        }
        return PageResult.build(totalCount, pageNum, pageSize, gatewayTemplateList);
    }

    @DeleteMapping(value = "/gateway")
    public ReplyResult deleteGatewayTemplate(@RequestBody GatewayTemplate gatewayTemplate) {
        try {
            gatewayTemplateService.deleteGatewayTemplate(gatewayTemplate.getFirmName(),gatewayTemplate.getGatewayName());
        }catch (MqttTransferException e){
            return ReplyResult.build(e.getCode(),e.getMessage());
        }
        return ReplyResult.ok();
    }

}
