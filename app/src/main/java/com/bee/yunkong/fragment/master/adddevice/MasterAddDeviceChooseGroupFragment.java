package com.bee.yunkong.fragment.master.adddevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;

import java.util.ArrayList;
import java.util.List;

public class MasterAddDeviceChooseGroupFragment extends BaseFragment {
    private static final String DATASTRING = "DATASTRING";
    private String currenGroupName;

    public static MasterAddDeviceChooseGroupFragment newInstance(String currenGroupName) {

        Bundle args = new Bundle();
        args.putString(DATASTRING, currenGroupName);
        MasterAddDeviceChooseGroupFragment fragment = new MasterAddDeviceChooseGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_device_choose_group;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private ListView listView;
    private MyAdapter adapter;
    private List<DataManager.GroupInfo> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currenGroupName = getArguments().getString(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        listView = (ListView) findViewById(R.id.list_view);
        setPopOrFinish();
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(MasterAddDeviceAddGroupFragment.newInstance(true, "", "",""));
            }
        });
        registerForContextMenu(listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        getData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currenGroupName = data.get(position).name;
                adapter.notifyDataSetChanged();
                MyEvent event = new MyEvent(EventTag.master_change_group);
                event.setObject(currenGroupName);
                postEvent(event);
                pop();
            }
        });
    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_change_add_group:
                getData();
                break;
        }
    }


    private void getData() {
        DataManager.getMasterGroups(getHostActivity().getApplicationContext(), new DataManager.DataCallBack<List<DataManager.GroupInfo>>() {
            @Override
            public void onSuccess(final List<DataManager.GroupInfo> result) {
                if (null != result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            data.clear();
                            data.addAll(result);
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }

            @Override
            public void onFail(DataManager.FailData data) {
                toast(data.msg);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == listView) {
            MenuInflater inflater = getHostActivity().getMenuInflater();
            inflater.inflate(R.menu.meun_group, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        App app = (App) getHostActivity().getApplicationContext();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                MyLog.d(menuInfo.position);
                toast("编辑");
                start(MasterAddDeviceAddGroupFragment.newInstance(false, data.get(menuInfo.position).name, data.get(menuInfo.position).groupID,data.get(menuInfo.position).code));
                break;
            case R.id.delete:


                MyLog.d(menuInfo.position);
                //todo 调用删除接口
                toast("删除");
                DataManager.masterdeleteGroup(app, data.get(menuInfo.position).groupID, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toast("删除成功");
                                getData();
                            }
                        });
                    }

                    @Override
                    public void onFail(DataManager.FailData data) {
                        toast(data.msg);
                    }
                });
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
                view = LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_group, null);
                vh.tv_name = (TextView) view.findViewById(R.id.tv_name);
                vh.tv_code = (TextView) view.findViewById(R.id.tv_code);
                vh.iv_check = (ImageView) view.findViewById(R.id.iv_check);
                vh.view_line = (View) view.findViewById(R.id.view_line);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            final DataManager.GroupInfo item = data.get(i);
            vh.tv_name.setText(item.name);
            vh.tv_code.setText(item.code);
            vh.iv_check.setVisibility(item.name.equals(currenGroupName) ? View.VISIBLE : View.INVISIBLE);
            if (i == getCount() - 1) {
                vh.view_line.setVisibility(View.GONE);
            } else {
                vh.view_line.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    public final class ViewHolder {
        public TextView tv_name;
        public TextView tv_code;
        public ImageView iv_check;
        public View view_line;
    }

}
