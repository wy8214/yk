package com.bee.yunkong.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baina.floatwindowlib.OnFlingListener;
import com.baina.floatwindowlib.freeposition.DraggableFloatView;
import com.baina.floatwindowlib.freeposition.DraggableFloatWindow;
import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.activity.MainActivity;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FloatWindowService extends Service {

    private WindowManager.LayoutParams mParams = null;
    private WindowManager mWindowManager = null;
    private ViewGroup folatInput;

    private WindowManager.LayoutParams mParams2 = null;
    private WindowManager mWindowManager2 = null;
    private ViewGroup folatConfirm;

    private WindowManager.LayoutParams mParams1 = null;
    private WindowManager mWindowManager1 = null;
    private DraggableFloatView folatView;
    private Handler handler;

    public static volatile boolean isRecording = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };


//        initView(getApplicationContext());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * toast信息
     *
     * @param messsage
     */
    protected void toast(final String messsage) {
//        ToastTool.toast(messsage);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), messsage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRecording = true;
        initView(getApplicationContext());
        attachFloatViewToWindow1();


        return super.onStartCommand(intent, flags, startId);
    }

    private TextView tvTip;
    private EditText etInput;
    private TextView tvSure;
    private TextView tvCancle;
    private TextView tvTip2;
    private TextView tvSure2;
    private TextView tvCancle2;


    private void initView(final Context context) {

        final App app = (App)context;
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.packageName = context.getPackageName();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                /*| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE */ | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //The default position is vertically to the right
        mParams.gravity = Gravity.CENTER;
        mParams.format = PixelFormat.RGBA_8888;

        folatInput = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_input, null);
        tvTip = (TextView) folatInput.findViewById(R.id.tv_tip);
        etInput = (EditText) folatInput.findViewById(R.id.et_input);
        tvSure = (TextView) folatInput.findViewById(R.id.tv_sure);
        tvCancle = (TextView) folatInput.findViewById(R.id.tv_cancle);

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etInput.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    toast("请先输入脚本名称");
                    return;
                }

                DataManager.masterScriptStop(context, name, new DataManager.DataCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mWindowManager.removeView(folatInput);
                                    mWindowManager1.removeView(folatView);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    EventBus.getDefault().post(new MyEvent(EventTag.master_switch_to_third));
                                    isRecording = false;
                                    stopSelf();
                                } catch (Exception e) {
                                    e.printStackTrace();
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
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //录制脚本完了，取消按钮是不保存的推出  changed at 2018-11-01
                try {
                    mWindowManager.removeView(folatInput);
                    mWindowManager1.removeView(folatView);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    EventBus.getDefault().post(new MyEvent(EventTag.master_switch_to_third));
                    isRecording = false;
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*- 确认框--*/
        mWindowManager2 = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mParams2 = new WindowManager.LayoutParams();
        mParams2.packageName = context.getPackageName();
        mParams2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams2.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //The default position is vertically to the right
        mParams2.gravity = Gravity.CENTER;
        mParams2.format = PixelFormat.RGBA_8888;

        folatConfirm = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_confirm, null);
        tvTip2 = (TextView) folatConfirm.findViewById(R.id.tv_tip);
        tvSure2 = (TextView) folatConfirm.findViewById(R.id.tv_sure);
        tvCancle2 = (TextView) folatConfirm.findViewById(R.id.tv_cancle);
        tvTip2.setText("是否确认退出录制");
        tvSure2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mWindowManager2.removeView(folatConfirm);
                    attachFloatViewToWindow();
                } catch (Exception e) {
                    mWindowManager2.removeView(folatConfirm);
                    e.printStackTrace();
                }
            }
        });

        tvCancle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mWindowManager2.removeView(folatConfirm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        mWindowManager.updateViewLayout(folatInput, mParams);

        /*--操作框--*/

        mWindowManager1 = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mParams1 = new WindowManager.LayoutParams();
        mParams1.packageName = context.getPackageName();
        mParams1.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams1.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams1.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams1.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams1.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //The default position is vertically to the right
//        mParams1.gravity = Gravity.CENTER;
        mParams1.format = PixelFormat.RGBA_8888;

        folatView = new DraggableFloatView(context, new OnFlingListener() {

            @Override
            public void onMove(float moveX, float moveY) {

                app.setIsFloatClick(true);
                mParams1.x = (int) (mParams1.x + moveX);
                mParams1.y = (int) (mParams1.y + moveY);
                mWindowManager1.updateViewLayout(folatView, mParams1);
            }
        });


        folatView.setTouchButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                app.setIsFloatClick(true);

                switch (v.getId()) {
                    case R.id.ll_input_read_article:
                        DataManager.masterScriptInputReadArticle(getApplicationContext(),new JSONObject(),new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {

                            }

                            @Override
                            public void onFail(DataManager.FailData result) {

                            }
                        });
                        break;
                    case R.id.ll_input_phone_num:
                        DataManager.masterScriptInputPhoneNum(getApplicationContext(),new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {

                            }

                            @Override
                            public void onFail(DataManager.FailData result) {

                            }
                        });
                        break;
                    case R.id.ll_input_code:
                        DataManager.masterScriptInputCode(getApplicationContext(),getSmsInPhone(), new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {

                            }

                            @Override
                            public void onFail(DataManager.FailData result) {

                            }
                        });
                        break;
                    case R.id.ll_pasue:
                        app.setIsKeyevent(folatView.isPasue());
                        folatView.setState(!folatView.isPasue());
                        break;
                    case R.id.ll_stop:
                        //todo 弹出输入对话框，让输入名字
                        //点击退出的时候 界面上不会变为暂停状态 ；
                        // 点击退出 先弹出提示框 是否确认退出录制 按钮为确认 和返回  点击返回后 继续录制  ；
                        // 点击确认 再弹出当前的提示框  提示标题为 是否保存当前脚本  按钮为保存  取消；
                        // 点保存需要保证有脚本名称
                        app.setIsKeyevent(false);
                        app.setIsRecode(false);
                        attachFloatViewToWindow2();
                       /* DataManager.masterScriptPause(new DataManager.DataCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        folatView.setState(true);
                                        attachFloatViewToWindow();
                                    }
                                });
                            }

                            @Override
                            public void onFail(DataManager.FailData result) {

                            }
                        });*/
                        break;
                }
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainTheadEvent(MyEvent event) {
        switch (event.getTag()) {
            case master_start_record:

                break;
        }
    }

    /**
     * attach floatView to window
     */
    private void attachFloatViewToWindow() {
        if (folatInput == null)
            throw new IllegalStateException("DraggableFloatView can not be null");
        if (mParams == null)
            throw new IllegalStateException("WindowManager.LayoutParams can not be null");
        try {
            mWindowManager.updateViewLayout(folatInput, mParams);
        } catch (IllegalArgumentException e) {
            //if floatView not attached to window,addView
            mWindowManager.addView(folatInput, mParams);
       /*     etInput.setFocusable(true);
            etInput.setFocusableInTouchMode(true);
            etInput.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) etInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etInput, 0);*/
        }

    }

    /**
     * attach floatView to window
     */
    private void attachFloatViewToWindow2() {
        if (folatConfirm == null)
            throw new IllegalStateException("DraggableFloatView can not be null");
        if (mParams2 == null)
            throw new IllegalStateException("WindowManager.LayoutParams can not be null");
        try {
            mWindowManager2.updateViewLayout(folatConfirm, mParams2);
        } catch (IllegalArgumentException e) {
            //if floatView not attached to window,addView
            mWindowManager2.addView(folatConfirm, mParams2);
        }

    }

    /**
     * attach floatView to window
     */
    private void attachFloatViewToWindow1() {
        if (folatView == null)
            throw new IllegalStateException("DraggableFloatView can not be null");
        if (mParams1 == null)
            throw new IllegalStateException("WindowManager.LayoutParams can not be null");
        try {
            isRecording = true;
            mWindowManager1.updateViewLayout(folatView, mParams1);
          /*  etInput.setFocusable(true);
            etInput.setFocusableInTouchMode(true);
            etInput.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) etInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etInput, 0);*/
        } catch (IllegalArgumentException e) {
            //if floatView not attached to window,addView
            mWindowManager1.addView(folatView, mParams1);
           /* etInput.setFocusable(true);
            etInput.setFocusableInTouchMode(true);
            etInput.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) etInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etInput, 0);*/
            isRecording = true;
        }

    }


    public String getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
        final String SMS_URI_SEND = "content://sms/sent"; // 已发送
        final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
        final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
        final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
        final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表

        StringBuilder smsBuilder = new StringBuilder();

        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type",};
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc limit 1 "); // 获取手机内部短信
            // 获取短信中最新的未读短信
            // Cursor cur = getContentResolver().query(uri, projection,
            // "read = ?", new String[]{"0"}, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    String strType = "";
                    if (intType == 1) {
                        strType = "接收";
                    } else if (intType == 2) {
                        strType = "发送";
                    } else if (intType == 3) {
                        strType = "草稿";
                    } else if (intType == 4) {
                        strType = "发件箱";
                    } else if (intType == 5) {
                        strType = "发送失败";
                    } else if (intType == 6) {
                        strType = "待发送列表";
                    } else if (intType == 0) {
                        strType = "所以短信";
                    } else {
                        strType = "null";
                    }

//                    smsBuilder.append("[ ");
//                    smsBuilder.append(strAddress + ", ");
//                    smsBuilder.append(intPerson + ", ");
                    smsBuilder.append(strbody);
//                    smsBuilder.append(strDate + ", ");
//                    smsBuilder.append(strType);
//                    smsBuilder.append(" ]\n\n");
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            }

            smsBuilder.append("getSmsInPhone has executed!");

        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }

        return smsBuilder.toString();
    }
}
