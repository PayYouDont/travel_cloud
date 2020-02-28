package com.gospell.travel.ui.device;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import com.gospell.travel.ftp.FTPUploader;
import com.gospell.travel.ui.util.ToastUtil;

import java.util.List;

import me.rosuh.filepicker.config.FilePickerManager;
import ru.alexbykov.nopermission.PermissionHelper;

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
            if(FTPUploader.UPLOAD_PAUSE){
                testPauseBtn.setText ("暂停上传");
                ((MainActivity)(getActivity ())).restartUploadService ();
            }else {
                testPauseBtn.setText ("继续上传");
                ((MainActivity)(getActivity ())).stopUploadService ();
            }
            FTPUploader.UPLOAD_PAUSE = !FTPUploader.UPLOAD_PAUSE;
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
            if(!FTPUploader.UPLOAD_PAUSE){
                FTPUploader.UPLOAD_PAUSE = true;
                testPauseBtn.setText ("开始上传");
            }
            Constants.FTP_SERVER_IP = ip;
            Constants.FTP_SERVER_PORT = port;
            Constants.FTP_USERNAME = username;
            Constants.FTP_PASSWORD = password;
            Toast.makeText (getContext (),"保存配置成功！",Toast.LENGTH_SHORT).show ();
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
            Handler handler = new Handler (){
                @Override
                public void handleMessage(Message msg) {
                    String message = msg.obj.toString ();
                    ToastUtil.makeText (getContext (), message);
                }
            };
            FTPUploader ftpUploader = new FTPUploader (getContext ()).setMaxRetries (3).setRemotePath ("travel");
            new Thread (() -> {
                String msg = "";
                try {
                    String path = ftpUploader.downloadFile (fileName);
                    msg = "下载成功！路径：" + path;
                } catch (Exception e) {
                    e.printStackTrace ();
                    msg = "下载失败：" + e.getMessage ();
                }
                Message message = new Message ();
                message.obj =msg;
                handler.handleMessage (message);
            }).start ();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            List<String> list = FilePickerManager.INSTANCE.obtainData ();
            System.out.println (list);
            Toast.makeText(this.getActivity (), "当前已选："+list.size ()+"项，路径："+list, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this.getActivity (), "没有选择任何东西~", Toast.LENGTH_SHORT).show();
        }
    }

}