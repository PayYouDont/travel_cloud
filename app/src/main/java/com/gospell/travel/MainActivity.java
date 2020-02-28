package com.gospell.travel;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.gospell.travel.ftp.FTPService;
import com.gospell.travel.helper.ReplaceViewHelper;
import com.gospell.travel.service.MediaService;
import com.gospell.travel.ui.home.HomeFragment;
import com.gospell.travel.ui.log.UserLogFragment;
import com.gospell.travel.ui.view.CustomButton;
import com.gospell.travel.ui.view.CustomDialog;

import cn.bingoogolapple.badgeview.BGABadgeLinearLayout;
import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private PermissionHelper permissionHelper;
    private BGABadgeLinearLayout messageCountBv;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Intent uploadIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        drawer = findViewById (R.id.drawer_layout);
        navigationView = findViewById (R.id.nav_view);
        messageCountBv = navigationView.getHeaderView (0).findViewById (R.id.message_count_bv);
        initMessage();
        mAppBarConfiguration = new AppBarConfiguration.Builder (
                R.id.nav_home,
                R.id.nav_setting_client,
                R.id.nav_device_access,
                R.id.nav_wifi_setting,
                R.id.nav_device_controller,
                R.id.nav_login_client
        ).setDrawerLayout (drawer).build ();
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener ((controller, destination, arguments) -> {
            String title = destination.getLabel ().toString ();
            if(!getString (R.string.menu_device_list).equals (title)){
                LinearLayout layout = (LinearLayout)toolbar.getChildAt (0);
                TextView textView = new TextView (layout.getContext ());
                textView.setText (title);
                layout.removeAllViews ();
                layout.addView (textView);
            }
        });
        System.out.println ("view="+navigationView.getRootView ().findViewById (R.id.menu_other_title));
        NavigationUI.setupActionBarWithNavController (this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController (navigationView, navController);
        permissionHelper = new PermissionHelper(this);
        getPermission();
        if(Constants.FTP_AUTOUPLOAD){
            Intent mediaLoadIntent = new Intent (this, MediaService.class);
            startService (mediaLoadIntent);
            uploadIntent = new Intent (this, FTPService.class);
            startService (uploadIntent);
        }
    }
    public void restartUploadService(){
        stopService (uploadIntent);
        startService (uploadIntent);
    }
    public void stopUploadService(){
        stopService (uploadIntent);
    }
    private void initDialog() {
        final CustomDialog dialog = new CustomDialog (MainActivity.this);
        dialog.setMessage ("这是一个自定义Dialog。")
                //.setImageResId (R.mipmap.ic_launcher)
                //.setTitle("系统提示")
                .setSingle (true)
                .setPositive ("返回")
                .show ();
        ReplaceViewHelper helper = new ReplaceViewHelper (this);
        dialog.setViewByTag (CustomDialog.Tag.positiveBn,view -> {
            CustomButton button = new CustomButton (this, CustomButton.Type.success);
            button.setText (((Button)view).getText ());
            button.setBackgroundColor (Color.parseColor ("#00D473"));
            button.setOnClickListener (v -> dialog.dismiss ());
            helper.toReplaceView (view,button);
        });
    }
    private void initMessage(){
        //向服务器发送请求获取用户log
        //。。。
        messageCountBv.showTextBadge ("2");
        messageCountBv.setOnClickListener (v -> {
            String text = messageCountBv.getBadgeViewHelper ().getBadgeText ();
            if (!"0".equals (text)){
                getSupportFragmentManager ()
                        .beginTransaction ()
                        .replace ( R.id.nav_host_fragment,new UserLogFragment (),"UserLogFragment")
                        .addToBackStack (getSelectedItem ())
                        .commit ();
                drawer.closeDrawers ();
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent (intent);
        String fragment = intent.getStringExtra ("fragment");
        if("homeFragment".equals (fragment)){
            getSupportFragmentManager ().beginTransaction ().replace ( R.id.nav_host_fragment,new HomeFragment ()).commit ();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp (navController, mAppBarConfiguration) || super.onSupportNavigateUp ();
    }
    private void getPermission() {
        permissionHelper.check(
                //Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.MEDIA_CONTENT_CONTROL)
                .onSuccess(() -> System.out.println ("获取权限成功"))
                .onDenied(() -> Toast.makeText (this,"权限被拒绝！将无法获取到WiFi信息!",Toast.LENGTH_SHORT).show ())
                .onNeverAskAgain(() -> Toast.makeText (this,"权限被拒绝！将无法获取到WiFi信息,下次不会再询问了！",Toast.LENGTH_SHORT).show ()).run();
    }
    public String getSelectedItem(){
        return navigationView.getCheckedItem ().getTitle ().toString ();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UserLogFragment logFragment = (UserLogFragment) getSupportFragmentManager ().findFragmentByTag ("UserLogFragment");
            if(logFragment!=null){
                logFragment.onBack ();
                return true;
            }
        }
        return super.onKeyDown (keyCode, event);
    }
}
