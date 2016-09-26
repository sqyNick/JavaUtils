package com.tian.sun.screendemo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sqy on 2016/9/3.
 */
public class SPUtil {

    private static String SPName = "CopyRight_SQY";

    private static void initSharedPreferences(String spName){
        SPName = spName;
    }
    private static SharedPreferences getSP(Context context){
        return context.getSharedPreferences(SPName,context.MODE_PRIVATE);
    }

    public static void put(Context context,String key,Object value){

        SharedPreferences.Editor edit = getSP(context).edit();
        if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        }else if (value instanceof String) {
            edit.putString(key, (String) value);
        }else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        }

        edit.commit();
    }

    public static int getInt(Context context,String key){
        return getSP(context).getInt(key, 0);
    }
    public static String getString(Context context,String key){
        return getSP(context).getString(key, null);
    }
    public static boolean getBoolean(Context context, String key){
        return getSP(context).getBoolean(key, false);
    }
}
