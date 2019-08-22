package com.yzq.zxinglibrary.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.FragmentHolderActivity;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.fragment.master.adddevice.MasterAddDeviceFailFragment;
import com.bee.yunkong.fragment.master.adddevice.MasterAddDeviceSuccessFragment;
import com.bee.yunkong.network.DataManager;
import com.google.zxing.Result;
import com.hwytapp.Bean.PhoneBean;
import com.hwytapp.Common.MasterMethod;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.camera.CameraManager;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.decode.DecodeImgCallback;
import com.yzq.zxinglibrary.decode.DecodeImgThread;
import com.yzq.zxinglibrary.decode.ImageUtil;
import com.yzq.zxinglibrary.view.ViewfinderView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;


/**
 * @author: yzq
 * @date: 2017/10/26 15:22
 * @declare :扫一扫
 */

public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    public ZxingConfig config;
    private SurfaceView previewView;
    private ViewfinderView viewfinderView;
    private AppCompatImageView flashLightIv;
    private TextView flashLightTv;
    private AppCompatImageView backIv;
    private LinearLayoutCompat flashLightLayout;
    private LinearLayoutCompat albumLayout;
    private LinearLayoutCompat bottomLayout;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private SurfaceHolder surfaceHolder;
    private TextView tv_location;
    private TextView tv_state;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }

        /*先获取配置信息*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {

            Log.i("config", e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }


        setContentView(R.layout.activity_capture);


        initView();

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());


    }


    private void initView() {
        previewView = findViewById(R.id.preview_view);
        previewView.setOnClickListener(this);

        viewfinderView = findViewById(R.id.viewfinder_view);
        viewfinderView.setZxingConfig(config);


        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(this);

        flashLightIv = findViewById(R.id.flashLightIv);
        flashLightTv = findViewById(R.id.flashLightTv);

        flashLightLayout = findViewById(R.id.flashLightLayout);
        flashLightLayout.setOnClickListener(this);
        albumLayout = findViewById(R.id.albumLayout);
        albumLayout.setOnClickListener(this);
        bottomLayout = findViewById(R.id.bottomLayout);
        tv_location = findViewById(R.id.tv_location);
        tv_state = findViewById(R.id.tv_state);


        switchVisibility(bottomLayout, config.isShowbottomLayout());
        switchVisibility(flashLightLayout, config.isShowFlashLight());
        switchVisibility(albumLayout, config.isShowAlbum());


        /*有闪光灯就显示手电筒按钮  否则不显示*/
        if (isSupportCameraLedFlash(getPackageManager())) {
            flashLightLayout.setVisibility(View.VISIBLE);
        } else {
            flashLightLayout.setVisibility(View.GONE);
        }

        bindData();

    }

    private void bindData() {
        tv_location.setText(config.getGroupName() + " 第" + config.getY() + "排 第" + config.getX() + "台");
    }


    /**
     * @param pm
     * @return 是否有闪光灯
     */
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param flashState 切换闪光灯图片
     */
    public void switchFlashImg(int flashState) {

        if (flashState == Constant.FLASH_OPEN) {
            flashLightIv.setImageResource(R.drawable.ic_open);
            flashLightTv.setText("关闭闪光灯");
        } else {
            flashLightIv.setImageResource(R.drawable.ic_close);
            flashLightTv.setText("打开闪光灯");
        }

    }

    private volatile boolean isRequestIng = false;

    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {
        if (isRequestIng) {
            return;
        }
        isRequestIng = true;
        tv_state.setText("处理中");
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        String res = rawResult.getText();

        String [] result = res.split("\\|\\|\\|");

        String clientSerail = result[0];
        String clientFd = result[1];

        App app = (App)getApplicationContext();
        MasterMethod mm = new MasterMethod(app);

        Map<String,PhoneBean> phoneMap  = app.getPhoneMap();

        if(phoneMap.get(clientSerail)!=null)
        {
            isRequestIng = false;
            FragmentHolderActivity.startFragmentInNewActivity(CaptureActivity.this,
                    MasterAddDeviceFailFragment.newInstance(tv_location.getText().toString()));
            return;
        }

        //注册任务机信息
        mm.clientPhoneRegister(clientSerail,clientFd,config.getGroupName(), config.getX()+"",config.getY()+"", app.getMerID());

        DataManager.masterAddDevice( clientSerail,config.getGroupName(), config.getY(), config.getX(),app, new DataManager.DataCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentHolderActivity.startFragmentInNewActivity(CaptureActivity.this,
                                MasterAddDeviceSuccessFragment.newInstance(tv_location.getText().toString()));
                        isRequestIng = false;
                        if (98 >= config.getX()) {
                            config.setX(config.getX() + 1);
                            MyEvent event = new MyEvent(EventTag.controlled_change_x);
                            event.setObject(config.getX() + "");
                            EventBus.getDefault().post(event);
                        }
                        bindData();
                    }
                });
            }

            @Override
            public void onFail(DataManager.FailData data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isRequestIng = false;
                        FragmentHolderActivity.startFragmentInNewActivity(CaptureActivity.this,
                                MasterAddDeviceFailFragment.newInstance(tv_location.getText().toString()));
                    }
                });
            }
        });

      /*  Intent intent = getIntent();
        intent.putExtra(Constant.CODED_CONTENT, rawResult.getText());
        setResult(RESULT_OK, intent);
        this.finish();*/


        //todo 发送

    }


    private void switchVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(getApplication(), config);

        viewfinderView.setCameraManager(cameraManager);
        handler = null;

        surfaceHolder = previewView.getHolder();
        if (hasSurface) {

            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();
        tv_state.setText("等待录入");

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("扫一扫");
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();

        if (!hasSurface) {

            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.flashLightLayout) {
            /*切换闪光灯*/
            cameraManager.switchFlashLight(handler);
        } else if (id == R.id.albumLayout) {
            /*打开相册*/
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constant.REQUEST_IMAGE);
        } else if (id == R.id.backIv) {
            finish();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());

            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed() {
                    Toast.makeText(CaptureActivity.this, "抱歉，解析失败,换个图片试试.", Toast.LENGTH_SHORT).show();
                }
            }).run();


        }
    }


}
