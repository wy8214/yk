package com.bee.yunkong.fragment.controlled;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;

public class ControlledInputNumFragment extends BaseFragment {
    private static final String TYPESTRING = "TYPESTRING";
    private static final String DATASTRING = "DATASTRING";
    private boolean isX = false;
    private int input = 1;

    public static ControlledInputNumFragment newInstance(boolean isX, int input) {

        Bundle args = new Bundle();
        args.putBoolean(TYPESTRING, isX);
        args.putInt(DATASTRING, input);
        ControlledInputNumFragment fragment = new ControlledInputNumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_input_num;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvTip;
    private EditText etInput;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isX = getArguments().getBoolean(TYPESTRING);
        input = getArguments().getInt(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        etInput = (EditText) findViewById(R.id.et_input);
        setPopOrFinish();
        tvTip.setText(isX ? "列数" : "行数");
        etInput.setText(input + "");
        etInput.setSelection(etInput.getText().toString().length());
        etInput.requestFocus();
        showSoftInput(etInput);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = etInput.getText().toString().trim();
                if (TextUtils.isEmpty(input) || input.contains(".")) {
                    toast("请输入1-99的数字");
                    return;
                }
                try {
                    Integer integer = Integer.parseInt(input);
                    if (0 == integer || integer > 99 || integer < 0) {
                        toast("请输入1-99的数字");
                    } else {
                        if (isX) {
                            MyEvent event = new MyEvent(EventTag.controlled_change_x);
                            event.setObject(input);
                            postEvent(event);
                            hideSoftInput();
                            pop();
                        } else {
                            MyEvent event = new MyEvent(EventTag.controlled_change_y);
                            event.setObject(input);
                            postEvent(event);
                            hideSoftInput();
                            pop();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("请输入1-99的数字");
                }
            }
        });
    }
}
