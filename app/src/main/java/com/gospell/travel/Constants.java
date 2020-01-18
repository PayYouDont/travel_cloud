package com.gospell.travel;

import android.widget.Spinner;

import com.gospell.travel.entity.SpinnerData;
import com.gospell.travel.entity.UserLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Constants {
    public static final String APP_ID = "wx1094c1641cc70b55";
    public static final String APP_SECRET = "eb814d999f9544e1daf78287bdb0e9e4";
    private static List<UserLog> logList;
    private static List<SpinnerData> syncMenuDataList;
    private static List<SpinnerData> notifMenuDataList;
    public static UserLog getTestUserLog(Integer id){
        initUserLogs();
        for (UserLog userLog:logList) {
            if(userLog.getId ()==id){
                return userLog;
            }
        }
        return null;
    }
    private static void initUserLogs(){
        if(logList==null){
            logList = new ArrayList<> ();
            UserLog log1 = new UserLog ();
            log1.setId (1);
            log1.setCreateTime (new Date ());
            log1.setLog ("用户A在自动登录设备H时，登录失败，请查看并重新登录");
            log1.setTitle ("登录异常");
            log1.setStatus (UserLog.ERROR);
            logList.add (log1);
            UserLog log2 = new UserLog ();
            log2.setId (2);
            log2.setCreateTime (new Date (new Date ().getTime ()-7*24*60*60*1000));
            log2.setTitle ("同步结束");
            log2.setLog ("用户A在同步了xx张图片到设备H");
            log2.setStatus (UserLog.INFO);
            logList.add (log2);
            UserLog log3 = new UserLog ();
            log3.setId (3);
            try {
                log3.setCreateTime (new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss").parse ("2019/12/01 13:00:00"));
            }catch (Exception e){
                e.printStackTrace ();
            }
            log3.setTitle ("授权新客户端");
            log3.setLog ("用户A将xx权限授权给用户B");
            log3.setStatus (UserLog.WARNING);
            logList.add (log3);
        }
    }
    public static List<UserLog> getLogList(){
        initUserLogs();
        return logList;
    }
    private static void initSyncMenuData(){
        syncMenuDataList = new ArrayList<> ();
        syncMenuDataList.add (new SpinnerData ("1","同步完成百分比"));
        syncMenuDataList.add (new SpinnerData ("2","剩余文件总大小"));
        syncMenuDataList.add (new SpinnerData ("3","剩余文件总个数"));
        syncMenuDataList.add (new SpinnerData ("4","预估剩余同步时间"));
        syncMenuDataList.add (new SpinnerData ("5","同步总速度"));
    }
    public static List<SpinnerData> getSyncMenuDataListList(){
        initSyncMenuData();
        return syncMenuDataList;
    }
    private static void initnotifMenuData(){
        notifMenuDataList = new ArrayList<> ();
        notifMenuDataList.add (new SpinnerData ("1","显示A"));
        notifMenuDataList.add (new SpinnerData ("2","显示B"));
        notifMenuDataList.add (new SpinnerData ("3","显示C"));
    }
    public static List<SpinnerData> getnotifMenuDataList(){
        initnotifMenuData();
        return notifMenuDataList;
    }
}
