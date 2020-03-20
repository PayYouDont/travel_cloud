package com.gospell.travel.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.Value;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.util.DeviceIdUtil;
import com.gospell.travel.common.util.HttpUtil;
import com.gospell.travel.common.util.JsonUtil;
import com.gospell.travel.common.util.NetworkUtil;
import com.gospell.travel.common.util.ReflectUtil;
import com.gospell.travel.entity.BroadcastInfo;
import com.gospell.travel.entity.User;
import com.gospell.travel.netty.NettyBroadcast;
import com.gospell.travel.ui.util.ToastUtil;
import com.gospell.travel.ui.view.CircleImageView;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    @ViewById(R.id.headImg)
    private CircleImageView headImgView;

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
    @Value("service.ip")
    private String serviceIP;
    @Value("service.port")
    private Integer port;
    @Value("wx.appId")
    private String appId;
    @Value("wx.secret")
    private String secret;
    private IWXAPI api;
    private User user;

    @Override
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initViewByAnnotation ();
        ReflectUtil.initFieldByConfig (this, this);
        loginViewModel = ViewModelProviders.of (this, new LoginViewModelFactory ()).get (LoginViewModel.class);
        final ProgressBar loadingProgressBar = findViewById (R.id.loading);

        loginViewModel.getLoginFormState ().observe (this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginBtn.setEnabled (loginFormState.isDataValid ());
            if (loginFormState.getUsernameError () != null) {
                userText.setError (getString (loginFormState.getUsernameError ()));
            }
            if (loginFormState.getPasswordError () != null) {
                passwordText.setError (getString (loginFormState.getPasswordError ()));
            }
        });

        loginViewModel.getLoginResult ().observe (this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility (View.GONE);
            if (loginResult.getError () != null) {
                showLoginFailed (loginResult.getError ());
            }
            if (loginResult.getSuccess () != null) {
                updateUiWithUser (loginResult.getSuccess ());
            }
            setResult (Activity.RESULT_OK);
            finish ();
        });

        TextWatcher afterTextChangedListener = new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged (userText.getText ().toString (), passwordText.getText ().toString ());
            }
        };
        userText.addTextChangedListener (afterTextChangedListener);
        passwordText.addTextChangedListener (afterTextChangedListener);
        passwordText.setOnEditorActionListener ((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login (userText.getText ().toString (), passwordText.getText ().toString ());
            }
            return false;
        });

        loginBtn.setOnClickListener (v -> {
            loadingProgressBar.setVisibility (View.VISIBLE);
            loginViewModel.login (userText.getText ().toString (), passwordText.getText ().toString ());
        });
        retrievePasswordText.setOnClickListener (v -> retrievePassword ());
        registText.setOnClickListener (v -> regist ());
        wechatImage.setOnClickListener (v -> loginByWechat ());
        facebookImage.setOnClickListener (v -> loginByFacebook ());
        googleImage.setOnClickListener (v -> loginByGoogle ());
    }

    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString (R.string.welcome) + model.getDisplayName ();
        if(user!=null){
            new Thread (() -> {
                NettyBroadcast client = new NettyBroadcast (new InetSocketAddress ("192.168.1.255",10000));
                BroadcastInfo info = new BroadcastInfo ();
                info.setDeviceId (DeviceIdUtil.getDeviceId (this));
                info.setSimId (DeviceIdUtil.getSimId (this));
                info.setAccount (user.getAccount ());
                info.setServerAddress (NetworkUtil.getLocalIpAddress (this));
                try {
                    client.run (JsonUtil.toJson (info));
                } finally {
                    client.destroy ();
                }
            }).start ();
        }
        Toast.makeText (getApplicationContext (), welcome, Toast.LENGTH_LONG).show ();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText (getApplicationContext (), errorString, Toast.LENGTH_SHORT).show ();
    }
    /**
    * @Author peiyongdong
    * @Description ( 初始化注解 )
    * @Date 11:21 2020/3/20
    * @Param []
    * @return void
    **/
    private void initViewByAnnotation() {
        ReflectUtil.initFieldByAnnotation (getClass (), ViewById.class, (annotation, field) -> {
            ViewById viewById = (ViewById) annotation;
            try {
                if (viewById.value () == -1) {
                    field.set (this, findViewById (getId (this, field.getName ())));
                } else {
                    field.set (this, findViewById (viewById.value ()));
                }
            } catch (IllegalAccessException e) {
                Log.e (getClass ().getName (), e.getMessage (),e);
            }
        });
    }

    private int getId(Context context, String resName) {
        return context.getResources ().getIdentifier (resName, "id", context.getPackageName ());
    }

    /**
     * @return void
     * @Author peiyongdong
     * @Description (找回密码)
     * @Date 17:00 2019/11/26
     * @Param []
     **/
    private void retrievePassword() {
        String account = userText.getText ().toString ();
    }

    /**
     * @return void
     * @Author peiyongdong
     * @Description (注册)
     * @Date 17:01 2019/11/26
     * @Param []
     **/
    private void regist() {

    }

    /**
     * @return void
     * @Author peiyongdong
     * @Description (微信账号登录)
     * @Date 17:02 2019/11/26
     * @Param []
     **/
    private void loginByWechat() {
        api = WXAPIFactory.createWXAPI (this, appId, true);
        api.registerApp (appId);
        if (!api.isWXAppInstalled ()) {
            Toast.makeText (this, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show ();
        } else {
            final SendAuth.Req req = new SendAuth.Req ();
            //应用授权作用域，如获取用户个人信息则填写 snsapi_userinfo
            req.scope = "snsapi_userinfo";
            //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止 csrf 攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加 session 进行校验
            req.state = "wechat_sdk_demo_test";
            api.sendReq (req);
        }

    }

    @Override
    public void onResume() {
        super.onResume ();
        SharedPreferences sharedPreferences = getSharedPreferences ("userInfo", MODE_PRIVATE);
        String response = sharedPreferences.getString ("responseInfo", "");
        if (!response.isEmpty ()) {
            Handler handler = new Handler (msg -> {
                Bitmap bitmap = (Bitmap) msg.obj;
                headImgView.setImageBitmap (bitmap);
                return true;
            });
            user = new User ().parseWXUserinfo (response);
            userText.setText (user.getNickname ());
            HttpUtil.get (user.getHeadimgurl (), call -> call.enqueue (new Callback () {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ToastUtil.makeText (getBaseContext (), "获取头像出错了！");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    Bitmap bitmap = BitmapFactory.decodeStream (response.body ().byteStream ());
                    Message message = new Message ();
                    message.obj = bitmap;
                    handler.sendMessage (message);
                }
            }));
        }
    }

    /**
     * @return void
     * @Author peiyongdong
     * @Description (facebook账号登录)
     * @Date 17:03 2019/11/26
     * @Param []
     **/
    private void loginByFacebook() {

    }

    /**
     * @return void
     * @Author peiyongdong
     * @Description (谷歌账号登录)
     * @Date 17:03 2019/11/26
     * @Param []
     **/
    private void loginByGoogle() {

    }
}
