package com.hwytapp.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.hwytapp.Bean.PhoneBean;
import com.hwytapp.Bean.TestSendData;
import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.action.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.client.connection.NoneReconnect;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyEventService  extends IntentService {

    private final String TAG = "getevent";
//    private final ConnectionInfo info=null ;

    //必须实现父类的构造方法
    public KeyEventService() {
        super("KeyEventService");
    }

    //必须重写的核心方法
    @Override
    protected void onHandleIntent(Intent intent) {
        App ha = (App) getApplication();

        if (!ha.getIsMasterPhone())
            return;



        ConnectionInfo info = ha.getSocketinfo();
        Process process;
        String cmd = "getevent -t";
        try {
            process = Runtime.getRuntime().exec("su");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            writer.write(cmd + "\n");
            writer.write("exit\n");
            writer.flush();
            String line;

            while ((line = reader.readLine()) != null) {
                getLine(line, info);
            }
            while ((line = error.readLine()) != null) {
                Log.e("error", line);
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public String last_time = "";

    public List<String> line_list = new ArrayList<String>();

    public void getLine(String line, ConnectionInfo info) {

        String oldLine = line;
        String item_e="";
        String item_t="";
        try {
            App ha = (App) getApplication();
            if(!ha.getIsKeyevent())
                return;



            if(line.contains("FLYME_HIPS_DEBUG"))
                line = line.substring(0,22);


            if (line.length() < 55)
                return;
             item_e = line.substring(44, 46);
             item_t = line.substring(6, 16);

            Double t = Double.parseDouble((String) item_t);
            Double last_t = 0.0;


            if ("36".equals(item_e.toString()) || "35".equals(item_e.toString()) || "c4".equals(item_e.toString()) || "66".equals(item_e.toString())) {
                line_list.add(line);
                last_time = item_t;
            }

            if (last_time != "")
                last_t = Double.parseDouble((String) last_time);

            if ((t - last_t) >= 0.4 && last_time != "") {
                if (line_list.size() > 0) {
                    resolveLine(line_list, info);
                    line_list = new ArrayList<String>();
                    last_time = "";
                }

            }
        }catch (Exception e)
        {
            Log.i("item_t++++",item_t);
            Log.i("item_e++++",item_e);
            Log.i("line++++",oldLine);
            Log.i("getLine++++",e.toString());
        }



    }

    public void resolveLine(List<String> newList, ConnectionInfo info) {
        Collections.reverse(newList);
        String item;
        String item_t;
        String item_e;
        String item_z;
        List arr = new ArrayList<Map>();
        int j = 0;
        int z = 0;
        for (int i = 0, len = newList.size(); i < len; i++) {
            item = newList.get(i);
            if (item.length() < 55) {
                continue;
            }
            item_t = item.substring(5, 16);//Double.parseDouble(item.substring(5,16));
            item_e = item.substring(44, 46);//Integer.parseInt(item.substring(44,46));
            item_z = String.valueOf(Long.parseLong(item.substring(47, 55), 16));//item.substring(47,55);//
            if (0 == z && "35".equals(item_e.toString())) {
                continue;
            }
            j = (int) Math.floor(z / 2);
            if ("36".equals(item_e.toString())) {
                Map<String, String> keyValue = new HashMap<String, String>();
                keyValue.put("time", item_t);
                keyValue.put("y", item_z);
                keyValue.put("x", "0");
                arr.add(keyValue);
            } else if ("35".equals(item_e.toString())) {
                int ind = getIndex(arr, j);
                if (ind != -1) {
                    ((HashMap) arr.get(ind)).put("x", item_z);
                }
            } else if ("c4".equals(item_e.toString()) && "0".equals(item_z)) {
                Map<String, String> keyValue = new HashMap<String, String>();
                keyValue.put("time", item_t);
                keyValue.put("x", "1");
                keyValue.put("y", String.valueOf(Long.parseLong("c4", 16)));
                arr.add(keyValue);
                z++;
            } else if ("66".equals(item_e.toString()) && "0".equals(item_z)) {
                Map<String, String> keyValue = new HashMap<String, String>();
                keyValue.put("time", item_t);
                keyValue.put("x", "1");
                keyValue.put("y", String.valueOf(Long.parseLong("66", 16)));
                arr.add(keyValue);
                z++;
            } else {
                continue;
            }
            z++;
        }


        List lastList = new ArrayList<Map>();
        double lastTime = 9999999999999.0;
        for (int i = 0, len = arr.size(); i < len; i++) {
            Map<String, Map> keyValue = new HashMap<String, Map>();
            HashMap m = (HashMap) arr.get(i);
            Double t = Double.parseDouble((String) m.get("time"));
            Double x = Double.parseDouble((String) m.get("x"));
            Double y = Double.parseDouble((String) m.get("y"));
            if (x == 0) {
                continue;
            }
            Map<String, Double> c = new HashMap<String, Double>();
            c.put("t", t);
            c.put("x", x);
            c.put("y", y);
            //物理按键
            if (x == 1) {
                if (y == 102) {
                    //66 home键
                    Map<String, Map> kv = (HashMap) lastList.get(lastList.size() - 1);
                    x = (Double) kv.get("s").get("x");
                    y = (Double) kv.get("s").get("y");
                    if (x == 1 && y == 196) {
                        kv.put("s", c);
                        kv.put("e", c);
                    } else {
                        keyValue.put("s", c);
                        keyValue.put("e", c);
                        lastList.add(keyValue);
                    }
                } else if (y == 196) {
                    //c4 返回back键
                    keyValue.put("s", c);
                    keyValue.put("e", c);
                    lastList.add(keyValue);
                }

            } else {
                if (lastTime - t > 0.4) {
                    keyValue.put("s", c);
                    keyValue.put("e", c);
                    lastList.add(keyValue);
                } else {
                    keyValue = (HashMap) lastList.get(lastList.size() - 1);
                    keyValue.put("s", c);
                }
            }
            lastTime = t;
        }
        List eventArray = new ArrayList<Map>();
        for (int i = 0, len = lastList.size(); i < len; i++) {
            Map<String, String> keyValue = new HashMap<String, String>();
            HashMap m = (HashMap) lastList.get(i);
            HashMap s = (HashMap) m.get("s");
            HashMap e = (HashMap) m.get("e");
            String eventType = "tap";
            Double xMove = Double.valueOf(e.get("x").toString()) - Double.valueOf(s.get("x").toString());
            Double yMove = Double.valueOf(e.get("y").toString()) - Double.valueOf(s.get("y").toString());
            Double tMove = Double.valueOf(e.get("t").toString()) - Double.valueOf(s.get("t").toString());
            if (Double.valueOf(e.get("x").toString()) == 1) {
                if (Double.valueOf(e.get("y").toString()) == 102) {
                    eventType = "home";
                } else if (Double.valueOf(e.get("y").toString()) == 196) {
                    eventType = "back";
                }
            } else {
                if (xMove > 10 || yMove > 10 || xMove < -10 || yMove < -10) {
                    eventType = "swipe";
                } else if (tMove > 1) {
                    eventType = "longTap";
                } else {
                    eventType = "tap";
                }
            }

            keyValue.put("eventType", eventType);
            keyValue.put("startPosition", s.get("x") + "," + s.get("y"));
            keyValue.put("endPosition", e.get("x") + "," + e.get("y"));
            keyValue.put("startTime", "" + s.get("t"));
            keyValue.put("endTime", "" + e.get("t"));
            keyValue.put("moveTime", "" + tMove);
            keyValue.put("moveX", "" + xMove);
            keyValue.put("moveY", "" + yMove);
            String cmd = "";
            if (eventType == "tap") {
                cmd = "input tap " + s.get("x") + " " + s.get("y");
            } else if (eventType == "swipe") {
                cmd = "input swipe " + s.get("x") + " " + s.get("y") + " " + e.get("x") + " " + e.get("y") + " " + (int) (tMove * 1000);
            } else if (eventType == "longTap") {
                cmd = "input swipe " + s.get("x") + " " + s.get("y") + " " + e.get("x") + " " + e.get("y") + " " + (int) (tMove * 1000);
            } else if (eventType == "home") {
                cmd = "input keyevent 3";
            } else if (eventType == "back") {
                cmd = "input keyevent 4";
            }




            App ha = (App) getApplication();

            if(ha.getIsFloatClick())
            {
                ha.setIsFloatClick(false);
                return;
            }
            Log.i("event++++++++++++", cmd);
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", "distribute");
                jsonObject.put("action", "client");

                //是否录制脚本
                if(ha.getIsRecode())
                    recodeCmd(cmd,ha);

                cmd = cmd+"$$$0|||end$$$1000";
                jsonObject.put("cmd_data", cmd);

                Map<String,PhoneBean> phoneMap  = ha.getPhoneSelectedMap();
                for (PhoneBean pb : phoneMap.values()) {
                    if(!pb.isMaster()&&pb.isSelected()&&pb.getFd()>0)
                    {
                        jsonObject.put("rec_fd", pb.getFd());
                        OkSocket.open(info).send(new TestSendData(jsonObject));
                    }
                }

            } catch (Exception se) {
                Log.i("event++++++++++++", se.toString());
            }
            eventArray.add(keyValue);
        }

    }

    /**
     * 从最后一行开始读取
     */
    public List<String> read(String line) {
        //String charset = "UTF-8";
        List<String> list = new ArrayList<String>();
        //RandomAccessFile rf = null;
        String[] lists = line.split("\\+");
        for (int i = 0; i < lists.length; i++) {
            list.add(lists[i]);
        }
        return list;
    }

    public int getIndex(List arr, int j) {
        boolean has = false;
        int size = arr.size();
        size = size < 20 ? size : 20;
        for (int i = 0; i < size; i++) {
            j = j - i;
            HashMap hm = (HashMap) arr.get(j);
            String x = hm.get("x").toString();
            if ("1".equals(x)) {
                break;
            }
            if ("0".equals(x)) {
                has = true;
                break;
            }
        }
        return has ? j : -1;
    }



    private  void recodeCmd(String cmd,App ha)
    {
        long gapTime = 0;

        gapTime = System.currentTimeMillis() - ha.getLastRecTime();

        Log.i("gapTime++++++++++++", gapTime+"");
        cmd = cmd+"$$$"+gapTime;

        List<String> cmdList = ha.getCmdList();
        cmdList.add(cmd);

        ha.setCmdList(cmdList);
    }


    //重写其他方法,用于查看方法的调用顺序
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");
        flags = START_STICKY_COMPATIBILITY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);
        Log.i(TAG, "setIntentRedelivery");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

        Intent intent = new Intent();
        intent.setAction("com.restart.service");
        //发送广播
        sendBroadcast(intent);

    }
}