package com.bee.yunkong.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bee.yunkong.R;
import com.bee.yunkong.activity.SplashActivity;
import com.bee.yunkong.fragment.DeviceCheckFailFragment;
import com.bee.yunkong.fragment.NoNetWorkFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by yuanshenghong on 2017/3/14.
 * Fragment基类，已集成了开启进度条，关闭进度条，手势返回，返回动画等
 */

public abstract class BaseFragment extends SupportFragment {
    private String TAG = getClass().getSimpleName();
    private Activity activity;

    private Dialog progressDialog;
    private ViewGroup mRootViewGroup;
    private TextView tv_title;

    public static final int PAGESIZE = 20;


    protected String getTAG() {
        return TAG;
    }


    @Nullable
    @Override
    public Context getContext() {
        return getHostActivity();
    }


    /**
     * 获取依附在的activity
     *
     * @return
     */
    protected Activity getHostActivity() {
        return activity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (null != activity) {
            this.activity = activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == activity) {
            activity = getActivity();
        }
    }

    protected Resources getRes() {
        return getHostActivity().getResources();
    }

    /**
     * toast信息
     *
     * @param messsage
     */
    protected void toast(final String messsage) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getHostActivity(), messsage, Toast.LENGTH_SHORT).show();
            }
        });
//        ToastTool.toast(messsage);
    }

    /**
     * 带动画
     *
     * @param intent
     * @param requestCode
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getHostActivity().startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(
                R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootViewGroup = (ViewGroup) inflater.inflate(getLayoutResId(), null);
        return (mRootViewGroup);
    }

    protected View findViewById(int resId) {
        if (null == mRootViewGroup) {
            return null;
        }
        return mRootViewGroup.findViewById(resId);
    }

    protected abstract int getLayoutResId();

    protected void runOnUiThread(Runnable r){
        getHostActivity().runOnUiThread(r);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closePrograssBar();
        //在view被销毁的时候清除所有回调
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected void setTitleStr(String title) {
        tv_title = (TextView) findViewById(R.id.tv_title);
        if (null != tv_title) {
            tv_title.setText(title);
        }
    }

    /**
     * 设置返回键，返回或者关闭acitivty
     */
    protected void setPopOrFinish() {
        LinearLayout ivback = (LinearLayout) findViewById(R.id.ll_title_left);
        if (null != ivback) {
            ivback.setVisibility(View.VISIBLE);
            ivback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getPreFragment() != null) {
                        hideSoftInput();
                        pop();
                    } else {
                        hideSoftInput();
                        getHostActivity().finish();
                    }
                }
            });
        }
    }

    protected void showRightText(String text, View.OnClickListener l) {
        TextView textView = (TextView) findViewById(R.id.tv_right);
        if (null != textView) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
            textView.setOnClickListener(l);
        }
    }

    /**
     * 统一eventbus事件处理方法，
     * 方便查看
     * 在主线程处理
     *
     * @param noEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadEvent(MyEvent noEvent) {
        //do nothing
        switch (noEvent.getTag()) {
            case master_changed:
                Intent intent = new Intent(getHostActivity(),SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case socket_disconnet:
                startFragmentAcitivty(NoNetWorkFragment.newInstance());
                break;
            case server_notice:
                String info = noEvent.getObject().toString();
                toast(info);
                break;

        }


    }

    /**
     * 发送事件
     *
     * @param event
     */
    protected void postEvent(MyEvent event) {
        EventBus.getDefault().post(event);
    }

    /**
     * @param tag
     */
    protected void postEvent(EventTag tag) {
        EventBus.getDefault().post(new MyEvent(tag));
    }

    /**
     * 统一eventbus事件处理方法，
     * 方便查看
     * 在后台线程处理
     *
     * @param noEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundTheadEvent(MyEvent noEvent) {
        //do nothing
    }

    /**
     * 显示取数据失败的Toast
     */
    protected void showErrorDataToast() {
        if (isVisible()) {
            toast("数据获取失败，请稍后再试！");
        }
    }

    /**
     * 显示无网络的Toast
     */
    protected void showNoNetworkToast() {
        if (isVisible()) {
            toast("暂无网络连接，请稍后再试！");
        }
    }

    /**
     * 显示网络连接异常的Toast
     */
    protected void showErrorNetworkToast() {
        if (isVisible()) {
            toast("连接网络失败，请稍后再试！");
        }
    }

    /**
     * 启动加载进度条
     *
     * @param message
     */
    protected void startProgressBar(String message) {
        if (!isVisible()) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new Dialog(getHostActivity(), R.style.dialog);
            LayoutInflater inflater = LayoutInflater.from(getHostActivity());
            ViewGroup layout_mainLayout = (ViewGroup) inflater.inflate(
                    R.layout.dialog_progress, null);
            progressDialog.setContentView(layout_mainLayout);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        TextView tv = progressDialog.findViewById(R.id.tv_message);
        if (!TextUtils.isEmpty(message)) {
            tv.setText(message);
        } else {
            tv.setText("请稍等....");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 关闭加载图
     */
    protected void closePrograssBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void startFragmentAcitivty(BaseFragment fragment) {
        FragmentHolderActivity.startFragmentInNewActivity(getHostActivity(), fragment);
    }



}
