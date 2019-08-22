package com.bee.yunkong.fragment.master.addscript;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.network.DataManager;
import com.google.common.base.Joiner;
import com.hwytapp.Bean.CmdBean;
import com.hwytapp.Bean.PhoneBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterAddScriptChooseDevicesFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private String scriptID="";

    public static MasterAddScriptChooseDevicesFragment newInstance() {

        Bundle args = new Bundle();

        MasterAddScriptChooseDevicesFragment fragment = new MasterAddScriptChooseDevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MasterAddScriptChooseDevicesFragment newInstance(String scriptID) {

        Bundle args = new Bundle();
        args.putString(DATASTRING, scriptID);
        MasterAddScriptChooseDevicesFragment fragment = new MasterAddScriptChooseDevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_script_choose_devices;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private ViewSwitcher viewSwitcher;
    private LinearLayout llState1;
    private LinearLayout llState1ChooseGroup;
    private TextView tvState1GroupName;
    private TextView tvState1ChooseAll;
    private TextView tvState1ChooseReverse;
    private RecyclerView recycleState1;
    private LinearLayout llState2;
    private LinearLayout llState2ChooseAllGroup;
    private ImageView ivState2All;
    private TextView tvState2Sure;
    private TextView tvState2Cancle;
    private RecyclerView recycleState2Left;
    private LinearLayout llChooseAllRows;
    private TextView tvChooseAllRows;
    private ImageView ivAllRowsCheck;
    private RecyclerView recycleState2Right;


    private List<DataManager.DeviceGroupInfo> groupInfos = new ArrayList<>();
    private List<DataManager.DeviceYInfo> yInfos = new ArrayList<>();
    private List<DataManager.DeviceXInfo> xInfos = new ArrayList<>();

    private DevicesAdapter devicesAdapter;
    private GroupAdapter groupAdapter;
    private YAdapter yAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scriptID = getArguments().getString(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
        llState1 = (LinearLayout) findViewById(R.id.ll_state1);
        llState1ChooseGroup = (LinearLayout) findViewById(R.id.ll_state1_choose_group);
        tvState1GroupName = (TextView) findViewById(R.id.tv_state1_group_name);
        tvState1ChooseAll = (TextView) findViewById(R.id.tv_state1_choose_all);
        tvState1ChooseReverse = (TextView) findViewById(R.id.tv_state1_choose_reverse);
        recycleState1 = (RecyclerView) findViewById(R.id.recycle_state1);
        llState2 = (LinearLayout) findViewById(R.id.ll_state2);
        llState2ChooseAllGroup = (LinearLayout) findViewById(R.id.ll_state2_choose_all_group);
        ivState2All = (ImageView) findViewById(R.id.iv_state2_all);
        tvState2Sure = (TextView) findViewById(R.id.tv_state2_sure);
        tvState2Cancle = (TextView) findViewById(R.id.tv_state2_cancle);
        recycleState2Left = (RecyclerView) findViewById(R.id.recycle_state2_left);
        llChooseAllRows = (LinearLayout) findViewById(R.id.ll_choose_all_rows);
        tvChooseAllRows = (TextView) findViewById(R.id.tv_choose_all_rows);
        ivAllRowsCheck = (ImageView) findViewById(R.id.iv_all_rows_check);
        recycleState2Right = (RecyclerView) findViewById(R.id.recycle_state2_right);




        if (!TextUtils.isEmpty(scriptID)) {

            App app = (App)getHostActivity().getApplicationContext();

            Map<String,PhoneBean> phoneBeanMap = app.getPhoneMap();
            for(PhoneBean pb : phoneBeanMap.values())
            {
                pb.setIsSelected(false);
                if(pb.getRunCmdID().equals(scriptID))
                    pb.setIsSelected(true);
            }

            app.setPhoneMap(phoneBeanMap);

            CmdBean cb = app.getCmdMap().get(scriptID);
            tvRight.setText("执行脚本");
            if(cb.isSelected())
                tvRight.setText("停止执行");
        }

//        List<DataManager.DeviceGroupInfo> target = new ArrayList<>();


        viewSwitcher.setInAnimation(getHostActivity().getApplicationContext(), R.anim.umeng_fb_slide_in_from_left);
//        viewSwitcher.setOutAnimation(getHostActivity().getApplicationContext(), R.anim.umeng_fb_slide_out_from_right);


        recycleState1.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        recycleState2Left.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        recycleState2Right.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        devicesAdapter = new DevicesAdapter();
        groupAdapter = new GroupAdapter();
        yAdapter = new YAdapter();

        recycleState1.setAdapter(devicesAdapter);
        recycleState2Left.setAdapter(groupAdapter);
        recycleState2Right.setAdapter(yAdapter);

        tvRight.setVisibility(View.GONE);

        setClick();
        setPopOrFinish();
        getAllGroups();
//        getDeviceLsit(target);
    }

    private volatile boolean allGroupChoose = false;
    private volatile boolean allRowChoose = false;

    private void setClick() {
        llState1ChooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                llState1.setVisibility(View.GONE);
//                llState2.setVisibility(View.VISIBLE);
                viewSwitcher.setInAnimation(getHostActivity().getApplicationContext(), R.anim.umeng_fb_slide_in_from_left);
                viewSwitcher.setDisplayedChild(0);
                tvRight.setVisibility(View.INVISIBLE);
                showAllCount();
            }
        });

        llState2ChooseAllGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全选设备组
                for (DataManager.DeviceGroupInfo item : groupInfos) {
                    for (DataManager.DeviceYInfo inner : item.yInfos) {
                        inner.choose = !allGroupChoose;
                    }
                }
                groupAdapter.notifyDataSetChanged();
                yAdapter.notifyDataSetChanged();
                changeChooseAllState();
            }
        });

        llChooseAllRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全选当前选中组中的所有排
                for (DataManager.DeviceYInfo item : yInfos) {
                    item.choose = !allRowChoose;
                }
                yAdapter.notifyDataSetChanged();
                changeChooseAllState();
            }
        });

        tvState2Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DataManager.DeviceGroupInfo> target = new ArrayList<>();
                for (DataManager.DeviceGroupInfo out : groupInfos) {
                    DataManager.DeviceGroupInfo outItem = new DataManager.DeviceGroupInfo();
                    outItem.groupName = out.groupName;
                    outItem.yInfos = new ArrayList<>();
                    for (DataManager.DeviceYInfo inner : out.yInfos) {
                        if (inner.choose) {
                            outItem.yInfos.add(inner);
                        }
                    }
                    if (outItem.yInfos.size() > 0) {
                        target.add(outItem);
                    }
                }

                if (target.size() == 0) {
                    toast("请至少选择一组");
                    return;
                }
                getDeviceLsit(target);
