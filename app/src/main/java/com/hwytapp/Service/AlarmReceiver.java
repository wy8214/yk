package com.hwytapp.Service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.hwytapp.Bean.QueueTaskBean;
import com.hwytapp.Common.MasterMethod;
import com.hwytapp.Utils.AppUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    //private static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        App hwtyApp = (App) context.getApplicationContext();

        String action = intent.getAction();
         /*  if(action.equals(action_boot))
        {
         Log.d("onReceive:", "Boot system");
            Intent startIntent = new Intent(context,MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }*/

        if(action.equals("intent_alarm_log")) {

            MasterMethod sms = new MasterMethod(hwtyApp);
            sms.getSMSInfo();
        }





        String pName = "hwyt.com.watch";
        boolean rstA = AppUtils.getRunningActivity(pName);

        if(!rstA){
            PackageManager packageManager = context.getPackageManager();
            Intent intent1 = new Intent();
            intent1 =packageManager.getLaunchIntentForPackage(pName);
            if(intent1!=null){
                context.startActivity(intent1);
            }
        }


        if(hwtyApp.getIsMasterPhone()) {
            MasterMethod mm = new MasterMethod(hwtyApp);
            Map<String, QueueTaskBean> queueTaskMap = hwtyApp.getQueueTaskMap();
            for (QueueTaskBean qt : queueTaskMap.values()) {
                mm.pulseQueueTask(qt.getID());
            }

        }


    }

    private boolean strContains(String apkName,List<String> apkList)
    {
        for(String aa : apkList)
        {
            if(apkName.equals(aa))
                return true;
        }

        return false;

    }
    private void execShellCmd(String cmd) {
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
            Log.v("color","----------------"+t.getMessage());
            t.printStackTrace();
        }
    }



}
