package com.gospell.travel.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gospell.travel.LoginAuthorizeActivity;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.entity.CardEntity;
import com.gospell.travel.ui.util.ViewUtil;
import com.gospell.travel.ui.view.CustomBubbleMenu;
import com.gospell.travel.ui.view.CustomButton;
import com.gospell.travel.ui.view.CustomCardView;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    @RootView(R.layout.fragment_home)
    private View root;
    @ViewById(R.id.home_content)
    private LinearLayout contentView;
    @ViewById(R.id.search_edit)
    private EditText searchView;
    @ViewById(R.id.search_button)
    private Button searchBtn;
    @ViewById(R.id.add_button)
    private Button addBtn;

    private int REQUEST_CODE_SCAN = 111;
    private List<CustomCardView> cardViews;
    @Override
    protected void onCreateView() {
        List<String> viewList = new ArrayList<> ();
        viewList.add ("扫描二维码");
        viewList.add ("申请登录权限");
        addBtn.setOnClickListener (v -> {
            CustomBubbleMenu menu = new CustomBubbleMenu (getContext (),viewList);
            menu.showAsDropDown (addBtn);
            menu.setOnItemListener (textView -> {
                if(textView.getText ().equals (viewList.get (0))){
                    menu.dismiss ();
                    //调用相机扫描
                    Intent intent = new Intent(getContext (), CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }else if(textView.getText ().equals (viewList.get (1))){
                    Intent intent = new Intent (getActivity (), LoginAuthorizeActivity.class);
                    getActivity ().startActivity (intent);
                }
            });
        });
        searchBtn.setOnClickListener (v -> {
            if(searchView.getVisibility () == View.INVISIBLE){
                searchView.setVisibility (View.VISIBLE);
            }
        });
        initCardList();
    }
    private void initCardList(){
        cardViews = new ArrayList<> ();
        List<CardEntity> cardEntities = new ArrayList<> ();
        CardEntity cardEntity = new CardEntity ();
        cardEntity.setLabel ("小米电视");
        cardEntity.setImageResId (R.drawable.ic_home);
        TextView textView = new TextView (getContext ());
        textView.setTextColor (Color.RED);
        textView.setTextSize (11);
        textView.setText ("您没有登录权限");
        cardEntity.setContentView (textView);
        cardEntities.add (cardEntity);
        CardEntity cardEntity2 = new CardEntity ();
        cardEntity2.setLabel ("小米机顶盒");
        cardEntity2.setImageResId (R.drawable.ic_limits);
        TextView textView2 = new TextView (getContext ());
        textView2.setTextColor (Color.RED);
        textView2.setTextSize (11);
        textView2.setText ("您没有登录权限");
        cardEntity2.setContentView (textView2);
        cardEntities.add (cardEntity2);
        CustomCardView customCardView = new CustomCardView (getContext (),"蓝牙连接中的设备",cardEntities);
        cardViews.add (customCardView);
        for (int i=0;i<5;i++){
            List<CardEntity> cardEntities2 = new ArrayList<> ();
            CardEntity cardEntity3 = new CardEntity ();
            cardEntity3.setLabel ("小米机顶盒");
            cardEntity3.setImageResId (R.drawable.ic_tv_cloud);
            LinearLayout buttonLayout = new LinearLayout (getContext ());
            buttonLayout.setOrientation (LinearLayout.HORIZONTAL);
            CustomButton button = new CustomButton (getContext (), CustomButton.Type.success);
            button.setText ("微信登录");
            buttonLayout.addView (button);
            CustomButton button2 = new CustomButton (getContext (), CustomButton.Type.normal);
            button2.setText ("手机登录");
            buttonLayout.addView (button2);
            CustomButton button3 = new CustomButton (getContext (), CustomButton.Type.warning);
            button3.setText ("手机卡登录");
            buttonLayout.addView (button3);
            cardEntity3.setContentView (buttonLayout);
            cardEntities2.add (cardEntity3);
            CustomCardView customCardView2 = new CustomCardView (getContext (),"无线连接已登录的设备",cardEntities2);
            cardViews.add (customCardView2);
        }
        cardViews.forEach (cardView -> {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)contentView.getLayoutParams ();
            params.bottomMargin = ViewUtil.dip2px (getContext (),10);
            cardView.setLayoutParams (params);
            contentView.addView (cardView);
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getString (R.string.menu_scan).equals (item.getTitle ())){
            Intent intent = new Intent(getContext (), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
        }
        return super.onOptionsItemSelected (item);
    }
    //接收扫描结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText (getContext (),"扫描结果："+content,Toast.LENGTH_SHORT).show ();
            }
        }
    }

}