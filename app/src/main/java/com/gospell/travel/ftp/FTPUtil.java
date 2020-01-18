package com.gospell.travel.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.OutputStream;

public class FTPUtil {
    public static boolean createDirecroty(FTPClient ftpClient,String remote) throws Exception {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(ftpClient,directory)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                System.out.println("***************");
                System.out.println("subDirectory:"+subDirectory);
                System.out.println("path:"+path);
                //false表示当前文件夹下没有文件
                if (!existFile(ftpClient,path)) {
                    if (makeDirectory(ftpClient,subDirectory)) {
                        changeWorkingDirectory(ftpClient,subDirectory);
                    } else {
                        System.out.println("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(ftpClient,subDirectory);
                    }
                } else {
                    changeWorkingDirectory(ftpClient,subDirectory);
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }
    /**
     * @Author peiyongdong
     * @Description ( 改变目录路径 )
     * @Date 9:52 2019/10/29
     * @Param [directory]
     * @return boolean
     **/
    public static boolean changeWorkingDirectory(FTPClient ftpClient, String directory) throws Exception {
        boolean flag = true;
        try {
            //flag = getDefaultFtpClient().changeWorkingDirectory(directory);
            flag = ftpClient.changeWorkingDirectory (directory);
            if (flag) {
                System.out.println (Thread.currentThread ().getName ()+"进入文件夹" + directory + " 成功！");
            } else {
                System.out.println (Thread.currentThread ().getName ()+"进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace ();
            throw new Exception ("改变目录路径失败");
        }
        return flag;
    }
    /**
     * @Author peiyongdong
     * @Description ( 判断ftp服务器文件是否存在 )
     * @Date 9:52 2019/10/29
     * @Param [path]
     * @return boolean
     **/
    public static boolean existFile(FTPClient ftpClient,String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        //FTPFile[] ftpFileArr = getDefaultFtpClient().listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }
    /**
     * @Author peiyongdong
     * @Description ( 创建目录 )
     * @Date 9:51 2019/10/29
     * @Param [dir]
     * @return boolean
     **/
    public static boolean makeDirectory(FTPClient ftpClient,String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                System.out.println("创建文件夹" + dir + " 成功！");

            } else {
                System.out.println("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * @Author peiyongdong
     * @Description ( 下载文件 )
     * @Date 9:51 2019/10/29
     * @Param [ftpClient, pathName, fileName, os]
     * @return boolean
     **/
    public  boolean downloadFile(FTPClient ftpClient,String pathName, String fileName, OutputStream os) throws Exception{
        boolean flag;
        //OutputStream os=null;
        try {
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathName);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile file : ftpFiles){
                if(fileName.equalsIgnoreCase(file.getName())){
                    //File localFile = new File(localpath + "/" + file.getName());
                    //os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
            System.out.println("下载文件成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("下载文件失败");
        } finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                    throw new Exception("下载文件失败");
                }
            }
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception("下载文件失败");
                }
            }
        }
        return flag;
    }
    /**
     * @Author peiyongdong
     * @Description ( 删除文件 )
     * @Date 9:51 2019/10/29
     * @Param [ftpClient, pathName, fileName]
     * @return boolean
     **/
    public boolean deleteFile(FTPClient ftpClient,String pathName, String fileName) throws Exception{
        boolean flag;

        try {
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathName);
            ftpClient.dele(fileName);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("删除文件失败");
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                    throw new Exception("删除文件失败");
                }
            }
        }
        return flag;
    }
}
