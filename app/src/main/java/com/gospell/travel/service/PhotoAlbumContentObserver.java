package com.gospell.travel.service;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class PhotoAlbumContentObserver extends ContentObserver {
    private OnChangeListener onChangeListener;
    public PhotoAlbumContentObserver(Handler handler) {
        super (handler);
    }
    // 接口的set方法
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
    // 定义接口
    public interface OnChangeListener {
        void onChange();
    }
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        System.out.println ("图库有改变：Uri = " + uri);
        if (uri.toString().contains("images")) {
            // 要实现的接口回掉方法
            if (onChangeListener != null) {
                onChangeListener.onChange();
            }
        }
    }

}
