package com.gospell.travel.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.gospell.travel.entity.MediaBean;

public class MediaService extends Service {
    private PhotoAlbumContentObserver photoAlbumContentObserver;
    private ContentResolver photoAlbumContentResolver;
    MediaLoader mediaLoader;
    public MediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate ();
        mediaLoader = new MediaLoader (getBaseContext ());
        mediaLoader.getAllPhotoInfo ();
        listenPhotoAlbumDB();
    }
    /**
     * 对图库的数据库变化添加监听
     */
    private void listenPhotoAlbumDB() {
        Log.i (getClass ().getName (),"相册变化的相关监听启动");
        if (photoAlbumContentObserver == null) {
            PhotoAlbumHandler photoAlbumHandler = new PhotoAlbumHandler();
            photoAlbumContentObserver = new PhotoAlbumContentObserver(photoAlbumHandler);
            photoAlbumContentResolver = getBaseContext ().getContentResolver();
            registerContentObserver();
        }
        Log.i (getClass ().getName (),"相册变化的相关监听启动完毕");
    }
    /**
     * 注册相册变化的相关监听
     */
    private void registerContentObserver() {
        if (photoAlbumContentObserver == null) {
            return;
        }
        Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        photoAlbumContentResolver.registerContentObserver(photoUri, false, photoAlbumContentObserver);
        photoAlbumContentObserver.setOnChangeListener(() -> {
            MediaBean mediaBean = mediaLoader.getMediaByUri (photoUri,photoAlbumContentResolver);
            if(mediaBean!=null){
                mediaBean.save ();
            }
        });
    }
    /**
     * 图库更新的Handle 防止内存泄漏写成静态内部类
     */
    private static class PhotoAlbumHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
