package com.hwytapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.activity.MainActivity;
import com.hwytapp.Common.HandleCmd;
import com.hwytapp.Common.MasterMethod;

import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                App ha = (App)getApplication();
                ha.setContext(getApplicationContext());

                Intent it1 = new Intent(getApplicationContext(), com.hwytapp.Service.YkSokectsService.class);
                it1.setPackage("com.hwytapp.Service.YkSokectsService");
                Bundle b1 = new Bundle();
                b1.putString("param", "s1");
                it1.putExtras(b1);

                startService(it1);
            }
        });

        Button button1= (Button) findViewById(R.id.button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App ha = (App)getApplication();
                HandleCmd hc =new HandleCmd(ha);

                MasterMethod mm = new MasterMethod(ha);

                try {
                    mm.getClientPhoneNum();
                }catch (Exception e)
                {

                }


            }
        });

        Button bt_reg= (Button) findViewById(R.id.bt_reg);

        bt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App ha = (App)getApplication();
                HandleCmd hc =new HandleCmd(ha);

                try {
                    TelephonyManager tm = (TelephonyManager) getApplicationContext().getApplicationContext().getSystemService(getApplicationContext().getApplicationContext().TELEPHONY_SERVICE);
                    String imei = tm.getDeviceId();
                    //hc.clientPhoneRegister(imei,"1","1","3","5");
                }catch (Exception e)
                {

                }


            }
        });


        Button bt_code= (Button) findViewById(R.id.bt_code);

        bt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App ha = (App)getApplication();
                HandleCmd hc =new HandleCmd(ha);

                try {

                    hc.sendVerificationCode();
                }catch (Exception e)
                {

                }


            }
        });

        Button bt_upload= (Button) findViewById(R.id.bt_uploadFile);

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                try {
//
//                    String path = "/storage/emulated/0/Android/data/com.hwyt.hwyt_app/files/shzx.apk";
//                    App ha = (App)getApplication();
//                    MasterMethod mm = new MasterMethod(ha);
//                    mm.uploadApkFile(path,"搜狐资讯","com.sohu.infonews","");
//
//                }catch (Exception e)
//                {
//
//                }


            }
        });

        Button bt_recorde_start= (Button) findViewById(R.id.bt_recorde_start);

        bt_recorde_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                App ha = (App)getApplication();
                ha.setIsRecode(true);
            }

         });

        Button bt_recorde_end= (Button) findViewById(R.id.bt_recorde_end);

        bt_recorde_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App ha = (App)getApplication();
                ha.setIsRecode(false);

                MasterMethod mm = new MasterMethod(ha);
                mm.editRecordeCmd("");
            }

        });



        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.SECOND, 1);
        Intent intent =new Intent("intent_alarm_log");
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),  0,intent,  0);
        long intervalMillis  = 1000;// 3秒,此设置无用，时间间隔误差60s
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intervalMillis,sender);
    }

    @Override
    protected void onPause() {
        super.onPause();

        App ha = (App)getApplication();
        ha.setIsKeyevent(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App ha = (App)getApplication();
        ha.setIsKeyevent(false);
        ha.setIsRecode(true);
    }
}
