/**
 * @author kf7688
 * @date 2018/11/3
 * @version 1.0
 */
package com.mqtt.transfer.exception;

public class MqttTransferException extends RuntimeException {
    private int code;

    public MqttTransferException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public MqttTransferException(String message){
        super(message);
    }

    public int getCode(){
        return code;
    }

}
