package com.bee.yunkong.util;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.bee.yunkong.R;


/**
 * Created by Jackie on 2015/11/30.
 */
public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;

    /**
     * @param textView          The TextView
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receiver
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setEnabled(false);
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "秒");  //设置倒计时时间
        mTextView.setTextColor(Color.parseColor("#FEFEFE"));
//        mTextView.setBackground(mTextView.getResources().getDrawable(R.drawable.shape_bt_bg_gray));
    }

    @Override
    public void onFinish() {
        mTextView.setText("重新获取");
        mTextView.setEnabled(true);
        mTextView.setClickable(true);//重新获得点击
        mTextView.setTextColor(Color.parseColor("#ffffff"));
//        mTextView.setBackground(mTextView.getResources().getDrawable(R.drawable.shape_bt_bg_red));
    }
}
