package com.gospell.travel.entity;

import lombok.Data;

/**
* @Author peiyongdong
* @Description ( 探测信息 )
* @Date 13:59 2019/12/19
* @Param
* @return
**/
@Data
public class BroadcastInfo {
    //手机id
    private String deviceId;
    //sim卡id
    private String simId;
    //微信id
    private String account;
    //netty服务端地址
    private String serverAddress;
}
