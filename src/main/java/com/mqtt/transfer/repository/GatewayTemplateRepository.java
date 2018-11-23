package com.mqtt.transfer.repository;

import com.mqtt.transfer.pojo.GatewayTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;


@Component
public interface GatewayTemplateRepository extends MongoRepository<GatewayTemplate,String> {

    GatewayTemplate findByGatewayNameAndFirmName(String gatewayName, String firmName);

    /**
     * 模糊查询、分页、id倒序
     * @param firmName
     * @return
     */
    Page<GatewayTemplate> findByFirmNameAndGatewayNameLike(String firmName, String gatewayName, Pageable pageable);

    Page<GatewayTemplate> findByFirmName(String firmName, Pageable pageable);

    Long countByFirmNameAndGatewayNameLike(String firmName, String gatewayName);

    Long countByFirmName(String firmName);

    void deleteGatewayTemplateByFirmNameAndGatewayName(String firmName, String gatewayName);

}