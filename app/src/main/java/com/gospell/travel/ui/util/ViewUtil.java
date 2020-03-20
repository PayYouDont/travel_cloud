package com.gospell.travel.ui.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gospell.travel.R;
import com.gospell.travel.entity.UserLog;

import java.util.ArrayList;
import java.util.List;

public class ViewUtil {
    public static List<View> getAllChildViews(View view) {
        List<View> allChildren = new ArrayList<> ();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount (); i++) {
                View viewChild = vp.getChildAt (i);
                allChildren.add (viewChild);
                //再次 调用本身（递归）
                allChildren.addAll (getAllChildViews (viewChild));
            }
        }
        return allChildren;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources ().getDisplayMetrics ().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static int getdip(Context context, float value) {
        return ((int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, value, context.getResources ().getDisplayMetrics ()));
    }

    public static LinearLayout createLinearLayout(Context context, int resId, int width, int height) {
        LinearLayout borderBottomLayout = new LinearLayout (context);
        borderBottomLayout.setTag ("borderBottomLayout");
        borderBottomLayout.setBackgroundResource (resId);
        LinearLayout.LayoutParams borderParam = new LinearLayout.LayoutParams (width, height);
        borderBottomLayout.setLayoutParams (borderParam);
        return borderBottomLayout;
    }

    public static void getScreenRelatedInformation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            System.out.println ("widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +",densityDpi = " + densityDpi + "\n" +",density = " + density + ",scaledDensity = " + scaledDensity);

        }
    }

    public static void getRealScreenRelatedInformation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            System.out.println ("widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                    ",densityDpi = " + densityDpi + "\n" +
                    ",density = " + density + ",scaledDensity = " + scaledDensity);
        }
    }
    public static int getColorByStatus(Context context,int status){
        switch (status){
            case UserLog.ERROR:
                return context.getResources ().getColor (R.color.colorError,null);
            case UserLog.WARNING:
                return context.getResources ().getColor (R.color.colorWarning,null);
            case UserLog.INFO:
                return context.getResources ().getColor (R.color.colorSuccess,null);
        }
        return 0;
    }
    public static void setBackgroundRadius(View view,int radius,int color){
        GradientDrawable drawable = new GradientDrawable ();
        drawable.setCornerRadius (ViewUtil.getdip (view.getContext (),radius));
        //int color = ViewUtil.getColorByStatus (view.getContext (),userLog.getStatus ());
        drawable.setColor (color);
        drawable.setStroke (1, Color.parseColor ("#D5DDDB"));
        view.setBackground (drawable);
    }
}
