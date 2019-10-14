package com.gospell.travel.ui.home;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.service.MediaCursorLoader;
import com.gospell.travel.service.PhotoAlbumContentObserver;

public class HomeFragment extends BaseFragment {

    private HomeViewModel homeViewModel;
    @RootView(R.layout.fragment_home)
    private View root;
    @ViewById(R.id.text_home)
    private TextView textView;
    private PhotoAlbumContentObserver photoAlbumContentObserver;
    private ContentResolver photoAlbumContentResolver;
    @Override
    protected void onCreateView() {
        homeViewModel = ViewModelProviders.of (this).get (HomeViewModel.class);
        homeViewModel.getText ().observe (this, s -> textView.setText (s));
        MediaCursorLoader mediaCursorLoader = new MediaCursorLoader (getActivity ());
        mediaCursorLoader.getAllPhotoInfo ();
        listenPhotoAlbumDB();
    }
    /**
     * 对图库的数据库变化添加监听
     */
    private void listenPhotoAlbumDB() {
        if (photoAlbumContentObserver == null) {
            PhotoAlbumHandler photoAlbumHandler = new PhotoAlbumHandler();
            photoAlbumContentObserver = new PhotoAlbumContentObserver(photoAlbumHandler);
            photoAlbumContentResolver = getActivity ().getContentResolver();
            registerContentObserver();
        }
    }
    /**
     * 注册相册变化的相关监听
     */
    private void registerContentObserver() {
        if (photoAlbumContentObserver == null) {
            return;
        }
        Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        photoAlbumContentResolver.registerContentObserver(photoUri, false, photoAlbumContentObserver);
        photoAlbumContentObserver.setOnChangeListener(() -> {
            System.out.println ("aaaaaaaaaaa");

        });
    }
    /**
     * 图库更新的Handle 防止内存泄漏写成静态内部类
     */
    private static class PhotoAlbumHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}