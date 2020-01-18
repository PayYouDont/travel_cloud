package com.gospell.travel.ui.home;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.gospell.travel.R;
import com.gospell.travel.common.adapter.BaseAdapter;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.entity.UploadInfo;
import com.gospell.travel.ftp.FTPReceiver;
import com.gospell.travel.ftp.FTPService;
import com.gospell.travel.ui.util.FileUtil;

import java.util.List;

import cn.bingoogolapple.progressbar.BGAProgressBar;

public class HomeFragment2 extends BaseFragment {

    //private HomeViewModel homeViewModel;
    @RootView(R.layout.fragment_home)
    private View root;
    @ViewById(R.id.upload_status_list)
    private RecyclerView uploadListView;
    private List<UploadInfo> uploadInfoList;
    private BaseAdapter<UploadInfo> baseAdapter;
    @ViewById(R.id.upload_all_progress)
    private BGAProgressBar uploadAllProgress;
    @ViewById(R.id.upload_all_info)
    private TextView uploadAllInfo;
    @ViewById(R.id.result_tabLayout)
    private TabLayout mTabLaout;
    private String[] tabTexts = {"已上传", "正在下载"};

    @Override
    protected void onCreateView() {
        for (int i = 0; i < tabTexts.length; i++) {
            TabLayout.Tab tab = mTabLaout.newTab ();
            tab.setText (tabTexts[i]);
            mTabLaout.addTab (tab);
        }
        mTabLaout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                baseAdapter.getTList ().clear ();
                switch (tab.getText ().toString ()) {
                    case "已上传":
                        initUploadedRecyclerView ();
                        break;
                    case "已下载":
                        initDownloadedRecyclerView ();
                        break;
                }
                baseAdapter.notifyDataSetChanged ();
                uploadAllProgress.setProgress (100);
                uploadAllInfo.setText ("上传进度：100%");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                System.out.println ("onTabUnselected::tab=" + tab.getText ());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                System.out.println ("onTabReselected::tab=" + tab.getText ());
            }
        });
        uploadInfoList = FTPService.uploadInfoList;
        int allProgress = 0;
        if(FTPService.total>0){
            allProgress = uploadInfoList.size () * 100 / FTPService.total;
        }
        uploadAllProgress.setProgress (allProgress);
        uploadAllInfo.setText ("上传进度：" + allProgress + "%");
        uploadListView.setLayoutManager (new LinearLayoutManager (getContext (), LinearLayoutManager.VERTICAL, false));
        baseAdapter = new BaseAdapter<> (uploadInfoList, R.layout.upload_status_item);
        baseAdapter.setOnSetContetnViewListener ((holder, uploadInfo) -> holder.views.forEach (view -> {
            switch (view.getId ()) {
                case R.id.upload_image:
                    ImageView imageView = (ImageView) view;
                    Bitmap bitmap = FileUtil.parseToBitmap (root.getContext (), uploadInfo.getMediaBean ().getPath (), 100, 100, 5, 1);
                    imageView.setImageBitmap (bitmap);
                    holder.itemView.setOnClickListener (null);
                    break;
                case R.id.upload_info:
                    TextView textView = (TextView) view;
                    textView.setText (uploadInfo.getMediaBean ().getDisplayName () + uploadInfo.getTitle ());
                    break;
                case R.id.upload_progress:
                    BGAProgressBar progressBar = (BGAProgressBar) view;
                    progressBar.setProgress (uploadInfo.getProgress ());
                    break;
                case R.id.upload_status:
                    ImageView statusView = (ImageView) view;
                    /*if(uploadInfo.isPaused ()){
                        statusView.setImageResource (R.drawable.ic_status_pause);
                    }else {
                        statusView.setImageResource (R.drawable.ic_status_play);
                    }
                    statusView.setOnClickListener (v -> {
                        if(ftpUploader.isPaused ()){
                            ftpUploader.setPaused (false);
                        }else {
                            ftpUploader.setCanceled (true);
                        }
                    });*/
                    break;
            }
        }));
        uploadListView.setAdapter (baseAdapter);
        FTPReceiver ftpReceiver = new FTPReceiver ();
        ftpReceiver.setOnUpdateStatus (uploadInfo -> {
            if (baseAdapter.getTList ().contains (uploadInfo)) {
                int index = baseAdapter.getTList ().indexOf (uploadInfo);
                baseAdapter.getTList ().set (index, uploadInfo);
                baseAdapter.notifyItemChanged (index);
            } else {
                baseAdapter.getTList ().add (uploadInfo);
                baseAdapter.notifyItemInserted (baseAdapter.getTList ().size () - 1);
            }
            int progress = uploadInfoList.size () * 100 / FTPService.total;
            uploadAllProgress.setProgress (progress);
            uploadAllInfo.setText ("上传进度：" + progress + "%");
        });
        IntentFilter filter = new IntentFilter ();
        filter.addAction (FTPReceiver.ACTION_UPLOADINFO);
        LocalBroadcastManager.getInstance (getContext ()).registerReceiver (ftpReceiver, filter);
    }

    private void initUploadedRecyclerView() {
        baseAdapter.getTList ().addAll (FTPService.uploadInfoList);
    }

    private void initDownloadedRecyclerView() {

    }
}