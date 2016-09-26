package com.tian.sun.screendemo.util;

import android.util.Log;

/**
 * Created by sqy on 2016/9/3.
 */
public class LogUtil {
    private static boolean allwoLog = false;
    private static String TAG = "LogUtil";
    public static void setAllowLog(boolean allow){
        allwoLog = allow;
    }
    public static void d(String msg){
        if(allwoLog){
            Log.d(TAG,msg);
        }
    }
    public static void d(String tag,String msg){
        if(allwoLog){
            Log.d(tag,msg);
        }
    }
    public static void i(String msg){
        if(allwoLog){
            Log.i(TAG,msg);
        }
    }
    public static void i(String tag,String msg){
        if(allwoLog){
            Log.i(tag,msg);
        }
    }
    public static void e(String msg){
        if(allwoLog){
            Log.e(TAG,msg);
        }
    }
    public static void e(String tag,String msg){
        if(allwoLog){
            Log.e(tag,msg);
        }
    }
}
