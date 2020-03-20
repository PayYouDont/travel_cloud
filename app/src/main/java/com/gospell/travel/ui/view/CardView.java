package com.gospell.travel.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.R;
import com.gospell.travel.common.util.DateUtil;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.ui.fragment.CardAdapter;
import com.gospell.travel.ui.fragment.GridLayoutItemDecoration;
import com.gospell.travel.ui.util.ViewUtil;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CardView extends LinearLayout {
    private List<MediaBean> mediaBeanList;
    private CardAdapter cardAdapter;
    private TextView createDateText;
    private TextView itemCountText;
    private RecyclerView itemRecycler;
    private String createDate;
    private String itemCount;
    private int itemWidth = 80;
    private int itemHeight = 80;
    private int spanCount = 4;
    public CardView(Context context,List<MediaBean> mediaBeans) {
        super (context);
        this.mediaBeanList = mediaBeans;
        init();
    }

    public CardView(Context context, @Nullable AttributeSet attrs) {
        this (context, attrs,-1);
    }

    public CardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this (context, attrs, defStyleAttr,-1);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super (context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes (attrs,R.styleable.CardView);
        createDate = typedArray.getString (R.styleable.CardView_dateText);
        itemCount = typedArray.getString (R.styleable.CardView_itemCount);
        itemWidth = typedArray.getInt (R.styleable.CardView_itemWidth,50);
        itemHeight = typedArray.getInt (R.styleable.CardView_itemHeight,80);
        spanCount = typedArray.getInt (R.styleable.CardView_spanCount,4);
        init();
    }
    //获取到控件，初始化
    private void init(){
        View inflate = inflate(getContext(), R.layout.card_view, this);
        createDateText = inflate.findViewById(R.id.create_time);
        itemCountText = inflate.findViewById (R.id.item_count);
        itemRecycler = inflate.findViewById (R.id.list_recycler);
        if(mediaBeanList.size ()>0){
            createDate = DateUtil.getDateStr (mediaBeanList.get (0).getCreateTime ());
        }
        itemCount = mediaBeanList.size ()+getContext ().getString (R.string.item);
        createDateText.setText (createDate);
        itemCountText.setText (itemCount);
        cardAdapter = new CardAdapter (getContext (),mediaBeanList,itemWidth,itemHeight);
        itemRecycler.setAdapter (cardAdapter);
        int width = getResources ().getDisplayMetrics ().widthPixels;
        int space = (ViewUtil.px2dip (getContext (),width)- 40 - itemWidth*spanCount)/3;
        itemRecycler.addItemDecoration (new GridLayoutItemDecoration (getContext (),spanCount,space));
        GridLayoutManager gridLayoutManager = new GridLayoutManager (getContext (),spanCount,GridLayoutManager.VERTICAL,false);
        itemRecycler.setLayoutManager (gridLayoutManager);
    }

    public void setOnItemClickListener(CardAdapter.ItemClickListener itemClickListener) {
        if(itemClickListener!=null){
            cardAdapter.setItemClickListener (itemClickListener);
        }
    }

    public void setOnItemOnLongClickListener(CardAdapter.ItemOnLongClickListener itemOnLongClickListener) {
        if(itemOnLongClickListener!=null){
            cardAdapter.setItemOnLongClickListener (itemOnLongClickListener);
        }
    }

    public void setOnItemCheckedChangeListener(CardAdapter.ItemCheckedChangeListener itemCheckedChangeListener) {
        if(itemCheckedChangeListener!=null){
            cardAdapter.setItemCheckedChangeListener (itemCheckedChangeListener);
        }
    }

    public void updateView(){
        //cardAdapter.notifyItemInserted (mediaBeanList.size ()-1);
        cardAdapter.notifyDataSetChanged ();
    }

    public void setEdit(boolean edit) {
        cardAdapter.setEdit (edit);
        LinearLayout layout = (LinearLayout) itemCountText.getParent ();
        CustomButton button ;
        if(layout.getChildCount ()>1){
            button = (CustomButton) layout.getChildAt (1);
        }else {
            button = new CustomButton (getContext (), CustomButton.Type.success);
            layout.addView (button);
        }
        if(edit){
            itemCountText.setVisibility (GONE);
            button.setVisibility (VISIBLE);
            button.setBackgroundColor (Color.WHITE);
            button.setTextColor (Color.BLACK);
            button.setPressedColor (Color.WHITE);
            String selectAllTrue = getContext ().getString (R.string.select_all_true);
            String selectAllFalse = getContext ().getString (R.string.select_all_false);
            button.setText (selectAllTrue);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)button.getLayoutParams ();
            params.gravity = Gravity.CENTER;
            button.setLayoutParams (params);
            button.setOnClickListener (v -> {
                if(button.getText ().toString ().equals (selectAllTrue)){
                    cardAdapter.setCheckedAll (true);
                    button.setText (selectAllFalse);
                }else {
                    cardAdapter.setCheckedAll (false);
                    button.setText (selectAllTrue);
                }
            });
        }else {
            //取消编辑状态
            button.setVisibility (GONE);
            itemCountText.setVisibility (VISIBLE);
        }
    }
}
