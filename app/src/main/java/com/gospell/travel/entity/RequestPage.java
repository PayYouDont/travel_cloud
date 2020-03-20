package com.gospell.travel.entity;

import java.util.Date;

import lombok.Data;

@Data
public class RequestPage {
    //第几页
    private int pageIndex = 1;
    //每页最多加载多少数据
    private int pageSize = 20;
    //已加载的总数量
    private int loadCount = 0;
    //加载哪一天的数据
    private Date loadDate = new Date ();
    //数据是否加载完毕
    private boolean isAllLoaded = false;
}
