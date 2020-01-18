package com.gospell.travel.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gospell.travel.ui.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CustomBubbleMenu extends PopupWindow {
    private List<String> menuItems;
    private int width;
    private int height;
    private Context context;
    private BubbleLayout mLayout;
    private List<TextView> itemViews;
    public CustomBubbleMenu(Context context,List<String> menuItems) {
        super (context);
        this.menuItems = menuItems;
        this.context = context;
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
        itemViews = new ArrayList<> ();
        initView();
    }

    private void initView(){
        if(mLayout == null){
            mLayout = new BubbleLayout (context);
        }
        if(width == 0){
            width = ViewUtil.getdip (getContext (),150);
        }
        if(height == 0){
            height = ViewUtil.getdip (getContext (),130);
        }
        this.setWidth (width);
        this.setHeight (height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (width,height);
        params.gravity = Gravity.CENTER;
        mLayout.setLayoutParams (params);
        mLayout.setOrientation (LinearLayout.VERTICAL);
        mLayout.setMBubbleColor (Color.parseColor ("#CC222121"));
        mLayout.setMBubbleRadius (ViewUtil.getdip (getContext (),10));
        initItemView ();
        setContentView (mLayout);
    }
    private void initItemView(){
        LinearLayout layoutTop = new LinearLayout (getContext ());
        layoutTop.setLayoutParams (new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,0,1.5f));
        mLayout.addView (layoutTop);
        menuItems.forEach (menuName -> {
            TextView textView = new TextView (getContext ());
            textView.setText (menuName);
            textView.setTextColor (Color.parseColor ("#ffffff"));
            textView.setGravity (Gravity.CENTER);
            itemViews.add (textView);
            mLayout.addView (textView);
            LinearLayout borderLayout = new LinearLayout (getContext ());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,ViewUtil.getdip (getContext (),0.8f));
            int margin = ViewUtil.getdip (getContext (),10);
            params.setMargins (margin,margin,margin,margin);
            borderLayout.setLayoutParams (params);
            borderLayout.setBackgroundColor (Color.parseColor ("#444444"));
            mLayout.addView (borderLayout);
        });
        mLayout.removeViewAt (mLayout.getChildCount ()-1);
        LinearLayout layoutBottom = new LinearLayout (getContext ());
        layoutBottom.setLayoutParams (new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,0,1));
        mLayout.addView (layoutBottom);
    }
    public void setOnItemListener(OnItemListener listener){
        itemViews.forEach (textView -> textView.setOnClickListener (v -> listener.itemClick (textView)));
    }
    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown (anchor);
        mLayout.setMLook (BubbleLayout.Look.TOP);
        DisplayMetrics outMetrics = getDisplayMetrics ();
        int widthPixels = outMetrics.widthPixels;
        if(anchor.getX ()+width>widthPixels){
            int position = (int)(width + anchor.getX () - widthPixels);
            mLayout.setMLookPosition (position);
        }

    }
    private DisplayMetrics getDisplayMetrics(){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }
    public interface OnItemListener{
        void itemClick(TextView textView);
    }
}
