package com.gospell.travel.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.gospell.travel.R;

import lombok.Getter;

@Getter
public class FuncMenuView extends LinearLayout{
    private LinearLayout downloadLayout,moveLayout,deleteLayout,moreLayout;

    public FuncMenuView(Context context) {
        super (context);
        init();
    }

    public FuncMenuView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        init();
    }

    public FuncMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        init();
    }

    public FuncMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super (context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        View inflate = inflate(getContext(), R.layout.function_menu_layout, this);
        downloadLayout = inflate.findViewById (R.id.func_menu_download);
        moveLayout = inflate.findViewById (R.id.func_menu_move);
        deleteLayout = inflate.findViewById (R.id.func_menu_delete);
        moreLayout = inflate.findViewById (R.id.func_menu_more);

    }
    public void download(OnClickListener listener){
        downloadLayout.setOnClickListener (listener);
    }
    public void move(OnClickListener listener){
        moveLayout.setOnClickListener (listener);
    }
    public void delete(OnClickListener listener){
        deleteLayout.setOnClickListener (listener);
    }
    public void more(OnClickListener listener){
        moreLayout.setOnClickListener (listener);
    }
}
