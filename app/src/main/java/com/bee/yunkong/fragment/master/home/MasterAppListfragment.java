package com.bee.yunkong.fragment.master.home;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.HostNfcFService;
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
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.MasterAddApkFragment;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class MasterAppListfragment extends BaseFragment {
    public static MasterAppListfragment newInstance() {

        Bundle args = new Bundle();

        MasterAppListfragment fragment = new MasterAppListfragment();
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
    private MyAdapter adapter;
    private List<DataManager.ApkBean> data = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        listView = (ListView) findViewById(R.id.list_view);

        registerForContextMenu(listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showLoginErrorDialog();
            }
        });*/

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("应用列表正在打开，请等待...");
                startFragmentAcitivty(MasterAddApkFragment.newInstance());
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == listView) {
            MenuInflater inflater = getHostActivity().getMenuInflater();
            inflater.inflate(R.menu.meun_app, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.reinstall:
                MyLog.d(menuInfo.position);
                toast("重新安装");
                DataManager.masterReinstallApk(getHostActivity().getApplicationContext(),data.get(menuInfo.position).apkID, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        toast("操作成功");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
            case R.id.uninstall:
                MyLog.d(menuInfo.position);
                toast("卸载并删除");
                showLoginErrorDialog(data.get(menuInfo.position).apkID,"uninstall","是否确定卸载并删除\n当前应用");
                break;
            case R.id.deleteFile:
                MyLog.d(menuInfo.position);
                toast("删除文件");
                showLoginErrorDialog(data.get(menuInfo.position).apkID,"delete","是否确定删除应用文件");
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
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            DataManager.ApkBean item = data.get(i);
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
