package com.gospell.travel.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gospell.travel.entity.MediaBean;

import org.litepal.LitePal;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public HomeViewModel() {
        mText = new MutableLiveData<> ();
        List<MediaBean> list = LitePal.findAll (MediaBean.class);
        StringBuffer stringBuffer = new StringBuffer ();
        list.forEach (mediaBean -> {
            stringBuffer.append (mediaBean.getDisplayName ());
            stringBuffer.append (" \n");
        });
        mText.setValue (stringBuffer.substring (0,stringBuffer.length ()-2));
    }

    public LiveData<String> getText() {
        return mText;
    }
}