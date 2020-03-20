package com.gospell.travel.entity;

import java.util.Date;

import lombok.Data;

@Data
public class LoadInfo {
    private float evalRate;
    //已下载： Kb
    private double loadTotalSize;
    //文件总大小 Mb
    private double totalSize;
    //进度： %
    private int progress;
    private Date beginLoadDate;

    //平均下载速度： Kb/s
    public float getEvalRate(){
        if(beginLoadDate!=null){
            Date date = new Date ();
            float second = (date.getTime () - beginLoadDate.getTime ())/1000;
            if(second>0){
                return (float) loadTotalSize/second;
            }else {
                return 0;
            }
        }
        return -1;
    }
}
