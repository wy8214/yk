package com.bee.yunkong.fragment.master.addscript;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.fragment.master.home.MasterAppListfragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class MasterAddScriptChooseApkFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private List<DataManager.DeviceXInfo> deviceXInfos;

    public static MasterAddScriptChooseApkFragment newInstance(ArrayList<DataManager.DeviceXInfo> deviceXInfos) {

        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, deviceXInfos);
        MasterAddScriptChooseApkFragment fragment = new MasterAddScriptChooseApkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_scrpit_choose_apk;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private RecyclerView recycle;
    private ListView listView;
    private TextView tvNoData;

    private List<DataManager.ApkBean> data = new ArrayList<>();
    private MyAdapter adapter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceXInfos = (List<DataManager.DeviceXInfo>) getArguments().getSerializable(DATASTRING);
        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        listView = (ListView) findViewById(R.id.list_view);

//        registerForContextMenu(recycle);
//        adapter = new MyAdapter();
//        recycle.setAdapter(adapter);
//        recycle.setLayoutManager(new LinearLayoutManager(getHostActivity()));

        registerForContextMenu(listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DataManager.ApkBean item = data.get(position);
                DataManager.masterStartRecordScrpit(getHostActivity().getApplicationContext(), deviceXInfos, item, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getHostActivity().finish();
                                postEvent(EventTag.master_start_record);
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

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.masterStartRecordScrpit(getHostActivity().getApplicationContext(), deviceXInfos, null, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getHostActivity().finish();
                                postEvent(EventTag.master_start_record);
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


        setPopOrFinish();

        getData();
    }

    private void getData() {
        DataManager.masterGetApkList(getHostActivity().getApplicationContext(), new DataManager.DataCallBack<List<DataManager.ApkBean>>() {
            @Override
            public void onSuccess(final List<DataManager.ApkBean> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        data.addAll(result);
                        adapter.notifyDataSetChanged();
                        if (data.size() == 0) {
                            listView.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            tvNoData.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData result) {
                toast(result.msg);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == listView) {
            MenuInflater inflater = getHostActivity().getMenuInflater();
            inflater.inflate(R.menu.meun_app_script, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.autorun:
                MyLog.d(menuInfo.position);
                toast(data.get(menuInfo.position).apkID);
                toast("自动阅读");
                break;
        }
        return super.onContextItemSelected(item);

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if (view == null) {
                vh = new ViewHolder();
                view = LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_app_list, null);
                vh.tv_l_1 = (TextView) view.findViewById(R.id.tv_l_1);
                vh.tv_l_2 = (TextView) view.findViewById(R.id.tv_l_2);
                vh.tv_r_1 = (TextView) view.findViewById(R.id.tv_r_1);
                vh.tv_r_2 = (TextView) view.findViewById(R.id.tv_r_2);
                vh.iv = (ImageView) view.findViewById(R.id.iv);
                vh.ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            final DataManager.ApkBean item = data.get(i);
//            Glide.with(getHostActivity().getApplicationContext())
//                    .load(item.logoUrl).into(vh.iv);
            vh.tv_l_1.setText(item.appName);
            vh.tv_l_2.setText(item.stateName);
            if (item.state == 1) {
                vh.tv_r_2.setVisibility(View.GONE);
                vh.tv_r_1.setVisibility(View.VISIBLE);
                vh.tv_r_1.setText(item.uploadProgress);
            } else if (item.state == 2) {
                vh.tv_r_2.setVisibility(View.VISIBLE);
                vh.tv_r_1.setVisibility(View.VISIBLE);
                vh.tv_r_1.setText(item.installProgress);
                vh.tv_r_2.setText("设备已安装");
            } else {
                vh.tv_r_1.setVisibility(View.GONE);
                vh.tv_r_2.setVisibility(View.GONE);
            }


            if (null != item.logoUrl) {
                Drawable drawable = AppUtils.getApkIcon(getHostActivity().getApplicationContext(), item.logoUrl);
                vh.iv.setImageDrawable(drawable);
            } else {
                vh.iv.setImageResource(R.drawable.icon_apk);
            }


            return view;
        }
    }

    public final class ViewHolder {
        public LinearLayout ll_container;
        public TextView tv_l_1;
        public TextView tv_l_2;
        public TextView tv_r_1;
        public TextView tv_r_2;
        public ImageView iv;
    }
}
