package com.gospell.travel.ftp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.gospell.travel.Constants;
import com.gospell.travel.common.util.StringUtil;
import com.gospell.travel.entity.LoadInfo;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.ui.view.CustomNotification;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FTPService extends Service implements CopyStreamListener {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_NOTSUCCESS = 0;
    private static ThreadPoolExecutor poolExecutor;
    private static Map<Runnable, FTPClient> ftpClientMap = new HashMap<> ();
    // ftp服务保存地址
    private String remotePath="travel";
    //连接超时默认30秒
    private int connectTimeout = 30000;
    //传输超时默认60秒
    private int dataTimeout = 60000;
    //重连次数
    private int maxRetries = 2;
    private String encoding = "ISO-8859-1";
    //线程池中最大线程数量
    private int maxiPoolSize = 1;
    private int total = 0;
    private int successCount;
    private int errorCount;
    private int progress;
    private double totalSize = 0;
    public static boolean UPLOAD_PAUSE = false;
    private LoadInfo loadInfo;
    int sum = 0;
    private Handler uploadHandler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            if(msg!=null){
                progress = successCount*100/total;
                float rate = loadInfo.getEvalRate ();
                String rateStr = rate>1024? StringUtil.formatFloat (rate/1024)+"Mb/s":StringUtil.formatFloat (rate)+"kb/s";
                String fileName = msg.obj.toString ();
                CustomNotification.create (FTPService.this)
                        .setTitle ("同步中...")
                        .setProgress (progress)
                        .setFileName (fileName)
                        .setLoadType (CustomNotification.LoadType.upload)
                        .setRateMsg ("当前速度："+rateStr)
                        .setLoadSize ("共计:"+ total +"条" )
                        .setLoadClickListener (loading -> UPLOAD_PAUSE = !loading)
                        .show ();
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate ();
        Log.d (getClass ().getName (),"服务已启动");
        loadInfo = new LoadInfo ();
        loadInfo.setBeginLoadDate (new Date ());
        new Thread (()->{
            while (true){
                List<MediaBean> list = LitePal.where ("status=" + STATUS_SUCCESS+" and isExist="+MediaBean.FILE_EXIST).limit (10).offset (total).find (MediaBean.class);
                if (list.size ()>0){
                    System.out.println ("size="+list.size ());
                    total += list.size ();
                    list.forEach (mediaBean -> {
                        uploadMediaBean (mediaBean);
                    });
                }else {
                    try {
                        Thread.sleep (1000);
                    }catch (InterruptedException e){
                        e.printStackTrace ();
                    }
                }
            }
        }).start ();
    }

    public void uploadMediaBean(MediaBean mediaBean){
        String path = mediaBean.getPath ();
        File file = new File (path);
        if (!file.exists ()) {
            total--;
            mediaBean.setIsExist (MediaBean.FILE_NOTEXIST);
            mediaBean.update (mediaBean.getId ());
            return;
        }
        totalSize += mediaBean.getSize ()/1024/1024;
        loadInfo.setTotalSize (totalSize);
        if(poolExecutor == null){
            synchronized (this){
                if(poolExecutor==null){
                    poolExecutor = new ThreadPoolExecutor (maxiPoolSize,maxiPoolSize,10, TimeUnit.SECONDS,new LinkedBlockingQueue<> ());
                }
            }
        }
        poolExecutor.execute (() -> {
            FTPClient ftpClient;
            if(ftpClientMap.containsKey (Thread.currentThread ())){
                ftpClient = ftpClientMap.get (Thread.currentThread ());
            }else {
                ftpClient = getFtpClient ();
                ftpClientMap.put (Thread.currentThread (),ftpClient);
            }
            FTPHelper helper = new FTPHelper (mediaBean,ftpClient);
            try {
                if(!ftpClient.isConnected ()){
                    loginFTP (ftpClient);
                }
                helper.upload ();
                successCount++;
            }catch (Exception e){
                e.printStackTrace ();
                errorCount++;
            }
            Message message = new Message ();
            message.obj = mediaBean.getDisplayName ();
            uploadHandler.sendMessage (message);
        });
    }
    private FTPClient getFtpClient(){
        FTPClient ftpClient = new FTPClient ();
        ftpClient.setBufferSize(1024 * 1024);
        ftpClient.setControlEncoding (encoding);
        ftpClient.setConnectTimeout (connectTimeout);//连接超时
        ftpClient.setDataTimeout (dataTimeout);//传输超时默认
        ftpClient.setCopyStreamListener (this);
        return ftpClient;
    }
    private void loginFTP(FTPClient ftpClient) throws Exception{
        int retries = maxRetries;
        boolean loginSuccess = false;
        do {
            ftpClient.connect (Constants.FTP_SERVER_IP,Constants.FTP_SERVER_PORT);
            if(FTPReply.isPositiveCompletion (ftpClient.getReplyCode ())){
                loginSuccess = ftpClient.login (Constants.FTP_USERNAME,Constants.FTP_PASSWORD);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //被动模式
                ftpClient.enterLocalPassiveMode();
                if(--retries==0&&!loginSuccess){
                    closeClient (ftpClient);
                    throw new ConnectException ("未连接到FTP，用户名或密码错误。");
                }
            }
        }while (retries>0&&!loginSuccess);
        if(ftpClient.printWorkingDirectory ().indexOf (remotePath)==-1){
            FTPUtil.createDirecroty(ftpClient,remotePath);
        }
        ftpClient.changeWorkingDirectory(remotePath);
    }
    private void closeClient(FTPClient ftpClient) throws IOException{
        if(ftpClient!=null&&ftpClient.isConnected ()){
            ftpClient.disconnect();
        }
    }

    @Override
    public void bytesTransferred(CopyStreamEvent event) {

    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
        /*System.out.println ("当前已传输字节:"+totalBytesTransferred);
        System.out.println ("当前传输字节:"+bytesTransferred);
        System.out.println ("复制流中的字节数:"+streamSize);*/
        while (UPLOAD_PAUSE){
            try {
                Thread.sleep (1000);
            }catch (Exception e){
                e.printStackTrace ();
            }
        }
        loadInfo.setLoadTotalSize (totalBytesTransferred/1024);
    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        System.out.println ("服务已经被终止");

    }
}