//                llState1.setVisibility(View.VISIBLE);
//                llState2.setVisibility(View.GONE);
                viewSwitcher.setInAnimation(getHostActivity().getApplicationContext(), R.anim.umeng_fb_slide_in_from_right);
                viewSwitcher.setDisplayedChild(1);
                tvRight.setVisibility(View.VISIBLE);
            }
        });

        tvState2Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  llState2.setVisibility(View.GONE);
                llState1.setVisibility(View.VISIBLE);*/
                viewSwitcher.setInAnimation(getHostActivity().getApplicationContext(), R.anim.umeng_fb_slide_in_from_right);
                viewSwitcher.setDisplayedChild(1);
                tvRight.setVisibility(View.VISIBLE);
            }
        });

        tvState1ChooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DataManager.DeviceXInfo item : xInfos) {
                    item.choose = true;
                }
                App app = (App)getHostActivity().getApplicationContext();
                for (PhoneBean pb : app.getPhoneMap().values())
                {
                    pb.setIsSelected(true);
                }
                devicesAdapter.notifyDataSetChanged();
                showAllCount();
            }
        });

        tvState1ChooseReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DataManager.DeviceXInfo item : xInfos) {
                    item.choose = !item.choose;
                }
                App app = (App)getHostActivity().getApplicationContext();
                for (PhoneBean pb : app.getPhoneMap().values())
                {
                    pb.setIsSelected(!pb.isSelected());
                }
                devicesAdapter.notifyDataSetChanged();
                showAllCount();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                App app = (App)getHostActivity().getApplicationContext();

                Map<String ,PhoneBean> phoneSelectedMap = new HashMap<String ,PhoneBean>();
                ArrayList<DataManager.DeviceXInfo> next = new ArrayList<>();
                for (DataManager.DeviceXInfo item : xInfos) {
                    if (item.choose) {
                        next.add(item);
                    }

                    phoneSelectedMap.put(item.id,app.getPhoneMap().get(item.id));
                }

                app.setPhoneSelectedMap(phoneSelectedMap);


                if (!TextUtils.isEmpty(scriptID)) {

                    CmdBean cb = app.getCmdMap().get(scriptID);
                    if(cb.isSelected())
                    {
                        DataManager.masterStopScipt(getHostActivity().getApplicationContext(), scriptID,
                                new DataManager.DataCallBack<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                toast("停止执行成功");
                                                if (null != errorDialog && errorDialog.isShowing()) {
                                                    errorDialog.dismiss();
                                                }
                                                getHostActivity().finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(DataManager.FailData result) {
                                        toast(result.msg);
                                    }
                                });

                    }else
                    {
                        //todo 弹窗让用户 输入执行次数
                        showInputCountDialog(next);
                    }

                } else {
                    start(MasterAddScriptChooseApkFragment.newInstance(next));
                }

            }
        });

    }

    private Dialog errorDialog;
    private TextView tvTip;
    private EditText etInput;
    private TextView tvSure;
    private TextView tvCancle;

    private void showInputCountDialog(final ArrayList<DataManager.DeviceXInfo> next) {
        if (errorDialog == null) {
            errorDialog = new Dialog(getHostActivity(), R.style.dialog);
        }
        LayoutInflater inflater = LayoutInflater.from(getHostActivity());
        ViewGroup layout_mainLayout = (ViewGroup) inflater.inflate(
                R.layout.dialog_input, null);
        errorDialog.setContentView(layout_mainLayout);
        errorDialog.setCancelable(true);
        errorDialog.setCanceledOnTouchOutside(true);


        tvTip = (TextView) errorDialog.findViewById(R.id.tv_tip);
        etInput = (EditText) errorDialog.findViewById(R.id.et_input);
        tvSure = (TextView) errorDialog.findViewById(R.id.tv_sure);
        tvCancle = (TextView) errorDialog.findViewById(R.id.tv_cancle);

        tvTip.setText("执行次数");
        etInput.setText("1");
        etInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != errorDialog && errorDialog.isShowing()) {
                    errorDialog.dismiss();
                }
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etInput.getText())) {
                    toast("请输入执行次数");
                    return;
                }
                DataManager.masterStartScipt(getHostActivity().getApplicationContext(), next, scriptID, Integer.parseInt(etInput.getText().toString()),
                        new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast("开始执行成功");
                                        if (null != errorDialog && errorDialog.isShowing()) {
                                            errorDialog.dismiss();
                                        }
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

        if (!errorDialog.isShowing()) {
            errorDialog.show();
            etInput.requestFocus();
            showSoftInput(etInput);
        }
    }

    private void showAllCount() {
        if (llState2.getVisibility() == View.VISIBLE) {
            tvTitle.setText("选择设备");
        } else {
            int allcount = 0;
            for (DataManager.DeviceXInfo item : xInfos) {
                if (item.choose) {
                    allcount++;
                }
            }
            if (0 == allcount) {
                tvRight.setVisibility(View.GONE);
            } else {
                tvRight.setVisibility(View.VISIBLE);
            }
            tvTitle.setText("选择设备(" + allcount + ")");
        }
    }

    private void getAllGroups() {
        DataManager.masterGetAllGroups(getHostActivity().getApplicationContext(), new DataManager.DataCallBack<List<DataManager.DeviceGroupInfo>>() {
            @Override
            public void onSuccess(final List<DataManager.DeviceGroupInfo> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupInfos.clear();
                        groupInfos.addAll(result);
                        if (groupInfos.size() > 0) {
                            groupInfos.get(0).choose = true;
                            yInfos.clear();
                            yInfos.addAll(groupInfos.get(0).yInfos);
                            yAdapter.notifyDataSetChanged();
//                            List<DataManager.DeviceGroupInfo> defaultList = new ArrayList<>();
//                            defaultList.add(groupInfos.get(0));
//                            getDeviceLsit(defaultList);
                        }
                        groupAdapter.notifyDataSetChanged();
                        changeChooseAllState();
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData result) {

            }
        });
    }

    private void getDeviceLsit(List<DataManager.DeviceGroupInfo> defaultList) {
        tvState1GroupName.setText(Joiner.on(",").join(defaultList));
        DataManager.masterGetDevices(getHostActivity().getApplicationContext(), defaultList, scriptID, new DataManager.DataCallBack<List<DataManager.DeviceXInfo>>() {
            @Override
            public void onSuccess(final List<DataManager.DeviceXInfo> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 这里做了数据处理，之前选过的不会清掉选中状态
                        List<DataManager.DeviceXInfo> old = xInfos;
                        xInfos.clear();
                        xInfos.addAll(result);
                        for (DataManager.DeviceXInfo item : xInfos) {
                            cut:
                            for (DataManager.DeviceXInfo inner : old) {
                                if (item.id.equals(inner.id) && inner.choose) {
                                    break cut;
                                }
                            }
                        }
                        devicesAdapter.notifyDataSetChanged();
                        showAllCount();
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData result) {

            }
        });
    }

    private void changeChooseAllState() {
        allGroupChoose = true;
        for (DataManager.DeviceGroupInfo item : groupInfos) {
            for (DataManager.DeviceYInfo inner : item.yInfos) {
                if (!inner.choose) {
                    allGroupChoose = false;
                }
            }
        }
        ivState2All.setImageResource(allGroupChoose ? R.drawable.icon_check_on : R.drawable.choose_all_devices);
        allRowChoose = true;
        for (DataManager.DeviceYInfo inner : yInfos) {
            if (!inner.choose) {
                allRowChoose = false;
            }
        }
        ivAllRowsCheck.setImageResource(allRowChoose ? R.drawable.icon_check_on : R.drawable.icon_check_off);
    }

    private class DevicesAdapter extends RecyclerView.Adapter<VH_Devices> {

        @NonNull
        @Override
        public VH_Devices onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH_Devices(LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_choose_devices, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH_Devices vh_devices, int i) {
            final DataManager.DeviceXInfo item = xInfos.get(vh_devices.getAdapterPosition());
            vh_devices.tvName.setText(item.name);
            vh_devices.ivCheck.setImageResource(item.choose ? R.drawable.icon_check_on : R.drawable.icon_check_off);

            final App app = (App)getHostActivity().getApplicationContext();
            vh_devices.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if( !TextUtils.isEmpty(scriptID))
                    {
                        if(!TextUtils.isEmpty(app.getPhoneMap().get(item.id).getRunCmdID())&&!app.getPhoneMap().get(item.id).getRunCmdID().equals(scriptID))
                        {
                            toast("设备正在执行脚本任务");
                            return;

                        }
                    }

                    item.choose = !item.choose;

                    app.getPhoneMap().get(item.id).setIsSelected(item.choose);




                    notifyDataSetChanged();
                    changeChooseAllState();
                    showAllCount();
                }
            });
        }

        @Override
        public int getItemCount() {
            return xInfos.size();
        }
    }

    private class GroupAdapter extends RecyclerView.Adapter<VH_Devices> {

        @NonNull
        @Override
        public VH_Devices onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH_Devices(LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_group_left, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH_Devices vh_devices, final int i) {
            final DataManager.DeviceGroupInfo item = groupInfos.get(vh_devices.getAdapterPosition());
            vh_devices.tvName.setText(item.groupName);
            vh_devices.llContainer.setBackgroundColor(item.choose ? Color.parseColor("#cccccc") : Color.parseColor("#f6f6f6"));
            vh_devices.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (DataManager.DeviceGroupInfo temp : groupInfos) {
                        temp.choose = false;
                    }
                    item.choose = true;
                    yInfos.clear();
                    yInfos.addAll(item.yInfos);
                    notifyDataSetChanged();
                    yAdapter.notifyDataSetChanged();
                    changeChooseAllState();
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupInfos.size();
        }
    }

    private class YAdapter extends RecyclerView.Adapter<VH_Devices> {

        @NonNull
        @Override
        public VH_Devices onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH_Devices(LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_group_right, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH_Devices vh_devices, int i) {
            final DataManager.DeviceYInfo item = yInfos.get(vh_devices.getAdapterPosition());
            vh_devices.tvName.setText("第" + item.y + "排");
            vh_devices.ivCheck.setImageResource(item.choose ? R.drawable.icon_check_on : R.drawable.icon_check_off);
            vh_devices.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.choose = !item.choose;
                    notifyDataSetChanged();
                    changeChooseAllState();
                }
            });
        }

        @Override
        public int getItemCount() {
            return yInfos.size();
        }
    }

    private static class VH_Devices extends RecyclerView.ViewHolder {
        LinearLayout llContainer;
        TextView tvName;
        ImageView ivCheck;


        public VH_Devices(@NonNull View itemView) {
            super(itemView);
            llContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivCheck = (ImageView) itemView.findViewById(R.id.iv_check);

        }
    }
}
