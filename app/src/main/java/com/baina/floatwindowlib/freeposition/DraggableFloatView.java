package com.baina.floatwindowlib.freeposition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baina.floatwindowlib.OnFlingListener;
import com.bee.yunkong.App;
import com.bee.yunkong.R;
import com.bee.yunkong.network.DataManager;
import com.bee.yunkong.util.logger.MyLog;

/**
 * Created by chentao on 2018/1/14.
 * 浮动按钮视图
 */

@SuppressLint("ViewConstructor")
public class DraggableFloatView extends LinearLayout {

    private static final String TAG = DraggableFloatView.class.getSimpleName();

    private Context mContext;
    private OnFlingListener mOnFlingListener;
    private LinearLayout llAll;
    private LinearLayout llAction;
    private LinearLayout llInputPhoneNum;
    private LinearLayout llInputReadArticle;
    private ImageView ivInputPhoneNum;
    private TextView tvInputPhoneNum;
    private ImageView ivInputReadArticle;
    private TextView tvInputReadArticle;
    private LinearLayout llInputCode;
    private ImageView ivInputCode;
    private TextView tvInputCode;
    private LinearLayout llPasue;
    private ImageView ivPasue;
    private TextView tvPasue;
    private LinearLayout llStop;
    private ImageView ivStop;
    private TextView tvStop;
    private ImageView touchBt;



    private OnClickListener onClickListener;

    public DraggableFloatView(Context context, OnFlingListener flingListener) {
        super(context);
        mContext = context;



        LayoutInflater.from(mContext).inflate(R.layout.layout_floatview_freeposition, this);

        llAll = (LinearLayout) findViewById(R.id.ll_all);
        llAction = (LinearLayout) findViewById(R.id.ll_action);
        llInputPhoneNum = (LinearLayout) findViewById(R.id.ll_input_phone_num);
        llInputReadArticle = (LinearLayout) findViewById(R.id.ll_input_read_article);
        ivInputPhoneNum = (ImageView) findViewById(R.id.iv_input_phone_num);
        tvInputPhoneNum = (TextView) findViewById(R.id.tv_input_phone_num);
        ivInputReadArticle = (ImageView) findViewById(R.id.iv_input_read_article);
        tvInputReadArticle = (TextView) findViewById(R.id.tv_input_read_article);
        llInputCode = (LinearLayout) findViewById(R.id.ll_input_code);
        ivInputCode = (ImageView) findViewById(R.id.iv_input_code);
        tvInputCode = (TextView) findViewById(R.id.tv_input_code);
        llPasue = (LinearLayout) findViewById(R.id.ll_pasue);
        ivPasue = (ImageView) findViewById(R.id.iv_pasue);
        tvPasue = (TextView) findViewById(R.id.tv_pasue);
        llStop = (LinearLayout) findViewById(R.id.ll_stop);
        ivStop = (ImageView) findViewById(R.id.iv_stop);
        tvStop = (TextView) findViewById(R.id.tv_stop);
        touchBt = (ImageView) findViewById(R.id.touchBt);


        mOnFlingListener = flingListener;
        touchBt.setOnTouchListener(new OnTouchListener() {

            //刚按下是起始位置的坐标
            float startDownX, startDownY;
            float downX, downY;
            float moveX, moveY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN");
                        startDownX = downX = motionEvent.getRawX();
                        startDownY = downY = motionEvent.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "ACTION_MOVE");
                        moveX = motionEvent.getRawX();
                        moveY = motionEvent.getRawY();
                        if (mOnFlingListener != null)
                            mOnFlingListener.onMove(moveX - downX, moveY - downY);
                        downX = moveX;
                        downY = moveY;
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP");
                        float upX = motionEvent.getRawX();
                        float upY = motionEvent.getRawY();
                        if (upX == startDownX && upY == startDownY)
                            return false;
                        else
                            return true;
                }
                return true;
            }
        });
        touchBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                App app = (App)mContext;
                app.setIsFloatClick(true);
                if (llAction.getVisibility() == GONE) {
                    llAction.setVisibility(VISIBLE);
                } else {
                    llAction.setVisibility(GONE);
                }
            }
        });


    }

    public void setTouchButtonClickListener(OnClickListener touchButtonClickListener) {
        onClickListener = touchButtonClickListener;
        llInputReadArticle.setOnClickListener(onClickListener);
        llInputPhoneNum.setOnClickListener(onClickListener);
        llInputCode.setOnClickListener(onClickListener);
        llPasue.setOnClickListener(onClickListener);
        llStop.setOnClickListener(onClickListener);
    }

    public boolean isPasue() {
        return pasue;
    }

    public interface OnTouchButtonClickListener {
        void onClick(View view);
    }

    private volatile boolean pasue = false;

    public void setState(boolean pause) {
        this.pasue = pause;

        MyLog.d(pause);
        if (pause) {
            llAll.setBackground(getResources().getDrawable(R.drawable.shape_bt_gray_big));
            ivInputReadArticle.setImageResource(R.drawable.script_input_phone_num_s);
            ivInputPhoneNum.setImageResource(R.drawable.script_input_phone_num_s);
            ivInputCode.setImageResource(R.drawable.script_input_ver_s);
            ivPasue.setImageResource(R.drawable.script_pause_s);
            ivStop.setImageResource(R.drawable.script_stop_s);
            tvInputReadArticle.setTextColor(Color.parseColor("#FCF270"));
            tvInputPhoneNum.setTextColor(Color.parseColor("#FCF270"));
            tvInputCode.setTextColor(Color.parseColor("#FCF270"));
            tvPasue.setTextColor(Color.parseColor("#FCF270"));
            tvStop.setTextColor(Color.parseColor("#FCF270"));
            tvPasue.setText("继续");
            touchBt.setImageResource(R.drawable.script_btn_paused);



        } else {
            llAll.setBackground(getResources().getDrawable(R.drawable.shape_bt_yellow_big));
            ivInputReadArticle.setImageResource(R.drawable.script_input_phone_num);
            ivInputPhoneNum.setImageResource(R.drawable.script_input_phone_num);
            ivInputCode.setImageResource(R.drawable.script_input_ver);
            ivPasue.setImageResource(R.drawable.script_pause);
            ivStop.setImageResource(R.drawable.script_stop);
            tvInputReadArticle.setTextColor(Color.parseColor("#333333"));
            tvInputPhoneNum.setTextColor(Color.parseColor("#333333"));
            tvInputCode.setTextColor(Color.parseColor("#333333"));
            tvPasue.setTextColor(Color.parseColor("#333333"));
            tvStop.setTextColor(Color.parseColor("#333333"));
            tvPasue.setText("暂停");
            touchBt.setImageResource(R.drawable.script_btn_active);
        }
        App app = (App)mContext;
        app =(App)mContext;
        app.setIsRecode(!this.pasue);
    }
}
