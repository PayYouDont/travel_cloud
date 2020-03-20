package com.gospell.travel.ui.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gospell.travel.MainActivity;
import com.gospell.travel.R;

import lombok.Getter;

public class CustomNotification {
    private static CustomNotification notification;
    private NotificationManagerCompat mNotificationManager;
    private RemoteViews notifyView;
    private NotificationCompat.Builder builder;
    private Context mContext;
    @Getter
    private boolean loading = true;
    private String channelId = "CustomNotification";
    private int progress;
    private String title;
    private String fileName;
    private String rateMsg;
    private String loadSize;
    private NotificationChannel channel;
    private LoadType loadType;
    private LoadClickListener loadClickListener;
    private Service mService;
    private CustomNotification(Context mContext) {
        this.mContext = mContext;
        init();
    }

    private CustomNotification(Service mService) {
        this.mService = mService;
        this.mContext = mService.getBaseContext ();
        init();
    }

    public static CustomNotification create(Context mContext) {
        if(notification == null){
            synchronized (CustomNotification.class){
                if(notification==null){
                    notification = new CustomNotification (mContext);
                }
            }
        }
        return notification;
    }
    public static CustomNotification create(Service service) {
        if(notification == null){
            synchronized (CustomNotification.class){
                if(notification==null){
                    notification = new CustomNotification (service);
                }
            }
        }
        return notification;
    }
    private void init() {
        //1.得到 NotificationManagerCompat
        mNotificationManager = NotificationManagerCompat.from(mContext);
        //2.构造Notify上的View
        notifyView = new RemoteViews(mContext.getPackageName (),R.layout.custion_notifycation);
        Intent playIntent = new Intent (mContext,NotifyReceiver.class);
        playIntent.setAction (NotifyReceiver.LOAD_ACTION);
        PendingIntent play = PendingIntent.getBroadcast (mContext,1,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notifyView.setOnClickPendingIntent (R.id.notify_play_btn,play);
        //3.构造Notify,设置通知的参数
        builder = new NotificationCompat.Builder(mContext,channelId);
        //点击整个通知栏时，响应通知的应用组件。
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent notifyIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //设置通知栏参数
        builder.setWhen(System.currentTimeMillis())   //优先级.
                .setAutoCancel(true)
                .setOngoing(false)  //常驻通知栏
                .setPriority( Notification.PRIORITY_MAX )
                .setContentIntent(notifyIntent)
                .setCustomContentView(notifyView)  //完全自定义,不调用.setStyle()
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setWhen (System.currentTimeMillis ())
                .setShowWhen (true)
                .setSmallIcon (R.mipmap.ic_launcher)
                .setLargeIcon (BitmapFactory.decodeResource (mContext.getResources (),R.mipmap.ic_launcher));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel (channelId,"上传和下载通知", NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            channel.setShowBadge(false);//是否在久按桌面图标时显示此渠道的通知
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.enableVibration (false);
            channel.setVibrationPattern (new long[]{0});
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId (channel.getId ());
        }
    }

    public CustomNotification setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public CustomNotification setLoadType(LoadType loadType) {
        this.loadType = loadType;
        return this;
    }
    public CustomNotification setTitle(String title) {
        this.title = title;
        return this;
    }
    public CustomNotification setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    public CustomNotification setRateMsg(String rateMsg) {
        this.rateMsg = rateMsg;
        return this;
    }
    public CustomNotification setLoadSize(String loadSize) {
        this.loadSize = loadSize;
        return this;
    }
    public CustomNotification setLoadClickListener(LoadClickListener loadClickListener) {
        this.loadClickListener = loadClickListener;
        return this;
    }
    public CustomNotification setLoading(boolean loading) {
        this.loading = loading;
        if(loadClickListener!=null){
            loadClickListener.onClick (loading);
        }
        return this;
    }
    public void show() {
        int icon = loading ? R.drawable.ic_status_pause : R.drawable.ic_status_play;
        int loadImg = R.drawable.ic_upload;
        if(loadType!=null&&loadType.equals (LoadType.download)){
            loadImg = R.drawable.ic_download;
        }
        notifyView.setImageViewResource (R.id.load_type_img,loadImg);
        notifyView.setTextViewText(R.id.notify_progress_title, title);
        notifyView.setTextViewText (R.id.notify_filename,fileName);
        notifyView.setTextViewText(R.id.load_progress,"进度:" + progress + "%" );
        notifyView.setTextViewText(R.id.load_rate,rateMsg );
        notifyView.setTextViewText(R.id.load_size,loadSize );
        notifyView.setImageViewResource(R.id.notify_play_btn, icon);
        notifyView.setProgressBar(R.id.notify_progress,100, progress,false);
        Notification notification = builder.build();
        //5.发出通知
        if(mService!=null){
            try {
                mService.startForeground (2,notification);
            }catch (Exception e){
                e.printStackTrace ();
            }
        }else {
            mNotificationManager.notify(channelId,2, notification);
        }
    }
    public enum LoadType{
        upload,download
    }

    public interface LoadClickListener{
        void onClick(boolean loading);
    }
    public static class NotifyReceiver extends BroadcastReceiver {
        public final static String LOAD_ACTION = "com.gospell.travel.notifications.intent.action.LoadClick";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals (LOAD_ACTION)) {
                notification.setLoading (!notification.isLoading ()).show ();
            }
        }
    }
}
