package com.gospell.travel.ui.util;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;


public class DrawableUtil {

    /**
     * @param solidColor  填充色
     * @param strokeW     边框大小
     * @param strokeColor 边框颜色
     */
    public static GradientDrawable getGradientDrawable(Context context, int solidColor, float strokeW, int strokeColor) {
        float density = context.getResources().getDisplayMetrics().density;
        float strokeWidth = strokeW * density;
        // 创建drawable
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setStroke((int) strokeWidth, strokeColor);
        return gradientDrawable;
    }

    /**
     * @param radius      圆角大小
     * @param solidColor  填充色
     * @param strokeW     边框大小
     * @param strokeColor 边框颜色
     */
    public static GradientDrawable getGradientDrawable(Context context,int radius, int solidColor, int strokeW, int strokeColor) {
        float density = context.getResources().getDisplayMetrics().density;
        float roundRadius = radius * density;
        float strokeWidth = strokeW * density;
        // 创建drawable
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setCornerRadius(roundRadius);

        gradientDrawable.setStroke((int) strokeWidth, strokeColor);
        return gradientDrawable;
    }


    /**
     * @param orientation 渐变色方向 eg:GradientDrawable.Orientation.LEFT_RIGHT
     * @param radius      圆角大小
     * @param colors      渐变色默认从左到右
     * @param strokeW     边框大小
     * @param strokeColor 边框颜色
     */
    public static GradientDrawable getGradientDrawable(Context context,
                                                       float radius, GradientDrawable.Orientation orientation, int[] colors, float strokeW, int strokeColor) {
        float density = context.getResources().getDisplayMetrics().density;
        float roundRadius = radius * density;
        float strokeWidth = strokeW * density;
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, colors);// 创建drawable
        gradientDrawable.setCornerRadius(roundRadius);

        gradientDrawable.setStroke((int) strokeWidth, strokeColor);
        return gradientDrawable;

    }


    /**
     * @param radii       每一个角的圆角大小
     * @param solidColor  填充色
     * @param strokeW     边框大小
     * @param strokeColor 边框颜色
     */
    public static GradientDrawable getGradientDrawable(Context context, float[] radii, int solidColor, float strokeW, int strokeColor) {
        float density = context.getResources().getDisplayMetrics().density;
        float strokeWidth = strokeW * density;
        GradientDrawable gradientDrawable = new GradientDrawable();// 创建drawable
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setCornerRadii(radii);
        gradientDrawable.setStroke((int) strokeWidth, strokeColor);
        return gradientDrawable;
    }

    /**
     * @param colorNormal 正常颜色
     * @param colorPress  按下的颜色
     * @param radius      圆角
     * @param strokeWidth 边框
     * @param strokeColor 边框色
     */
    public static StateListDrawable getStateListDrawable(Context context,
                                                         int colorNormal, int colorPress, float radius, int strokeWidth,
                                                         int strokeColor) {
        float density = context.getResources().getDisplayMetrics().density;
        float roundRadius = radius * density;
        strokeWidth = (int) (strokeWidth * density);

        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(colorNormal);
        gdNormal.setCornerRadius(roundRadius);
        gdNormal.setStroke(strokeWidth, strokeColor);

        GradientDrawable gdPress = new GradientDrawable();
        gdPress.setColor(colorPress);
        gdPress.setCornerRadius(roundRadius);
        gdPress.setStroke(strokeWidth, strokeColor);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_pressed},
                gdNormal);

        drawable.addState(new int[]{android.R.attr.state_pressed}, gdPress);
        return drawable;
    }

    /**
     * @param radii       每个方向的圆角大小
     * @param colorNormal 正常颜色
     * @param colorPress  按下的颜色
     * @param radius      圆角
     * @param strokeWidth 边框
     * @param strokeColor 边框色
     */
    public static StateListDrawable getStateListDrawable(Context context, float[] radii,
                                                         int colorNormal, int colorPress, float radius, int strokeWidth,
                                                         int strokeColor) {
        float density = context.getResources().getDisplayMetrics().density;
        float roundRadius = radius * density;
        strokeWidth = (int) (strokeWidth * density);

        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(colorNormal);
        gdNormal.setCornerRadius(roundRadius);
        gdNormal.setCornerRadii(radii);
        gdNormal.setStroke(strokeWidth, strokeColor);

        GradientDrawable gdPress = new GradientDrawable();
        gdPress.setColor(colorPress);
        gdPress.setCornerRadius(roundRadius);
        gdPress.setCornerRadii(radii);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_pressed},
                gdNormal);

        drawable.addState(new int[]{android.R.attr.state_pressed}, gdPress);
        return drawable;
    }
}
