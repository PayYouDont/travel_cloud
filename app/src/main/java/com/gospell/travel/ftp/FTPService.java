package com.gospell.travel.ftp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gospell.travel.Constants;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.ui.util.UploadNotification;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FTPService extends Service implements CopyStreamListener {
    private static final int STATUS_SUCCESS = 1;
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
    private int maxiPoolSize = 3;
    private int total = 0;
    private int successCount;
    private int errorCount;
    private int progress;
    private Handler uploadHandler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            if(msg!=null){
                progress = successCount*100/total;
                UploadNotification.create (FTPService.this)
                        .setTitle ("同步结果")
                        .setText ("共计：" + total + "条,成功：" + successCount + "条,失败：" + errorCount + "条 \n进度:" + progress + "%")
                        .setProgress (progress)
                        .show ();
                 /*Intent intent = new Intent();
        intent.setAction(FTPReceiver.ACTION_UPLOADINFO);
        intent.putExtra ("uploadInfo",uploadInfo);
        LocalBroadcastManager.getInstance(getBaseContext ()).sendBroadcast(intent);*/
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
        LitePal.where ("status=" + STATUS_SUCCESS).findAsync (MediaBean.class).listen (list -> {
            total += list.size ();
            Toast.makeText (getBaseContext (),"查询到"+list.size ()+"条待上传的数据！",Toast.LENGTH_SHORT).show ();
            list.forEach (mediaBean -> uploadMediaBean (mediaBean));
        });
    }

    public void uploadMediaBean(MediaBean mediaBean){
        String path = mediaBean.getPath ();
        File file = new File (path);
        if (!file.exists ()) {
            mediaBean.setIsExist (MediaBean.FILE_NOTEXIST);
            mediaBean.update (mediaBean.getId ());
            return;
        }
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
                //mediaBean.setStatus (STATUS_SUCCESS);
                //mediaBean.save ();
            }catch (Exception e){
                e.printStackTrace ();
                errorCount++;
            }
            uploadHandler.handleMessage (new Message ());
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
        System.out.println ("当前已传输字节:"+totalBytesTransferred);
        System.out.println ("当前传输字节:"+bytesTransferred);
        System.out.println ("复制流中的字节数:"+streamSize);


    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        System.out.println ("服务已经被终止");

    }
}
