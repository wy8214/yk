package com.bee.yunkong.core;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bee.yunkong.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by yuanshenghong on 2017/3/14.
 */

public abstract class BaseActivity extends SupportActivity {
    private String TAG = getClass().getSimpleName();


    private Dialog dialog;
    /**
     * Activity是否可见
     */
    private boolean mIsShown;

    protected String getTAG() {
        return TAG;
    }

    private BaseActivity host;


    protected BaseActivity getHostActivity() {
        return host;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = this;
    }

    private Dialog checkDialog;
    private TextView tvSure;


    protected void checkNetWork() {

    }

    /**
     * 说明：Android 6.0+ 状态栏图标原生反色操作
     */
    protected void setDarkStatusIcon(boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            if (decorView == null) return;

            int vis = decorView.getSystemUiVisibility();
            if (dark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }


   /* private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            MyStatusBarUtil.setStatusBarColor(BaseActivity.this,R.color.white);
        }else {
            StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        }
    }*/



    @Override
    protected void onResume() {
        super.onResume();
        mIsShown = true;
//        AVAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsShown = false;
//        AVAnalytics.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    private Dialog progressDialog;

    /**
     * 启动加载进度条
     *
     * @param message
     */
    protected void startProgressBar(String message) {

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

    /**
     * toast信息
     *
     * @param messsage
     */
    protected void toast(final String messsage) {
//        ToastTool.toast(messsage);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getHostActivity(), messsage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 统一eventbus事件处理方法，
     * 方便查看
     * 在主线程处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainTheadEvent(MyEvent event) {
        //do nothing
    }

    /**
     * 统一eventbus事件处理方法，
     * 方便查看
     * 在后台线程处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundTheadEvent(MyEvent event) {
        //do nothing
    }

    protected void startFragmentAcitivty(BaseFragment fragment) {
        FragmentHolderActivity.startFragmentInNewActivity(getHostActivity(), fragment);
    }

    /**
     * 发送事件
     *
     * @param event
     */
    protected void postEvent(MyEvent event) {
        EventBus.getDefault().post(event);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left, R.anim.umeng_fb_slide_out_from_right);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(
                R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    public boolean isVisibled() {
        return mIsShown;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
//        return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
        // 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }
}
