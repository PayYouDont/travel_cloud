package com.gospell.travel.ui.send;

import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;

public class SendFragment extends BaseFragment {
    private SendViewModel sendViewModel;
    @RootView(R.layout.fragment_send)
    private View root;
    @ViewById(R.id.text_send)
    private TextView textView;
    @Override
    protected void onCreateView() {
        sendViewModel = ViewModelProviders.of (this).get (SendViewModel.class);
        sendViewModel.getText ().observe (this, s-> textView.setText (s));
    }
}