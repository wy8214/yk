package com.bee.yunkong.fragment.master.queuetask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
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
import com.bee.yunkong.activity.MainActivity;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.addscript.MasterAddScriptChooseDevicesFragment;
import com.bee.yunkong.fragment.master.home.MasterQueueTaskListfragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;
import com.bumptech.glide.Glide;
import com.hwytapp.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MasterQueueTaskItemFragment extends BaseFragment {

    private static final String DATASTRING = "DATASTRING";

    private  DataManager.QueueTaskInfo qtInfo;
    public static MasterQueueTaskItemFragment newInstance(DataManager.QueueTaskInfo qtInfo) {


        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, qtInfo);
        MasterQueueTaskItemFragment fragment = new MasterQueueTaskItemFragment();
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
    private MasterQueueTaskItemFragment.MyAdapter adapter;
    private List<DataManager.QueueTaskItem> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qtInfo = (DataManager.QueueTaskInfo)getArguments().getSerializable(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        listView = (ListView) findViewById(R.id.list_view);

        ivLeft.setVisibility(View.GONE);
        tvTitle.setText("返回");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP , 16);
        tvTitle.setTextColor(Color.parseColor("#666666"));
        registerForContextMenu(listView);
        adapter = new MasterQueueTaskItemFragment.MyAdapter();
        listView.setAdapter(adapter);

        tvTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getHostActivity().getApplicationContext(), MainActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startFragmentAcitivty(MasterAddScriptChooseDevicesFragment.newInstance(data.get(position).ID));
//            }
//        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAcitivty(MasterChooseQueueTaskApkFragment.newInstance(qtInfo));
            }
        });

        getData();
    }

    private void getData() {
        DataManager.getQueueTaskItemList(getHostActivity().getApplicationContext(),qtInfo,new DataManager.DataCallBack<List<DataManager.QueueTaskItem>>() {
            @Override
            public void onSuccess(final List<DataManager.QueueTaskItem> result) {
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
            DataManager.QueueTaskItem info = data.get(menuInfo1.position);
            //todo 这里根据状态来动态生成 弹窗内容

            menu.add(0, 1, Menu.NONE, "编辑");
            menu.add(0, 2, Menu.NONE, "删除队列");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                MyLog.d(menuInfo.position);
                DataManager.QueueTaskItem info = data.get(menuInfo.position);
                startFragmentAcitivty(MasterEditQueueTaskItemFragment.newInstance(info));
                break;
            case 2:
                MyLog.d(menuInfo.position);
                DataManager.deleteQueueTaskItem(getHostActivity().getApplicationContext(),data.get(menuInfo.position).ID,
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

        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_queue_task_item_changed:
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
            MasterQueueTaskItemFragment.ViewHolder vh;
            if (view == null) {
                vh = new MasterQueueTaskItemFragment.ViewHolder();
                view = LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_task_list, null);
                vh.tv_l_1 = (TextView) view.findViewById(R.id.tv_l_1);
                vh.tv_l_2 = (TextView) view.findViewById(R.id.tv_l_2);
                vh.tv_l_3 = (TextView) view.findViewById(R.id.tv_l_3);
                vh.tv_l_4 = (TextView) view.findViewById(R.id.tv_l_4);
                vh.tv_l_5 = (TextView) view.findViewById(R.id.tv_l_5);
                vh.iv = (ImageView) view.findViewById(R.id.iv);
                view.setTag(vh);
            } else {
                vh = (MasterQueueTaskItemFragment.ViewHolder) view.getTag();
            }
            DataManager.QueueTaskItem item = data.get(i);
            Glide.with(getHostActivity().getApplicationContext())
                    .load("")
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .into(vh.iv);
            vh.tv_l_1.setText(item.apkName);
            vh.tv_l_2.setText(item.CreatedAt);
            vh.tv_l_3.setText("");
            vh.tv_l_4.setText("");
            vh.tv_l_5.setText("执行"+item.execTime+"分钟");//todo 这里填数据


            if(null!=item.logoUrl&&!item.logoUrl.equals("")){
                Drawable drawable = AppUtils.getApkIcon(getHostActivity().getApplicationContext(),item.logoUrl);
                vh.iv.setImageDrawable(drawable);
            }else {
                vh.iv.setImageResource(R.drawable.icon_apk);
            }
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