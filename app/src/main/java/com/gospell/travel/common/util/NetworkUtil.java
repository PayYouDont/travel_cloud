package com.gospell.travel.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.gospell.travel.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtil {
    /**
     * 检查网络是否可用
     *
     * @param paramContext
     * @return
     */
    public static boolean checkEnable(Context paramContext) {
        //boolean i = false;
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo ();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable ())) {
            return true;
        }
        return false;
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder ();
        sb.append (ipInt & 0xFF).append (".");
        sb.append ((ipInt >> 8) & 0xFF).append (".");
        sb.append ((ipInt >> 16) & 0xFF).append (".");
        sb.append ((ipInt >> 24) & 0xFF);
        return sb.toString ();
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        try {

            WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo ();
            int i = wifiInfo.getIpAddress ();
            return int2ip (i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage ();
        }
        // return null;
    }

    //GPRS连接下的ip
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces (); en.hasMoreElements (); ) {
                NetworkInterface intf = en.nextElement ();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses (); enumIpAddr.hasMoreElements (); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement ();
                    if (!inetAddress.isLoopbackAddress ()) {
                        return inetAddress.getHostAddress ();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e ("WifiPreference IpAddress", ex.toString ());
        }
        return null;
    }

    /**
     * 检查wifi是否处开连接状态
     *
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo (ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected ();
    }

    public static String getWiFiRssiMsg(int rssi) {
        String msg = "";
        if (rssi >= -50 && rssi <= 0) {//最强
            msg = "最强";
        } else if (rssi >= -70 && rssi <= -50) {//较强
            msg = "较强";
        } else if (rssi >= -80 && rssi <= -70) {//较弱
            msg = "较弱";
        } else if (rssi >= -100 && rssi <= -80) {//微弱
            msg = "微弱";
        }
        return msg;
    }
    public static int getWiFiRssiImgRes(int rssi) {
        int res = 0;
        if (rssi >= -50 && rssi <= 0) {//最强
            res = R.drawable.ic_wifi_full;
        } else if (rssi >= -70 && rssi <= -50) {//较强
            res = R.drawable.ic_wifi_03;
        } else if (rssi >= -80 && rssi <= -70) {//较弱
            res = R.drawable.ic_wifi_02;
        } else if (rssi >= -100 && rssi <= -80) {//微弱
            res = R.drawable.ic_wifi_01;
        }
        return res;
    }
    public static WifiInfo getCurrentWiFiInfo(Context context) {
        if (isWifiConnect (context)) {
            WifiManager mWifiManager = (WifiManager) context.getApplicationContext ().getSystemService (Context.WIFI_SERVICE);
            return mWifiManager.getConnectionInfo ();
        }
        return null;
    }
}
