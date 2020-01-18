package com.gospell.travel.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gospell.travel.Constants;
import com.gospell.travel.NFCActivity;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.entity.SpinnerData;
import com.gospell.travel.ui.util.ViewUtil;
import com.gospell.travel.ui.view.CustomDialog;
import com.gospell.travel.ui.view.CustomSlideButton;

import java.util.List;

public class ClientSettingFragment extends BaseFragment {
    @RootView(R.layout.fragment_setting_client)
    private View root;
    @ViewById(R.id.custom_nav_title)
    private TextView navTitle;
    @ViewById(R.id.custom_nav_back)
    private LinearLayout navBack;
    @ViewById(R.id.nfc_btn)
    private Button nfcBtn;
    @ViewById(R.id.wifi_btn)
    private Button wifiBtn;
    @ViewById(R.id.expert_switch_btn)
    private CustomSlideButton expertSwitchBtn;
    @ViewById(R.id.notification_dropdown_menu)
    private Spinner notifMenu;
    @ViewById(R.id.sync_dropdown_menu)
    private Spinner syncMenu;
    @Override
    protected void onCreateView() {
        navTitle.setText ("客户端设置");
        navBack.setOnClickListener (v -> onBack());
        initNotifMenu ();
        initSyncMenu();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius (ViewUtil.dip2px (getContext (),5));
        gradientDrawable.setColor (getResources ().getColor (R.color.defaultColor,null));
        nfcBtn.setBackground (gradientDrawable);
        nfcBtn.setOnClickListener (v -> {
            Intent intent = new Intent (getActivity (), NFCActivity.class);
            startActivity (intent);
        });
        wifiBtn.setBackground (gradientDrawable);
        wifiBtn.setOnClickListener (v -> {
            initDeviceSetDialog ();
        });
        expertSwitchBtn.setBigCircleModel ();
        expertSwitchBtn.setChecked (true);
        expertSwitchBtn.setOnCheckedListener (isChecked -> {

        });
    }
    private void onBack(){
        getActivity ().onBackPressed ();
    }
    private void initDeviceSetDialog(){
        CustomDialog dialog = new CustomDialog (getContext ())
                .setSingle (false)
                .setNegtive ("前往设置")
                .setPositive ("取消")
                .setMessage ("没有设备连接，\n请设备本机WiFi");
        dialog.show ();
        dialog.setViewByTag (CustomDialog.Tag.negtiveBn,view -> {
            ViewUtil.setBackgroundRadius (view,ViewUtil.getdip (getContext (),1),Color.parseColor ("#00D473"));
            ((Button)view).setTextColor (Color.WHITE);
            view.setOnClickListener (v -> {
                startActivity(new Intent (Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
            });
        });
        dialog.setViewByTag (CustomDialog.Tag.positiveBn,view -> {
            ViewUtil.setBackgroundRadius (view,ViewUtil.getdip (getContext (),1),Color.parseColor ("#D6D6D6"));
            ((Button)view).setTextColor (Color.BLACK);
            view.setOnClickListener (v -> dialog.dismiss ());
        });
        dialog.setViewByTag (CustomDialog.Tag.messageTv,view -> {
            TextView textView = (TextView)view;
            textView.setGravity (Gravity.CENTER);
            textView.setTextSize (18);
            textView.setTextColor (Color.BLACK);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams ();
            params.topMargin = params.bottomMargin = ViewUtil.getdip (getContext (),40);
            view.setLayoutParams (params);
        });
        dialog.setViewByTag (CustomDialog.Tag.imageLayout,view -> view.setVisibility (View.GONE));
    }
    private void initNotifMenu(){
        List<SpinnerData> data = Constants.getnotifMenuDataList ();
        ArrayAdapter<SpinnerData> adapter = new ArrayAdapter<> (root.getContext (),android.R.layout.simple_spinner_item,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notifMenu.setAdapter (adapter);
        notifMenu.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerData spinnerData = data.get (position);
                Toast.makeText (getContext (),"当前点击："+spinnerData.getText ()+",值为："+spinnerData.getValue (),Toast.LENGTH_SHORT).show ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void initSyncMenu(){
        List<SpinnerData> data = Constants.getSyncMenuDataListList ();
        ArrayAdapter<SpinnerData> adapter = new ArrayAdapter<> (root.getContext (),android.R.layout.simple_spinner_item,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        syncMenu.setAdapter (adapter);
        syncMenu.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerData spinnerData = data.get (position);
                Toast.makeText (getContext (),"当前点击："+spinnerData.getText ()+",值为："+spinnerData.getValue (),Toast.LENGTH_SHORT).show ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    public boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity ().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}