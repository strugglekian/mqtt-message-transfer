package com.mqtt.transfer.exception;

public interface ErrorCode {
    int SUCCESS = 200;
    int DUPLICATE_NAME = 1100;  //名字重复
    int EXISTED_DEVICE = 1200;  //存在子设备
}
