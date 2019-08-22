package com.bee.yunkong.fragment.master.queuetask;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
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
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.MasterAddApkFragment;
import com.bee.yunkong.fragment.master.home.MasterAppListfragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Bean.ApkBean;
import com.hwytapp.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;


public class MasterChooseQueueTaskApkFragment extends BaseFragment {
    private static final String TYPESTRING = "TYPESTRING";
    private static final String DATASTRING = "DATASTRING";
    private boolean isX = false;
    private int input = 1;

    private DataManager.QueueTaskInfo qtInfo;

    public static MasterChooseQueueTaskApkFragment newInstance(DataManager.QueueTaskInfo qtInfo) {

        Bundle args = new Bundle();
        args.putSerializable(DATASTRING, qtInfo);
        MasterChooseQueueTaskApkFragment fragment = new MasterChooseQueueTaskApkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_app_list;
    }

    private LinearLayout llTitleLeft;
    private ImageView ivLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private TextView tvNoData;
    private ListView listView;
    private MasterChooseQueueTaskApkFragment.MyAdapter adapter;
    private List<DataManager.ApkBean> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qtInfo = (DataManager.QueueTaskInfo)getArguments().getSerializable(DATASTRING);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
//        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        listView = (ListView) findViewById(R.id.list_view);

        tvTitle.setText(tvTitle.getText()+"选择APP");

        adapter = new MasterChooseQueueTaskApkFragment.MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toast(data.get(i).appName);

                App app= (App)getHostActivity().getApplicationContext();

                ApkBean apk = app.getApkMap().get(data.get(i).apkID);

                DataManager.QueueTaskItem qti = new DataManager.QueueTaskItem();
                qti.apkName = apk.getApkName();
                qti.packageName = apk.getApkPackageName();
                qti.queueTaskID = qtInfo.ID;

                startFragmentAcitivty(MasterEditQueueTaskItemFragment.newInstance(qti));
            }
        });

        getData();
    }

    @Override
    public void onMainThreadEvent(MyEvent noEvent) {
        super.onMainThreadEvent(noEvent);
        switch (noEvent.getTag()) {
            case master_change_add_apks:
                getData();
                break;
        }
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

    private Dialog errorDialog;
    private TextView tvCancle;
    private TextView tvSure;


    private void showLoginErrorDialog(final String apkID,final String action ,String tip) {
        if (errorDialog == null) {
            errorDialog = new Dialog(getHostActivity(), R.style.dialog);
        }
        LayoutInflater inflater = LayoutInflater.from(getHostActivity());
        ViewGroup layout_mainLayout = (ViewGroup) inflater.inflate(
                R.layout.dialog_confirm, null);
        errorDialog.setContentView(layout_mainLayout);
        errorDialog.setCancelable(false);
        errorDialog.setCanceledOnTouchOutside(false);
        TextView tv_tip = errorDialog.findViewById(R.id.tv_tip);
        tv_tip.setText(tip);
        tvCancle = (TextView) errorDialog.findViewById(R.id.tv_cancle);
        tvSure = (TextView) errorDialog.findViewById(R.id.tv_sure);

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
                if (null != errorDialog && errorDialog.isShowing()) {
                    DataManager.masterDeleteApk(getHostActivity().getApplicationContext(), action ,apkID, new DataManager.DataCallBack<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorDialog.dismiss();
                                    toast("操作成功");
                                    getData();
                                }
                            });
                        }

                        @Override
                        public void onFail(DataManager.FailData result) {
                            toast(result.msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorDialog.dismiss();
                                }
                            });
                        }
                    });

                }
            }
        });

        if (!errorDialog.isShowing()) {
            errorDialog.show();
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
            MasterChooseQueueTaskApkFragment.ViewHolder vh;
            if (view == null) {
                vh = new MasterChooseQueueTaskApkFragment.ViewHolder();
                view = LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_app_list, null);
                vh.tv_l_1 = (TextView) view.findViewById(R.id.tv_l_1);
                vh.tv_l_2 = (TextView) view.findViewById(R.id.tv_l_2);
                vh.tv_r_1 = (TextView) view.findViewById(R.id.tv_r_1);
                vh.tv_r_2 = (TextView) view.findViewById(R.id.tv_r_2);
                vh.iv = (ImageView) view.findViewById(R.id.iv);
                view.setTag(vh);
            } else {
                vh = (MasterChooseQueueTaskApkFragment.ViewHolder) view.getTag();
            }
            DataManager.ApkBean item = data.get(i);
//            Glide.with(getHostActivity().getApplicationContext())
//                    .load(item.logoUrl).into(vh.iv);
            vh.tv_l_1.setText(item.appName);
            vh.tv_l_2.setText("");
            vh.tv_r_1.setVisibility(View.VISIBLE);
            vh.tv_r_2.setVisibility(View.VISIBLE);
            vh.tv_r_1.setText("");
            vh.tv_r_2.setText("");


            if(null!=item.logoUrl){
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
        public TextView tv_r_1;
        public TextView tv_r_2;
        public ImageView iv;
    }
}