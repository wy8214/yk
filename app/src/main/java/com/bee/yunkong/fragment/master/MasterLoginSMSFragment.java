package com.bee.yunkong.fragment.master;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.activity.MainActivity;
import com.bee.yunkong.activity.SplashActivity;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.controlled.ControlledConnectSuccessFragment;
import com.bee.yunkong.fragment.master.home.MasterHomefragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;
import com.hwytapp.Common.MasterMethod;

public class MasterLoginSMSFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private String phoneNum;

    public static MasterLoginSMSFragment newInstance(String phoneNum) {

        Bundle args = new Bundle();
        args.putString(DATASTRING, phoneNum);
        MasterLoginSMSFragment fragment = new MasterLoginSMSFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_register_success:
                startFragmentAcitivty(MasterHomefragment.newInstance());
                getHostActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_login_sms;
    }


    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvPhonenum;
    private EditText etSms;
    private TextView tvLogin;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        phoneNum = getArguments().getString(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvPhonenum = (TextView) findViewById(R.id.tv_phonenum);
        etSms = (EditText) findViewById(R.id.et_sms);
        tvLogin = (TextView) findViewById(R.id.tv_login);

        tvPhonenum.setText(phoneNum);


        etSms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    setEnable(tvLogin);
                } else {
                    setDisble(tvLogin);
                }
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DataManager.checkMasterLoginSMS(getHostActivity().getApplicationContext(),phoneNum, etSms.getText().toString().trim()
                        , DeviceUtil.getImei(getHostActivity()),new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                if (data) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getHostActivity(),SplashActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFail(DataManager.FailData data) {
                                    toast(data.msg);
                            }
                        });

            }
        });
        setDisble(tvLogin);
    }

    private void setEnable(TextView tv) {
        tv.setClickable(true);
        tv.setBackground(getRes().getDrawable(R.drawable.shape_bt_save));
    }


    private void setDisble(TextView tv) {
        tv.setClickable(false);
        tv.setBackground(getRes().getDrawable(R.drawable.shape_bt_gray));
    }
}
