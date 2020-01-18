package com.gospell.travel.ui.setting;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.R;
import com.gospell.travel.entity.UserLog;
import com.gospell.travel.ui.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<UserLog> logList;
    private OnItemListener listener;

    public LogAdapter(Context context, List<UserLog> logList, OnItemListener listener) {
        this.context = context;
        this.logList = logList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.log_item,parent,false);
        ViewHolder holder = new ViewHolder (view);
        GradientDrawable drawable = new GradientDrawable ();
        drawable.setCornerRadius (ViewUtil.getdip (context,4));
        int color = context.getResources ().getColor (R.color.colorLogCardBg,null);
        drawable.setColor (color);
        drawable.setStroke (1, color);
        holder.logView.setBackground (drawable);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserLog userLog = logList.get (position);
        ViewHolder viewHolder = (ViewHolder) holder;
        int color = ViewUtil.getColorByStatus (context,userLog.getStatus ());
        ViewUtil.setBackgroundRadius (viewHolder.statusView,10,color);
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
        String createTime = sdf.format (userLog.getCreateTime ());
        viewHolder.contentView.setText (createTime + "："+userLog.getTitle ());
        viewHolder.logView.setTag (userLog.getId ());
        viewHolder.logView.setOnClickListener (v -> listener.click (userLog));
    }

    @Override
    public int getItemCount() {
        return logList.size ();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        View logView;
        ImageView statusView;
        TextView contentView;
        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            logView = itemView;
            statusView = logView.findViewById (R.id.log_status);
            contentView = logView.findViewById (R.id.log_content);
        }
    }
    public interface OnItemListener{
        void click(UserLog log);
    }
}
