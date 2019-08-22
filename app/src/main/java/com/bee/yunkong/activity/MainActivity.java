package com.bee.yunkong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baina.floatwindowlib.freeposition.DraggableFloatWindow;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseActivity;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.addscript.MasterAddScriptChooseDevicesFragment;
import com.bee.yunkong.fragment.master.home.MasterAppListfragment;
import com.bee.yunkong.fragment.master.home.MasterHomefragment;
import com.bee.yunkong.fragment.master.home.MasterQueueTaskListfragment;
import com.bee.yunkong.fragment.master.home.MasterTaskListfragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.service.FloatWindowService;
import com.bee.yunkong.view.BottomBar;
import com.bee.yunkong.view.BottomBarTab;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BaseActivity {

    private DraggableFloatWindow mFloatWindow;
    private FrameLayout framehost;
    private ImageView ivStart;
    private BottomBar bottomBar;

    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int FOURTH = 3;

    BottomBarTab home;
    BottomBarTab app;
    BottomBarTab task;
    BottomBarTab queue;
    private SupportFragment[] mFragments = new SupportFragment[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        framehost = (FrameLayout) findViewById(R.id.framehost);
        ivStart = (ImageView) findViewById(R.id.iv_start);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        if (savedInstanceState == null) {
            mFragments[FIRST] = MasterHomefragment.newInstance();
            mFragments[SECOND] = MasterAppListfragment.newInstance();
            mFragments[THIRD] = MasterTaskListfragment.newInstance();
            mFragments[FOURTH] = MasterQueueTaskListfragment.newInstance();

            loadMultipleRootFragment(R.id.framehost, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.getFragments()自行进行判断查找(效率更高些),
            // 用下面的方法查找更方便些
            mFragments[FIRST] = findFragment(MasterHomefragment.class);
            mFragments[SECOND] = findFragment(MasterAppListfragment.class);
            mFragments[THIRD] = findFragment(MasterTaskListfragment.class);
            mFragments[FOURTH] = findFragment(MasterQueueTaskListfragment.class);
        }
        initView();
        updateDevicePhone();
    }

    private void updateDevicePhone() {
        DataManager.updatePhoneState(getImsi(getApplicationContext()), new DataManager.DataCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onFail(DataManager.FailData result) {

            }
        });
    }

    public static DataManager.SimCardInfo getImsi(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        DataManager.SimCardInfo info = new DataManager.SimCardInfo();
        info.deviceId = tm.getDeviceId();// 获取智能设备唯一编号
        info.tel = tm.getLine1Number();// 获取本机号码
        info.simei = tm.getSimSerialNumber();// 获得SIM卡的序号
        info.imsi = tm.getSubscriberId();// 得到用户Id
        return info;
    }

    private void initView() {
        mFloatWindow = DraggableFloatWindow.getDraggableFloatWindow(this, null);

        home = new BottomBarTab(getHostActivity(), R.drawable.nav_home, "设备");
        app = new BottomBarTab(getHostActivity(), R.drawable.nav_app, "应用");
        task = new BottomBarTab(getHostActivity(), R.drawable.nav_task, "脚本");
        queue = new BottomBarTab(getHostActivity(), R.drawable.nav_08, "任务");
        bottomBar.addItem(home);
        bottomBar.addItem(app);
        bottomBar.addItem(task);
        bottomBar.addItem(queue);
        ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFloatWindow.show();
                startFragmentAcitivty(MasterAddScriptChooseDevicesFragment.newInstance());
            }
        });

        bottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 这里推荐使用EventBus来实现 -> 解耦
//                EventBus.getDefault().post(new TabSelectedEvent(position));
            }

            @Override
            public boolean onTabClick(int position) {
                return true;
            }
        });
    }


    @Override
    public void onBackPressedSupport() {
//        super.onBackPressedSupport();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onMainTheadEvent(MyEvent event) {
        super.onMainTheadEvent(event);
        switch (event.getTag()) {
            case master_start_record:
                startService(new Intent(getApplicationContext(), FloatWindowService.class));
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                ivStart.setVisibility(View.GONE);
                break;
            case master_switch_to_third:
                bottomBar.setCurrentItem(THIRD);
                postEvent(new MyEvent(EventTag.master_change_tasks));
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(FloatWindowService.isRecording){
            ivStart.setVisibility(View.GONE);
        }else {
            ivStart.setVisibility(View.VISIBLE);
        }
    }
}
