package com.gospell.travel.ui.fragment;

import com.gospell.travel.R;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.ui.device.DeviceAccessFragment;

public class RecentFragment extends BaseFragment implements BackListener {
    @Override
    protected void onCreateView() {

    }

    @Override
    public void onBack() {
        getFragmentManager ().beginTransaction ().replace (R.id.nav_host_fragment,new DeviceAccessFragment ()).commit ();
    }
}
