package com.gospell.travel.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gospell.travel.R;
import com.gospell.travel.entity.CardEntity;
import com.gospell.travel.ui.util.ViewUtil;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomCardView extends LinearLayout {
    private String title;
    private List<CardEntity> cardEntities;
    private int imageWidth = 80;
    private int imageHeight = 80;
    private int imageLayoutWidth = 140;
    private int imageLayoutHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private int rootLayoutHeight = 120;
    private int textFontSize = 14;
    private int titleMarginLeft = 40;
    private int dp = 320;
    private int titleColor = Color.parseColor ("#626262");

    public CustomCardView(Context context, String title, List<CardEntity> cardEntities) {
        super (context);
        this.title = title;
        this.cardEntities = cardEntities;
        this.setOrientation (VERTICAL);
        LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams (param);
        this.setBackgroundColor (Color.parseColor ("#ffffff"));
        init ();
    }

    public void init(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, parseToDP (40));
        params.leftMargin = parseToDP (titleMarginLeft);
        TextView titleView = new TextView (getContext ());
        titleView.setText (title);
        titleView.setTextColor (titleColor);
        titleView.setTextSize (textFontSize);
        titleView.setGravity (Gravity.CENTER);
        titleView.setLayoutParams (params);
        this.addView (titleView);
        LinearLayout borderBottomLayout = ViewUtil.createLinearLayout (getContext (),R.drawable.border_bottom,ViewGroup.LayoutParams.MATCH_PARENT,ViewUtil.dip2px (getContext (),0.4f));
        this.addView (borderBottomLayout);
        cardEntities.forEach (cardEntity -> {
            LinearLayout rootLayout = new LinearLayout (getContext ());
            rootLayout.setTag (Tag.rootLayout);
            rootLayout.setOrientation (VERTICAL);
            rootLayout.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,parseToDP (rootLayoutHeight)));
            rootLayout.addView (ViewUtil.createLinearLayout (getContext (),R.drawable.border_bottom,ViewGroup.LayoutParams.MATCH_PARENT,ViewUtil.dip2px (getContext (),0.4f)));
            LinearLayout mainLayout = new LinearLayout (getContext ());
            mainLayout.setTag (Tag.mainLayout);
            mainLayout.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            mainLayout.setOrientation (HORIZONTAL);
            //图标
            LinearLayout imageLayout = new LinearLayout (mainLayout.getContext ());
            imageLayout.setTag (Tag.imageLayout);
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(parseToDP (imageLayoutWidth),parseToDP (imageLayoutWidth));
            imageLayoutParams.topMargin = imageLayoutParams.bottomMargin = parseToDP ((rootLayoutHeight-imageHeight)/2);
            imageLayout.setLayoutParams (imageLayoutParams);
            ImageView imageView = new ImageView (getContext ());
            imageView.setTag (Tag.imageView);
            imageView.setImageResource (cardEntity.getImageResId ());
            LayoutParams imageParam = new LinearLayout.LayoutParams(parseToDP (imageWidth),parseToDP (imageWidth));
            imageParam.leftMargin =parseToDP (titleMarginLeft);
            imageView.setLayoutParams (imageParam);
            imageLayout.addView (imageView);
            mainLayout.addView (imageLayout);
            //border
            mainLayout.addView (ViewUtil.createLinearLayout (getContext (),R.drawable.border_left,ViewUtil.dip2px (getContext (),0.4f),ViewGroup.LayoutParams.MATCH_PARENT));
            //内容
            LinearLayout contentLayout = new LinearLayout (mainLayout.getContext ());
            contentLayout.setTag (Tag.contentLayout);
            contentLayout.setOrientation (VERTICAL);
            LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT, 1.0f);
            contentLayout.setLayoutParams (contentLayoutParams);
            //内容标题
            TextView contentLabelView = new TextView (contentLayout.getContext ());
            contentLabelView.setTag (Tag.contentLabelView);
            contentLabelView.setTextSize (textFontSize);
            contentLabelView.setText (cardEntity.getLabel ());
            contentLabelView.setTextColor (Color.parseColor ("#000000"));
            contentLabelView.setGravity (Gravity.CENTER);
            LinearLayout.LayoutParams contentMarginParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,1.0F);
            contentMarginParam.leftMargin =  ViewUtil.dip2px (getContext (),10);
            contentLabelView.setLayoutParams (contentMarginParam);
            contentLayout.addView (contentLabelView);
            //具体内容
            View view = cardEntity.getContentView ();
            view.setTag (Tag.contentView);
            view.setLayoutParams (contentMarginParam);
            contentLayout.addView (view);
            mainLayout.addView (contentLayout);
            rootLayout.addView (mainLayout);
            this.addView (rootLayout);
        });
    }
    public void setImageSize(int imageWidth,int imageHeight){
        setViewSizeByTag (Tag.imageView,parseToDP (imageWidth),parseToDP (imageHeight));
    }
    public void setImageLayoutSize(int imageWidth,int imageHeight){
        setViewSizeByTag (Tag.imageLayout,parseToDP (imageWidth),parseToDP (imageHeight));
    }
    public void setContentLabelColor(int color){
        setViewByTag (Tag.contentLabelView,view -> ((TextView)view).setTextColor (color));
    }
    private void setViewSizeByTag(Tag tag,int w,int h){
        setViewByTag (tag,view -> {
            ViewGroup.LayoutParams imageLayoutParams = view.getLayoutParams ();
            imageLayoutParams.width = w;
            imageLayoutParams.height = h;
            view.setLayoutParams (imageLayoutParams);
        });
    }
    public void setViewByTag(Tag tag,@NonNull OnFindTagListener listener){
        ViewUtil.getAllChildViews (this).forEach (view -> {
            if(view.getTag ()!=null&&tag.equals (view.getTag ())&&listener!=null){
                listener.setView (view);
            }
        });
    }
    public enum Tag{
        rootLayout,mainLayout,imageLayout,imageView,contentLayout,contentLabelView,contentView
    }
    private int parseToDP(int px){
        int dip = px*160/dp;
        return ViewUtil.getdip (getContext (),dip);
    }
}
