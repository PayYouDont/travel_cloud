package com.gospell.travel.ftp;

import com.gospell.travel.entity.MediaBean;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;

public class FTPHelper implements Serializable {
    private MediaBean mediaBean;
    private FTPClient ftpClient;
    @Getter
    private String uuid;

    public FTPHelper(MediaBean mediaBean, FTPClient ftpClient) {
        this.mediaBean = mediaBean;
        this.ftpClient = ftpClient;
        this.uuid = UUID.randomUUID ().toString ();
    }
    public void upload() throws IOException {
        String filePath = new File (mediaBean.getPath ()).getAbsolutePath ();
        File file = new File (filePath);
        if(file.exists ()&&ftpClient.isConnected ()){
            String fileName = new String (file.getName ().getBytes ("GBK"),"ISO-8859-1");
            String remoteFilename = ftpClient.printWorkingDirectory () + File.separatorChar+ fileName;
            long fileLength = file.length ();
            FTPFile[] files = ftpClient.listFiles(remoteFilename);// 判断软件中心是否包含这个文件
            InputStream inputStream = new FileInputStream (file);
            if (files.length == 1) { // 软件中心包含该文件
                long remoteSize = files[0].getSize();// 软件中心的文件大小
                if (remoteSize == fileLength) { // // 软件中心有这个文件，并且和打算要上传的文件大小一样，则说要上传的文件已存在
                    System.out.println ("要上传的文件已存在");
                    return;
                }else if(remoteSize>fileLength){// 软件中心的文件比要上传的大，可能新上传的文件被修改了，然后再次上传的
                    //不一致是否重新上传功能待开发，这里默认用重新上传处理
                    ftpClient.dele(fileName);
                    ftpClient.storeFile(fileName, inputStream);
                    return;
                }
                // 软件中心存的文件比要上传的文件小，则尝试移动文件内读取指针,实现断点续传
                if(inputStream.skip(remoteSize) == remoteSize){
                    ftpClient.setRestartOffset(remoteSize);
                    ftpClient.storeFile(fileName, inputStream);
                }
            }else {
                ftpClient.storeFile(fileName, inputStream);
            }
        }
    }
    public void download(String fileName,String outPath) throws Exception{
        if(ftpClient.isConnected ()){
            File file = new File (outPath);
            OutputStream out = new FileOutputStream (file);
            FTPUtil.downloadFile (this.ftpClient,ftpClient.printWorkingDirectory (),file.getName (),out);
        }
    }
}
