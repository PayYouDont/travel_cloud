package com.gospell.travel.ui.home;

import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.entity.MediaBean;

import org.litepal.LitePal;

import java.util.List;

public class HomeFragment extends BaseFragment {

    private HomeViewModel homeViewModel;
    @RootView(R.layout.fragment_home)
    private View root;
    @ViewById(R.id.text_home)
    private TextView textView;
    @Override
    protected void onCreateView() {
        homeViewModel = ViewModelProviders.of (this).get (HomeViewModel.class);
        homeViewModel.getText ().observe (this, mediaBeans -> textView.setText (mediaBeans.toString ()));

    }
}