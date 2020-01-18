package com.gospell.travel.ui.log;

import android.view.View;
import android.widget.TextView;

import com.gospell.travel.Constants;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.entity.UserLog;
import com.gospell.travel.ui.util.ViewUtil;

import java.text.SimpleDateFormat;

public class LogInfoFragment extends BaseFragment {
    @RootView(R.layout.fragment_log_info)
    private View root;
    @ViewById(R.id.log_datetime)
    private TextView datetimeText;
    @ViewById(R.id.log_title)
    private TextView titleText;
    @ViewById(R.id.log_content)
    private TextView contentText;
    private UserLog userLog;
    @Override
    protected void onCreateView() {
        if(getArguments () != null){
            Integer logId = getArguments ().getInt ("logId",1);
            //根据日志id向服务器获取操作日志(待实现)
            userLog = Constants.getTestUserLog (logId);
        }
        if(userLog != null){
            datetimeText.setText (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format (userLog.getCreateTime ()));
            titleText.setText ("类型："+userLog.getTitle ());
            contentText.setText (userLog.getLog ());
            int color = ViewUtil.getColorByStatus (getContext (),userLog.getStatus ());
            ViewUtil.setBackgroundRadius (root,25,color);
        }
    }
}
