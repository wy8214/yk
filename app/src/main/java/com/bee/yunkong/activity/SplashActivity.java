package com.bee.yunkong.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseActivity;
import com.bee.yunkong.fragment.DeviceCheckFailFragment;
import com.bee.yunkong.fragment.NoNetWorkFragment;
import com.bee.yunkong.fragment.controlled.ControlledCodeFragment;
import com.bee.yunkong.fragment.controlled.ControlledConnectSuccessFragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;
import com.hwytapp.Common.Config;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //todo 网络检查 是主控还是受控  跳转到相应界面
        if (!DeviceUtil.isNetworkAvalible(getApplicationContext())) {
            startFragmentAcitivty(NoNetWorkFragment.newInstance());
            finish();
        } else {

            final App ha = (App)getApplication();
            ha.setContext(getApplicationContext());

            //通讯服务
            Intent it = new Intent(getApplicationContext(), com.hwytapp.Service.YkSokectsService.class);
            it.setPackage("com.hwytapp.Service.YkSokectsService");
            startService(it);


            //通过imei号检查是那种设备 是被控还是主控
            String imei = DeviceUtil.getImei(getApplicationContext());
            DataManager.getDeviceType(imei,getApplicationContext(), new DataManager.DataCallBack<DataManager.DeviceTypeResponse>() {
                @Override
                public void onSuccess(final DataManager.DeviceTypeResponse data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //todo 我加了一个参数，判断是否已注册，如果未注册，走被控流程，如果已注册，走原先的逻辑
                           // ha.setCurrentApkPackName("com.jifen.qukan");
                            String alarmLog = "intent_alarm_package";

                            //任务机
                            if(data.deviceType.equals("1"))
                            {
                                startAlarm(alarmLog);
                                startFragmentAcitivty(ControlledConnectSuccessFragment.newInstance());
                                getHostActivity().finish();
                            }
                            //主控机
                            if (data.deviceType.equals("2")) {


                                File dir = new File(Config.APKDIR);
                                if(!dir.exists())
                                {
                                    dir.mkdirs();
                                }

                                startAlarm(alarmLog);
                                //主控机
                                startActivity(new Intent(getHostActivity(),MainActivity.class));
                                finish();
                            }

                            if (data.deviceType.equals("3")) {
                                //未注册，进入扫码页面
                                startFragmentAcitivty(ControlledCodeFragment.newInstance());
                                finish();
                            }

                            //服务机
                            if(data.deviceType.equals("4")){
                                alarmLog = "intent_alarm_log";

                                startAlarm(alarmLog);
                            }


                        }
                    });
                }

                @Override
                public void onFail(final DataManager.FailData data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast(data.msg);
                            startFragmentAcitivty(DeviceCheckFailFragment.newInstance());
                            finish();
                        }
                    });
                }
            });
        }
    }

    private  void startAlarm(String intentStr)
    {

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.SECOND, 1);
        Intent intent =new Intent(intentStr);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),  0,intent,  0);
        long intervalMillis  = 1000;// 3秒,此设置无用，时间间隔误差60s
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intervalMillis,sender);

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
