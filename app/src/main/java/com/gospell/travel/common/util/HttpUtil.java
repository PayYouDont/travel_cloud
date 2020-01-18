package com.gospell.travel.common.util;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    public static void get(String url, Map<String,String> param, ResponseListener listener){
        if (param!=null&&!param.isEmpty ()){
            StringBuffer buffer = new StringBuffer ();
            if(url.indexOf ("?")==-1){
                buffer.append ("?");
            }else {
                buffer.append ("&");
            }
            param.forEach ((key, value) -> {
                buffer.append (key);
                buffer.append ("=");
                buffer.append (value);
                buffer.append ("&");
            });
            buffer.deleteCharAt(buffer.length()-1);
            url = buffer.toString();
        }
        OkHttpClient client = new OkHttpClient ().newBuilder ().readTimeout (30, TimeUnit.SECONDS).build ();
        Request.Builder builder = new Request.Builder ().url (url);
        builder.method ("GET",null);
        Call call = client.newCall (builder.build ());
        if(listener!=null){
            listener.callback (call);
        }else {
            call.enqueue (new Callback () {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e ("HttpUtil.get()",e.getMessage (),e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d ("HttpUtil.get()",response.message ());
                }
            });
        }
    }
    public static void get(String url, ResponseListener listener) {
        get(url, null,listener);
    }
    public static void post(String url, Map<String,String> param, ResponseListener listener){
        FormBody.Builder formBody = new FormBody.Builder();
        if(!param.isEmpty ()){
            param.forEach ((key, value) -> formBody.add (key,value));
        }
        RequestBody body = formBody.build ();
        Request request = new Request.Builder ().post (body).url (url).build ();
        OkHttpClient client = new OkHttpClient ().newBuilder ().readTimeout (30, TimeUnit.SECONDS).build ();
        Call call = client.newCall (request);
        if(listener!=null){
            listener.callback (call);
        }else {
            call.enqueue (new Callback () {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e ("HttpUtil.post()",e.getMessage (),e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d ("HttpUtil.post()",response.message ());
                }
            });
        }
    }
    public static void post(String url,ResponseListener listener){
        post (url,null,listener);
    }
    public interface ResponseListener{
        void callback(Call call);
    }

}
