package com.bee.yunkong.fragment.master;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;

public class MasterLoginFragment extends BaseFragment {
    public static MasterLoginFragment newInstance() {

        Bundle args = new Bundle();

        MasterLoginFragment fragment = new MasterLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_login;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private EditText etInput;
    private TextView tvNext;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        etInput = (EditText) findViewById(R.id.et_input);
        tvNext = (TextView) findViewById(R.id.tv_next);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 11) {
                    setEnable(tvNext);
                } else {
                    setDisble(tvNext);
                }
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 验证手机号，并发生短信验证码
                startFragmentAcitivty(MasterLoginSMSFragment.newInstance(etInput.getText().toString().trim()));
                getHostActivity().finish();
//                DataManager.checkMasterPhoneNumber(etInput.getText().toString().trim(), new DataManager.DataCallBack<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean data) {
//                        if (data) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    startFragmentAcitivty(MasterLoginSMSFragment.newInstance(etInput.getText().toString().trim()));
//                                    getHostActivity().finish();
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onFail(DataManager.FailData data) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showLoginErrorDialog();
//                            }
//                        });
//                    }
//                });
            }
        });

        setDisble(tvNext);
    }

    private void setEnable(TextView tv) {
        tv.setClickable(true);
        tv.setBackground(getRes().getDrawable(R.drawable.shape_bt_save));
    }


    private void setDisble(TextView tv) {
        tv.setClickable(false);
        tv.setBackground(getRes().getDrawable(R.drawable.shape_bt_gray));
    }

    private Dialog errorDialog;
    private TextView tvSure;


    private void showLoginErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new Dialog(getHostActivity(), R.style.dialog);
            LayoutInflater inflater = LayoutInflater.from(getHostActivity());
            ViewGroup layout_mainLayout = (ViewGroup) inflater.inflate(
                    R.layout.dialog_tip, null);
            errorDialog.setContentView(layout_mainLayout);
            errorDialog.setCancelable(true);
            errorDialog.setCanceledOnTouchOutside(true);
            tvSure = (TextView) errorDialog.findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != errorDialog && errorDialog.isShowing()) {
                        errorDialog.dismiss();
                    }
                }
            });

        }
        if (!errorDialog.isShowing()) {
            errorDialog.show();
        }
    }
}
