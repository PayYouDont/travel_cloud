package com.gospell.travel.ui.device;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.helper.ReplaceViewHelper;
import com.gospell.travel.ui.util.ViewUtil;
import com.gospell.travel.ui.view.CustomButton;
import com.gospell.travel.ui.view.CustomDialog;

public class DeviceControllerFragment extends BaseFragment {
    @RootView(R.layout.fragment_device_controll)
    private View root;
    @ViewById(R.id.close_device_img)
    private ImageView closeImg;
    @ViewById(R.id.close_device_text)
    private TextView closeText;
    @ViewById(R.id.logout_device_text)
    private TextView logoutText;
    @ViewById(R.id.close_layout)
    private LinearLayout closeLayout;
    @ViewById(R.id.logout_layout)
    private LinearLayout logoutLayout;
    @Override
    protected void onCreateView() {
        changeCloseStatus();
        if(!closeed ()){
            closeLayout.setOnClickListener (v -> {
                initColseDialog(R.drawable.ic_tips_bg01,"确认关闭设备？",getMessage(3,"A","540MB"),false);
                //initColseDialog(R.drawable.ic_tips_bg02,"设备关闭成功",Html.fromHtml ("当前设备为缓存查看方式",0),true);
                //initColseDialog(R.drawable.ic_tips_bg03,"确认退出登录？",
                        //Html.fromHtml ("<font color='#00D473'>用户：A<br>设备：电视电影</font><br>当前有文件进行同步中，<br>剩余文件大小：45MB" ,0),false);
                //initColseDialog(R.drawable.ic_tips_bg02,"退出成功",Html.fromHtml ("用户：<font color='#00D473'>A</font><br>" +
                        //"已退出设备：<font color='#00D473'>电影电视</font>",0),true);
                //initColseDialog(R.drawable.ic_tips_bg04,"设备",Html.fromHtml ("当前设备为缓存查看状态，<br>并未连接，无法关闭",0),true);
            });
        }else {
            closeLayout.setOnClickListener (null);
        }
        logoutLayout.setOnClickListener (v -> {
            Toast.makeText (getContext (),"待开发注销界面",Toast.LENGTH_SHORT).show ();
        });
    }
    private void changeCloseStatus(){
        if("ic_close2".equals (closeImg.getTag ())){
            closeImg.setTag ("ic_close1");
            closeImg.setImageResource (R.drawable.ic_close1);
            closeText.setTextColor (getResources ().getColor (R.color.colorTextDark,null));
        }else {
            closeImg.setTag ("ic_close2");
            closeImg.setImageResource (R.drawable.ic_close2);
            closeText.setTextColor (Color.parseColor ("#9F9F9F"));
        }
        ViewGroup.LayoutParams params = closeImg.getLayoutParams ();
        params.width = params.height = ViewUtil.getdip (getContext (),20);
        closeImg.setLayoutParams (params);
    }
    private boolean closeed(){
        return "ic_close2".equals (closeImg.getTag ());
    }
    private CustomDialog initColseDialog(int topBgColorRes,String title,Spanned content,boolean singleBtn){
        CustomDialog dialog = new CustomDialog (getContext ());
        dialog.setcontentText (content)
                .setImageResId (R.drawable.ic_tips_bg01)
                .setSingle (singleBtn)
                .setPositive ("取消")
                .setNegtive ("确定")
                .show ();
        dialog.setViewByTag (CustomDialog.Tag.messageTv,view -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams ();
            params.topMargin = ViewUtil.getdip (getContext (),20);
            params.leftMargin = ViewUtil.getdip (getContext (),40);
        });
        dialog.setViewByTag (CustomDialog.Tag.negtiveBn,view -> {//重写确定按钮样式
            ViewUtil.setBackgroundRadius (view,ViewUtil.getdip (getContext (),1),Color.parseColor ("#00D473"));
            ((Button)view).setTextColor (Color.WHITE);
            view.setOnClickListener (v -> dialog.dismiss ());
        });
        dialog.setViewByTag (CustomDialog.Tag.positiveBn,view -> {//重写取消按钮样式
            ViewUtil.setBackgroundRadius (view,ViewUtil.getdip (getContext (),1),Color.parseColor ("#D6D6D6"));
            ((Button)view).setTextColor (Color.BLACK);
            view.setOnClickListener (v -> dialog.dismiss ());
        });
        dialog.setViewByTag (CustomDialog.Tag.imageLayout,view -> {//重写顶部样式
            Drawable drawable = getResources ().getDrawable (topBgColorRes,null);
            view.setBackground (drawable);
            ((LinearLayout)view).removeAllViews ();
            ((LinearLayout)view).setOrientation (LinearLayout.VERTICAL);
            LinearLayout linearLayout = new LinearLayout (getContext ());
            ViewGroup.LayoutParams params = view.getLayoutParams ();
            params.width = ViewUtil.getdip (getContext (),260);
            params.height = ViewUtil.getdip (getContext (),90);
            linearLayout.setLayoutParams (params);
            linearLayout.setOrientation (LinearLayout.VERTICAL);
            linearLayout.setGravity (Gravity.CENTER);
            ((LinearLayout) view).addView (linearLayout);
            TextView textView = new TextView (getContext ());
            textView.setTextSize (14);
            textView.setTextColor (Color.BLACK);
            textView.setGravity (Gravity.CENTER);
            textView.setText (title);
            textView.setLetterSpacing (0.05f);
            linearLayout.addView (textView);
            LinearLayout imgLayout = new LinearLayout (getContext ());
            imgLayout.setOrientation (LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams imgLayoutParam = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            imgLayoutParam.topMargin = ViewUtil.getdip (getContext (),20);
            imgLayout.setLayoutParams (imgLayoutParam);
            ImageView tvImage = new ImageView (getContext ());
            tvImage.setBackground (getResources ().getDrawable (R.drawable.ic_tv_wifi,null));
            int size = ViewUtil.getdip (getContext (),20);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams (size,size);
            param.rightMargin = ViewUtil.getdip (getContext (),5);
            tvImage.setLayoutParams (param);
            TextView tvView = new TextView (getContext ());
            tvView.setText ("电影电视");
            tvView.setTextSize (16);
            tvView.setTextColor (Color.BLACK);
            imgLayout.addView (tvImage);
            imgLayout.addView (tvView);
            linearLayout.addView (imgLayout);
        });
        return dialog;
    }
    private Spanned getMessage(int userCount, String userName, String dataSize){
        String message = "该设备当前连接用户：<font color='#00D473'>"+userCount+"</font><br>" +
                "你当前以用户<font color='#00D473'>"+userName+"</font>登录，<br>" +
                "剩余同步数据：<font color='red'>"+dataSize+"</font>";
        return Html.fromHtml (message,0);
    }
}