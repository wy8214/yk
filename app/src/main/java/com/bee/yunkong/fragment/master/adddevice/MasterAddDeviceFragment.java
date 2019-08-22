package com.bee.yunkong.fragment.master.adddevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.activity.MainActivity;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.controlled.ControlledInputNumFragment;
import com.bee.yunkong.network.DataManager;
import com.hwytapp.Bean.GroupBean;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.Map;

public class MasterAddDeviceFragment extends BaseFragment {
    public static MasterAddDeviceFragment newInstance() {

        Bundle args = new Bundle();

        MasterAddDeviceFragment fragment = new MasterAddDeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_device;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private LinearLayout ll1;
    private TextView tvGroup;
    private LinearLayout ll2;
    private TextView tvY;
    private LinearLayout ll3;
    private TextView tvX;
    private TextView tvStart;

    private DataManager.LocationInfo info;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        tvGroup = (TextView) findViewById(R.id.tv_group);
        ll2 = (LinearLayout) findViewById(R.id.ll_2);
        tvY = (TextView) findViewById(R.id.tv_y);
        ll3 = (LinearLayout) findViewById(R.id.ll_3);
        tvX = (TextView) findViewById(R.id.tv_x);
        tvStart = (TextView) findViewById(R.id.tv_start);





        setPopOrFinish();
        setTitleStr("录入设备");

        DataManager.masterAddDeviceGetStartLocationInfo(getHostActivity().getApplicationContext(),new DataManager.DataCallBack<DataManager.LocationInfo>() {
            @Override
            public void onSuccess(final DataManager.LocationInfo data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info = data;
                        bindData();
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData data) {

            }
        });

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(MasterAddDeviceChooseGroupFragment.newInstance(info.groupName));
            }
        });

        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(ControlledInputNumFragment.newInstance(false, info.y));
            }
        });
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(ControlledInputNumFragment.newInstance(true, info.x));
            }
        });

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 开始扫码
                startScan();
            }
        });

    }

    private void startScan() {
        Intent intent = new Intent(getHostActivity(), CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(false);//是否扫描条形码 默认为true
        config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
        config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
        config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        config.setShowbottomLayout(false);
        config.setGroupName(info.groupName);
        config.setX(info.x);
        config.setY(info.y);
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, 1112);
    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_change_group:
                info.groupName = (String) noEvent.getObject();
                bindData();
                break;
            case controlled_change_x:
                String x = (String) noEvent.getObject();
                info.x = Integer.parseInt(x);
                bindData();
                break;
            case controlled_change_y:
                String y = (String) noEvent.getObject();
                info.y = Integer.parseInt(y);
                bindData();
                break;
        }
    }

    private void bindData() {
        tvGroup.setText(info.groupName);
        tvX.setText("第" + info.x + "台");
        tvY.setText("第" + info.y + "排");
    }
}
