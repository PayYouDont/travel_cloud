package com.gospell.travel.common.util;

import java.text.DecimalFormat;

public class StringUtil {
    private static final DecimalFormat decimalFormat = new DecimalFormat ("#.00");
    public static boolean isEmpty(String str){
        return str == null||str.trim ().equals ("");
    }
    public static String formatDouble(Double value){
        return decimalFormat.format (value);
    }
}
