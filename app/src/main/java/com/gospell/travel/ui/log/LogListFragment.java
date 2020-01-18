package com.gospell.travel.ui.log;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gospell.travel.Constants;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.entity.UserLog;
import com.gospell.travel.ui.setting.LogAdapter;
import com.gospell.travel.ui.util.ViewUtil;

import java.util.List;

public class LogListFragment extends BaseFragment {
    @RootView(R.layout.fragment_log_list)
    private View root;
    @ViewById(R.id.log_list)
    private RecyclerView logListView;
    @Override
    protected void onCreateView() {
        List<UserLog> logList = Constants.getLogList ();
        LogAdapter adapter = new LogAdapter (getActivity (),logList, userLog -> {
            LogInfoFragment fragment = new LogInfoFragment ();
            Bundle args = new Bundle ();
            args.putInt ("logId",userLog.getId ());
            fragment.setArguments (args);
            getFragmentManager ().beginTransaction ().replace ( R.id.log_fragment,fragment).addToBackStack (null).commit ();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager (root.getContext ());
        logListView.setLayoutManager (layoutManager);
        logListView.addItemDecoration (new RecyclerView.ItemDecoration (){
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets (outRect, view, parent, state);
                outRect.top = ViewUtil.dip2px (getContext (),10);
            }
        });
        logListView.setAdapter (adapter);
    }
}
