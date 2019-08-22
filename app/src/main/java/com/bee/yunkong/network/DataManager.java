package com.bee.yunkong.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.bee.yunkong.util.common.DeviceUtil;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Bean.ApkBean;
import com.hwytapp.Bean.CmdBean;
import com.hwytapp.Bean.GroupBean;
import com.hwytapp.Bean.PhoneBean;
import com.hwytapp.Bean.QueueTaskBean;
import com.hwytapp.Bean.QueueTaskItemBean;
import com.hwytapp.Common.Config;
import com.hwytapp.Common.MasterMethod;
import com.hwytapp.Utils.AppUtils;

import net.dongliu.apk.parser.bean.ApkMeta;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 与接口的交互类
 */
public class DataManager {
    public static class FailData {
        /**
         * 失败的原因
         */
        public String msg;
        /**
         * 失败码
         */
        public int code;


    }

    public interface DataCallBack<T> {
        void onSuccess(T result);

        void onFail(FailData result);
    }

    public static class DeviceTypeResponse {
        /**
         * 是否是主控
         */
        public boolean isMaster;

        public String deviceType;
    }

    /**
     * @see DeviceTypeResponse
     * @see FailData
     * @see DataCallBack
     * 获取设备的类型，是主控还是被控机
     * 如果成功，则返回DeviceTypeResponse
     * 如果检测失败则在fail中返回
     */
    public static void getDeviceType(String imei,final Context context, final DataCallBack<DeviceTypeResponse> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                App app = (App) context.getApplicationContext();

                while (true) {
                    SystemClock.sleep(2000);
                    if (app.getSocketinfo() != null) {
                        MasterMethod mm = new MasterMethod(app);
                        app.appInit();
                        mm.initDevice();
                        break;
                    }
                }

                DeviceTypeResponse response = new DeviceTypeResponse();
                int index = 0;
                while (true)
                {
                    index++;
                    SystemClock.sleep(2000);
                    if(!app.getDeviceType().equals("0"))
                    {
                        response.deviceType = app.getDeviceType();
                        callBack.onSuccess(response);
                        break;
                    }

                    if(index>5)
                    {
                        FailData fd = new FailData();
                        fd.msg = "获取服务器数据异常";
                        callBack.onFail(fd);
                        break;
                    }
                    index++;
                }

//                callBack.onFail(new FailData());
            }
        }).start();
    }


    public static class QueueTaskInfo implements Serializable {
        public String ID;//任务ID
        public String taskName;//任务名称
        public String taskNum;//任务数
        public String taskTime;//任务时长
        public String createTime;//创建时间
        public String status;
    }

    /**
     * 编辑任务队列
     *
     */
    public static void editQueueTask( Context context,final String taskName,final String taskID ,final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MasterMethod mm = new MasterMethod(app);
                mm.editQueueTask(taskName,taskID);
                callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 删除任务队列
     *
     */
    public static void deleteQueueTask( Context context,final String taskID ,final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MasterMethod mm = new MasterMethod(app);
                mm.deleteQueueTask(taskID);
                callBack.onSuccess(true);

            }
        }).start();
    }

    /**
     * 获取任务队列
     *
     */
    public static void getQueueTaskList( Context context ,final DataCallBack<List<QueueTaskInfo>> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,QueueTaskBean> queueTaskMap = app.getQueueTaskMap();

                List<QueueTaskInfo> result = new ArrayList<>();
                for (QueueTaskBean qt : queueTaskMap.values())
                {
                    QueueTaskInfo item = new QueueTaskInfo();
                    item.ID = qt.getID();
                    item.taskName = qt.getTaskName();
                    item.taskNum = qt.getTaskNum() +" 队列 "+  qt.getTaskTime() + "分钟 " +  qt.getDecviceCount() + "个设备";
                    item.taskTime = qt.getTaskTime();
                    item.createTime =  qt.getCreatedAt();
                    item.status = qt.getStatus().equals("1")?"执行中":"";
                    result.add(item);
                }
                callBack.onSuccess(result);

            }
        }).start();
    }

    /**
     * 执行任务队列
     *
     */
    public static void execQueueTask( Context context,final String taskID ,final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MasterMethod mm = new MasterMethod(app);
                mm.execQueueTask(taskID);
                app.getQueueTaskMap().get(taskID).setStatus("1");
                callBack.onSuccess(true);

            }
        }).start();
    }
    public static void stopQueueTask( Context context,final String taskID ,final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MasterMethod mm = new MasterMethod(app);
                mm.stopQueueTask(taskID);
                app.getQueueTaskMap().get(taskID).setStatus("0");
                callBack.onSuccess(true);

            }
        }).start();
    }


    public static class QueueTaskItem implements Serializable {
        public String ID;
        public String queueTaskID;
        public String apkName;
        public String packageName;
        public String execTime;
        public String CreatedAt;
        public String status;
        public String logoUrl;
    }

    /**
     * 编辑任务队列
     *
     */
    public static void editQueueTaskItem( Context context,final QueueTaskItem qti,final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isExsist = false;
                Map<String,QueueTaskItemBean> queueTaskItemMap = app.getQueueTaskItemMap();


                if(qti.ID==null)
                {
                    for (QueueTaskItemBean qtiVal : queueTaskItemMap.values())
                    {
                        if(qti.queueTaskID.equals(qtiVal.getQueueTaskID())&&qti.packageName.equals(qtiVal.getPackageName()))
                        {
                            isExsist = true;
                            break;
                        }
                    }
                }

                if(isExsist)
                {
                    DataManager.FailData data = new FailData(); ;
                    data.msg = "APP已存在队列中";
                    callBack.onFail(data);
                    return;
                }


                MasterMethod mm = new MasterMethod(app);
                mm.editQueueTaskItem( qti.queueTaskID, qti.ID, qti.apkName, qti.packageName, qti.execTime);
                callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 删除任务队列
     *
     */
    public static void deleteQueueTaskItem( Context context,final String itemID ,final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MasterMethod mm = new MasterMethod(app);
                mm.deleteQueueTaskItem(itemID);
                callBack.onSuccess(true);

            }
        }).start();
    }

    /**
     * 获取任务队列
     *
     */
    public static void getQueueTaskItemList(final Context context , final QueueTaskInfo qtInfo, final DataCallBack<List<QueueTaskItem>> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,QueueTaskItemBean> queueTaskItemMap = app.getQueueTaskItemMap();

                List<QueueTaskItem> result = new ArrayList<>();


                for (QueueTaskItemBean qti : queueTaskItemMap.values())
                {
                    if(qti.getQueueTaskID()!=null&&qti.getQueueTaskID().equals(qtInfo.ID))
                    {
                        QueueTaskItem item = new QueueTaskItem();

                        item.ID = qti.getID();
                        item.queueTaskID = qti.getQueueTaskID();
                        item.apkName = qti.getApkName();
                        item.packageName = qti.getPackageName();
                        item.execTime = qti.getExecTime();
                        item.CreatedAt =   qti.getCreatedAt();
                        item.status = "";
                        item.logoUrl = getApkByPackageName(context,qti.getPackageName());
                        result.add(item);
                    }
                }
                callBack.onSuccess(result);

            }
        }).start();
    }

    private  static String getApkByPackageName(Context context,String packageName)
    {
        final App app = (App) context.getApplicationContext();
        for(com.hwytapp.Bean.ApkBean ab : app.getApkMap().values())
        {
            if(ab.getApkPackageName().equals(packageName))
            {
                return  Config.APKDIR + ab.getFileName();
            }
        }

        return "";

    }

    public static void submitQueueTaskDevices(Context context , final String taskID, final List<DeviceXInfo> deviceList, final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    MasterMethod mm = new MasterMethod(app);
                    mm.deleteQueueTaskDevice(taskID);

                    Thread.sleep(1000);
                    for(DeviceXInfo d : deviceList)
                    {
                        mm.addQueueTaskDevice(taskID,d.id);
                    }
                    callBack.onSuccess(true);
                }catch (Exception e)
                {}


            }
        }).start();
    }

    /**
     * 扫描成功后的返回数据
     */
    public static class ControlledDeviceInfo implements Serializable {
        public String locationName;//位置编号
        public String deviceCode;//设备串号
        public String masterName;//所属账号
        public String myNumber;//本机手机号
        public String groupName;//组名
        public String y;//排
        public String x;//台
        public String code;
    }

    /**
     * 受控机等待主控扫描回调，只有返回成功，其他不用返回
     *
     * @param imei
     * @param callBack
     */
    public static void waitForScan( Context context,final String imei, final DataCallBack<Boolean> callBack) {

        final App app = (App) context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {

                int index = 0;
                while (true)
                {
                    SystemClock.sleep(2000);
                    if(app.getPhoneMap().get(imei)!=null)
                    {
                        MasterMethod mm = new MasterMethod(app);
                        mm.initDevice();
                        //发送获取手机号短信，获取手机号
                        mm.sendPhoneNum();
                        callBack.onSuccess(true);
                        break;
                    }
                }
            }
        }).start();
    }

    /**
     * 根据imei获取受控机信息
     *
     * @param imei
     * @param callBack
     */
    public static void getDeviceInfoOfControlled(final Context context,final String imei, final DataCallBack<ControlledDeviceInfo> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);

                App app = (App)context.getApplicationContext();
                Map<String,PhoneBean> phoneBeanMap = app.getPhoneMap();
                PhoneBean pb = phoneBeanMap.get(imei);

                ControlledDeviceInfo info = new ControlledDeviceInfo();
                info.locationName = pb.getMobileCode();
                info.deviceCode = imei;
                info.masterName = app.getMerPhone();
                info.myNumber = pb.getPhone_number();
                info.groupName = pb.getGroup();
                info.y = pb.getLocationY();
                info.x = pb.getLocationX();
                info.code = pb.getMobileCode();
                callBack.onSuccess(info);
            }
        }).start();
    }


    /**
     * 受控机在余额不足的时候的通知页面状态发生变化，在onSuccess中回调
     * ture代表已断开
     *
     * @param callBack
     */
    public static void disConnect(final DataCallBack<Boolean> callBack) {
        //todo 当余额不足的时候通知页面连接已断开
        // callBack.onSuccess(true);
    }

    /**
     * 恢复出厂设置
     *
     * @param callBack
     */
    public static void resetToStart(Context context,final String imei, final DataCallBack<Boolean> callBack) {

        final  App app = (App)context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500);
                MasterMethod mm = new MasterMethod(app);
                mm.recoverMobile(imei);

                callBack.onSuccess(true);
            }
        }).start();
    }

    public static class GroupInfo {

        public String groupID;
        public String name;//组名
        public String code;//组编码
    }

    /**
     * 受控端获取可用的组
     *
     * @param imei
     * @param callBack
     */
    public static void getGroups(Context context,String imei, final DataCallBack<List<GroupInfo>> callBack) {


        final  App app = (App)context;

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                Map<String,GroupBean> groupMap = app.getGroupMap();

                List<GroupInfo> result = new ArrayList<>();
                for (GroupBean gb : groupMap.values())
                {
                    GroupInfo item = new GroupInfo();
                    item.name = gb.getGroupName();
                    item.groupID = gb.getGroupID()+"";
                    item.code = gb.getCode()+"";
                    result.add(item);
                }
                callBack.onSuccess(result);
            }
        }).start();

    }

    /**
     * 受控端修改位置信息
     *
     * @param groupName
     * @param x
     * @param y
     * @param callBack
     */
    public static void changeControlledAddress(final Context context,final String  serail, final String groupName,final String x,final String y, final DataCallBack<Boolean> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                App app = (App)context.getApplicationContext();

                String code ="";
                for (GroupBean gb : app.getGroupMap().values())
                {
                    if(gb.getGroupName().equals(groupName))
                    {
                        code = gb.getCode();
                    }
                }

                MasterMethod mm = new MasterMethod(app);
                mm.updateMobileGroupInfo(serail,code,groupName,x,y);
                callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 主控机，检验手机号是否是已注册的
     *
     * @param phone
     * @param callBack
     */
    public static void checkMasterPhoneNumber(String phone, final DataCallBack<Boolean> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
               callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 主控机登录校验验证码
     *
     * @param phone
     * @param sms
     * @param callBack
     */
    public static void checkMasterLoginSMS(Context context,final String phone, final String sms,final String imei, final DataCallBack<Boolean> callBack) {

        final App app = (App) context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                //注册主控手机
                try {
                    MasterMethod mm = new MasterMethod(app);
                    mm.masterPhoneRegister(sms,phone);

                    SystemClock.sleep(2000);

                    while (true)
                    {
                        SystemClock.sleep(2000);

                        if(app.getPhoneMap().get(imei)!=null)
                        {
                            mm.initDevice();
                            //发送获取手机号短信，获取手机号
                            mm.sendPhoneNum();
                            callBack.onSuccess(true);
                            break;
                        }

                    }
                }catch (Exception e)
                {}

            }
        }).start();
    }

    public static class DeviceListBean {
        public String name;
        public String phone;
        public boolean error;
    }

    /**
     * 主控机获取所有设备，如果设备列表或者状态发生变化，也从这个接口回调,回调可以长期持有
     *
     * @param callBack
     */
    public static void getAllDevices(Context context,final DataCallBack<List<DeviceListBean>> callBack) {

        final  App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                List<DeviceListBean> result = new ArrayList<>();
                Map<String,PhoneBean> phoneMap= app.getPhoneMap();

                for (PhoneBean pb : phoneMap.values()) {

                    if(pb.isMaster()) {
                        continue;
                    }
                    DeviceListBean item = new DeviceListBean();
                    item.name =  pb.getMobileCode() + "   " + pb.getVerison();
                    item.phone =  pb.getPhone_number() ;
                    item.error = true;
                    if(pb.getStatus().equals("1")) {
                        item.error = false;
                    }
                    result.add(item);
                }
                callBack.onSuccess(result);
            }
        }).start();
    }

    /**
     * 设备定位信息
     */
    public static class LocationInfo {
        public String groupName;
        public int y;
        public int x;
    }

    /**
     * 主控机录入设备第一步，获取默认组和起始位置
     */
    public static void masterAddDeviceGetStartLocationInfo(Context context,final DataCallBack<LocationInfo> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                Map<String,GroupBean> groupBeanMap = app.getGroupMap();

                String groupName = "默认组";
                if(groupBeanMap.size()>0)
                {
                    Set<String> set = groupBeanMap.keySet();
                    Object[] obj = set.toArray();
                    Arrays.sort(obj);
                    GroupBean pb =groupBeanMap.get(obj[0]);
                    groupName = pb.getGroupName();
                }

                int str_x = 1;
                int str_y = 1;

                Map<String,PhoneBean> phoneBeanMap = app.getPhoneMap();

                if(phoneBeanMap.size()>0)
                {
                    Set<String> set = phoneBeanMap.keySet();
                    Object[] obj = set.toArray();
                    Arrays.sort(obj);

                    PhoneBean pb =phoneBeanMap.get(obj[0]);

                    if(!pb.isMaster()) {
                        str_x = Integer.parseInt(pb.getLocationX()) + 1;
                        str_y = Integer.parseInt(pb.getLocationY());
                    }
                }

                LocationInfo info = new LocationInfo();
                info.groupName = groupName;
                info.y = str_y;
                info.x = str_x;
                callBack.onSuccess(info);
            }
        }).start();
    }

    /**
     * 主控端获取可用的组
     *
     * @param callBack
     */
    public static void getMasterGroups(final Context context, final DataCallBack<List<GroupInfo>> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                App app = (App)context.getApplicationContext();

                Map<String,GroupBean> groupMap = app.getGroupMap();

                List<GroupInfo> result = new ArrayList<>();
                for (GroupBean gb : groupMap.values())
                {
                    GroupInfo item = new GroupInfo();
                    item.name = gb.getGroupName();
                    item.groupID = gb.getGroupID()+"";
                    item.code = gb.getCode()+"";
                    result.add(item);
                }

                callBack.onSuccess(result);
            }
        }).start();
    }

    /**
     * 主控端添加组
     *
     * @param groupName
     * @param callBack
     */
    public static void masterAddGroup(final String groupName,final String code, final App app, final DataCallBack<Boolean> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                MasterMethod mm = new MasterMethod(app);
                mm.addMobileGroup(groupName,code,app.getMerID());
                SystemClock.sleep(1000);
                callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 主控端修改组名
     *
     * @param groupNameBefore 之前的名字
     * @param groupNameAfter  改制后的名字
     * @param callBack
     */
    public static void masterEditGroup(final String groupName,final String code,final String groupID, final App app, final DataCallBack<Boolean> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                MasterMethod mm = new MasterMethod(app);
                mm.updateMobileGroup(groupName,code ,groupID);
                callBack.onSuccess(true);
            }
        }).start();
    }


    /**
     * 主控端删除组
     *
     * @param groupName
     * @param callBack
     */
    public static void masterdeleteGroup(final App app,final String groupID, final DataCallBack<Boolean> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                MasterMethod mm = new MasterMethod(app);
                mm.deleteMobileGroup(groupID,app.getMerID());

                callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 主控端 添加设备操作
     *
     * @param groupName
     * @param y         排
     * @param x         台
     * @param callBack
     */
    public static void masterAddDevice(final String clientSerail,String groupName, int y, int x,final App app, final DataCallBack<Boolean> callBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Map<String,PhoneBean> phoneMap  = app.getPhoneMap();
                int index = 0;
                while (true)
                {
                    SystemClock.sleep(1000);

                    if(phoneMap.get(clientSerail)!=null)
                    {
                        callBack.onSuccess(true);
                        break;
                    }

                    if(index>5)
                    {
                        FailData failData = new FailData();
                        failData.msg = "当前位置已经录入过了";
                        callBack.onFail(failData);
                        break;
                    }

                    index++;

                }


            }
        }).start();
    }

    public static class ApkBean {
        public ApkBean() {
        }

        public ApkBean(String apkID,String logoUrl, String appName, int state, String stateName, String uploadProgress, String installProgress) {
            this.apkID = apkID;
            this.logoUrl = logoUrl;
            this.appName = appName;
            this.state = state;
            this.stateName = stateName;
            this.uploadProgress = uploadProgress;
            this.installProgress = installProgress;
        }
        public String apkID;
        public String logoUrl;// http://logurl
        public String appName;
        public int state;//1:正在上传---2:正在安装应用 ---- 3:应用已安装   界面用state来判断状态，进行布局调整
        public String stateName;//1:正在上传---2:正在安装应用 ---- 3:应用已安装
        public String uploadProgress;// 80%
        public String installProgress;//  1/50

    }

    /**
     * 主控机获取应用列表，可以主动推送，和设备列表一样操作,回调可以长期持有
     *
     * @param callBack
     */
    public static void masterGetApkList(Context context,final DataCallBack<List<ApkBean>> callBack) {

        final App app = (App)context.getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                List<ApkBean> result = new ArrayList<>();

                Map<String, com.hwytapp.Bean.ApkBean> apkMap= app.getApkMap();

                for (com.hwytapp.Bean.ApkBean apkBean : apkMap.values()) {

                    String apkIconUrl = Config.APKDIR+apkBean.getFileName();
                    result.add(new ApkBean(apkBean.getID(),apkIconUrl,apkBean.getApkName()+"|"+apkBean.getVersion(),apkBean.getState(),apkBean.getStateName(),apkBean.getUploadProgress(), apkBean.getInstalledNum()));
                }

                callBack.onSuccess(result);

            }
        }).start();
    }

    /**
     * 重新安装某个应用
     *
     * @param appName
     * @param callBack
     */
    public static void masterReinstallApk(Context context,final String appID, final DataCallBack<Boolean> callBack) {
        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                MasterMethod mm = new MasterMethod(app);

                mm.reInstallApk(appID);

            }
        }).start();
    }


    /**
     * 删除某个应用
     *
     * @param appName
     * @param callBack
     */
    public static void masterDeleteApk(Context context, final String action, final String appID, final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                MasterMethod mm = new MasterMethod(app);

                if(action.equals("uninstall")) {
                    mm.uninstallApk(appID);
                }
                if(action.equals("delete"))
                    mm.deleteApk(appID);

                callBack.onSuccess(true);
            }
        }).start();
    }

    /**
     * 获取 sdcard/fengchaoyun 中需要列出在上传界面的文件后缀,只返回后缀，不要加 "."
     */
    public static void masterGetFileType(final DataCallBack<List<String>> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> result = new ArrayList<>();
                result.add("apk");
                callBack.onSuccess(result);
            }
        }).start();
    }

    /**
     * 开始上传文件，这里不用等到上传完成再返回，开始上传了就回调
     *
     * @param apks
     * @param callBack
     */
    public static void masterUploadApks( final Context context, final List<File> apks, final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                Map<String, com.hwytapp.Bean.ApkBean> apkMap = app.getApkMap();
                for(File file : apks)
                {
                    ApkMeta am  = App.getApkInfoFromApkFile(file.getPath());
                    for (com.hwytapp.Bean.ApkBean ab : apkMap.values())
                    {
                        if(ab.getApkPackageName().equals(am.getPackageName()))
                        {
                            continue;
                        }
                    }

                    String packageName = am.getPackageName();
                    String apkName = am.getName();
                    String version = am.getVersionName();

                    Drawable drawable = AppUtils.getApkIcon(context,file.getAbsolutePath());

                    File dir = new File("sdcard/apkicon");
                    if(!dir.exists())
                    {
                        dir.mkdirs();
                    }
                    String imgPath = dir +"/"+packageName+".png";
                    AppUtils.drawableToFile(drawable,imgPath);

                    MasterMethod mm = new MasterMethod(app);
                    mm.uploadApkFile(file.getPath(),apkName,packageName,"",version);

                }


                callBack.onSuccess(true);
            }
        }).start();
    }

    public static class TaskInfo {
        public TaskInfo() {
        }

        public TaskInfo(String taskImageUrl, String taskName,String taskCode, String taskAddTime, String taskId,String status) {
            this.taskImageUrl = taskImageUrl;
            this.taskName = taskName;
            this.taskCode = taskCode;
            this.taskAddTime = taskAddTime;
            this.taskId = taskId;
            this.status = status;
        }

        public String taskImageUrl;
        public String taskCode;
        public String taskName;
        public String taskAddTime;
        public String taskId;
        public String status;
    }

    /**
     * 主控机获取脚本列表，可长期持有回调，实时变化
     *
     * @param callBack
     */
    public static void masterGetTaskList(Context context,final DataCallBack<List<TaskInfo>> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                Map<String,CmdBean> cmdBeanMap = app.getCmdMap();
                List<TaskInfo> result = new ArrayList<>();
                for(CmdBean cb : cmdBeanMap.values())
                {
                    String status ="";
                    if(cb.isSelected())
                        status ="执行中";
                    result.add(new TaskInfo("", cb.getName(),cb.getCmdCode(), cb.getCreatedAt(), cb.getID()+"",status));
                }

//                for (int i = 0; i < 10; i++) {
//                    result.add(new TaskInfo("", "脚本" + i, "创建时间", "id"));
//                }
                callBack.onSuccess(result);
            }
        }).start();
    }

    /**
     * 主控端编辑脚本名称
     *
     * @param newName
     * @param taskId
     * @param callBack
     */
    public static void masterEditTask(Context context,  final String cmdCode, final String cmdName, final String cmdID, final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                try {
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("name", cmdName);
                    jsonData.put("cmd_code", cmdCode);

                    MasterMethod mm = new MasterMethod(app);
                    mm.updateCmd(cmdID,jsonData);

                    callBack.onSuccess(true);

                }catch (Exception e)
                {}

            }
        }).start();
    }

    /**
     * 主控端删除脚本
     *
     * @param taskId
     * @param callBack
     */
    public static void masterDeleteTask(Context context,final String taskId, final DataCallBack<Boolean> callBack) {
        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                MasterMethod mm = new MasterMethod(app);
                mm.deleteCmd(taskId);
                callBack.onSuccess(true);
            }
        }).start();
    }

    public static class DeviceGroupInfo {
        @Override
        public String toString() {
            return groupName;
        }

        public String groupName;//组名
        public List<DeviceYInfo> yInfos; // 排列表
        public boolean choose = false;//这是前端用的，不用管
    }

    public static class DeviceYInfo {
        public int y;//排名
        public boolean choose = true;//这是前端用的，不用管
    }

    public static class DeviceXInfo implements Serializable {
        public String name;//例 A01001
        public String id;//这是方便后端接口的
        public boolean choose = false;//这是前端用的，不用管
    }

    /**
     * 主控机获取所有组信息，需要知道 DeviceGroupInfo 和 DeviceYInfo 这两层信息，前端先获取组信息，再
     * 拿到组信息中的第一条组信息来获取设备列表 见 masterGetDevices 方法
     */
    public static void masterGetAllGroups(Context context,final DataCallBack<List<DeviceGroupInfo>> callBack) {

        final App app = (App)context;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);

                        Map<String,GroupBean> groupBeanMap = app.getGroupMap();
                        Map<String,PhoneBean> phoneBeanMap = app.getPhoneMap();

                        List<DeviceGroupInfo> result = new ArrayList<>();
                        List<DeviceYInfo> deviceYInfos ;

                        DeviceGroupInfo groupInfo;
                        for(GroupBean gb : groupBeanMap.values())
                        {
                            groupInfo = new DeviceGroupInfo();
                            deviceYInfos = new ArrayList<>();

                            HashSet<String> yHash = new HashSet<String>();

                            for(PhoneBean pb : phoneBeanMap.values())
                            {
                                if(pb.isMaster()) {
                                    continue;
                                }

                                if(pb.getGroup().equals(gb.getGroupName()))
                                {

                                    yHash.add(pb.getLocationY());

                                }
                            }

                            for (String s : yHash)
                            {
                                DeviceYInfo item = new DeviceYInfo();
                                item.y = Integer.parseInt(s);
                                deviceYInfos.add(item);

                            }

                            groupInfo.groupName = gb.getGroupName();
                            groupInfo.yInfos = deviceYInfos;

                            result.add(groupInfo);
                        }


                        callBack.onSuccess(result);
                    }
                }
        ).start();
    }

    /**
     * 根据组列表获取设备列表，列表中的List<DeviceYInfo> yInfos  表示选中的是哪几排
     *
     * @param groupInfos
     * @param callBack
     */
    public static void masterQueueTaskGetDevices(Context context,final List<DeviceGroupInfo> groupInfos,final String cmdID, final DataCallBack<List<DeviceXInfo>> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                Map<String,PhoneBean> phoneBeanMap = app.getPhoneMap();

                if(groupInfos.size()>0) {
                    for (PhoneBean pb : phoneBeanMap.values()) {
                        pb.setIsSelected(false);
                    }
                }
                app.setPhoneMap(phoneBeanMap);

                for (DeviceGroupInfo group : groupInfos)
                {
                    for (DeviceYInfo dy : group.yInfos)
                    {
                        for (PhoneBean pb : phoneBeanMap.values())
                        {
                            if(dy.choose&&pb.getGroup().equals(group.groupName)&&pb.getLocationY().equals(dy.y+""))
                            {
                                pb.setIsSelected(true);
                            }
                        }
                    }
                }

                app.setPhoneMap(phoneBeanMap);

                phoneBeanMap = app.getPhoneMap();
                List<DeviceXInfo> result = new ArrayList<>();
                for (PhoneBean pb : phoneBeanMap.values())
                {
                    if(pb.isMaster()||pb.getStatus().equals("0")||!pb.isSelected()) {
                        continue;
                    }

                    DeviceXInfo item = new DeviceXInfo();
                    item.id = pb.getSerila()+"";
                    item.name = pb.getMobileCode();
                    item.choose = pb.isSelected();
                    result.add(item);
                }
                callBack.onSuccess(result);
            }
        }).start();
    }

    /**
     * 根据组列表获取设备列表，列表中的List<DeviceYInfo> yInfos  表示选中的是哪几排
     *
     * @param groupInfos
     * @param callBack
     */
    public static void masterGetDevices(Context context,final List<DeviceGroupInfo> groupInfos,final String cmdID, final DataCallBack<List<DeviceXInfo>> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);

                Map<String,PhoneBean> phoneBeanMap = app.getPhoneMap();

                if(groupInfos.size()>0) {
                    for (PhoneBean pb : phoneBeanMap.values()) {
                        pb.setIsSelected(false);
                    }
                }
                app.setPhoneMap(phoneBeanMap);

                for (DeviceGroupInfo group : groupInfos)
                {
                    for (DeviceYInfo dy : group.yInfos)
                    {
                        for (PhoneBean pb : phoneBeanMap.values())
                        {
                            if(dy.choose&&pb.getGroup().equals(group.groupName)&&pb.getLocationY().equals(dy.y+""))
                            {
                                pb.setIsSelected(true);
                            }
                        }
                    }
                }

                app.setPhoneMap(phoneBeanMap);

                phoneBeanMap = app.getPhoneMap();
                List<DeviceXInfo> result = new ArrayList<>();
                for (PhoneBean pb : phoneBeanMap.values())
                {
                    if(pb.isMaster()||pb.getStatus().equals("0")||!pb.isSelected()) {
                        continue;
                    }

                    DeviceXInfo item = new DeviceXInfo();
                    item.id = pb.getSerila()+"";
                    item.name = pb.getMobileCode();

                    if(!TextUtils.isEmpty(cmdID))
                    {
                        String cmdPhoneID = app.getCmdMap().get(cmdID).getCmdPhoneID();

                        if(cmdPhoneID!=null&&cmdPhoneID.contains(pb.getId()+""))
                        {
                            item.name += " |  "+app.getCmdMap().get(cmdID).getName()+"脚本执行中";
                        }
                    }


                    item.choose = pb.isSelected();
                    result.add(item);
                }
                callBack.onSuccess(result);
            }
        }).start();
    }

    /**
     * 主控机获取可用应用列表，录制脚本中选择应用界面  使用
     *
     * @param callBack
     */
    public static void masterGetUseableApkList(Context context,final DataCallBack<List<ApkBean>> callBack) {
        final  App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                List<ApkBean> result = new ArrayList<>();
                Map<String, com.hwytapp.Bean.ApkBean> apkMap= app.getApkMap();

                for (com.hwytapp.Bean.ApkBean apkBean : apkMap.values()) {

                    result.add(new ApkBean(apkBean.getID(),Config.APKDIR+apkBean.getFileName(),apkBean.getApkName(),apkBean.getState(),apkBean.getStateName(),apkBean.getUploadProgress(), apkBean.getInstalledNum()));
                }
                callBack.onSuccess(result);

            }
        }).start();
    }


    /**
     * 开始录制脚本
     *
     * @param xInfos   选中的设备列表
     * @param apkBean  选中的app 可能为null
     * @param callBack 回调中的String参数是脚本开始成功返回的id
     */
    public static void masterStartRecordScrpit(Context context, final List<DeviceXInfo> xInfos, final ApkBean apkBean, final DataCallBack<Boolean> callBack) {

        final App app =(App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                app.setIsKeyevent(true);
                app.setIsRecode(true);
                app.setIsFloatClick(true);

                Map<String,PhoneBean>  phoneBeanMap = app.getPhoneMap();

//                for (PhoneBean pb : phoneBeanMap.values())
//                {
//                    pb.setIsSelected(false);
//                }
//
//                app.setPhoneMap(phoneBeanMap);

                for(DeviceXInfo dx : xInfos )
                {
                    phoneBeanMap.get(dx.id).setIsSelected(true);
                }

                app.setPhoneMap(phoneBeanMap);


                MasterMethod mm = new MasterMethod(app);
                Map<String, com.hwytapp.Bean.ApkBean>  apkMap = app.getApkMap();
                if(apkBean!=null)
                {
                    String packageName = apkMap.get(apkBean.apkID).getApkPackageName();
                    app.setCmdList(new ArrayList<String>() );
                    List<String> cmdList = app.getCmdList();

                    cmdList.add("open_apk@@@"+packageName+"$$$0");

                    app.setLastRecTime(System.currentTimeMillis());

                    app.setCmdList(cmdList);
                    mm.openApk(apkBean.apkID,apkMap.get(apkBean.apkID).getApkPackageName());
                }

                callBack.onSuccess(true);
            }
        }).start();

    }


    /**
     * 开始自动阅读APP
     *
     * @param xInfos   选中的设备列表
     * @param apkBean  选中的app 可能为null
     * @param callBack 回调中的String参数是脚本开始成功返回的id
     */
    public static void masterStartAutoRunScrpit(Context context, final List<DeviceXInfo> xInfos, final ApkBean apkBean, final DataCallBack<Boolean> callBack) {

        final App app =(App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                app.setIsKeyevent(true);
                app.setIsRecode(true);
                app.setIsFloatClick(true);

                Map<String,PhoneBean>  phoneBeanMap = app.getPhoneMap();

                for(DeviceXInfo dx : xInfos )
                {
                    phoneBeanMap.get(dx.id).setIsSelected(true);
                }

                app.setPhoneMap(phoneBeanMap);

                MasterMethod mm = new MasterMethod(app);
                Map<String, com.hwytapp.Bean.ApkBean>  apkMap = app.getApkMap();
                if(apkBean!=null)
                {
                    String packageName = apkMap.get(apkBean.apkID).getApkPackageName();
                    app.setCmdList(new ArrayList<String>() );
                    List<String> cmdList = app.getCmdList();
                    cmdList.add("open_apk@@@"+packageName+"$$$0");
                    app.setLastRecTime(System.currentTimeMillis());
                    app.setCmdList(cmdList);

                    mm.autoRunApk(apkBean.apkID,packageName);
                }

                callBack.onSuccess(true);
            }
        }).start();

    }

    /**
     * 录制中---输入手机号
     *
     * @param callBack
     */
    public static void masterScriptInputPhoneNum(Context context,final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);

                        try {
                            MasterMethod mm = new MasterMethod(app);
                            mm.inputPhoneNum();
                        }catch (Exception e)
                        {}
                        callBack.onSuccess(true);
                    }
                }
        ).start();
    }


    /**
     * 任务机统一阅读文章
     *
     * @param callBack
     */
    public static void masterScriptInputReadArticle(Context context,final JSONObject jb,final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        try {
        MasterMethod mm = new MasterMethod(app);
        mm.inputReadArticle(jb);
        }catch (Exception e)
        {
            MyLog.d("xposed hook" + e.toString());
        }
        callBack.onSuccess(true);
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            MasterMethod mm = new MasterMethod(app);
//                            mm.inputReadArticle(jb);
//                        }catch (Exception e)
//                        {
//                            MyLog.d("xposed hook" + e.toString());
//                        }
//                        callBack.onSuccess(true);
//                    }
//                }
//        ).start();
    }


    /**
     * 录制中---输入验证码
     *
     * @param callBack
     */
    public static void masterScriptInputCode(Context context,String sms, final DataCallBack<Boolean> callBack) {
        final App app = (App)context;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);
                        MasterMethod mm = new MasterMethod(app);
                        mm.getClientPhoneVerCode();
                        callBack.onSuccess(true);
                    }
                }
        ).start();
    }

    /**
     * 录制中---暂停
     *
     * @param callBack
     */
    public static void masterScriptPause(final DataCallBack<Boolean> callBack) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);
                        callBack.onSuccess(true);
                    }
                }
        ).start();
    }
    /**
     * 录制中---继续
     *
     * @param callBack
     */
    public static void masterScriptGoOn(Context context,final DataCallBack<Boolean> callBack) {
        final App app = (App)context;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);

                        app.setIsKeyevent(!app.getIsKeyevent());
                        callBack.onSuccess(true);
                    }
                }
        ).start();
    }

    /**
     * 录制中---退出
     *
     * @param callBack
     */
    public static void masterScriptStop(Context context, final String scriptName, final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);

                        MasterMethod mm = new MasterMethod(app);
                        mm.editRecordeCmd(scriptName);

                        callBack.onSuccess(true);
                    }
                }
        ).start();
    }

    /**
     * 主控端--启动脚本
     *
     * @param xInfos
     * @param scriptName
     * @param callBack
     * @param count 执行次数
     */
    public static void masterStartScipt(Context context, final  List<DeviceXInfo> xInfos, final String scriptID, final int count, final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                MasterMethod mm = new MasterMethod(app);


                String cmdPhoneID = "";
                for (PhoneBean pb :app.getPhoneMap().values())
                {
                    if(pb.isSelected()) {
                        pb.setRunCmdID(scriptID);
//                        try {
//                            JSONObject jsonData = new JSONObject();
//                            jsonData.put("run_cmd_id", scriptID);
//
//                            mm.updateMobile(pb.getId()+"",jsonData);
//                        }catch (Exception e)
//                        {}

                        if (cmdPhoneID!="")
                            cmdPhoneID += ",";
                        cmdPhoneID += pb.getId();
                    }
                }


                Map<String,CmdBean> cmdBeanMap = app.getCmdMap();

                cmdBeanMap.get(scriptID).setSelected(true);

                mm.runCmd(scriptID,count+"");

                try {
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("status", "2");
                    jsonData.put("cmd_phone_id", cmdPhoneID);
                    mm.updateCmd(scriptID,jsonData);
                }catch (Exception e)
                {}
                callBack.onSuccess(true);
            }
        }).start();
    }

    public static void masterStopScipt(Context context,  final String scriptID,final DataCallBack<Boolean> callBack) {

        final App app = (App)context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);


                Map<String,CmdBean> cmdBeanMap = app.getCmdMap();

                CmdBean cb = cmdBeanMap.get(scriptID);
                MasterMethod mm = new MasterMethod(app);


                if(cb.getCmdPhoneID().contains(","))
                {
                    String [] strPhoneIDs =  cb.getCmdPhoneID().split(",");

                    for (String pID : strPhoneIDs)
                    {
                        for (PhoneBean pb : app.getPhoneMap().values())
                        {
                            if(pID.equals(pb.getId()+""))
                            {
                                mm.stopCmd(scriptID,pb);
                            }
                        }
                    }
                }

                cmdBeanMap.get(scriptID).setSelected(false);


                try {
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("status", "1");
                    jsonData.put("cmd_phone_id", "");
                    mm.updateCmd(scriptID,jsonData);
                }catch (Exception e)
                {}
                callBack.onSuccess(true);
            }
        }).start();
    }

    public static class SimCardInfo{
        public String deviceId;
        public String tel;
        public String simei;
        public String imsi;

        @Override
        public String toString() {
            return "SimCardInfo{" +
                    "deviceId='" + deviceId + '\'' +
                    ", tel='" + tel + '\'' +
                    ", simei='" + simei + '\'' +
                    ", imsi='" + imsi + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DataManager{}";
    }

    /**
     * 手机卡状态监听
     *
     * @param callBack
     */
    public static void updatePhoneState(SimCardInfo info, final DataCallBack<Boolean> callBack) {
        MyLog.d(info.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                callBack.onSuccess(true);
            }
        }).start();
    }


    /**
     * 新增功能---更改设备类型
     *
     * @param isMaster 是否是主控
     * @param callBack
     */
    public static void changeRollType(boolean isMaster, final DataCallBack<Boolean> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                callBack.onSuccess(true);
            }
        }).start();
    }
}











































































