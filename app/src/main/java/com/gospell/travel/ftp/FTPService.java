package com.gospell.travel.ftp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import com.gospell.travel.MainActivity;
import com.gospell.travel.common.annotation.Value;
import com.gospell.travel.common.util.ReflectUtil;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.service.MediaService;
import com.gospell.travel.ui.util.UploadNotification;

import net.gotev.uploadservice.Placeholders;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.ftp.FTPUploadRequest;

import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.jar.Pack200;

public class FTPService extends Service{
    @Value ("ftp.serverIP")
    private String ip ;
    @Value ("ftp.serverPort")
    private int port;
    @Value ("ftp.username")
    private String ftpUserName;
    @Value ("ftp.password")
    private String ftpPasswrod;
    public static Queue<MediaBean> mediaBeanQueue;
    public static final int UPLOAD_STATUS_SUCCESS = 1;
    public static int total = 0;
    private static int successCount = 0;
    private static int errorCount;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate ();
        ReflectUtil.initFieldByConfig (this,getBaseContext ());
        broadcastReceiver.register (getBaseContext ());
        mediaBeanQueue = new LinkedList<> ();
        mediaBeanQueue.addAll (LitePal.where ("status=0").find (MediaBean.class));
        total = mediaBeanQueue.size ();
        new Thread (()->{
            while (true){
                if(mediaBeanQueue.size ()>0){
                    MediaBean mediaBean = mediaBeanQueue.poll ();
                    uploadMediaBean (mediaBean);
                }else{
                    try {
                        Thread.sleep (10000);
                    }catch (InterruptedException e){
                        LogUtil.e (MediaService.class.getName (),e);
                    }
                }
            }
        }).start ();
        if(total>0){
            int progress = (successCount+errorCount)*100/total;
            UploadNotification.create (FTPService.this)
                    .setProgress (0)
                    .setTitle ("同步结果")
                    .setText ("共计："+total+"条,成功："+successCount+"条,失败："+errorCount+"条 \n进度:"+progress+"%")
                    .show ();
        }
    }
    private void uploadMediaBean(MediaBean mediaBean){
        String path = mediaBean.getPath ();
        File file = new File (path);
        if(!file.exists ()){
            mediaBean.setIsExist (-1);
        }else {
            String uploadId = uploadFTP (getBaseContext (),file);
            if(uploadId!=null){
                mediaBean.setUploadId (uploadId);
            }
        }
        mediaBean.update (mediaBean.getId ());
    }
    public String uploadFTP(final Context context, File file) {
        try {
            String uploadId = new FTPUploadRequest (context, ip, port)
                    .setUsernameAndPassword(ftpUserName, ftpPasswrod)
                    //.addFileToUpload(file.getAbsolutePath (), "/remote/path")
                    .addFileToUpload(file.getAbsolutePath ())
                    .setNotificationConfig(getNotificationConfig (file.getName ()))
                    .setMaxRetries(3)
                    .setConnectTimeout (5000)
                    .startUpload();
            return uploadId;
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
        return null;
    }
    /**
    * @Author peiyongdong
    * @Description ( 上传结果回调 )
    * @Date 14:25 2019/10/16
    * @Param
    * @return
    **/
    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
            super.onProgress (context,uploadInfo);
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
            errorCount++;
            int progress = (successCount+errorCount)*100/total;
            UploadNotification.create (FTPService.this)
                    .setTitle ("同步结果")
                    .setText ("共计："+total+"条,成功："+successCount+"条,失败："+errorCount+"条 \n进度:"+progress+"%")
                    .setProgress (progress)
                    .show ();
            LogUtil.e ("上传"+getClass ().getName (),exception);
            super.onError (context,uploadInfo,serverResponse,exception);

        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
            successCount++;
            String uploadId = uploadInfo.getUploadId ();
            List<MediaBean> mediaBeanList = LitePal.where ("uploadId='"+uploadId+"'").find (MediaBean.class);
            if(mediaBeanList!=null&&mediaBeanList.size ()>0){
                MediaBean mediaBean = mediaBeanList.get (0);
                mediaBean.setStatus (UPLOAD_STATUS_SUCCESS);
                mediaBean.update (mediaBean.getId ());
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if(uploadInfo.getNotificationID ()!=null){
                notificationManager.cancel (uploadInfo.getNotificationID ());
            }
            int progress = (successCount+errorCount)*100/total;
            UploadNotification.create (FTPService.this)
                    .setTitle ("同步结果")
                    .setText ("共计："+total+"条,成功："+successCount+"条,失败："+errorCount+"条 \n进度:"+progress+"%")
                    .setProgress (progress)
                    .show ();
            super.onCompleted (context,uploadInfo,serverResponse);
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
            super.onCancelled (context,uploadInfo);
        }
    };
    protected UploadNotificationConfig getNotificationConfig(String title) {
        UploadNotificationConfig config = new UploadNotificationConfig();

        PendingIntent clickIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        config.setTitleForAllStatuses(title).setRingToneEnabled(true).setClickIntentForAllStatuses(clickIntent).setClearOnActionForAllStatuses(true);
        config.getProgress().message = "开始同步:" + Placeholders.UPLOADED_FILES +"/"+Placeholders.TOTAL_FILES+ " " + Placeholders.UPLOAD_RATE + " - " + Pack200.Packer.PROGRESS;
       /* config.getProgress().iconResourceID = R.drawable.ic_upload;
        config.getProgress().iconColorResourceID = Color.BLUE;*/

        config.getCompleted().message = "同步成功！耗时: " + Placeholders.ELAPSED_TIME;
        config.getCompleted().iconColorResourceID = Color.GREEN;

        config.getError().message = "同步出错啦！";
        config.getError().iconColorResourceID = Color.RED;
        return config;
    }
}
