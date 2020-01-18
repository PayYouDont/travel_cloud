package com.gospell.travel.ftp;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.entity.UploadInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

public class FTPUploader implements Serializable {
    private Context context;
    //账号
    private String userName;
    //密码
    private String password;
    //ftp服务器ip
    private String ip;
    //端口号
    private int port;
    private String encoding = "ISO-8859-1";
    //文件路径
    @Getter
    private String filePath="";
    @Getter
    private MediaBean mediaBean;
    // ftp服务保存地址
    private String remotePath="";
    //连接超时默认30秒
    private int connectTimeout = 30000;
    //传输超时默认60秒
    private int dataTimeout = 60000;
    //重连次数
    private int maxRetries = 2;
    private int maxiPoolSize = 2;
    private FTPClient ftpClient;
    private static ThreadPoolExecutor poolExecutor;
    private static Map<Runnable,FTPClient> ftpClientMap = new HashMap<> ();
    private String uuid;
    private OnSendUploadInfo onSendUploadInfo;
    public FTPUploader(Context context, String ip, int port) {
        this.context = context;
        this.ip = ip;
        this.port = port;
        this.uuid = UUID.randomUUID ().toString ();
    }
    public FTPUploader setUsernameAndPassword(String userName,String password){
        this.userName = userName;
        this.password = password;
        return this;
    }
    public FTPUploader addFileToUpload(String filePath){
        this.filePath = filePath;
        return this;
    }
    public FTPUploader addFileToUpload(String filePath,String remotePath){
        this.filePath = filePath;
        this.remotePath = remotePath;
        return this;
    }
    public FTPUploader addFileToUpload(MediaBean mediaBean,String remotePath){
        this.mediaBean = mediaBean;
        this.remotePath = remotePath;
        this.filePath = new File (mediaBean.getPath ()).getAbsolutePath ();
        return this;
    }
    public FTPUploader setEncoding(String encoding){
        this.encoding = encoding;
        return this;
    }
    public FTPUploader setConnectTimeout(int connectTimeout){
        this.connectTimeout = connectTimeout;
        return this;
    }
    public FTPUploader setDataTimeout(int dataTimeout){
        this.dataTimeout = dataTimeout;
        return this;
    }
    public FTPUploader setMaxRetries(int maxRetries){
        this.maxRetries = maxRetries;
        return this;
    }
    public FTPUploader setMaxiPoolSize(int maxiPoolSize){
        this.maxiPoolSize = maxiPoolSize;
        return this;
    }
    public FTPUploader setOnSendUploadInfo(OnSendUploadInfo onSendUploadInfo){
        this.onSendUploadInfo = onSendUploadInfo;
        return this;
    }
    /**
    * @Author peiyongdong
    * @Description ( 创建FTP客户端 )
    * @Date 14:48 2019/10/29
    * @Param []
    * @return org.apache.commons.net.ftp.FTPClient
    **/
    private FTPClient getFtpClient(){
        ftpClient = new FTPClient ();
        ftpClient.setBufferSize(1024 * 1024);
        ftpClient.setControlEncoding (encoding);
        ftpClient.setConnectTimeout (connectTimeout);//连接超时
        ftpClient.setDataTimeout (dataTimeout);//传输超时默认
        try {
            loginFTP ();
        }catch (IOException e){
            Looper.prepare();
            Toast.makeText (context,"FTP的端口错误,请正确配置。",Toast.LENGTH_SHORT).show ();
            Looper.loop();
            e.printStackTrace ();
        }
        return ftpClient;
    }
    private void loginFTP() throws IOException{
        int retries = maxRetries;
        boolean status = false;
        do {
            ftpClient.connect (ip,port);
            if(FTPReply.isPositiveCompletion (ftpClient.getReplyCode ())){
                status = ftpClient.login (userName,password);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //被动模式
                ftpClient.enterLocalPassiveMode();
                if(--retries==0&&!status){
                    Toast.makeText (context,"未连接到FTP，用户名或密码错误。",Toast.LENGTH_SHORT).show ();
                    closeClient (ftpClient);
                }
            }
        }while (retries>0&&!status);

    }
    /**
    * @Author peiyongdong
    * @Description ( 上传文件 )
    * @Date 9:53 2019/10/29
    * @Param [ftpClient, fileName=上传到ftp的文件名]
    * @return boolean
    **/
    private boolean uploadFile(FTPClient ftpClient) throws Exception {
        File file = new File (filePath);
        if(file.exists ()){
            InputStream inputStream = new FileInputStream (file);
            return uploadFile(ftpClient,remotePath,file.getName (),inputStream,file.length ());
        }
        return false;
    }
    /**
    * @Author peiyongdong
    * @Description ( 上传文件 )
    * @Date 9:52 2019/10/29
    * @Param [ftpClient, remotePath=ftp服务保存地址, uuidName=上传到ftp的文件名, inputStream]
    * @return boolean
    **/
    private boolean uploadFile(FTPClient ftpClient,String remotePath, String fileName,InputStream inputStream,long fileLength) throws Exception{
        try{
            if(remotePath!=null&&!remotePath.equals ("")&&ftpClient.printWorkingDirectory ().indexOf (remotePath)==-1){
                FTPUtil.createDirecroty(ftpClient,remotePath);
                ftpClient.changeWorkingDirectory(remotePath);
            }
            fileName = new String (fileName.getBytes ("GBK"),encoding);
            String remoteFilename = ftpClient.printWorkingDirectory () + "/"+ fileName;
            FTPFile[] files = ftpClient.listFiles(remoteFilename);// 判断软件中心是否包含这个文件
            // 软件中心包含该文件
            if (files.length == 1) {
                long remoteSize = files[0].getSize();// 软件中心的文件大小
                if (remoteSize == fileLength) { // 软件中心有这个文件，并且和打算要上传的文件大小一样，则说要上传的文件已存在
                    System.out.println("要上传的文件已存在");
                    createUploadInfo (true);
                }else if(remoteSize>fileLength){// 软件中心的文件比要上传的大，可能新上传的文件被修改了，然后再次上传的
                    System.out.println("软件中心的软件比即将上传的要大，无须上传或重新命名要上传的文件名");
                    ftpClient.dele(fileName);
                    ftpClient.storeFile(fileName, inputStream);
                    return false;
                }
                // 软件中心存的文件比要上传的文件小，则尝试移动文件内读取指针,实现断点续传 **************
                if (inputStream.skip(remoteSize) == remoteSize) {
                    ftpClient.setRestartOffset(remoteSize);
                    ftpClient.storeFile(fileName, inputStream);
                }
            }else{
                ftpClient.storeFile(fileName, inputStream);
            }
            createUploadInfo (true);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        createUploadInfo (false);
        return false;
    }
    private void createUploadInfo(boolean successed){
        UploadInfo uploadInfo = new UploadInfo ();
        uploadInfo.setProgress (100);
        uploadInfo.setUuid (uuid);
        uploadInfo.setFileBeanId (mediaBean.getId ());
        uploadInfo.setUploadStatus (1);
        String title = "上传成功";
        if (!successed){
            title = "上传失败";
            uploadInfo.setUploadStatus (0);
        }
        uploadInfo.setTitle (title);
        if(onSendUploadInfo!=null){
            onSendUploadInfo.send (uploadInfo);
        }
    }
    interface OnSendUploadInfo{
        void send(UploadInfo uploadInfo);
    }
    /**
    * @Author peiyongdong
    * @Description ( 关闭你客户端 )
    * @Date 14:48 2019/10/29
    * @Param [ftpClient]
    * @return void
    **/
    private void closeClient(FTPClient ftpClient){
        if(ftpClient!=null&&ftpClient.isConnected ()){
            try{
                ftpClient.disconnect();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    private void closeClients(){
        ftpClientMap.forEach ((runnable, client) -> {
            closeClient (client);
        });
    }
    /**********************************************************************************************/
    public void startUpload(){
        if(poolExecutor == null){
            synchronized (FTPUploader.class){
                if(poolExecutor==null){
                    poolExecutor = new ThreadPoolExecutor (maxiPoolSize,maxiPoolSize,10, TimeUnit.SECONDS,new LinkedBlockingQueue<> ());
                }
            }
        }
        poolExecutor.execute (() -> {
            if(ftpClientMap.containsKey (Thread.currentThread ())){
                ftpClient = ftpClientMap.get (Thread.currentThread ());
            }else {
                ftpClient = getFtpClient ();
                ftpClientMap.put (Thread.currentThread (),ftpClient);
            }
            try {
                if(!ftpClient.isConnected ()){
                    loginFTP ();
                }
                uploadFile (ftpClient);
            }catch (Exception e){
                e.printStackTrace ();
            }
        });
    }
}
