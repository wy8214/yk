package com.hwytapp.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.hwytapp.Bean.PulseData;
import com.hwytapp.Common.HandleCmd;
import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.libsocket.impl.client.PulseManager;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.action.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.client.connection.NoneReconnect;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YkSokectsService extends IntentService {

    private final String TAG = "YkSokects";

    private PulseData mPulseData = new PulseData();
    private IConnectionManager mManager;

    //必须实现父类的构造方法
    public YkSokectsService() {
        super("YkSokectsService");
    }

    //必须重写的核心方法
    @Override
    protected void onHandleIntent(Intent intent) {
        App ha = (App) getApplication();
        //连接服务器
        serverConnect();
    }


    private void serverConnect() {
        final App ha = (App) getApplication();

        if (ha.getSocketinfo() != null)
            return;

        String sockeIP = ha.getSocketIp();
        int socketPort = ha.getSocketPort();
        OkSocket.initialize(getApplication());

        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo(sockeIP, socketPort);

        final OkSocketOptions mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setWritePackageBytes(1024*1024)
                .setReadPackageBytes(1024*1024)
                .setCallbackInThread(false)
                .setPulseFrequency(30*1000)
                .setReaderProtocol(new IReaderProtocol() {
                    @Override
                    public int getHeaderLength() {
                        //返回自定义的包头长度,框架会解析该长度的包头
                        return 4;
                    }

                    @Override
                    public int getBodyLength(byte[] header, ByteOrder byteOrder) {

                        ByteBuffer bb = ByteBuffer.wrap(header);
                        bb.order(byteOrder);
                        int bodyLength =  bb.getInt();

                        //从header(包头数据)中解析出包体的长度,byteOrder是你在参配中配置的字节序,可以使用ByteBuffer比较方便解析
                        //int bodyLength =  Integer.parseInt(header[3]+"");

                        return  bodyLength-4;
                    }
                })
                .build();
        //调用OkSocket,开启这次连接的通道,调用通道的连接方法进行连接.
        OkSocket.open(info).option(mOkOptions).connect();

        IConnectionManager manager = OkSocket.open(info);
        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
                Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();

                Log.i("ConnectionSuccess", " onSocketConnectionSuccess++++++++++++" );
                ha.setSocketinfo(info);
                ha.appInit();

                EventBus.getDefault().post(new MyEvent(EventTag.master_changed));

                //此处也可将ConnectManager保存成成员变量使用.
                mManager = OkSocket.open(info);
                if(mManager != null){
                    PulseManager pulseManager = mManager.getPulseManager();
                    //给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
                    pulseManager.setPulseSendable(mPulseData);
                    //开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
                    pulseManager.pulse();
                }


            }
            @Override
            public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
                Log.i("onSocket", " onSocketConnectionFailed++++++++++++" + e.toString());
            }


            @Override
            public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {

                //遵循以上规则,这个回调才可以正常收到服务器返回的数据,数据在OriginalData中,为byte[]数组,该数组数据已经处理过字节序问题,直接放入ByteBuffer中即可使用
                byte[] bodyBytes = data.getBodyBytes();
                String dataJson = new String(bodyBytes);

                Log.i("OriginalData+++++++++", " " + dataJson);
                if(mManager != null && dataJson.equals("pulse") ){
                    //是否是心跳返回包,需要解析服务器返回的数据才可知道

                    //喂狗操作
                    mManager.getPulseManager().feed();
                    return;

                }

                try {
                    Log.i("OriginalData+++++++++", " " + dataJson);
                    JSONObject jb = new JSONObject(dataJson);
                    HandleCmd hc =new HandleCmd(ha);
                    hc.resolveHandle(jb);
                } catch (Exception ex) {
                    Log.i(TAG, ex.toString());
                }
            }

            @Override
            public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
                EventBus.getDefault().post(new MyEvent(EventTag.socket_disconnet));
                Log.i("onSocketDisconnection", " onSocketDisconnection++++++++++++" +OkSocket.open(info).isConnect());
                final ConnectionInfo ifo = info;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!OkSocket.open(ifo).isConnect()) {

                            try {
                                Log.i("onSocketDisconnection", " onSocketDisconnection++++++++++++" );
                                Thread.sleep(2000);
                                OkSocket.open(ifo).option(mOkOptions).connect();
                            }catch (Exception el)
                            {}

                        }
                    }
                }).start();

            }
        });


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