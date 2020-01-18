package com.gospell.travel.common.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.ui.util.ViewUtil;

import java.util.List;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public List<View> views;
    public BaseViewHolder(@NonNull View itemView) {
        super (itemView);
        this.views = ViewUtil.getAllChildViews (itemView);
    }
}
