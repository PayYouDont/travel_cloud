package com.gospell.travel.wxapi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gospell.travel.Constants;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    public static final String TAG = "WXEntryActivity:";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏并获取wxapi
        //getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //接收到分享以及登录的intent传递handleIntent方法，处理结果
        api = WXAPIFactory.createWXAPI(this,Constants.APP_ID,false);
        boolean result = api.handleIntent(getIntent(),this);
        if(!result){
            Log.d (TAG,"参数异常");
            finish ();
        }
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    //请求回调结果处理
    public void onResp(BaseResp baseResp) {
        //登录回调
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                //获取accesstoken
                getAccessToken(code);
                break;
            //用户拒绝授权
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            //用户取消授权
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
        }
    }

    private void getAccessToken(String code) {
        //新建一个progressDialog，避免长时间白屏（因为在进行多次网络请求）造成卡死的假象
//        createProgressDialog();
/**
 *        access_token:接口调用凭证
 *        appid：应用唯一标识，在微信开放平台提交应用审核通过后获得。
 *        secret：应用密钥AppSecret，在微信开放平台提交应用审核通过后获得。
 *        code：填写第一步获取的code参数。
 *        grant_type：填authorization_code。
 */
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(Constants.APP_ID)
                .append("&secret=")
                .append(Constants.APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        Log.d("urlurl", loginUrl.toString());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                //传入url
                .url(loginUrl.toString())
                //默认也是发起get请求
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: Fail");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseInfo = response.body().string();
                Log.d(TAG, "onResponse: Success");
                String access = null;
                String openId = null;
                //用json去解析返回来的access和token值
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("openid");
                    Log.d(TAG, "onResponse:"+access + "  " + openId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getUserInfo(access, openId);
            }
        });
    }
    //如果请求成功，我们通过JSON解析获取access和token值，再通过getUserInfo(access, openId)方法获取用户信息
    private void getUserInfo(String access,String openid){
        String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openid;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getUserInfoUrl)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: Fail(getUserInfo)");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseInfo = response.body().string();
                //用SharedPreference来缓存字符串
                SharedPreferences.Editor editor = getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                editor.putString("responseInfo",responseInfo);
                editor.commit();
                finish();
            }
        });
    }
}


