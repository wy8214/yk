package com.bee.yunkong.fragment.master.adddevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;

public class MasterAddDeviceAddGroupFragment extends BaseFragment {
    private static final String ISADD = "ISADD";
    private static final String GROUPNAME = "GROUPNAME";
    private static final String GROUPID = "GROUPID";
    private static final String GROUPCODE = "GROUPCODE";
    private boolean isAdd;
    private String groupName;
    private String groupID;
    private String groupCode;

    public static MasterAddDeviceAddGroupFragment newInstance(boolean isAdd, String groupName,String groupID,String groupCode) {

        Bundle args = new Bundle();
        args.putBoolean(ISADD, isAdd);
        args.putString(GROUPNAME, groupName);
        args.putString(GROUPID, groupID);
        args.putString(GROUPCODE, groupCode);
        MasterAddDeviceAddGroupFragment fragment = new MasterAddDeviceAddGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_device_add_group;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvTip;
    private EditText etInput;
    private TextView tvTip2;
    private EditText etInput2;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isAdd = getArguments().getBoolean(ISADD);
        groupName = getArguments().getString(GROUPNAME);
        groupID = getArguments().getString(GROUPID);
        groupCode = getArguments().getString(GROUPCODE);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        etInput = (EditText) findViewById(R.id.et_input);
        tvTip2 = (TextView) findViewById(R.id.tv_tip_2);
        etInput2 = (EditText) findViewById(R.id.et_input_2);


        setPopOrFinish();
        setTitleStr(isAdd ? "新建分组" : "编辑分组");
        if (!TextUtils.isEmpty(groupName)) {
            etInput.setText(groupName);

            etInput.setSelection(etInput.getText().toString().length());

            etInput2.setText(groupCode);
        }
        showSoftInput(etInput);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etInput.getText().toString().trim();
                if (TextUtils.isEmpty(input)) {
                    toast("请输入组名");
                    return;
                }
                String code = etInput2.getText().toString().trim();
                if(TextUtils.isEmpty(code)||code.length()>2){
                    toast("请输入正确的编码");
                }
                App app = (App)getHostActivity().getApplicationContext();
                if (isAdd) {

                    DataManager.masterAddGroup(input,code,app, new DataManager.DataCallBack<Boolean>() {
                        @Override
                        public void onSuccess(final Boolean data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data) {
                                       // postEvent(EventTag.master_change_add_group);
                                        pop();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFail(DataManager.FailData data) {
                            toast(data.msg);
                        }
                    });
                } else {
                    DataManager.masterEditGroup(input,code,groupID,app, new DataManager.DataCallBack<Boolean>() {
                        @Override
                        public void onSuccess(final Boolean data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data) {
                                        postEvent(EventTag.master_change_add_group);
                                        pop();
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
            }
        });
    }
}
