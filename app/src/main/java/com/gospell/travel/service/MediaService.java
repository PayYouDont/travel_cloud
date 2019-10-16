package com.gospell.travel.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;

import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.ftp.FTPService;

import org.litepal.util.LogUtil;

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
        new Thread (() -> {
            while (true){
                if(MediaLoader.mediaBeanQueue.size ()>0){
                    MediaBean mediaBean = MediaLoader.mediaBeanQueue.poll ();
                    if(mediaBean!=null){
                        mediaBean.save ();
                    }
                }else{
                    try {
                        Thread.sleep (10000);
                    }catch (InterruptedException e){
                        LogUtil.e (MediaService.class.getName (),e);
                    }
                }
            }
        }).start ();
    }
    /**
     * 对图库的数据库变化添加监听
     */
    private void listenPhotoAlbumDB() {
        System.out.println ("开始监听");
        if (photoAlbumContentObserver == null) {
            PhotoAlbumHandler photoAlbumHandler = new PhotoAlbumHandler();
            photoAlbumContentObserver = new PhotoAlbumContentObserver(photoAlbumHandler);
            photoAlbumContentResolver = getBaseContext ().getContentResolver();
            registerContentObserver();
        }
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
                MediaLoader.mediaBeanQueue.offer (mediaBean);
                FTPService.mediaBeanQueue.offer (mediaBean);
                FTPService.total++;
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
