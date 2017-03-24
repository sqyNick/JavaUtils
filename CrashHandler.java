package com.fhzz.cn.exploremap.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;

import com.fhzz.cn.exploremap.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/24.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler crashHandler;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Context context;

    private CrashHandler(){

    }

    public void init(Context context){
        this.context = context;
        //获取系统默认的UncatchException处理器
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance(){
        if (crashHandler == null) {
            crashHandler = new CrashHandler();
        }
        return crashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && uncaughtExceptionHandler == null) {
            uncaughtExceptionHandler.uncaughtException(thread,throwable);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable
     *            异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(final Throwable throwable) {
        if (throwable == null || context == null)
            return false;
        new Thread() {
            public void run() {
                Looper.prepare();
                save2File(getCrashReport(throwable));
                Looper.loop();
            }

        }.start();
        return true;
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param throwable
     * @return
     */
    private String getCrashReport(Throwable throwable) {
        PackageInfo pinfo = getPackageInfo();
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version Name : "  + pinfo.versionName + "Version Code : " + pinfo.versionCode);
        exceptionStr.append("Android SDK_INT: " + Build.VERSION.SDK_INT + "Android MODEL: " + android.os.Build.MODEL );
        exceptionStr.append("Exception: " + throwable.getMessage());
        StackTraceElement[] elements = throwable.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString());
        }
        return exceptionStr.toString();
    }

    private void save2File(String msg){
        PackageInfo info = getPackageInfo();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //存在sd卡
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + info.packageName;
            createFile(dir,msg);
        } else {
            String dir = context.getFilesDir() + File.separator + info.packageName;
            createFile(dir,msg);
        }
    }

    private PackageInfo getPackageInfo(){
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            pinfo = new PackageInfo();
        }
        return pinfo;
    }

    private void createFile(String dir , String msg){
        PackageInfo info = getPackageInfo();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String fileName = "Crash_" + sf.format(new Date());
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        LogUtil.d(dirFile.getAbsolutePath());
        File file = new File(dirFile,fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter ow = new OutputStreamWriter(fos,"utf-8");
            ow.write(msg);
            ow.flush();
            fos.flush();
            ow.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
