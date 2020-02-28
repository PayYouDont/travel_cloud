package com.gospell.travel.ui.setting;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.R;
import com.gospell.travel.common.util.NetworkUtil;

import java.util.List;

import lombok.Getter;

public class WifiAdapter extends RecyclerView.Adapter {
    @Getter
    private List<ScanResult> scanResultList;
    private OnItemListener listener;

    public WifiAdapter(List<ScanResult> scanResultList, OnItemListener listener) {
        this.scanResultList = scanResultList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.wifi_list_item,parent,false);
        ViewHolder holder = new ViewHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScanResult result = scanResultList.get (position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.wifiSSIDView.setText (result.SSID);
        viewHolder.wifiImage.setBackgroundResource (NetworkUtil.getWiFiRssiImgRes (result.level));
        viewHolder.wifiView.setOnClickListener (v -> listener.click (result));
    }

    @Override
    public int getItemCount() {
        return scanResultList.size ();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wifiView;
        TextView wifiSSIDView;
        ImageView wifiImage;
        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            wifiView = itemView;
            wifiSSIDView = wifiView.findViewById (R.id.wifi_ssid);
            wifiImage = wifiView.findViewById (R.id.wifi_image);
        }
    }
    public interface OnItemListener{
        void click(ScanResult result);
    }
}
