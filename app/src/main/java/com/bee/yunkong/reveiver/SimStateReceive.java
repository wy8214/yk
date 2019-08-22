package com.bee.yunkong.reveiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.bee.yunkong.network.DataManager;

public class SimStateReceive extends BroadcastReceiver {
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private final static int SIM_VALID = 0;
    private final static int SIM_INVALID = 1;
    private int simState = SIM_INVALID;

    public int getSimState() {
        return simState;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            int state = tm.getSimState();

            switch (state) {
                case TelephonyManager.SIM_STATE_READY:
                    updateDevicePhone(context);
                    simState = SIM_VALID;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                case TelephonyManager.SIM_STATE_ABSENT:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                default:
                    simState = SIM_INVALID;
                    break;
            }
        }
    }

    private void updateDevicePhone(Context context) {
        DataManager.updatePhoneState(getImsi(context), new DataManager.DataCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onFail(DataManager.FailData result) {

            }
        });
    }

    public static DataManager.SimCardInfo getImsi(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        DataManager.SimCardInfo info = new DataManager.SimCardInfo();
        info.deviceId = tm.getDeviceId();// 获取智能设备唯一编号
        info.tel = tm.getLine1Number();// 获取本机号码
        info.simei = tm.getSimSerialNumber();// 获得SIM卡的序号
        info.imsi = tm.getSubscriberId();// 得到用户Id
        return info;
    }
}