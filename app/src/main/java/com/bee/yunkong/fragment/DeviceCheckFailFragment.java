package com.bee.yunkong.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.util.common.DeviceUtil;

public class DeviceCheckFailFragment extends BaseFragment {
    public static DeviceCheckFailFragment newInstance() {

        Bundle args = new Bundle();

        DeviceCheckFailFragment fragment = new DeviceCheckFailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_device_check_fail;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvContactUs;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //todo 这个界面跳转到哪？不能返回?

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvContactUs = (TextView) findViewById(R.id.tv_contact_us);
        String imei = DeviceUtil.getImei(getHostActivity().getApplicationContext());
        tvRight.setText(imei);

        tvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 联系我们 交互是什么
            }
        });
    }
}
