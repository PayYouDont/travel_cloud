package com.gospell.travel.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;

public class GalleryFragment extends BaseFragment {
    @RootView(R.layout.fragment_gallery)
    private View root;
    @ViewById(R.id.text_gallery)
    private TextView textView;
    private GalleryViewModel galleryViewModel;
    @Override
    protected void onCreateView() {
        galleryViewModel = ViewModelProviders.of (this).get (GalleryViewModel.class);
        galleryViewModel.getText ().observe (this, s-> textView.setText (s));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续

        }
    }
}