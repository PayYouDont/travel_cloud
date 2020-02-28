package com.gospell.travel.common.util;

import android.os.Environment;

public class FileUtil {
    public static String getRootPath(){
        return Environment.getRootDirectory().getPath();
    }
}
