package com.gospell.travel.ui.tools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.Value;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.common.util.DeviceIdUtil;
import com.gospell.travel.common.util.HttpUtil;
import com.gospell.travel.common.util.JsonUtil;
import com.gospell.travel.common.util.ReflectUtil;
import com.gospell.travel.entity.ResponseData;
import com.gospell.travel.entity.User;
import com.gospell.travel.ui.login.LoginActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ToolsFragment extends BaseFragment {
    @RootView(R.layout.fragment_login)
    private View root;

    @ViewById(R.id.login_user)
    private EditText userText;

    @ViewById(R.id.login_password)
    private EditText passwordText;

    @ViewById(R.id.login_remember_password)
    private CheckBox rememberCheckBox;

    @ViewById(R.id.login_retrieve_password)
    private TextView retrievePasswordText;

    @ViewById(R.id.login_loginBtn)
    private Button loginBtn;

    @ViewById(R.id.login_go_register)
    private TextView registText;

    @ViewById(R.id.login_wechat)
    private ImageView wechatImage;

    @ViewById(R.id.login_facebook)
    private ImageView facebookImage;

    @ViewById(R.id.login_google)
    private ImageView googleImage;
    @Value ("service.ip")
    private String serviceIP;
    @Value ("service.port")
    private Integer port;
    @Value ("wx.appId")
    private String appId;
    @Value ("wx.secret")
    private String secret;
    private IWXAPI api;
    @Override
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    protected void onCreateView() {
        Intent intent = new Intent (getContext (), LoginActivity.class);
        getActivity ().startActivity (intent);
        /*loginBtn.setOnClickListener (v ->  login ());
        retrievePasswordText.setOnClickListener (v -> retrievePassword());
        registText.setOnClickListener (v -> regist());
        wechatImage.setOnClickListener (v -> loginByWechat ());
        facebookImage.setOnClickListener (v -> loginByFacebook ());
        googleImage.setOnClickListener (v -> loginByGoogle ());
        ReflectUtil.initFieldByConfig (this,getContext ());*/
    }
    /**
    * @Author peiyongdong
    * @Description ( 登录 )
    * @Date 16:59 2019/11/26
    * @Param []
    * @return void
    **/
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public void login(){
        String account = userText.getText ().toString ();
        String password = passwordText.getText ().toString ();
        if("".equals (account.trim ())||"".equals (password.trim ())){
            Toast.makeText (getContext (),"账号密码均不能为空！",Toast.LENGTH_SHORT).show ();
            return;
        }
        Boolean isRemember = rememberCheckBox.isChecked ();
        User user = new User ();
        user.setAccount (account);
        user.setPassword (password);
        user.setDeviceId (DeviceIdUtil.getDeviceId (getContext ()));
        Map<String,String> map = new HashMap<> ();
        map.put ("user", JsonUtil.toJson (user));
        String url = serviceIP+":"+port+"/app/login";
        HttpUtil.post (url,map,call ->call.enqueue (new Callback () {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText (getContext (),"登录出错！"+e.getMessage (),Toast.LENGTH_SHORT).show ();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseData data = JsonUtil.toBean (response.body ().string (),ResponseData.class);
                if(data.isSuccess ()){
                    System.out.println ("登录成功");
                }else {
                    System.out.println (response.body ().string ());
                }
            }
        }));
    }
    /**
    * @Author peiyongdong
    * @Description ( 找回密码 )
    * @Date 17:00 2019/11/26
    * @Param []
    * @return void
    **/
    private void retrievePassword(){
        String account = userText.getText ().toString ();
    }
    /**
    * @Author peiyongdong
    * @Description ( 注册 )
    * @Date 17:01 2019/11/26
    * @Param []
    * @return void
    **/
    private void regist(){

    }
    /**
    * @Author peiyongdong
    * @Description ( 微信账号登录 )
    * @Date 17:02 2019/11/26
    * @Param []
    * @return void
    **/
    private void loginByWechat(){
        api = WXAPIFactory.createWXAPI (getContext (),appId,true);
        api.registerApp (appId);
        if (!api.isWXAppInstalled()) {
            Toast.makeText(getContext (), "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            //应用授权作用域，如获取用户个人信息则填写 snsapi_userinfo
            req.scope = "snsapi_userinfo";
            //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止 csrf 攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加 session 进行校验
            req.state = "wechat_sdk_demo_test";
            api.sendReq(req);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp= getActivity ().getSharedPreferences("userInfo", getActivity ().MODE_PRIVATE);
        String responseInfo = sp.getString("responseInfo", "");

        if (!responseInfo.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(responseInfo);
                String nickname = jsonObject.getString("nickname");
                String headimgurl = jsonObject.getString("headimgurl");
                Log.d ("onResume","昵称："+ nickname + "\n"+ "头像："+ headimgurl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor= getActivity ().getSharedPreferences("userInfo",  getActivity ().MODE_PRIVATE).edit();
            System.out.println (editor);
            editor.clear();
            editor.commit();
        }
    }

    /**
    * @Author peiyongdong
    * @Description ( facebook账号登录 )
    * @Date 17:03 2019/11/26
    * @Param []
    * @return void
    **/
    private void loginByFacebook(){

    }
    /**
    * @Author peiyongdong
    * @Description ( 谷歌账号登录 )
    * @Date 17:03 2019/11/26
    * @Param []
    * @return void
    **/
    private void loginByGoogle(){

    }
}