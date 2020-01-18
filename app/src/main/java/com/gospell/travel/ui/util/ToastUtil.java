package com.gospell.travel.ui.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {
    public static void makeText(Context context, String msg){
        Looper.prepare();
        Toast.makeText (context,msg,Toast.LENGTH_SHORT).show ();
        Looper.loop();
    }
}
