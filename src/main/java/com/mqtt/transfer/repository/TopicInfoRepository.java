/**
 * @author kf7688
 * @date 2018/11/13
 * @version 1.0
 */
package com.mqtt.transfer.repository;

import com.mqtt.transfer.pojo.TopicInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TopicInfoRepository extends MongoRepository<TopicInfo, String> {
    List<TopicInfo> getByFirmNameAndGatewayName(String firmNamw, String gatewayName);
}
