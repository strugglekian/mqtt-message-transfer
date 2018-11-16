/**
 * @author kf7688
 * @date 2018/11/3
 * @version 1.0
 */
package com.mqtt.transfer.mqtt;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface DeviceTopic {

    //回复订阅的消息,不能在回调线程中调用publish，会阻塞线程，所以使用线程池
     ExecutorService executorService = new ThreadPoolExecutor(2,
            4, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());



    /**
     * 终端上线发布topic
     */
//    void online(String productKey,String gatewayId);

    /**
     * 终端下线发布topic
     */
//    void offline(String productKey,String gatewayId);

    /**
     * 终端上报数据，发布topic
     */
//    void upstream(String productKey,String gatewayId);

    /**
     * 配置下发topic,终端订阅
     */
//    void downstream(String productKey,String gatewayId);

    /**
     * 根据具体场景，决定是否需要实现
     */
//    void restart(String productKey,String gatewayId);

    /**
     * 删除设备，取消该设备所有的订阅
     */
//    void unsubscribeAll();
}
