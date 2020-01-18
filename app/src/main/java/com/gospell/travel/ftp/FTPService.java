package com.gospell.travel.ftp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.gospell.travel.common.annotation.Value;
import com.gospell.travel.common.util.ReflectUtil;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.entity.UploadInfo;
import com.gospell.travel.service.MediaService;
import com.gospell.travel.ui.util.UploadNotification;

import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FTPService extends Service implements FTPUploader.OnSendUploadInfo {
    @Value("ftp.serverIP")
    private String ip;
    @Value("ftp.serverPort")
    private int port;
    @Value("ftp.username")
    private String ftpUserName;
    @Value("ftp.password")
    private String ftpPasswrod;
    public static Queue<MediaBean> mediaBeanQueue;
    public static List<UploadInfo> uploadInfoList = new ArrayList<> ();
    public static final int UPLOAD_STATUS_SUCCESS = 1;
    public static int total = 0;
    public static int successCount = 0;
    public static int errorCount = 0;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate ();
        ReflectUtil.initFieldByConfig (this, getBaseContext ());
        mediaBeanQueue = new LinkedList<> ();
        mediaBeanQueue.addAll (LitePal.where ("status=1").find (MediaBean.class));
        total = mediaBeanQueue.size ();
        new Thread (() -> {
            while (true) {
                if (mediaBeanQueue.size () > 0) {
                    MediaBean mediaBean = mediaBeanQueue.poll ();
                    uploadMediaBean (mediaBean);
                } else {
                    try {
                        Thread.sleep (10000);
                    } catch (InterruptedException e) {
                        LogUtil.e (MediaService.class.getName (), e);
                    }
                }
            }
        }).start ();
        UploadNotification.create (FTPService.this)
                .setProgress (0)
                .setTitle ("同步结果")
                .setText ("共计：" + total + "条,成功：" + 0 + "条,失败：" + 0 + "条 \n进度:0%")
                .show ();
    }

    private void uploadMediaBean(MediaBean mediaBean) {
        String path = mediaBean.getPath ();
        File file = new File (path);
        if (!file.exists ()) {
            mediaBean.setIsExist (-1);
            mediaBean.update (mediaBean.getId ());
            return;
        }
        new FTPUploader (getBaseContext (), ip, port)
                .setUsernameAndPassword (ftpUserName, ftpPasswrod)
                .addFileToUpload (mediaBean, "travel")
                .setEncoding ("ISO-8859-1")
                .setMaxRetries (3)
                .setMaxiPoolSize (3)
                .setConnectTimeout (5000)
                .setOnSendUploadInfo (this)
                .startUpload ();
    }

    @Override
    public void send(UploadInfo uploadInfo) {
        if(uploadInfo.getUploadStatus ()==1){
            successCount++;
            MediaBean mediaBean = uploadInfo.getMediaBean ();
            mediaBean.setStatus (UPLOAD_STATUS_SUCCESS);
            mediaBean.update (mediaBean.getId ());
        }else {
            errorCount++;
        }
        if(!uploadInfoList.contains (uploadInfo)){
            uploadInfoList.add (uploadInfo);
        }
        int progress = uploadInfoList.size ()*100/total;
        UploadNotification.create (FTPService.this)
                .setTitle ("同步结果")
                .setText ("共计："+total+"条,成功："+successCount+"条,失败："+errorCount+"条 \n进度:"+progress+"%")
                .setProgress (progress)
                .show ();
        Intent intent = new Intent();
        intent.setAction(FTPReceiver.ACTION_UPLOADINFO);
        intent.putExtra ("uploadInfo",uploadInfo);
        LocalBroadcastManager.getInstance(getBaseContext ()).sendBroadcast(intent);
    }
}
