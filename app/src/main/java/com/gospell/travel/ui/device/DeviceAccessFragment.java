package com.gospell.travel.ui.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gospell.travel.Constants;
import com.gospell.travel.MainActivity;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.common.util.NetworkUtil;
import com.gospell.travel.common.util.StringUtil;
import com.gospell.travel.entity.LoadInfo;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.ftp.FTPHelper;
import com.gospell.travel.ftp.FTPService;
import com.gospell.travel.ftp.FTPUtil;
import com.gospell.travel.ui.util.ToastUtil;
import com.gospell.travel.ui.view.CustomDialog;
import com.gospell.travel.ui.view.CustomNotification;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.IOException;
import java.net.ConnectException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import me.rosuh.filepicker.config.FilePickerManager;

public class DeviceAccessFragment extends BaseFragment {
    @RootView(R.layout.fragment_device_access)
    private View root;
    @ViewById(R.id.access_tv_layout)
    private LinearLayout tvLayout;
    @ViewById(R.id.access_wifi_layout)
    private LinearLayout wifiLayout;
    @ViewById(R.id.access_tv_check)
    private ImageView tvCheckImage;
    @ViewById(R.id.access_wifi_check)
    private ImageView wifiCheckImage;
    @ViewById(R.id.test_pauseBtn)
    private Button testPauseBtn;
    @ViewById(R.id.select_ftp_server)
    private Button selectFtpServer;
    @ViewById(R.id.ftp_ip)
    private EditText ftpIP;
    @ViewById(R.id.ftp_port)
    private EditText ftpPort;
    @ViewById(R.id.ftp_username)
    private EditText ftpUsername;
    @ViewById(R.id.ftp_password)
    private EditText ftpPassword;
    @ViewById(R.id.save_config)
    private Button saveConfigBtn;
    @ViewById(R.id.local_ip)
    private Button localIpBtn;
    @ViewById(R.id.choose_file)
    private Button chooseFileBtn;
    @ViewById(R.id.download_localBtn)
    private Button downloadBtn;
    @ViewById(R.id.download_filename)
    private EditText downloadFilenameText;
    boolean pause = false;
    int last = 0;
    @Override
    protected void onCreateView() {
        tvLayout.setOnClickListener (v -> {
            if(tvCheckImage.getVisibility ()==View.VISIBLE){
                tvCheckImage.setVisibility (View.INVISIBLE);
            }else {
                tvCheckImage.setVisibility (View.VISIBLE);
            }
        });
        wifiLayout.setOnClickListener (v -> {
            if(wifiCheckImage.getVisibility ()==View.VISIBLE){
                wifiCheckImage.setVisibility (View.INVISIBLE);
            }else {
                wifiCheckImage.setVisibility (View.VISIBLE);
            }
        });
        testPauseBtn.setOnClickListener (v -> {
            if(FTPService.UPLOAD_PAUSE){
                testPauseBtn.setText ("暂停上传");
            }else {
                testPauseBtn.setText ("继续上传");
            }
            FTPService.UPLOAD_PAUSE = !FTPService.UPLOAD_PAUSE;
        });
        selectFtpServer.setOnClickListener (v -> {
            FileListFragment listFragment = new FileListFragment ();
            getFragmentManager ().beginTransaction ().replace (R.id.nav_host_fragment,listFragment).commit ();
        });
        saveConfigBtn.setOnClickListener (v -> {
            String ip = ftpIP.getText ().toString ();
            String portStr = ftpPort.getText ().toString ();
            String username = ftpUsername.getText ().toString ();
            String password = ftpPassword.getText ().toString ();
            if(StringUtil.isEmpty (ip)||StringUtil.isEmpty (portStr)){
                Toast.makeText (getContext (),"IP和端口均不能为空！",Toast.LENGTH_SHORT).show ();
                return;
            }
            int port = Integer.valueOf (portStr);
            if(!FTPService.UPLOAD_PAUSE){
                FTPService.UPLOAD_PAUSE = true;
                testPauseBtn.setText ("开始上传");
            }
            Constants.FTP_SERVER_IP = ip;
            Constants.FTP_SERVER_PORT = port;
            Constants.FTP_USERNAME = username;
            Constants.FTP_PASSWORD = password;
            Toast.makeText (getContext (),"保存配置成功！",Toast.LENGTH_SHORT).show ();
            ((MainActivity)(getActivity ())).restartUploadService ();
        });
        localIpBtn.setOnClickListener (v -> {
            Toast.makeText (getContext (),"当前ip:"+ NetworkUtil.getLocalIpAddress (getContext ()),Toast.LENGTH_SHORT).show ();
        });
        chooseFileBtn.setOnClickListener (v -> {
            FilePickerManager.INSTANCE.from (this).forResult (FilePickerManager.REQUEST_CODE);
        });
        downloadBtn.setOnClickListener (v -> {
            String fileName = downloadFilenameText.getText ().toString ();

            if(StringUtil.isEmpty (fileName)){
                Toast.makeText (getContext (),"文件名不能为空！",Toast.LENGTH_LONG).show ();
            }
            LoadInfo info = new LoadInfo ();
            long startDate = new Date ().getTime ();
            Handler handler = new Handler (){
                @Override
                public void handleMessage(Message msg) {
                    if(msg.arg1 == 1){
                        String message = msg.obj.toString ();
                        ToastUtil.makeText (getContext (), message);
                    }else if(msg.arg1 == 2){
                        DecimalFormat df = new DecimalFormat("######0.0");
                        double total = info.getTotalSize ()/1024/1024;
                        float rate = info.getEvalRate ();
                        //double downloadSize = info.getDownloadTotalSize ()/1024;
                        long nowDate = new Date ().getTime ();
                        int second = ((int)(nowDate - startDate)/1000);
                        if(second - last >0){
                            last = second;
                            String rateStr = rate>1024?df.format (rate/1024)+"Mb/s":df.format (rate)+"kb/s";
                            CustomNotification.create (getContext ())
                                    .setTitle ("下载中...")
                                    .setProgress (info.getProgress ())
                                    .setFileName (fileName)
                                    .setLoadType (CustomNotification.LoadType.download)
                                    .setRateMsg ("当前速度："+rateStr.trim ())
                                    .setLoadSize ("共计:"+ df.format (total).trim () +"MB" )
                                    .setLoadClickListener (loading -> pause = !loading)
                                    .show ();
                        }
                    }
                }
            };
            FTPClient ftpClient = new FTPClient ();
            ftpClient.setBufferSize(1024 * 1024);
            ftpClient.setControlEncoding ("ISO-8859-1");
            ftpClient.setConnectTimeout (30000);//连接超时
            ftpClient.setDataTimeout (60000);//传输超时默认
            ftpClient.setCopyStreamListener (new CopyStreamListener () {
                @Override
                public void bytesTransferred(CopyStreamEvent event) {
                }

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                    while (pause){
                        try {
                            Thread.sleep (1000);
                        }catch (Exception e){
                            e.printStackTrace ();
                        }
                    }
                    info.setLoadTotalSize (totalBytesTransferred/1024);
                    info.setProgress ((int)(totalBytesTransferred*100/info.getTotalSize ()));
                    Message message = new Message ();
                    message.arg1 = 2;
                    handler.sendMessage (message);
                }
            });
            FTPHelper helper = new FTPHelper (new MediaBean (),ftpClient);
            new Thread (() -> {
                String msg = "";
                try {
                    loginFTP (ftpClient);
                    info.setBeginLoadDate (new Date ());
                    info.setTotalSize (FTPUtil.getFileSize (ftpClient,fileName));
                    String path = helper.download (fileName, Environment.getExternalStorageDirectory ().getPath ());
                    msg = "下载成功！路径：" + path;
                } catch (Exception e) {
                    e.printStackTrace ();
                    msg = "下载失败：" + e.getMessage ();
                }
                Message message = new Message ();
                message.obj =msg;
                message.arg1 = 1;
                handler.sendMessage (message);
            }).start ();
        });
    }

    private void loginFTP(FTPClient ftpClient) throws Exception{
        int retries = 3;
        String remotePath="travel";
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
    private void closeClient(FTPClient ftpClient) throws IOException {
        if(ftpClient!=null&&ftpClient.isConnected ()){
            ftpClient.disconnect();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            List<String> list = FilePickerManager.INSTANCE.obtainData ();
            System.out.println (list);
            Toast.makeText(this.getActivity (), "当前已选："+list.size ()+"项，路径："+list, Toast.LENGTH_SHORT).show();
            //dialog.setcontentText ("当前已选："+list.size ()+"项，路径："+list)
        }else {
            Toast.makeText(this.getActivity (), "没有选择任何东西~", Toast.LENGTH_SHORT).show();
        }
    }

}