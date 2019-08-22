package com.bee.yunkong.fragment.master.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.activity.MainActivity;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.fragment.controlled.ControlledConnectSuccessFragment;
import com.bee.yunkong.fragment.master.home.MasterTaskListfragment;
import com.bee.yunkong.network.DataManager;

public class MasterEditTaskFragment extends BaseFragment {
    private static final String ISADD = "ISADD";
    private static final String TASKID = "TASKID";
    private static final String TASKNAME = "TASKNAME";
    private static final String TASKCODE = "TASKCODE";
    private String taskName;
    private String taskId;
    private String taskCode;

    public static MasterEditTaskFragment newInstance(String taskCode,String taskName, String taskId) {

        Bundle args = new Bundle();
        args.putString(ISADD, taskName);
        args.putString(TASKID, taskId);
        args.putString(TASKCODE, taskCode);
        args.putString(TASKNAME, taskName);
        MasterEditTaskFragment fragment = new MasterEditTaskFragment();
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
        taskName = getArguments().getString(ISADD);
        taskId = getArguments().getString(TASKID);
        taskCode = getArguments().getString(TASKCODE);


        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        etInput = (EditText) findViewById(R.id.et_input);

        tvTip.setText("脚本名称");



        setPopOrFinish();
        setTitleStr("编辑脚本名称");
        if (!TextUtils.isEmpty(taskName)) {
            etInput.setText(taskName);
            etInput.setSelection(etInput.getText().toString().length());
        }
        showSoftInput(etInput);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etInput.getText().toString().trim();
                if (TextUtils.isEmpty(input)) {
                    toast("请输入脚本名称");
                    return;
                }
                DataManager.masterEditTask(getHostActivity().getApplicationContext(),taskCode,input, taskId, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(final Boolean data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (data) {
                                    startActivity(new Intent(getHostActivity(),MainActivity.class));
//                                    startFragmentAcitivty(MasterTaskListfragment.newInstance());
                                    getHostActivity().finish();
//                                    pop();
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
}
