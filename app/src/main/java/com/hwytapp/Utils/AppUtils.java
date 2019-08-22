package com.hwytapp.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.bee.yunkong.App;
import com.hwytapp.Common.Config;
import com.hwytapp.Interface.CallBack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class AppUtils {

    public static String appDirName(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context, null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    public static String appPath()
    {
        String filePath = "";
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // SD卡根目录的hello.text
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator ;
        } else  // 系统下载缓存根目录的hello.text
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator ;

        return filePath;
    }

    public static Boolean saveFile(String str, String fileName) {

        try {
            File file = new File(fileName);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(fileName);
            outStream.write(str.getBytes());
            outStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return  false;

    }


    public static boolean installPmApk(String apkPath, String apkID,CallBack cb) {

        boolean result = false;
        String cmd = "pm install -r "+apkPath;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            //静默安装需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            if(successMsg.length()>0)
                cb.OnSuccess(apkID);

            if(errorMsg.length()>0)
                cb.OnFail(apkID);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean uninstallSlient(String apkPath,String apkID,CallBack cb) {
        String cmd = "pm uninstall " + apkPath;

        boolean result = false;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            //卸载也需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            result = true;
            cb.OnSuccess(apkID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean isAppInstalled(String uri,String version ,Context context) {

        PackageManager pm = context.getPackageManager();
        boolean installed =false;
        try {
            PackageInfo info =   pm.getPackageInfo(uri,PackageManager.GET_ACTIVITIES);

            String localVersion = info.versionName;

            if(!localVersion.equals(version))
                return false;

            installed =true;
        } catch(PackageManager.NameNotFoundException e) {
            installed =false;
        }
        return installed;
    }

    public static PackageInfo getPackageInfo(String uri,Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info =   pm.getPackageInfo(uri,PackageManager.GET_ACTIVITIES);

            return  info;
        } catch(PackageManager.NameNotFoundException e) {
            return   null;
        }
    }

    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }


    // Drawable转换成Bitmap
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(  0,   0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
        return bitmap;
    }

    /* 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap,String savePath) {
        File filePic;


        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }


    public static void drawableToFile(Drawable drawable, String filePath)
    {
        if (drawable == null)
            return;
        try {
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = null;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 方法描述：判断某一应用是否正在运行
     * Created by cafeting on 2017/2/4.
     * @param context   上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(400);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {

            Log.i("isAppRunning", "+++++++++++++"+info.baseActivity.getPackageName()+"");
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    //获取已安装应用的 uid，-1 表示未安装此应用或程序异常
    public static int getPackageUid(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                return applicationInfo.uid;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }
    /**
     * 判断某一 uid 的程序是否有正在运行的进程，即是否存活
     * Created by cafeting on 2017/2/4.
     *
     * @param context   上下文
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isProcessRunning(Context context, String  serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : lists) {// 获取运行服务再启动

            Log.i("RunningAppProcessInfo", "+++++++++++++"+info.processName+"");
            if (info.processName.equals(serviceName)) {
                isRunning = true;
            }
        }
        return isRunning;

    }

    public static boolean isRun(Context context,String  appName){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(400);

        boolean isAppRunning = false;
        String MY_PKG_NAME = appName;
        //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
        for (ActivityManager.RunningTaskInfo info : list) {

            Log.i("ActivityService isRun()", "+++++++++++++"+info.topActivity.getPackageName()+"");
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                Log.i("ActivityService isRun()",info.topActivity.getPackageName() + " info.baseActivity.getPackageName()="+info.baseActivity.getPackageName());
                break;
            }
        }
        return isAppRunning;
    }

    public static boolean getRunningActivity(String packageName)
    {
        Process process;
        String cmd = "dumpsys activity activities";
        try {
            process = Runtime.getRuntime().exec("su");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            writer.write(cmd + "\n");
            writer.write("exit\n");
            writer.flush();
            String line;

            while ((line = reader.readLine()) != null) {
               // Log.i("dumpsys++++++++", line);
                if(line.contains(packageName)&&line.contains("TaskRecord"))
                {
                    return true;
                }

            }
            while ((line = error.readLine()) != null) {
                Log.e("error", line);
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return  false;
    }

    /**
     * 获取当前APP Activity 名称
     *
     */
    public static String getTopActivity()
    {
        Process process;
        String cmd = "dumpsys activity top";
        try {
            process = Runtime.getRuntime().exec("su");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            writer.write(cmd + "\n");
            writer.write("exit\n");
            writer.flush();
            String line;

            while ((line = reader.readLine()) != null) {
                if(line.contains("ACTIVITY"))
                {
                    if(process!=null) {
                        process.destroy();
                    }
                    return line;
                }

            }
            while ((line = error.readLine()) != null) {
                Log.e("error", line);
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return  "";
    }


    public static void intent2YK(Context context)
    {

        App app = (App)context;
        app.setCurrentApkPackName("null");

//        execShellCmd("settings  put  secure  enabled_accessibility_services  com.bee.yunkong/com.hwytapp.Service.AutoReadService");
//        execShellCmd("settings  put  secure  accessibility_enabled  0");

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent =packageManager.getLaunchIntentForPackage("com.bee.yunkong");
        context.startActivity(intent);
    }

    public static void intent2App(Context context,String appName)
    {

        App app = (App)context;
        app.setCurrentApkPackName(appName);

//        execShellCmd("settings  put  secure  enabled_accessibility_services  com.bee.yunkong/com.hwytapp.Service.AutoReadService");
//        execShellCmd("settings  put  secure  accessibility_enabled  1");

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent =packageManager.getLaunchIntentForPackage(appName);
        context.startActivity(intent);
    }

    public static void execShellCmd(String cmd) {
        Log.i("MyLog","----------------"+cmd);
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            Log.v("Throwable","----------------"+t.getMessage());
            t.printStackTrace();
        }
    }


}
