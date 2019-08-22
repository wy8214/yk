package com.bee.yunkong.fragment.master;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;
import com.bee.yunkong.network.DataManager;
import com.hwytapp.Common.Config;
import com.hwytapp.Utils.AppUtils;

import net.dongliu.apk.parser.bean.ApkMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.bee.yunkong.core.EventTag.master_change_add_apks;

public class MasterAddApkFragment extends BaseFragment {
    public static MasterAddApkFragment newInstance() {

        Bundle args = new Bundle();

        MasterAddApkFragment fragment = new MasterAddApkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_master_add_apk;
    }

    private LinearLayout llTitleLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private RecyclerView recycle;
    private File dir = new File(Config.APKDIR);
    private List<String> types = new ArrayList<>();
    private List<FileChoose> data = new ArrayList<>();
    private MyAdapter adapter;

    private static class FileChoose {
        public File file;
        public String version;
        public boolean choose;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        recycle.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        adapter = new MyAdapter();
        recycle.setAdapter(adapter);
        setPopOrFinish();
        DataManager.masterGetFileType(new DataManager.DataCallBack<List<String>>() {
            @Override
            public void onSuccess(final List<String> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        types.clear();
                        types.addAll(result);
                        getData();
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData result) {
                toast(result.msg);
            }
        });

        tvRight.setEnabled(true);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvRight.setEnabled(false);
                toast("文件正在上传，请等待...");
                List<File> upload = new ArrayList<>();
                for (FileChoose item : data) {
                    if (item.choose) {
                        upload.add(item.file);
                    }
                }
                if (upload.size() == 0) {
                    toast("请选择至少一个文件");
                    return;
                }
                DataManager.masterUploadApks(getHostActivity().getApplicationContext(),upload, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postEvent(master_change_add_apks);
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
    }

    private void getData() {
        if (types.size() == 0) {
            return;
        }

        if (dir.exists()) {
            File[] files = dir.listFiles();
            data.clear();
            for (File item : files) {
                if (has(item, types)) {

                    ApkMeta am  = App.getApkInfoFromApkFile(item.getPath());
                    String version = am.getVersionName();
                    FileChoose temp = new FileChoose();
                    temp.version = version;
                    temp.file = item;
                    temp.choose = false;
                    data.add(temp);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private boolean has(File file, List<String> types) {
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        boolean has = false;
        for (String item : types) {
            if (item.equals(suffix)) {
                has = true;
                break;
            }
        }
        return has;
    }

    private class MyAdapter extends RecyclerView.Adapter<VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH(LayoutInflater.from(getHostActivity()).inflate(R.layout.adapter_add_apk, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            final FileChoose item = data.get(vh.getAdapterPosition());
            vh.tvName.setText(item.file.getName()+ item.version);
            if (item.choose) {
                vh.ivCheck.setImageResource(R.drawable.icon_check_on);
            } else {
                vh.ivCheck.setImageResource(R.drawable.icon_check_off);
            }
            Drawable drawable = AppUtils.getApkIcon(getHostActivity().getApplicationContext(),item.file.getAbsolutePath());
            if(null!=drawable){
                vh.iv_icon.setImageDrawable(drawable);
            }else {
                vh.iv_icon.setImageResource(R.drawable.icon_apk);
            }
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.choose = !item.choose;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class VH extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView ivCheck;
        private ImageView iv_icon;

        public VH(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivCheck = (ImageView) itemView.findViewById(R.id.iv_check);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);

        }
    }
}
