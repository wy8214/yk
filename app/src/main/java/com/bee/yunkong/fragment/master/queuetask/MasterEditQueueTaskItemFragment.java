package com.bee.yunkong.fragment.master.queuetask;

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
import com.bee.yunkong.fragment.master.home.MasterQueueTaskListfragment;
import com.bee.yunkong.network.DataManager;


public class MasterEditQueueTaskItemFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private boolean isX = false;
    private int input = 1;

    private DataManager.QueueTaskItem qtItem;
    public static MasterEditQueueTaskItemFragment newInstance(DataManager.QueueTaskItem qtItem) {

        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, qtItem);
        MasterEditQueueTaskItemFragment fragment = new MasterEditQueueTaskItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_edit_queue_task_item;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvTip;
    private EditText etAppName;
    private EditText etExecTime;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        qtItem = (DataManager.QueueTaskItem)getArguments().getSerializable(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
//        tvTip = (TextView) findViewById(R.id.tv_tip);
        etAppName = (EditText) findViewById(R.id.et_input_app_name);
        etExecTime = (EditText) findViewById(R.id.et_input_exec_time);


        setPopOrFinish();

        etAppName.setText(qtItem.apkName);

        if(qtItem.ID==null)
        {
            tvTitle.setText("创建队列");
            etExecTime.setText("");
        }

        etExecTime.setSelection(etExecTime.getText().toString().length());

        etExecTime.requestFocus();
        showSoftInput(etExecTime);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String execTime = etExecTime.getText().toString().trim();

                if (TextUtils.isEmpty(execTime) || execTime.contains(".")) {
                    toast("请输入任务名称");
                    return;
                }
                qtItem.execTime = etExecTime.getText().toString().trim();

                final  DataManager.QueueTaskInfo qt = new DataManager.QueueTaskInfo();
                qt.ID = qtItem.queueTaskID;
                try {
                    DataManager.editQueueTaskItem(getHostActivity().getApplicationContext(),qtItem, new DataManager.DataCallBack<Boolean>() {
                        @Override
                        public void onSuccess(final Boolean data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data) {

                                        startFragmentAcitivty(MasterQueueTaskItemFragment.newInstance(qt));
                                    }
                                }
                            });

                        }

                        @Override
                        public void onFail(DataManager.FailData data) {
                            toast(data.msg);
                            startFragmentAcitivty(MasterQueueTaskItemFragment.newInstance(qt));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("请输入任务名称");
                }
            }
        });
    }
}