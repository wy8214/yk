package com.bee.yunkong.fragment.master.adddevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;

/**
 * 录入设备 失败页面
 */
public class MasterAddDeviceFailFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private String data;

    public static MasterAddDeviceFailFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString(DATASTRING, data);
        MasterAddDeviceFailFragment fragment = new MasterAddDeviceFailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_device_fail;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvResult;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = getArguments().getString(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvResult = (TextView) findViewById(R.id.tv_result);
        setTitleStr("扫描二维码");
        tvResult.setText(data);
        setPopOrFinish();

    }
}
