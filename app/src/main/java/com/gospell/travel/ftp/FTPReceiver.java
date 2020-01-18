package com.gospell.travel.ftp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gospell.travel.entity.UploadInfo;

import lombok.Setter;

public class FTPReceiver extends BroadcastReceiver {
    public static final String ACTION_UPLOADINFO = "ACTION_UPLOADINFO";
    public static final String ACTION_DOWNLOADINFO = "ACTION_UPLOADINFO";
    @Setter
    private OnUpdateStatus onUpdateStatus;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction ().equals (ACTION_UPLOADINFO)){
            UploadInfo uploadInfo = (UploadInfo) intent.getSerializableExtra ("uploadInfo");
            if(onUpdateStatus!=null){
                onUpdateStatus.upload (uploadInfo);
            }
        }
    }
    public interface OnUpdateStatus{
        void upload(UploadInfo uploadInfo);
    }
}
