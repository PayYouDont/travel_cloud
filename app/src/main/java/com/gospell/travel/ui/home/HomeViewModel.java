package com.gospell.travel.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gospell.travel.entity.MediaBean;

import org.litepal.LitePal;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<MediaBean>> mText;
    public HomeViewModel() {
        mText = new MutableLiveData<> ();
        mText.setValue (LitePal.findAll (MediaBean.class));
    }

    public LiveData<List<MediaBean>> getText() {
        return mText;
    }
}