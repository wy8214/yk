package com.bee.yunkong.fragment.controlled;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.activity.SplashActivity;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;

public class ControlledSetFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private DataManager.ControlledDeviceInfo deviceInfo;

    public static ControlledSetFragment newInstance() {

        Bundle args = new Bundle();
        ControlledSetFragment fragment = new ControlledSetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_controlled_set;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvAddress;
    private LinearLayout llRecover;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        llRecover = (LinearLayout) findViewById(R.id.ll_recover);
        setPopOrFinish();
        setTitleStr("设置");

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(ControlledEditAddressFragment.newInstance(deviceInfo));
            }
        });

        llRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginErrorDialog();
            }
        });

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
                tvAddress.setText(deviceInfo.groupName + "  第" + deviceInfo.y + "排  第" + deviceInfo.x + "台");
            }
        });
    }

    private Dialog errorDialog;
    private TextView tvCancle;
    private TextView tvSure;


    private void showLoginErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new Dialog(getHostActivity(), R.style.dialog);
            LayoutInflater inflater = LayoutInflater.from(getHostActivity());
            ViewGroup layout_mainLayout = (ViewGroup) inflater.inflate(
                    R.layout.dialog_confirm, null);
            errorDialog.setContentView(layout_mainLayout);
            errorDialog.setCancelable(true);
            errorDialog.setCanceledOnTouchOutside(true);

            tvCancle = (TextView) errorDialog.findViewById(R.id.tv_cancle);
            tvSure = (TextView) errorDialog.findViewById(R.id.tv_sure);

            tvCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != errorDialog && errorDialog.isShowing()) {
                        errorDialog.dismiss();
                    }
                }
            });

            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != errorDialog && errorDialog.isShowing()) {
                        DataManager.resetToStart(getHostActivity().getApplicationContext(),DeviceUtil.getImei(getHostActivity()),
                                new DataManager.DataCallBack<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean data) {
                                        if (data) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (null != errorDialog) {
                                                        errorDialog.dismiss();
                                                    }

                                                    Context context = getHostActivity().getApplicationContext();
                                                    App app = (App)context;
                                                    app.getPhoneMap().clear();
                                                    app.setDeviceType("3");
                                                    getHostActivity().finish();
                                                    Intent intent = new Intent(getHostActivity(),SplashActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onFail(final DataManager.FailData data) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                toast(data.msg);
                                            }
                                        });
                                    }
                                });
                    }
                }
            });

        }
        if (!errorDialog.isShowing()) {
            errorDialog.show();
        }
    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case controlled_change_address:
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
                break;
        }
    }
}
