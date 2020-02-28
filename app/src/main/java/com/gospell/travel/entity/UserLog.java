package com.gospell.travel.entity;

import java.util.Date;

import lombok.Data;

@Data
public class UserLog {
    private Integer id;
    private Date createTime;
    private String log;
    private String title;
    private int status;
    public final static int ERROR = 0;
    public final static int WARNING = 1;
    public final static int INFO = 2;
}
