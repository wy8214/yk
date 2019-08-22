package com.bee.yunkong.fragment.controlled;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.common.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class ControlledChooseGroupFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private DataManager.ControlledDeviceInfo deviceInfo;

    public static ControlledChooseGroupFragment newInstance(DataManager.ControlledDeviceInfo deviceInfo) {

        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, deviceInfo);
        ControlledChooseGroupFragment fragment = new ControlledChooseGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_controlled_choose_group;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private RecyclerView recycle;
    private MyAdapter adapter;
    private List<DataManager.GroupInfo> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceInfo = (DataManager.ControlledDeviceInfo) getArguments().getSerializable(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        setPopOrFinish();
        adapter = new MyAdapter();
        recycle.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        recycle.setAdapter(adapter);
        DataManager.getGroups(getHostActivity().getApplicationContext(),DeviceUtil.getImei(getHostActivity()),
                new DataManager.DataCallBack<List<DataManager.GroupInfo>>() {
                    @Override
                    public void onSuccess(final List<DataManager.GroupInfo> infos) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                data.clear();
                                data.addAll(infos);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFail(DataManager.FailData data) {
                        toast(data.msg);
                    }
                });
    }

    private class MyAdapter extends RecyclerView.Adapter<VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH(LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_group,
                    viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            final DataManager.GroupInfo item = data.get(vh.getAdapterPosition());
            vh.tvName.setText(item.name);
            vh.tvCode.setText(item.code);
            vh.ivCheck.setVisibility(item.name.equals(deviceInfo.groupName) ? View.VISIBLE : View.INVISIBLE);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyEvent event = new MyEvent(EventTag.controlled_change_group);
                    event.setObject(item.name);
                    postEvent(event);
                    pop();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class VH extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvCode;
        ImageView ivCheck;
        View viewLine;

        public VH(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvCode = (TextView) itemView.findViewById(R.id.tv_code);

            ivCheck = (ImageView) itemView.findViewById(R.id.iv_check);
            viewLine = (View) itemView.findViewById(R.id.view_line);

        }
    }
}
