package com.bee.yunkong.fragment.controlled;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;

import static com.bee.yunkong.core.EventTag.controlled_change_address;

public class ControlledEditAddressFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private DataManager.ControlledDeviceInfo deviceInfo;

    public static ControlledEditAddressFragment newInstance(DataManager.ControlledDeviceInfo deviceInfo) {

        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, deviceInfo);
        ControlledEditAddressFragment fragment = new ControlledEditAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_controlled_edit_address;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private LinearLayout ll1;
    private TextView tv1;
    private LinearLayout ll2;
    private TextView tv2;
    private LinearLayout ll3;
    private TextView tv3;
    private TextView tvSave;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceInfo = (DataManager.ControlledDeviceInfo) getArguments().getSerializable(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        tv1 = (TextView) findViewById(R.id.tv_1);
        ll2 = (LinearLayout) findViewById(R.id.ll_2);
        tv2 = (TextView) findViewById(R.id.tv_2);
        ll3 = (LinearLayout) findViewById(R.id.ll_3);
        tv3 = (TextView) findViewById(R.id.tv_3);
        tvSave = (TextView) findViewById(R.id.tv_save);


        tv1.setText(deviceInfo.groupName);
        tv2.setText(deviceInfo.y);
        tv3.setText(deviceInfo.x);

        setPopOrFinish();
        setTitleStr("位置");

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(ControlledChooseGroupFragment.newInstance(deviceInfo));
            }
        });

        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 进入Y轴选择页面
                start(ControlledInputNumFragment.newInstance(false, Integer.parseInt(deviceInfo.y)));
            }
        });

        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start(ControlledInputNumFragment.newInstance(true, Integer.parseInt(deviceInfo.x)));
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.changeControlledAddress(getHostActivity().getApplicationContext(),DeviceUtil.getImei(getHostActivity()),tv1.getText().toString(), tv3.getText().toString(),
                        tv2.getText().toString(), new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(final Boolean data) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (data) {
                                            postEvent(controlled_change_address);
                                            pop();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onFail(DataManager.FailData data) {
                                toast(data.msg);
                            }
                        });
            }
        });

    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case controlled_change_group:

                String name = (String) noEvent.getObject();
                deviceInfo.groupName = name;
                tv1.setText(name);
                break;
            case controlled_change_x:
                String x = (String) noEvent.getObject();
                deviceInfo.x = x;
                tv3.setText(x);
                break;
            case controlled_change_y:
                String y = (String) noEvent.getObject();
                deviceInfo.y = y;
                tv2.setText(y);
                break;
        }
    }
}
