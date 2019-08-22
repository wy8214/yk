package com.bee.yunkong.fragment.master.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.addscript.MasterAddScriptChooseDevicesFragment;
import com.bee.yunkong.fragment.master.task.MasterEditTaskFragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;
import com.bumptech.glide.Glide;
import com.hwytapp.Bean.CmdBean;

import java.util.ArrayList;
import java.util.List;

public class MasterTaskListfragment extends BaseFragment {
    public static MasterTaskListfragment newInstance() {

        Bundle args = new Bundle();

        MasterTaskListfragment fragment = new MasterTaskListfragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_task_list;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private ListView listView;
    private MyAdapter adapter;
    private List<DataManager.TaskInfo> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        listView = (ListView) findViewById(R.id.list_view);

        ivRight.setVisibility(View.GONE);
        registerForContextMenu(listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startFragmentAcitivty(MasterAddScriptChooseDevicesFragment.newInstance(data.get(position).taskId));
            }
        });
        getData();
    }

    private void getData() {
        DataManager.masterGetTaskList(getHostActivity().getApplicationContext(),new DataManager.DataCallBack<List<DataManager.TaskInfo>>() {
            @Override
            public void onSuccess(final List<DataManager.TaskInfo> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        data.addAll(result);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData result) {

            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == listView) {
            AdapterView.AdapterContextMenuInfo menuInfo1 = (AdapterView.AdapterContextMenuInfo) menuInfo;
            DataManager.TaskInfo info = data.get(menuInfo1.position);
            //todo 这里根据状态来动态生成 弹窗内容
            if(info.status.equals("")){
                menu.add(0, 1, Menu.NONE, "编辑名称");
                menu.add(0, 2, Menu.NONE, "删除脚本");
            }else {
                menu.add(0, 1, Menu.NONE, "编辑名称");
                menu.add(0, 3, Menu.NONE, "停止脚本");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                MyLog.d(menuInfo.position);
                toast("编辑名称");
                DataManager.TaskInfo info = data.get(menuInfo.position);
                startFragmentAcitivty(MasterEditTaskFragment.newInstance(info.taskCode, info.taskName, info.taskId));
                break;
            case 2:
                MyLog.d(menuInfo.position);
                toast("删除脚本");
                DataManager.masterDeleteTask(getHostActivity().getApplicationContext(),data.get(menuInfo.position).taskId,
                        new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast("删除成功");
                                        getData();
                                    }
                                });
                            }

                            @Override
                            public void onFail(DataManager.FailData result) {
                                toast(result.msg);
                            }
                        });
                break;
            case 3:
                MyLog.d(menuInfo.position);
                DataManager.masterStopScipt(getHostActivity().getApplicationContext(),data.get(menuInfo.position).taskId,
                        new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        toast("停止成功");
                                        getData();
                                    }
                                });
                            }

                            @Override
                            public void onFail(DataManager.FailData result) {
                                toast(result.msg);
                            }
                        });
                //todo  这里执行停止的逻辑  代码和 case2中一样写就行
                break;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_change_tasks:
                getData();
                break;
        }
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
                view = LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_task_list, null);
                vh.tv_l_1 = (TextView) view.findViewById(R.id.tv_l_1);
                vh.tv_l_2 = (TextView) view.findViewById(R.id.tv_l_2);
                vh.tv_l_3 = (TextView) view.findViewById(R.id.tv_l_3);
                vh.tv_l_4 = (TextView) view.findViewById(R.id.tv_l_4);
                vh.iv = (ImageView) view.findViewById(R.id.iv);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            DataManager.TaskInfo item = data.get(i);
            Glide.with(getHostActivity().getApplicationContext())
                    .load(item.taskImageUrl)
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .into(vh.iv);
            vh.tv_l_1.setText(item.taskName);
            vh.tv_l_2.setText(item.taskAddTime);
            vh.tv_l_3.setText(item.status);
            vh.tv_l_4.setText("");//todo 这里填数据
            return view;
        }
    }

    public final class ViewHolder {
        public TextView tv_l_1;
        public TextView tv_l_2;
        public TextView tv_l_3;
        public TextView tv_l_4;
        public ImageView iv;
    }
}
