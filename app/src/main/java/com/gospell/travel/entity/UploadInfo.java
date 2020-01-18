package com.gospell.travel.entity;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

@Data
public class UploadInfo implements Serializable {
    private String uuid;
    private long fileBeanId;
    private int progress;
    private int uploadStatus;
    private String title;
    public MediaBean getMediaBean(){
        return LitePal.find (MediaBean.class,fileBeanId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;
        UploadInfo that = (UploadInfo) o;
        return Objects.equals (uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash (uuid);
    }
}
