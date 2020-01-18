package com.gospell.travel.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gospell.travel.entity.UploadInfo;
import com.gospell.travel.ftp.FTPService;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class HomeViewModel extends ViewModel {
    @Getter
    private MutableLiveData<List<UploadInfo>> liveData;
    public HomeViewModel() {
        liveData = new MutableLiveData<> ();
        List<UploadInfo> uploadInfos = new ArrayList<> ();
        if(FTPService.uploadInfoList.size ()!=0){
            uploadInfos.addAll (FTPService.uploadInfoList);
        }
        liveData.setValue (uploadInfos);
    }
}