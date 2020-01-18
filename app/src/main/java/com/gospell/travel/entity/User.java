package com.gospell.travel.entity;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

import java.util.Date;

import lombok.Data;

@Data
public class User {
    private Integer id;
    //设备唯一标志号
    private String nickname;
    private String deviceId;
    private String account;
    private String password;
    private Date createTime;
    private Date updateTime;
    private Integer status;
    private Integer sex;
    private String headimgurl;

    public User parseWXUserinfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject (response);
            /**
             * 微信：
             * 这里可以返回如下数据
             * openid	普通用户的标识，对当前开发者帐号唯一
             * nickname	普通用户昵称
             * sex	普通用户性别，1为男性，2为女性
             * province	普通用户个人资料填写的省份
             * city	普通用户个人资料填写的城市
             * country	国家，如中国为CN
             * headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
             * privilege	用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
             * unionid	用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
             */
            String name = jsonObject.getString("nickname");
            String sex = jsonObject.getString("sex");
            String headimgurl =  jsonObject.getString("headimgurl");
            String openid = jsonObject.getString("openid");
            this.nickname = name;
            this.account = openid;
            this.sex = Integer.valueOf (sex);
            this.headimgurl = headimgurl;
        }catch (JSONException e){
            LogUtil.e (getClass ().getName (),e);
        }
        return this;
    }
}
