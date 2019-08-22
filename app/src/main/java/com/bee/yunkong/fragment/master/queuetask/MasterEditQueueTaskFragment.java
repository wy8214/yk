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
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.home.MasterQueueTaskListfragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;

import static com.bee.yunkong.core.EventTag.controlled_change_address;

public class MasterEditQueueTaskFragment extends BaseFragment {
    private static final String TYPESTRING = "TYPESTRING";
    private static final String DATASTRING = "DATASTRING";
    private boolean isX = false;
    private int input = 1;

    private DataManager.QueueTaskInfo qtInfo;

    public static MasterEditQueueTaskFragment newInstance(DataManager.QueueTaskInfo qtInfo) {

        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, qtInfo);
        MasterEditQueueTaskFragment fragment = new MasterEditQueueTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_edit_queue_task;
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

        qtInfo = (DataManager.QueueTaskInfo)getArguments().getSerializable(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        etInput = (EditText) findViewById(R.id.et_input);


        setPopOrFinish();

        if(qtInfo.ID==null)
        {
            tvTitle.setText("创建任务");
            etInput.setText("");
        }


        etInput.setSelection(etInput.getText().toString().length());

        etInput.requestFocus();
        showSoftInput(etInput);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = etInput.getText().toString().trim();
                if (TextUtils.isEmpty(input) || input.contains(".")) {
                    toast("请输入任务名称");
                    return;
                }
                try {
                    DataManager.editQueueTask(getHostActivity().getApplicationContext(),input,qtInfo.ID, new DataManager.DataCallBack<Boolean>() {
                        @Override
                        public void onSuccess(final Boolean data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data) {
                                        startFragmentAcitivty(MasterQueueTaskListfragment.newInstance());
                                    }
                                }
                            });

                        }

                        @Override
                        public void onFail(DataManager.FailData data) {
                            toast(data.msg);
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