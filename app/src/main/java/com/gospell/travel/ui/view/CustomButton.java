package com.gospell.travel.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.gospell.travel.ui.util.ViewUtil;

import lombok.Getter;

public class CustomButton extends AppCompatButton {

    private int enabled_color = Color.GRAY;
    private int radius_size = ViewUtil.dip2px (getContext (),5);
    private int pressed_color = Color.parseColor ("#1BBC87");
    private int normal_color = Color.parseColor ("#3081CE");
    private int warning_color = Color.parseColor ("#FF7F00");
    private int success_color = Color.parseColor ("#1BBC87");
    @Getter
    private int background_color = Color.WHITE;

    private Type type;
    public CustomButton(Context context, Type type) {
        super (context);
        this.type = type;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewUtil.getdip (getContext (),70),ViewUtil.getdip (getContext (),20));
        params.rightMargin = ViewUtil.getdip (getContext (),10);
        setLayoutParams (params);
        setPadding (getLeft (),0,getRight (),getBottom ());
        setTextSize (12);
        setGravity(Gravity.CENTER);
        switch (type){
            case normal:
                setTextColor (normal_color);
                background_color = normal_color;
                break;
            case warning:
                setTextColor (warning_color);
                background_color = warning_color;
                break;
            case success:
                setBackgroundColor (success_color);
                background_color = success_color;
                setTextColor (Color.parseColor ("#ffffff"));
                break;
        }
        setBackgroundDrawable(getStateListDrawable(getSolidRectDrawable(radius_size, pressed_color), getSolidRectDrawable(radius_size, background_color)));
        setOnClickListener (v -> {
            System.out.println (getText ());
        });
    }

    public CustomButton(Context context) {
        this(context,Type.normal);
    }
    /**
     * 得到实心的drawable, 一般作为选中，点中的效果
     *
     * @param cornerRadius 圆角半径
     * @param solidColor   实心颜色
     * @return 得到实心效果
     */
    public GradientDrawable getSolidRectDrawable(int cornerRadius, int solidColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        // 设置矩形的圆角半径
        gradientDrawable.setCornerRadius(cornerRadius);
        if(type.equals (Type.success)){
            // 设置绘画图片色值
            gradientDrawable.setColor(solidColor);
        }else {
            gradientDrawable.setColor(Color.TRANSPARENT);
        }
        // 绘画的是矩形
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradientDrawable.setStroke(ViewUtil.getdip (getContext (),1), solidColor);
        return gradientDrawable;
    }
    /**
     * 背景选择器
     *
     * @param pressedDrawable 按下状态的Drawable
     * @param normalDrawable  正常状态的Drawable
     * @return 状态选择器
     */
    public StateListDrawable getStateListDrawable(Drawable pressedDrawable, Drawable normalDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        //((GradientDrawable)pressedDrawable).setColor(success_color);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
        //设置不能用的状态
        //默认其他状态背景
        GradientDrawable gray = getSolidRectDrawable(radius_size, enabled_color);
        stateListDrawable.addState(new int[]{}, gray);
        return stateListDrawable;
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor (color);
        this.background_color = color;
        setBackgroundDrawable(getStateListDrawable(getSolidRectDrawable(radius_size, pressed_color), getSolidRectDrawable(radius_size,color)));
    }
    public void setPressedColor(int color){
        this.pressed_color = color;
        setBackgroundDrawable(getStateListDrawable(getSolidRectDrawable(radius_size, pressed_color), getSolidRectDrawable(radius_size,background_color)));
    }
    public enum Type{
        normal,warning,success
    }
}
