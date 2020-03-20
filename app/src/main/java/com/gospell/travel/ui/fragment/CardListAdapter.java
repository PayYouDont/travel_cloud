package com.gospell.travel.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.R;
import com.gospell.travel.entity.MediaBean;

import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter {
    private List<MediaBean> mediaBeanList;

    public CardListAdapter(List<MediaBean> mediaBeanList) {
        this.mediaBeanList = mediaBeanList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.server_file_item,parent,false);
        return new CardListAdapter.ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaBean mediaBean = mediaBeanList.get (position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.fileImageView.setImageResource (R.drawable.ic_file_txt);

    }

    @Override
    public int getItemCount() {
        return mediaBeanList.size ();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CheckBox selectBox;
        ImageView fileImageView;
        TextView fileNameText;
        Button downloadBtn;
        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            this.itemView = itemView;
            selectBox = itemView.findViewById (R.id.select_box);
            fileImageView = itemView.findViewById (R.id.file_img);
            fileNameText = itemView.findViewById (R.id.file_name);
            downloadBtn = itemView.findViewById (R.id.download_btn);
        }
    }
}
