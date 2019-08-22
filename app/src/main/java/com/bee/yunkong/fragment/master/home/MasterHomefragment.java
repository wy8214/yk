package com.bee.yunkong.fragment.master.home;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.adddevice.MasterAddDeviceFragment;
import com.bee.yunkong.network.DataManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MasterHomefragment extends BaseFragment implements OnRefreshListener {
    public static MasterHomefragment newInstance() {

        Bundle args = new Bundle();

        MasterHomefragment fragment = new MasterHomefragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_home;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvNoData;
    private SmartRefreshLayout inquireSrl;
    private RecyclerView recycle;
    List<DataManager.DeviceListBean> data = new ArrayList<>();
    private MyAdapter adapter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        inquireSrl = (SmartRefreshLayout) findViewById(R.id.inquire_srl);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        inquireSrl.setOnRefreshListener(this);
        inquireSrl.setEnableLoadMore(false);
        adapter = new MyAdapter();
        recycle.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        recycle.setAdapter(adapter);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAcitivty(MasterAddDeviceFragment.newInstance());
            }
        });
        getData();
    }

    private void getData() {
        DataManager.getAllDevices(getHostActivity().getApplicationContext(), new DataManager.DataCallBack<List<DataManager.DeviceListBean>>() {
            @Override
            public void onSuccess(final List<DataManager.DeviceListBean> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != result) {
                            data.clear();
                            data.addAll(result);
                            inquireSrl.finishRefresh();
                            adapter.notifyDataSetChanged();
                            if (data.size() == 0) {
                                recycle.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
                            } else {
                                recycle.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                            }
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

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()){
            case master_device_list_changed:

                getData();
                //todo 更新数据
                break;
            case master_device_item_changed:
                List<String> changed = new ArrayList<>();
                changed = (List<String>) noEvent.getObject();
                //todo 单体数据发生变化
                break;

            default:
                break;
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData();
    }

    private class MyAdapter extends RecyclerView.Adapter<VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH(LayoutInflater.from(getHostActivity())
                    .inflate(R.layout.adapter_device_list, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            final DataManager.DeviceListBean item = data.get(vh.getAdapterPosition());
            vh.tvName.setText(item.name);
            vh.tvPhonenum.setText(TextUtils.isEmpty(item.phone)?"":item.phone);
            vh.rlContainer.setBackgroundColor(item.error ? Color.parseColor("#F8D5D5")
                    : Color.parseColor("#ffffff"));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class VH extends RecyclerView.ViewHolder {
        RelativeLayout rlContainer;
        TextView tvName;
        TextView tvPhonenum;
        View viewLine;

        public VH(@NonNull View itemView) {
            super(itemView);
            rlContainer = (RelativeLayout) itemView.findViewById(R.id.rl_container);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhonenum = (TextView) itemView.findViewById(R.id.tv_phonenum);
            viewLine = (View) itemView.findViewById(R.id.view_line);

        }
    }
}
