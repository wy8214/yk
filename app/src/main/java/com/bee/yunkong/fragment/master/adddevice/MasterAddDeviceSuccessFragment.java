package com.bee.yunkong.fragment.master.adddevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;

public class MasterAddDeviceSuccessFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private String name;

    public static MasterAddDeviceSuccessFragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString(DATASTRING, name);
        MasterAddDeviceSuccessFragment fragment = new MasterAddDeviceSuccessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_device_success;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvAddress;
    private TextView tvGoon;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = getArguments().getString(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvGoon = (TextView) findViewById(R.id.tv_goon);

        tvAddress.setText(name);

        setPopOrFinish();
        setTitleStr("扫描二维码");

        tvGoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHostActivity().finish();
            }
        });

    }
}
