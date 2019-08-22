package com.bee.yunkong.fragment.controlled;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;

public class ControlledConnectSuccessFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private DataManager.ControlledDeviceInfo deviceInfo;

    public static ControlledConnectSuccessFragment newInstance() {

        Bundle args = new Bundle();
        ControlledConnectSuccessFragment fragment = new ControlledConnectSuccessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_controlled_connect_success;
    }

    private RelativeLayout rlTitleBar;
    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private RelativeLayout rlState1;
    private ImageView ivLogo;
    private TextView tvName;
    private TextView tvAddress;
    private ImageView ivUp;
    private LinearLayout llState2;
    private ImageView ivDown;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rlTitleBar = (RelativeLayout) findViewById(R.id.rl_title_bar);
        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        rlState1 = (RelativeLayout) findViewById(R.id.rl_state1);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        ivUp = (ImageView) findViewById(R.id.iv_up);
        llState2 = (LinearLayout) findViewById(R.id.ll_state_2);
        ivDown = (ImageView) findViewById(R.id.iv_down);
        tv1 = (TextView) findViewById(R.id.tv_1);
        tv2 = (TextView) findViewById(R.id.tv_2);
        tv3 = (TextView) findViewById(R.id.tv_3);
        tv4 = (TextView) findViewById(R.id.tv_4);


        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragmentAcitivty(ControlledSetFragment.newInstance());
            }
        });

        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlState1.setVisibility(View.GONE);
                llState2.setVisibility(View.VISIBLE);
            }
        });

        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlState1.setVisibility(View.VISIBLE);
                llState2.setVisibility(View.GONE);
            }
        });


        DataManager.disConnect(new DataManager.DataCallBack<Boolean>() {
            @Override
            public void onSuccess(final Boolean data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeState(data);
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData data) {

            }
        });

        getDeviceInfo();
    }

    private void getDeviceInfo() {
        DataManager.getDeviceInfoOfControlled(getHostActivity().getApplicationContext(),DeviceUtil.getImei(getHostActivity())
                , new DataManager.DataCallBack<DataManager.ControlledDeviceInfo>() {
                    @Override
                    public void onSuccess(DataManager.ControlledDeviceInfo data) {
                        deviceInfo = data;
                        bindData();
                    }

                    @Override
                    public void onFail(DataManager.FailData data) {

                    }
                });
    }

    private void bindData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvName.setText(deviceInfo.locationName);
                tvAddress.setText(deviceInfo.groupName + "  第" + deviceInfo.y + "排  第" + deviceInfo.x + "台");
                tv1.setText(deviceInfo.locationName);
                tv2.setText(deviceInfo.deviceCode);
                tv3.setText(deviceInfo.masterName);
                tv4.setText(deviceInfo.myNumber);
            }
        });
    }

    private void changeState(boolean isDisConnect) {
        if (isDisConnect) {
            rlTitleBar.setBackgroundColor(Color.parseColor("#D54942"));
            ivLogo.setImageResource(R.drawable.logo_wrong);
            tvName.setText("设备已断开连接");
            tvAddress.setText("账号余额不足，请充值");
        } else {
            rlTitleBar.setBackgroundColor(Color.parseColor("#FCF270"));
            ivLogo.setImageResource(R.drawable.logo_white);
        }
    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case controlled_change_address:
                getDeviceInfo();
                break;
        }
    }
}
