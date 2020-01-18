package com.gospell.travel.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gospell.travel.R;
import com.gospell.travel.ui.util.ViewUtil;

import lombok.ToString;

/**
 * description:自定义dialog
 */

@ToString
public class CustomDialog extends Dialog {
    /**
     * 显示的图片
     */
    private LinearLayout imageLayout;
    private ImageView imageIv;

    /**
     * 显示的标题
     */
    private TextView titleTv;

    /**
     * 显示的消息
     */
    private TextView messageTv;

    /**
     * 确认和取消按钮
     */
    private Button negtiveBn, positiveBn;

    /**
     * 按钮之间的分割线
     */
    private View columnSpace;

    /**
     * 都是内容数据
     */
    private String message;
    private Spanned contentText;
    private String title;
    private String positive, negtive;
    private LinearLayout root;
    private int imageResId = -1;

    /**
     * 底部是否只有一个按钮
     */
    private boolean isSingle = false;

    public CustomDialog(Context context) {
        super (context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.custom_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside (false);
        //初始化界面控件
        initView ();
        //初始化界面数据
        refreshView ();
        //初始化界面控件的事件
        initEvent ();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn.setOnClickListener (v -> {
            if (onClickBottomListener != null) {
                onClickBottomListener.onPositiveClick ();
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negtiveBn.setOnClickListener (v -> {
            if (onClickBottomListener != null) {
                onClickBottomListener.onNegtiveClick ();
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void refreshView() {
        //如果用户自定了title和message
        if (!TextUtils.isEmpty (title)) {
            titleTv.setText (title);
            titleTv.setVisibility (View.VISIBLE);
        } else {
            titleTv.setVisibility (View.GONE);
        }
        if (!TextUtils.isEmpty (message)) {
            messageTv.setText (message);
        }
        if (contentText!=null) {
            messageTv.setText (contentText);
            messageTv.setTextColor (Color.BLACK);
        }
        //如果设置按钮的文字
        if (!TextUtils.isEmpty (positive)) {
            positiveBn.setText (positive);
        } else {
            positiveBn.setText ("确定");
        }
        if (!TextUtils.isEmpty (negtive)) {
            negtiveBn.setText (negtive);
        } else {
            negtiveBn.setText ("取消");
        }

        if (imageResId != -1) {
            imageIv.setImageResource (imageResId);
            imageIv.setVisibility (View.VISIBLE);
        } else {
            imageIv.setVisibility (View.INVISIBLE);
        }
        /**
         * 只显示一个按钮的时候隐藏取消按钮，回掉只执行确定的事件
         */
        if (isSingle) {
            columnSpace.setVisibility (View.GONE);
            negtiveBn.setVisibility (View.GONE);
        } else {
            negtiveBn.setVisibility (View.VISIBLE);
            columnSpace.setVisibility (View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show ();
        refreshView ();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        negtiveBn = findViewById (R.id.negtive);
        negtiveBn.setTag (Tag.negtiveBn);
        positiveBn = findViewById (R.id.positive);
        positiveBn.setTag (Tag.positiveBn);
        titleTv = findViewById (R.id.title);
        titleTv.setTag (Tag.titleTv);
        messageTv = findViewById (R.id.message);
        messageTv.setTag (Tag.messageTv);
        imageIv = findViewById (R.id.image);
        imageIv.setTag (Tag.imageIv);
        imageLayout = findViewById (R.id.image_layout);
        imageLayout.setTag (Tag.imageLayout);
        columnSpace = findViewById (R.id.column_space);
        root = findViewById (R.id.dialog_layout);
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;

    public CustomDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        void onPositiveClick();

        /**
         * 点击取消按钮事件
         */
        void onNegtiveClick();
    }

    public String getMessage() {
        return message;
    }

    public CustomDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPositive() {
        return positive;
    }

    public CustomDialog setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    public String getNegtive() {
        return negtive;
    }

    public CustomDialog setNegtive(String negtive) {
        this.negtive = negtive;
        return this;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public CustomDialog setSingle(boolean single) {
        isSingle = single;
        return this;
    }

    public CustomDialog setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this;
    }
    public CustomDialog setcontentText(Spanned contentText){
        this.contentText = contentText;
        return this;
    }
    public void setViewByTag(Tag tag, @NonNull OnFindTagListener listener){
        ViewUtil.getAllChildViews (root).forEach (view -> {
            if(view.getTag ()!=null&&tag.equals (view.getTag ())&&listener!=null){
                listener.setView (view);
            }
        });
    }

    public enum Tag{
        negtiveBn, positiveBn, titleTv, messageTv, imageIv,imageLayout
    }
}

