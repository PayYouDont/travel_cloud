package com.gospell.travel.ui.setting;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.ui.view.CustomSlideButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiSettingFragment extends BaseFragment {
    @RootView(R.layout.fragment_setting_wifi)
    private View root;
    @ViewById(R.id.wifi_switch_btn)
    private CustomSlideButton wifiSwitchBtn;
    @ViewById(R.id.wifi_list)
    private RecyclerView wifiListView;
    @ViewById(R.id.wifi_manager_layout)
    private LinearLayout wifiMananger;
    @ViewById(R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreateView() {
        wifiSwitchBtn.setBigCircleModel ();
        wifiSwitchBtn.setChecked (true);
        wifiSwitchBtn.setOnCheckedListener (isChecked -> {
            if(isChecked){
                wifiMananger.setVisibility (View.VISIBLE);
            }else {
                wifiMananger.setVisibility (View.INVISIBLE);
            }
        });
        refreshLayout.setOnRefreshListener (() -> refreshWiFiList ());
        List<ScanResult> scanWifiList = getWifiList();
        WifiAdapter adapter = new WifiAdapter (scanWifiList,result -> {
            Toast.makeText (getContext (),result.SSID,Toast.LENGTH_SHORT).show ();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager (root.getContext ());
        wifiListView.setLayoutManager (layoutManager);
        wifiListView.setAdapter (adapter);
    }
    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getActivity ().getSystemService(getActivity ().WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            Map<String, Integer> signalStrength = new HashMap<> ();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }else {
            Log.e(getClass ().getName (), "没有搜索到wifi");
        }
        wifiList.sort ((o1, o2) -> o2.level>o1.level?1:-1);
        return wifiList;
    }
    private void refreshWiFiList(){
        WifiAdapter adapter = (WifiAdapter)wifiListView.getAdapter ();
        adapter.getScanResultList ().clear ();
        adapter.getScanResultList ().addAll (getWifiList ());
        adapter.notifyDataSetChanged ();
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }
}