package com.gospell.travel.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.R;
import com.gospell.travel.common.util.BitmapUtil;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.ui.util.ViewUtil;

import java.util.List;

import lombok.Setter;

public class CardAdapter extends RecyclerView.Adapter {
    private List<MediaBean> mediaBeanList;
    private int width;
    private int height;
    @Setter
    private ItemClickListener itemClickListener;
    @Setter
    private ItemOnLongClickListener itemOnLongClickListener;
    @Setter
    private ItemCheckedChangeListener itemCheckedChangeListener;
    private boolean isEdit = false;
    private boolean isCheckedAll = false;
    private Context context;
    private int imageWidth,imageHeight;

    public CardAdapter(Context context,List<MediaBean> mediaBeanList,int width,int height) {
        this.context = context;
        this.mediaBeanList = mediaBeanList;
        this.width = width;
        this.height = height;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.file_item,parent,false);
        return new CardAdapter.ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaBean mediaBean = mediaBeanList.get (position);
        ViewHolder viewHolder = (ViewHolder) holder;
        Bitmap bitmap = BitmapUtil.addBitmapBorder (mediaBean.getBitmap (),width,height,10,0.2f);
        viewHolder.imageView.setImageBitmap (bitmap);
        if(itemClickListener!=null){
            viewHolder.itemView.setOnClickListener (v -> itemClickListener.onClick (v,mediaBean));
        }
        if(itemOnLongClickListener!=null){
            viewHolder.itemView.setOnLongClickListener (v -> {
                itemOnLongClickListener.onLongClick (viewHolder,mediaBean);
                return true;
            });
        }
        viewHolder.checkCover.setOnClickListener (v -> viewHolder.imageCheckBox.setChecked (!viewHolder.imageCheckBox.isChecked ()));
        if(position==0){
            imageWidth = viewHolder.imageView.getWidth ();
            imageHeight = viewHolder.imageView.getHeight ();
        }
        if(isEdit){
            editStatus (viewHolder,mediaBean);
        }else {
            isCheckedAll = false;
            viewHolder.checkCover.setVisibility (View.INVISIBLE);
        }
        viewHolder.imageCheckBox.setChecked (isCheckedAll);
    }
    private void editStatus(ViewHolder viewHolder,MediaBean mediaBean){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.checkCover.getLayoutParams ();
        layoutParams.width = imageWidth- ViewUtil.dip2px (context,10);
        layoutParams.height = imageHeight;
        layoutParams.leftMargin = ViewUtil.dip2px (context,5);
        viewHolder.checkCover.setLayoutParams (layoutParams);
        viewHolder.checkCover.setVisibility (View.VISIBLE);
        viewHolder.imageCheckBox.setOnCheckedChangeListener ((buttonView, isChecked) -> {
            if(itemCheckedChangeListener!=null){
                itemCheckedChangeListener.onCheck (mediaBean,isChecked);
            }
        });
    }
    public void setEdit(boolean isEdit){
        this.isEdit = isEdit;
        notifyDataSetChanged ();
    }
    public void setCheckedAll(boolean isChecked){
        this.isCheckedAll = isChecked;
        notifyDataSetChanged ();
    }
    public interface ItemClickListener{
        void onClick(View itemView,MediaBean mediaBean);
    }
    public interface ItemOnLongClickListener{
        void onLongClick(ViewHolder viewHolder,MediaBean mediaBean);
    }
    public interface ItemCheckedChangeListener{
        void onCheck(MediaBean mediaBean,boolean isChecked);
    }
    @Override
    public int getItemCount() {
        return mediaBeanList.size ();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        CheckBox imageCheckBox;
        RelativeLayout checkCover;
        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById (R.id.file_img);
            checkCover = itemView.findViewById (R.id.check_cover);
            imageCheckBox = itemView.findViewById (R.id.image_check_btn);
        }
    }
}
