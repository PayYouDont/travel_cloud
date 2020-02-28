package com.gospell.travel.entity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MediaBean extends LitePalSupport {
    private long id;
    //图片
    private Type type;
    private String path;
    private long size;
    private String displayName;
    private Date createTime;
    private Date updateTime;
    //0:未同步 1:已同步
    private int status = 0;
    //-1:文件已不存在 1：存在
    private int isExist = 1;
    public static final int FILE_EXIST = 1;
    public static final int FILE_NOTEXIST = -1;
    //视频
    private String thumbPath;
    private int duration;
    //上传id
    private String uploadId;

    public MediaBean() {}

    public MediaBean(Type type, String path, int size, String displayName) {
        this.type = type;
        this.path = path;
        this.size = size;
        this.displayName = displayName;
    }

    public MediaBean(Type type, String path, long size, String displayName, String thumbPath, int duration) {
        this.type = type;
        this.path = path;
        this.size = size;
        this.displayName = displayName;
        this.thumbPath = thumbPath;
        this.duration = duration;
    }
    @Override
    public boolean save(){
        int count = LitePal.where ("displayName=? and size=?",displayName,""+size).count (getClass ());
        if(count>0){//已经存在
            return true;
        }
        super.save ();
        return true;
    }
    public enum Type {
        Image,Video
    }
}
