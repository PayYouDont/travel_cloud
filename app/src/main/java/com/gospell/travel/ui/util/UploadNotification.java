package com.gospell.travel.ui.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.gospell.travel.MainActivity;
import com.gospell.travel.R;
/**
* @Author peiyongdong
* @Description ( 上传结果通知消息 )
* @Date 15:41 2019/10/16
* @Param
* @return
**/
public class UploadNotification {
    private static UploadNotification uploadNotification;
    private static Service service;
    private String title = "同步结果";
    private String text;
    private int progress;
    private String channelId = "com.gospell.travel.ftpservice.update.notification";

    public static UploadNotification create(final Service service){
        if(UploadNotification.service==null||!UploadNotification.service.equals (service)){
            UploadNotification.service = service;
        }
        if(uploadNotification==null){
            uploadNotification = new UploadNotification ();
        }
        return uploadNotification;
    }
    public static UploadNotification create(){
        if(uploadNotification==null){
            uploadNotification = new UploadNotification ();
        }
        return uploadNotification;
    }
    private UploadNotification() {}
    public void show(){
        service.startForeground (1,getNotification (progress));
    }
    private Notification getNotification(int progress) {
        Intent intent = new Intent (service, MainActivity.class);
        intent.putExtra("fragment", "homeFragment");
        PendingIntent pendingIntent = PendingIntent.getActivity (service,0,intent,0);
        Notification.Builder builder = new Notification.Builder (service.getApplicationContext())
                .setContentIntent (pendingIntent)
                .setContentTitle (title)
                .setWhen (System.currentTimeMillis ())
                .setShowWhen (true)
                .setVibrate(new long[]{0})
                .setSmallIcon (R.mipmap.ic_launcher)
                .setLargeIcon (BitmapFactory.decodeResource (service.getResources (),R.mipmap.ic_launcher));
        Notification.BigTextStyle style = new Notification.BigTextStyle();
        style.bigText(text);
        style.setBigContentTitle(title);
        builder.setStyle (style);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel (channelId,"上传和下载通知", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否在久按桌面图标时显示此渠道的通知
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationChannel.enableVibration (false);
            notificationChannel.setVibrationPattern (new long[]{0});
            NotificationManager manager = getNotificationManager ();
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId (notificationChannel.getId ());
        }
        if(progress>=0){
            //builder.setContentText (text);
            //style.setBigContentTitle("进度:"+progress+"%");
            builder.setProgress (100,progress,false);
        }
        return builder.build(); // 获取构建好的Notification
    }
    private NotificationManager getNotificationManager() {
        return (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);
    }

    public UploadNotification setTitle(String title) {
        this.title = title;
        return uploadNotification;
    }

    public UploadNotification setText(String text) {
        this.text = text;
        return uploadNotification;
    }

    public UploadNotification setProgress(int progress) {
        this.progress = progress;
        return uploadNotification;
    }
}
