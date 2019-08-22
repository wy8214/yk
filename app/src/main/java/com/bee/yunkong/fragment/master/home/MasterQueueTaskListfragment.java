package com.bee.yunkong.fragment.master.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.addscript.MasterQueueTaskChooseDevicesFragment;
import com.bee.yunkong.fragment.master.queuetask.MasterEditQueueTaskFragment;
import com.bee.yunkong.fragment.master.queuetask.MasterQueueTaskItemFragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;



public class MasterQueueTaskListfragment extends BaseFragment {
    public static MasterQueueTaskListfragment newInstance() {

        Bundle args = new Bundle();

        MasterQueueTaskListfragment fragment = new MasterQueueTaskListfragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_queue_task_list;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private ListView listView;
    private MyAdapter adapter;
    private List<DataManager.QueueTaskInfo> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        listView = (ListView) findViewById(R.id.list_view);

//        ivRight.setVisibility(View.GONE);

        tvTitle.setText(tvTitle.getText()+"任务列表");
        registerForContextMenu(listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(data.get(position).status.equals(""))
                {
                    startFragmentAcitivty(MasterQueueTaskItemFragment.newInstance(data.get(position)));
                }else {
                    toast("任务正在执行，不能进行编辑");
                }

            }
        });


        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataManager.QueueTaskInfo qtInfo = new DataManager.QueueTaskInfo();
                startFragmentAcitivty(MasterEditQueueTaskFragment.newInstance(qtInfo));
            }
        });
        getData();
    }

    private void getData() {
        DataManager.getQueueTaskList(getHostActivity().getApplicationContext(),new DataManager.DataCallBack<List<DataManager.QueueTaskInfo>>() {
            @Override
            public void onSuccess(final List<DataManager.QueueTaskInfo> result) {
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
            DataManager.QueueTaskInfo info = data.get(menuInfo1.position);
            //todo 这里根据状态来动态生成 弹窗内容


            if(info.status.equals("")){
                menu.add(0, 4, Menu.NONE, "执行任务");
                menu.add(0, 1, Menu.NONE, "编辑名称");
                menu.add(0, 5, Menu.NONE, "选择设备");
                menu.add(0, 2, Menu.NONE, "删除任务");
            }else {
                menu.add(0, 6, Menu.NONE, "停止执行");
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
                DataManager.QueueTaskInfo info = data.get(menuInfo.position);
                startFragmentAcitivty(MasterEditQueueTaskFragment.newInstance(info));
                break;
            case 2:
                MyLog.d(menuInfo.position);
                toast("删除脚本");
                DataManager.deleteQueueTask(getHostActivity().getApplicationContext(),data.get(menuInfo.position).ID,
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
                DataManager.masterStopScipt(getHostActivity().getApplicationContext(),data.get(menuInfo.position).ID,
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

            case 4:
                DataManager.execQueueTask(getHostActivity().getApplicationContext(),data.get(menuInfo.position).ID,
                        new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast("执行成功");
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
            case 5:
                MyLog.d(menuInfo.position);
                startFragmentAcitivty(MasterQueueTaskChooseDevicesFragment.newInstance(data.get(menuInfo.position).ID));
                break;

            case 6:
                DataManager.stopQueueTask(getHostActivity().getApplicationContext(),data.get(menuInfo.position).ID,
                        new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast("停止执行成功");
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
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_queue_task_list_changed:
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
                vh.tv_l_5 = (TextView) view.findViewById(R.id.tv_l_5);
                vh.iv = (ImageView) view.findViewById(R.id.iv);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            DataManager.QueueTaskInfo item = data.get(i);
            Glide.with(getHostActivity().getApplicationContext())
                    .load("")
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .into(vh.iv);
            vh.tv_l_1.setText(item.taskName);
            vh.tv_l_2.setText(item.createTime);
            vh.tv_l_3.setText("");
            vh.tv_l_4.setText(item.status);//todo 这里填数据
            vh.tv_l_5.setText(item.taskNum);



            return view;
        }
    }

    public final class ViewHolder {
        public TextView tv_l_1;
        public TextView tv_l_2;
        public TextView tv_l_3;
        public TextView tv_l_4;
        public TextView tv_l_5;
        public ImageView iv;
    }
}