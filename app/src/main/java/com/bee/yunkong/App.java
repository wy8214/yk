package com.bee.yunkong;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.bee.yunkong.util.common.DeviceUtil;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Bean.ApkBean;
import com.hwytapp.Bean.CmdBean;
import com.hwytapp.Bean.GroupBean;
import com.hwytapp.Bean.PhoneBean;
import com.hwytapp.Bean.QueueTaskBean;
import com.hwytapp.Bean.QueueTaskItemBean;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.UseFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sdcard/fengchaoyun  约定用来存放apk的位置
 * //todo app启动的时候检查这个目录是否存在，如果没有则需要建立这个目录
 */
public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInit();
        instance = this;
        String processName = DeviceUtil.getProcessName(getInstance(), android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(BuildConfig.APPLICATION_ID);
            //防止子进程也进行初始化
            if (defaultProcess) {
                init();
            }
        }
    }

    public void appInit()
    {
        setSocketIp("47.96.23.135"); //初始化全局变量
        setSocketPort(9502);
        setXpReadPackageName("");
        setCurrentApkPackName("null");
        setDeviceType("0");
        setTopActivity("");
        setIsShellRun(false);
        setIsMasterPhone(false);
        setIsKeyevent(false);
        setIsRecode(false);
        setIsFloatClick(false);
        setIsAbRun(false);
        setLastRecTime(System.currentTimeMillis());
        setLastRecTime(Long.parseLong("0"));
        setPhoneList(new ArrayList<PhoneBean>());
        setPhoneMap(new ConcurrentHashMap<String, PhoneBean>());
        setCmdList(new ArrayList<String>());
        setPackageList(new ArrayList<String>());
        setCmdMap(new HashMap<String,CmdBean>());
        setApkMap(new HashMap<String,ApkBean>());
        setGroupMap(new HashMap<String, GroupBean>());
        setQueueTaskMap(new HashMap<String, QueueTaskBean>());
        setQueueTaskItemMap(new HashMap<String, QueueTaskItemBean>());
    }

    private void init() {
        MyLog.init();
        //todo 初始化socket连接
    }

    /**
     * 从apk文件中获取apk信息
     * @param apkFilePath
     * @return
     */
    public static ApkMeta getApkInfoFromApkFile(String apkFilePath) {
        try (ApkFile apkFile = new ApkFile(new File(apkFilePath))) {
            return apkFile.getApkMeta();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String topActivity;

    public String getTopActivity() {
        return topActivity;
    }

    public void setTopActivity(String topActivity) {
        this.topActivity = topActivity;
    }

    private String serverPhoneNum;

    public String getServerPhoneNum() {
        return serverPhoneNum;
    }

    public void setServerPhoneNum(String serverPhoneNum) {
        this.serverPhoneNum = serverPhoneNum;
    }

    private String xpReadPackageName;

    public String getXpReadPackageName() {
        return xpReadPackageName;
    }

    public void setXpReadPackageName(String xpReadPackageName) {
        this.xpReadPackageName = xpReadPackageName;
    }

    private String fd;
    public String getFd(){
        return fd;
    }
    public void setFd(String fd){

        this.fd = fd;
    }

    private String merID;
    public String getMerID(){
        return merID;
    }
    public void setMerID(String merID){
        this.merID = merID;
    }

    private String deviceType;
    public String getDeviceType(){
        return deviceType;
    }
    public void setDeviceType(String deviceType){
        this.deviceType = deviceType;
    }


    private String currentApkPackName;
    public String getCurrentApkPackName(){
        return currentApkPackName;
    }
    public void setCurrentApkPackName(String currentApkPackName){
        this.currentApkPackName = currentApkPackName;
    }

    private Context context;

    public Context getContext(){
        return context;
    }
    public void setContext(Context context){
        this.context = context;
    }


    private ConnectionInfo socketinfo;

    public ConnectionInfo getSocketinfo(){
        return socketinfo;
    }
    public void setSocketinfo(ConnectionInfo socketinfo){
        this.socketinfo = socketinfo;
    }


    private Map<String,PhoneBean> phoneSelectedMap;

    public Map<String,PhoneBean> getPhoneSelectedMap(){
        return phoneSelectedMap;
    }
    public void setPhoneSelectedMap(Map<String,PhoneBean> phoneSelectedMap) {
        this.phoneSelectedMap = phoneSelectedMap;

    }

    private Map<String,PhoneBean> phoneMap;

    public Map<String,PhoneBean> getPhoneMap(){
        return phoneMap;
    }
    public void setPhoneMap(Map<String,PhoneBean> phonemap) {
        this.phoneMap = phonemap;

    }


    private Map<String,PhoneBean> runCmdMap;

    public Map<String,PhoneBean> getRunCmdMap(){
        return runCmdMap;
    }
    public void setRunCmdMap(Map<String,PhoneBean> runCmdMap) {
        this.runCmdMap = runCmdMap;

    }

    private Map<String,GroupBean> groupMap;

    public Map<String,GroupBean> getGroupMap(){
        return groupMap;
    }
    public void setGroupMap(Map<String,GroupBean> groupmap) {
        this.groupMap = groupmap;

    }

    private List<PhoneBean> phoneList;
    public List<PhoneBean> getPhoneList(){
        return phoneList;
    }
    public void setPhoneList( List<PhoneBean> list) {
        this.phoneList = list;
    }

    private Map<String,CmdBean> cmdMap;
    public  Map<String,CmdBean> getCmdMap(){
        return cmdMap;
    }
    public void setCmdMap( Map<String,CmdBean> list) {
        this.cmdMap = list;
    }

    private Map<String,QueueTaskBean> queueTaskMap;
    public  Map<String,QueueTaskBean> getQueueTaskMap(){
        return queueTaskMap;
    }
    public void setQueueTaskMap( Map<String,QueueTaskBean> list) {
        this.queueTaskMap = list;
    }

    private Map<String,QueueTaskItemBean> queueTaskItemMap;
    public  Map<String,QueueTaskItemBean> getQueueTaskItemMap(){
        return queueTaskItemMap;
    }
    public void setQueueTaskItemMap( Map<String,QueueTaskItemBean> list) {
        this.queueTaskItemMap = list;
    }

    private Map<String,ApkBean> apkBean;
    public  Map<String,ApkBean> getApkMap(){
        return apkBean;
    }
    public void setApkMap( Map<String,ApkBean> list) {
        this.apkBean = list;
    }

    private boolean ismasterphone;
    public boolean getIsMasterPhone(){
        return ismasterphone;
    }
    public void setIsMasterPhone(boolean ismasterphone){
        this.ismasterphone = ismasterphone;
    }

    private String socketIp;
    public String getSocketIp(){
        return socketIp;
    }
    public void setSocketIp(String socketIp){
        this.socketIp = socketIp;
    }

    private String merPhone;
    public String getMerPhone(){
        return merPhone;
    }
    public void setMerPhone(String merPhone){
        this.merPhone = merPhone;
    }

    private int socketPort;
    public int getSocketPort(){
        return socketPort;
    }
    public void setSocketPort(int socketPort){
        this.socketPort = socketPort;
    }

    private boolean isAbRun;
    public boolean getIsAbRun(){
        return isAbRun;
    }
    public void setIsAbRun(boolean isAbRun){
        this.isAbRun = isAbRun;
    }

    private boolean isShellRun;
    public boolean getIsShellRun(){
        return isShellRun;
    }
    public void setIsShellRun(boolean isShellRun){
        this.isShellRun = isShellRun;
    }

    private boolean isFloatClick;
    public boolean getIsFloatClick(){
        return isFloatClick;
    }
    public void setIsFloatClick(boolean isFloatClick){
        this.isFloatClick = isFloatClick;
    }

    private boolean isKeyevent;
    public boolean getIsKeyevent(){
        return isKeyevent;
    }
    public void setIsKeyevent(boolean isKeyevent){
        this.isKeyevent = isKeyevent;
    }

    private boolean isRecode;
    public boolean getIsRecode(){
        return isRecode;
    }
    public void setIsRecode(boolean isRecode){
        this.isRecode = isRecode;
    }

    private Long lastRecTime;
    public Long getLastRecTime(){
        return lastRecTime;
    }
    public void setLastRecTime(Long lastRecTime){
        this.lastRecTime = lastRecTime;
    }

    private List<String> packageList;
    public List<String> getPackageList(){
        return packageList;
    }
    public void setPackageList( List<String> list) {
        this.packageList = list;
    }

    private List<String> cmdList;
    public List<String> getCmdList(){
        return cmdList;
    }
    public void setCmdList( List<String> list) {
        this.cmdList = list;
    }

    private List<Runnable> runnableList;

    public List<Runnable> getRunnableList(){
        return runnableList;
    }
    public void setRunnableList( List<Runnable> list) {
        this.runnableList = list;
    }

    private Handler handler;

    public Handler getHandler(){
        return handler;
    }
    public void setHandler(Handler handler){
        this.handler = handler;
    }
}

