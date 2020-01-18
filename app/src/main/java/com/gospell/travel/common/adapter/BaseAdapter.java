package com.gospell.travel.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BaseAdapter<T> extends RecyclerView.Adapter {
    @Getter
    private List<T> tList;
    private int resourceId;
    private View root;
    @Getter
    private BaseViewHolder holder;
    @Setter
    private View.OnLongClickListener onLongClickListener;
    @Setter
    private OnSelectItemListener onSelectItemListener;
    @Setter
    private OnSetContetnViewListener<T> onSetContetnViewListener;
    public BaseAdapter(List<T> tList, int resourceId) {
        this.tList = tList;
        this.resourceId = resourceId;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from (parent.getContext ()).inflate (resourceId,parent,false);
        holder = new BaseViewHolder (root);
        holder.itemView.setOnClickListener (view -> {
            if(onSelectItemListener!=null){
                onSelectItemListener.select (holder.getAdapterPosition ());
            }
        });
        if(onLongClickListener!=null){
            holder.itemView.setOnLongClickListener (onLongClickListener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T t = tList.get (position);
        BaseViewHolder viewHolder = (BaseViewHolder) holder;
        if(onSetContetnViewListener!=null){
            onSetContetnViewListener.setViews (viewHolder,t);
        }
    }

    @Override
    public int getItemCount() {
        return tList.size ();
    }
    public interface OnSelectItemListener{
        void select(int position);
    }
    public interface OnSetContetnViewListener<T>{
        void setViews(BaseViewHolder holder,T t);
    }
}
