package com.gospell.travel.common.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateUtil {
    /**
    * @Author peiyongdong
    * @Description ( 获取某段时间内的所有日期 )
    * @Date 16:10 2020/3/12
    * @Param [dBegin, dEnd]
    * @return java.util.List<java.util.Date>
    **/
    public static List<Date> findDates(Date dBegin, Date dEnd) {
        List<Date> dateList = new ArrayList<> ();
        dateList.add (dBegin);
        Calendar calBegin = Calendar.getInstance ();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime (dBegin);
        Calendar calEnd = Calendar.getInstance ();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime (dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after (calBegin.getTime ())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add (Calendar.DAY_OF_MONTH, 1);
            dateList.add (calBegin.getTime ());
        }
        Collections.reverse (dateList);
        return dateList;
    }
    /**
    * @Author peiyongdong
    * @Description ( 获取某个时间到当前时间的所有天数 )
    * @Date 16:11 2020/3/12
    * @Param [beginDate]
    * @return java.util.List<java.util.Date>
    **/
    public static List<Date> getDays(Date beginDate){
        return findDates (beginDate,new Date ());
    }
    public static String formatDate(Date date, String format){
        try {
            return new SimpleDateFormat (format).format (date);
        }catch (Exception e){
            Log.e ("DateUtil", "formatDate: 解析日期错误", e);
        }
        return null;
    }
    public static String getDateStr(Date date){
        String dateStr = "";
        int dist = getTimeDistance (date,new Date ());
        if(dist==0){
            dateStr = "今天";
        }else if(dist<=7){
            dateStr = dist+"天前";
        }else if(dist<365){
            dateStr = formatDate (date,"MM月dd日");
        }else{
            dateStr = formatDate (date,"yyyy年MM月dd日");
        }
        return dateStr;
    }
    //获取2个日期相差天数
    public static int getTimeDistance(Date beginDate , Date endDate ) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        long beginTime = beginCalendar.getTime().getTime();
        long endTime = endCalendar.getTime().getTime();
        int betweenDays = (int)((endTime - beginTime) / (1000 * 60 * 60 *24));//先算出两时间的毫秒数之差大于一天的天数

        endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
        if(beginCalendar.get(Calendar.DAY_OF_MONTH)==endCalendar.get(Calendar.DAY_OF_MONTH))//比较两日期的DAY_OF_MONTH是否相等
            return betweenDays + 1;	//相等说明确实跨天了
        else
            return betweenDays + 0;	//不相等说明确实未跨天
    }
    public static Date parseToDate(String dateStr, String formatter){
        try {
            return new SimpleDateFormat (formatter).parse (dateStr);
        }catch (Exception e){
            Log.e ("DateUtil", "formatDate: 解析日期错误", e);
        }
        return null;
    }
    public static Date getBeforeDay(Date date){
        Calendar calBegin = Calendar.getInstance ();
        calBegin.setTime (date);
        calBegin.add (Calendar.DAY_OF_MONTH,-1);
        return calBegin.getTime ();
    }
}
