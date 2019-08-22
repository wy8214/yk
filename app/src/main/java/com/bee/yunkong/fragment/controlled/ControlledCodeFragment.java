package com.bee.yunkong.fragment.controlled;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.MasterLoginFragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;
import com.google.zxing.WriterException;
import com.yzq.zxinglibrary.encode.CodeCreator;



public class ControlledCodeFragment extends BaseFragment {
    public static ControlledCodeFragment newInstance() {

        Bundle args = new Bundle();

        ControlledCodeFragment fragment = new ControlledCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_controlled_code;
    }


    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private ImageView ivCode;
    private TextView tvNext;

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case client_register_success:
                startFragmentAcitivty(ControlledConnectSuccessFragment.newInstance());
                getHostActivity().finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        ivCode = (ImageView) findViewById(R.id.iv_code);
        tvNext = (TextView) findViewById(R.id.tv_next);


        App app = (App)getHostActivity().getApplicationContext();
        try {
            Bitmap bitmap = CodeCreator.createQRCode(DeviceUtil.getImei(getHostActivity())+"|||"+app.getFd(),
                            DeviceUtil.dip2px(160)
                            , DeviceUtil.dip2px(160), null);
            ivCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        DataManager.waitForScan(getHostActivity().getApplicationContext(),DeviceUtil.getImei(getHostActivity())
                , new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(final Boolean data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startFragmentAcitivty(ControlledConnectSuccessFragment.newInstance());
                                getHostActivity().finish();
                            }
                        });

                    }

                    @Override
                    public void onFail(DataManager.FailData data) {

                    }
                });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataManager.changeRollType(true, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startFragmentAcitivty(MasterLoginFragment.newInstance());
                                getHostActivity().finish();
                            }
                        });
                    }

                    @Override
                    public void onFail(DataManager.FailData result) {
                        toast(result.msg);
                    }
                });
            }
        });
    }
}
