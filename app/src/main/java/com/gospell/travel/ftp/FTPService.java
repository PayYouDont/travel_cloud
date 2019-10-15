package com.gospell.travel.ftp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gospell.travel.common.annotation.Value;
import com.gospell.travel.common.util.ReflectUtil;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.service.MediaService;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.UploadServiceSingleBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.ftp.FTPUploadRequest;

import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FTPService extends Service{
    @Value ("ftp.serverUrl")
    private String url ;
    @Value ("ftp.serverPort")
    private int port;
    @Value ("ftp.username")
    private String ftpUserName;
    @Value ("ftp.password")
    private String ftpPasswrod;
    public static Queue<MediaBean> mediaBeanQueue;
    public static final int UPLOAD_STATUS_SUCCESS = 1;
    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
            // your implementation
            super.onProgress (context,uploadInfo);
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
            // your implementation
            System.out.print("上传出错！");
            System.out.println (serverResponse);
            System.out.println (exception);
            super.onError (context,uploadInfo,serverResponse,exception);

        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
            // your implementation
            super.onCompleted (context,uploadInfo,serverResponse);
            String uploadId = uploadInfo.getUploadId ();
            List<MediaBean> mediaBeanList = LitePal.where ("uploadId="+uploadId).find (MediaBean.class);
            System.out.print("上传成功！");
            if(mediaBeanList!=null&&mediaBeanList.size ()>0){
                MediaBean mediaBean = mediaBeanList.get (0);
                mediaBean.setStatus (UPLOAD_STATUS_SUCCESS);
                mediaBean.update (mediaBean.getId ());
            }
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
            super.onCancelled (context,uploadInfo);
            // your implementation
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException ("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate ();
        ReflectUtil.initFieldByConfig (this,getBaseContext ());
        broadcastReceiver.register (getBaseContext ());
        mediaBeanQueue = new LinkedList<> ();
        mediaBeanQueue.addAll (LitePal.where ("status=0").find (MediaBean.class));
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
    }
    public void uploadMediaBean(MediaBean mediaBean){
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
            String uploadId = new FTPUploadRequest (context, url, port)
                    .setUsernameAndPassword(ftpUserName, ftpPasswrod)
                    //.addFileToUpload(file.getAbsolutePath (), "/remote/path")
                    .addFileToUpload(file.getAbsolutePath ())
                    .setNotificationConfig(new UploadNotificationConfig ())
                    .setMaxRetries(4)
                    .setConnectTimeout (10000)
                    .startUpload();
            return uploadId;
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
        return null;
    }
}
