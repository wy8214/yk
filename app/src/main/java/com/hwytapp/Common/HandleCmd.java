package com.hwytapp.Common;

import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.util.common.DeviceUtil;
import com.hwytapp.Bean.ApkBean;
import com.hwytapp.Bean.CmdBean;
import com.hwytapp.Bean.GroupBean;
import com.hwytapp.Bean.PhoneBean;
import com.hwytapp.Bean.QueueTaskBean;
import com.hwytapp.Bean.QueueTaskItemBean;
import com.hwytapp.Bean.TestSendData;
import com.hwytapp.Interface.CallBack;
import com.hwytapp.Utils.AppUtils;
import com.hwytapp.Utils.MobileUtils;
import com.hwytapp.Utils.SmsWriteOpUtil;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;

import net.dongliu.apk.parser.bean.ApkMeta;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class HandleCmd {

    private App ha ;

    public  HandleCmd(App ha )
    {
        this.ha = ha;
    }

    public  void resolveHandle(JSONObject jb) throws Exception
    {

        String cmd = jb.get("cmd").toString();

        switch (cmd)
        {
            case "notice":
                MyEvent me = new MyEvent(EventTag.server_notice);
                me.setObject(jb.get("info").toString());
                EventBus.getDefault().post(me);
                break;

            case "retry_init":
                retryInit();
                break;
            case "init":
                initHandle(jb);
                break;
            case "get_phone_list":
                getPhoneListHandle(jb);
                break;
            case "distribute":
                exeCmd(jb);
                break;
            case "open_apk":
                openAPk(jb);
                break;
            case "auto_read_apk":
                autoReadApk(jb);
                break;

            case "send_phone_num":
                sendPhoneNum(jb);
                break;
            case "clip_code":
                clipCode(jb);
                break;
            case "send_ver_code":
                sendVerificationCode();
                break;
            case "cmd_list":
                getCmdListHandle(jb);
                break;
            case "cmd_update":
                updateCmd(jb);
                break;
            case "apk_list":
                getApkListHandle(jb);
                break;
            case "delete_cmd":
                deleteCmdHandle(jb);
                break;
            case "re_install_apk":
                reInstallApkHandle(jb);
                break;
            case "update_apk_status":
                updateApkStatus(jb);
                break;
            case "get_group_list":
                getMobileGroupListHandle(jb);
                break;
            case "recover_mobile":
                recoverMobileHandle(jb);
                break;
            case "delete_apk_file":
                deleteApkFileHandle(jb);
                break;
            case "input_phone_num":
                inputPhoneNumHandle(jb);
                break;
            case "run_cmd_script":
                runCmdScriptHandle(jb);
                break;
            case "stop_cmd_script":
                stopCmdScriptHandle();
                break;
            case "queue_task_list":
                getQueueTaskListHandle(jb);
                break;
            case "delete_queue_task":
                deleteQueueTaskHandle(jb);
                break;
            case "queue_task_item_list":
                getQueueTaskItemHandle(jb);
                break;
            case "delete_queue_task_item":
                deleteQueueTaskItemHandle(jb);
                break;
            case "client_queue_task_item":
                execQueueTaskItemHandle(jb);
                break;

            case "pulse_queue_task":
                pulseQueueTaskHandle(jb);
                break;

            case "xpread_article":
                getXPreadArticle(jb);
                break;
            default:
                break;
        }
    }


    private void retryInit()
    {
        ha.appInit();
        EventBus.getDefault().post(new MyEvent(EventTag.master_changed));
    }

    private void clipCode(JSONObject jb) throws Exception
    {

        String code = jb.get("code").toString();

        for (int x = 0; x < code.length(); x++) {
            try {
                execShellCmd("input text " + code.charAt(x));
                Thread.sleep(500);
            }catch (Exception e)
            {}

        }
//        String code = jb.get("code").toString();
//        Context context = this.ha.getContext();
//        ClipboardManager c= (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
//        c.setText(code);
    }

    //手机初始化
    private void initHandle(JSONObject jb) throws Exception
    {
        ha.setDeviceType(jb.get("is_master")+"");
        ha.setMerPhone(jb.get("mer_phone")+"");
        ha.setServerPhoneNum(jb.get("server_phone")+"");
        ha.setMerID(jb.get("mer_id")+"");
        ha.setFd(jb.get("fd")+"");
        //如果是主控手机
        if(jb.get("is_master").equals("2"))
        {
            ha.setIsMasterPhone(true);
            Context context = ha.getContext();

            Intent it = new Intent(context, com.hwytapp.Service.KeyEventService.class);
            it.setPackage("com.hwytapp.Service.KeyEventService");
            Bundle b1 = new Bundle();
            b1.putString("param", "s1");
            it.putExtras(b1);
            context.startService(it);
        }
        MyEvent me = new MyEvent(EventTag.server_notice);
        me.setObject("初始化成功");
        EventBus.getDefault().post(me);
    }

    private void  inputPhoneNumHandle(JSONObject jb) throws Exception
    {
        Map<String,PhoneBean> phoneBeanMap  = this.ha.getPhoneMap();
        PhoneBean pb = phoneBeanMap.get(MobileUtils.getMoblieIMEI(this.ha.getContext()));
        String cmd = "input text " + pb.getPhone_number();
        execShellCmd(cmd);
    }


    private void  runCmdScriptHandle(JSONObject jb) throws Exception
    {
        ha.setRunnableList(new ArrayList<Runnable>());
        ha.setHandler(new Handler());

        String cmdID = jb.get("cmd_id").toString();
        int runNum = Integer.parseInt(jb.get("run_num").toString());

        Map<String,CmdBean> cmdBeanMap  = this.ha.getCmdMap();
        CmdBean cb =cmdBeanMap.get(cmdID);


        final String localPath = cb.getLocalPath() ;
        final String localFileName = cb.getFileName();

        File file = new File(localPath + localFileName);

        //如果文件不存在，下载文件
        if(file.exists()) {
            int length=(int)file.length();
            byte[] buff=new byte[length];
            FileInputStream fin=new FileInputStream(file);
            fin.read(buff);
            fin.close();
            String strCmd=new String(buff,"UTF-8");
            String[] cmds = strCmd.split("\\|\\|\\|");

            String[] cmdLast = cmds[cmds.length-1].split("\\$\\$\\$");

            int lastExcTime = Integer.parseInt(cmdLast[1]);


            for (int n=0;n<runNum;n++) {

                Log.i("MyLog","n----------------"+n);

                for (int i = 0; i < cmds.length; i++) {

                    String[] cmd = cmds[i].split("\\$\\$\\$");

                    if(cmd[0].contains("open_apk"))
                    {
                        String [] cmdApk = cmd[0].split("\\@\\@\\@");

                        String packageName = cmdApk[1];

                        List<String>  packList =  new ArrayList<String>();
                        packList.add(packageName);
                        ha.setPackageList(packList);

                        PackageManager packageManager = ha.getContext().getPackageManager();
                        Intent intent = new Intent();
                        intent =packageManager.getLaunchIntentForPackage(packageName);
                        if(intent==null){
                            Toast.makeText(ha.getContext(), "未安装", Toast.LENGTH_LONG).show();
                        }else{
                            ha.getContext().startActivity(intent);
                        }

                    }else {

                        final String command = cmd[0];

                        int execTime = Integer.parseInt(cmd[1]);

                        if (command.equals("end"))
                            continue;


                        int intExecTime  =  Integer.parseInt((n*lastExcTime + execTime)+"");

                        Log.i("MyLog","intExecTime----------------"+intExecTime);


                        Runnable runnable=new Runnable(){

                            @Override
                            public void run() {
                                execShellCmd(command);
                            }
                        };



                        ha.getHandler().postDelayed(runnable, intExecTime);

                        ha.getRunnableList().add(runnable);


                        if(i==(cmds.length-1))
                        {
                            Runnable runnable1=new Runnable(){

                                @Override
                                public void run() {
                                    AppUtils.intent2YK(ha.getContext());
                                }
                            };

                            ha.getHandler().postDelayed(runnable1, intExecTime+2000);

                            ha.getRunnableList().add(runnable1);
                        }


                    }
                }
            }

        }
    }

    private void  stopCmdScriptHandle() throws Exception
    {
        for (Runnable r : ha.getRunnableList())
        {
            ha.getHandler().removeCallbacks(r);
        }
        ha.getHandler().removeCallbacksAndMessages(null);

        AppUtils.intent2YK(ha.getContext());

    }



    //获取命令脚本数据
    private void getCmdListHandle(JSONObject jb) throws Exception
    {
        JSONArray jb_cmd_list = new JSONArray(jb.get("cmd_list").toString());

        Map<String,CmdBean> cmdMap  = this.ha.getCmdMap();
        for (int i=0; i < jb_cmd_list.length(); i++)    {
            CmdBean cb = new CmdBean();
            cb.setID(Integer.parseInt(jb_cmd_list.getJSONObject(i).get("id")+""));
            cb.setName(jb_cmd_list.getJSONObject(i).get("name")+"");
            cb.setFileName(jb_cmd_list.getJSONObject(i).get("file_name")+"");
            cb.setCmdCode(jb_cmd_list.getJSONObject(i).get("cmd_code")+"");
            cb.setApkPackageName(jb_cmd_list.getJSONObject(i).get("apk_package_name")+"");
            cb.setOssFilePath(jb_cmd_list.getJSONObject(i).get("oss_file_path")+"");
            cb.setLocalPath(jb_cmd_list.getJSONObject(i).get("local_path")+"");
            cb.setFileSize(jb_cmd_list.getJSONObject(i).get("file_size")+"");
            cb.setCreatedAt(jb_cmd_list.getJSONObject(i).get("created_at")+"");
            cb.setUpdatedAt(jb_cmd_list.getJSONObject(i).get("updated_at")+"");
            cb.setDeletedAt(jb_cmd_list.getJSONObject(i).get("deleted_at") + "");
            cb.setStatus(jb_cmd_list.getJSONObject(i).get("status") + "");
            cb.setCmdPhoneID(jb_cmd_list.getJSONObject(i).get("cmd_phone_id") + "");
            if(!cb.getDeletedAt().equals("null"))
            {
                cmdMap.put(cb.getID()+"",cb);
                deleteCmdHandle(jb_cmd_list.getJSONObject(i));
                continue;
            }
            cb.setSelected(false);
            if(cb.getStatus().equals("2"))
            {
                cb.setSelected(true);
            }

            cb.setExist(false);
            String appPath = AppUtils.appPath();
            final String localPath = cb.getLocalPath() ;
            final String localFileName = cb.getFileName();

            File localDir = new File(localPath);

            if(!localDir.exists())
                localDir.mkdirs();

            File file = new File(localPath + localFileName);

            //如果文件不存在，下载文件
            if(!file.exists())
            {
                OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
                ofm.downAsyncFile(cb.getOssFilePath(),localPath + localFileName,cb.getID()+"",new CallBack() {

                    //上传成功后回调，更新数据库
                    @Override
                    public void OnSuccess(String id) {
                        //生成命令脚本数据记录
                        try {
                            Map<String,CmdBean> cmdMap = ha.getCmdMap();
                            CmdBean cb = cmdMap.get(id);
                            cb.setExist(true);

                            EventBus.getDefault().post(new MyEvent(EventTag.master_change_tasks));

                            MyEvent me = new MyEvent(EventTag.server_notice);
                            me.setObject(cb.getName()+"下载完成");
                            EventBus.getDefault().post(me);
                        }catch (Exception e)
                        {
                        }
                    }
                    @Override
                    public void OnUploading(String param) {

                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject("下载"+param);
                        EventBus.getDefault().post(me);

                    }

                    @Override
                    public void OnFail(String param)
                    {
                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject("下载失败");
                        EventBus.getDefault().post(me);
                    }
                });

            }
            else{
                cb.setExist(true);
            }

            String id = cb.getID()+"";
            cmdMap.put(id,cb);
        }
        this.ha.setCmdMap(cmdMap);
        EventBus.getDefault().post(new MyEvent(EventTag.master_change_tasks));
    }


    private  void  updateCmd(JSONObject jb) throws Exception
    {

        JSONArray jb_cmd_list = new JSONArray(jb.get("cmd_list").toString());

        Map<String,CmdBean> cmdMap  = this.ha.getCmdMap();
        for (int i=0; i < jb_cmd_list.length(); i++) {
            CmdBean cb = new CmdBean();
            cb.setID(Integer.parseInt(jb_cmd_list.getJSONObject(i).get("id") + ""));
            cb.setName(jb_cmd_list.getJSONObject(i).get("name") + "");
            cb.setFileName(jb_cmd_list.getJSONObject(i).get("file_name") + "");
            cb.setCmdCode(jb_cmd_list.getJSONObject(i).get("cmd_code")+"");
            cb.setApkPackageName(jb_cmd_list.getJSONObject(i).get("apk_package_name") + "");
            cb.setOssFilePath(jb_cmd_list.getJSONObject(i).get("oss_file_path") + "");
            cb.setLocalPath(jb_cmd_list.getJSONObject(i).get("local_path") + "");
            cb.setFileSize(jb_cmd_list.getJSONObject(i).get("file_size") + "");
            cb.setCreatedAt(jb_cmd_list.getJSONObject(i).get("created_at") + "");
            cb.setUpdatedAt(jb_cmd_list.getJSONObject(i).get("updated_at") + "");
            cb.setDeletedAt(jb_cmd_list.getJSONObject(i).get("deleted_at") + "");
            cb.setStatus(jb_cmd_list.getJSONObject(i).get("status") + "");
            cb.setCmdPhoneID(jb_cmd_list.getJSONObject(i).get("cmd_phone_id") + "");
            if(!cb.getDeletedAt().equals("null"))
            {
                cmdMap.put(cb.getID()+"",cb);
                deleteCmdHandle(jb_cmd_list.getJSONObject(i));
                continue;
            }
            cb.setSelected(false);
            if(cb.getStatus().equals("2"))
            {
                cb.setSelected(true);
            }
            cmdMap.put(cb.getID()+"",cb);

        }
        this.ha.setCmdMap(cmdMap);

        EventBus.getDefault().post(new MyEvent(EventTag.master_change_tasks));

    }



    //删除命令脚本文件
    public  void deleteCmdHandle(JSONObject jb) throws Exception
    {
        String cmdID = jb.get("id").toString();
        Map<String,CmdBean> cbMap = ha.getCmdMap();
        CmdBean cb = cbMap.get(cmdID);
        File file = new File(cb.getLocalPath() + cb.getFileName());

        //如果文件不存在，下载文件
        if(file.exists())
        {
            file.delete();
        }
        cbMap.remove(cmdID);
        ha.setCmdMap(cbMap);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "delete_cmd_rel");
        jsonObject.put("action", "client");
        jsonObject.put("mer_id", ha.getMerID()+"");
        jsonObject.put("cmd_id", cmdID);
        jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));
        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
        EventBus.getDefault().post(new MyEvent(EventTag.master_change_tasks));
    }


    private void updateApkStatus(JSONObject jb) throws Exception
    {
        JSONArray jb_apk_list = new JSONArray(jb.get("apk_list").toString());

        Map<String,ApkBean> apkMap  = this.ha.getApkMap();
        for (int i=0; i < jb_apk_list.length(); i++) {

            ApkBean ab = new ApkBean();
            ab.setID(jb_apk_list.getJSONObject(i).get("id") + "");
            ab.setMerID(jb_apk_list.getJSONObject(i).get("mer_id") + "");
            ab.setApkName(jb_apk_list.getJSONObject(i).get("apk_name") + "");
            ab.setApkPackageName(jb_apk_list.getJSONObject(i).get("apk_package_name") + "");
            ab.setApkOssFileUrl(jb_apk_list.getJSONObject(i).get("apk_oss_file_url") + "");
            ab.setApkLocalPath(jb_apk_list.getJSONObject(i).get("apk_local_path") + "");
            ab.setFileName(jb_apk_list.getJSONObject(i).get("file_name") + "");
            ab.setApkFileSize(jb_apk_list.getJSONObject(i).get("apk_file_size") + "");
            ab.setInstalledNum(jb_apk_list.getJSONObject(i).get("installed_count") + "");
            ab.setCreatedAt(jb_apk_list.getJSONObject(i).get("created_at") + "");
            ab.setUpdatedAt(jb_apk_list.getJSONObject(i).get("updated_at") + "");
            ab.setDeleteddAt(jb_apk_list.getJSONObject(i).get("deleted_at") + "");
            ab.setState(Integer.parseInt(jb_apk_list.getJSONObject(i).get("status") + ""));
            ab.setVersion(jb_apk_list.getJSONObject(i).get("apk_version")+"");

            if(!ab.getDeleteddAt().equals("null"))
            {
                deleteApkHandle(jb_apk_list.getJSONObject(i));
                continue;
            }


            if(ab.getState()==1)
            {
                ab.setState(1);
                ab.setUploadProgress("0%");
                ab.setStateName("正在上传");

            }
            else {

                ab.setState(3);
                ab.setStateName("应用已安装");

                if (Integer.parseInt(ab.getInstalledNum()) < ha.getPhoneMap().size()) {
                    ab.setState(2);
                    ab.setStateName("正在安装应用");
                    ab.setInstalledNum(ab.getInstalledNum() + "/" + ha.getPhoneMap().size());
                }
            }

            apkMap.put(ab.getID(),ab);
            EventBus.getDefault().post(new MyEvent(EventTag.master_change_add_apks));
        }

    }

    //获取apk数据
    private void getApkListHandle(JSONObject jb) throws Exception
    {
        JSONArray jb_apk_list = new JSONArray(jb.get("apk_list").toString());

        Map<String,ApkBean> apkMap  = this.ha.getApkMap();
        for (int i=0; i < jb_apk_list.length(); i++)    {

            ApkBean ab = new ApkBean();
            ab.setID(jb_apk_list.getJSONObject(i).get("id")+"");
            ab.setMerID(jb_apk_list.getJSONObject(i).get("mer_id")+"");
            ab.setApkName(jb_apk_list.getJSONObject(i).get("apk_name")+"");
            ab.setApkPackageName(jb_apk_list.getJSONObject(i).get("apk_package_name")+"");
            ab.setApkOssFileUrl(jb_apk_list.getJSONObject(i).get("apk_oss_file_url")+"");
            ab.setApkLocalPath(jb_apk_list.getJSONObject(i).get("apk_local_path")+"");
            ab.setFileName(jb_apk_list.getJSONObject(i).get("file_name")+"");
            ab.setApkFileSize(jb_apk_list.getJSONObject(i).get("apk_file_size")+"");
            ab.setInstalledNum(jb_apk_list.getJSONObject(i).get("installed_count")+"");
            ab.setCreatedAt(jb_apk_list.getJSONObject(i).get("created_at")+"");
            ab.setUpdatedAt(jb_apk_list.getJSONObject(i).get("updated_at")+"");
            ab.setDeleteddAt(jb_apk_list.getJSONObject(i).get("deleted_at")+"");
            ab.setState(Integer.parseInt(jb_apk_list.getJSONObject(i).get("status")+""));
            ab.setVersion(jb_apk_list.getJSONObject(i).get("apk_version")+"");

            String installed = jb_apk_list.getJSONObject(i).get("installed")+"";

            if(!ab.getDeleteddAt().equals("null"))
            {
                deleteApkHandle(jb_apk_list.getJSONObject(i));
                continue;
            }

            if(ab.getState()==1)
            {
                apkMap.put(ab.getID(),ab);
                continue;
            }

            ab.setExist(false);
            apkMap.put(ab.getID()+"",ab);
            final String localPath = ab.getApkLocalPath() ;
            final String localFileName = ab.getFileName();

            File localDir = new File(localPath);

            if(!localDir.exists())
                localDir.mkdirs();

            File file = new File(localPath + localFileName);

            //如果文件不存在，下载文件
            if(!file.exists())
            {
                OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
                ofm.downAsyncFile(ab.getApkOssFileUrl(),localPath + localFileName,ab.getID()+"",new CallBack() {

                    @Override
                    public void OnSuccess(String id) throws Exception {

                        Map<String,ApkBean> apkMap = ha.getApkMap();
                        final ApkBean ab = apkMap.get(id);

                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject(ab.getApkName()+"下载完成");
                        EventBus.getDefault().post(me);
                        ab.setExist(true);
                        if(!AppUtils.isAppInstalled(ab.getApkPackageName(),ab.getVersion(),ha.getContext()))
                        {

                            me.setObject(ab.getApkName()+"正在安装");
                            EventBus.getDefault().post(me);
                            AppUtils.installPmApk(ab.getApkLocalPath() + ab.getFileName(), ab.getID(), new CallBack() {

                                @Override
                                public void OnSuccess(String id) throws Exception {
                                    JSONObject jsonObject = new JSONObject();

                                    jsonObject.put("cmd", "client_apk_install");
                                    jsonObject.put("action", "client");
                                    jsonObject.put("mer_id", ab.getMerID());
                                    jsonObject.put("apk_id", id);
                                    jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));

                                    OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                                    MyEvent me = new MyEvent(EventTag.server_notice);
                                    me.setObject("安装完成");
                                    EventBus.getDefault().post(me);
                                }

                                @Override
                                public void OnFail(String param)
                                {
                                    MyEvent me = new MyEvent(EventTag.server_notice);
                                    me.setObject("安装失败");
                                    EventBus.getDefault().post(me);
                                }

                                @Override
                                public void OnUploading(String param) {

                                }
                            });
                        }
                    }
                    @Override
                    public void OnUploading(String param) {
                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject("下载"+param);
                        EventBus.getDefault().post(me);
                    }

                    @Override
                    public void OnFail(String param)
                    {
                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject("下载失败");
                        EventBus.getDefault().post(me);
                    }
                });


            }
            else{
                ab.setExist(true);
                if(!AppUtils.isAppInstalled(ab.getApkPackageName(),ab.getVersion(),ha.getContext())) {
                    AppUtils.installPmApk(ab.getApkLocalPath() + ab.getFileName(), ab.getID(), new CallBack() {

                        @Override
                        public void OnSuccess(String id) throws Exception {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("cmd", "client_apk_install");
                            jsonObject.put("action", "client");
                            jsonObject.put("mer_id", ha.getMerID());
                            jsonObject.put("apk_id", id);
                            jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));
                            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                            Map<String,ApkBean> apkMap  = ha.getApkMap();
                            MyEvent me = new MyEvent(EventTag.server_notice);
                            me.setObject(apkMap.get(id).getApkName()+"安装完成");
                            EventBus.getDefault().post(me);
                        }

                        @Override
                        public void OnUploading(String param) {

                        }

                        @Override
                        public void OnFail(String param)
                        {

                        }
                    });
                }else
                {
                    if(installed.equals("0"))
                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cmd", "client_apk_install");
                        jsonObject.put("action", "client");
                        jsonObject.put("mer_id", ha.getMerID());
                        jsonObject.put("apk_id", ab.getID());
                        jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));
                        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
                    }


                }
            }
            apkMap.put(ab.getID()+"",ab);
        }
        this.ha.setApkMap(apkMap);
        updateApkStatus(jb);
    }

    public void reInstallApkHandle(JSONObject jb) throws Exception
    {
        JSONObject jb_apk = new JSONObject(jb.get("apk").toString());
        String apkID =  jb_apk.get("id").toString();

        ApkBean ab  = ha.getApkMap().get(apkID);

        final String localPath = ab.getApkLocalPath() ;
        final String localFileName = ab.getFileName();

        File localDir = new File(localPath);

        if(!localDir.exists())
            localDir.mkdirs();

        File file = new File(localPath + localFileName);

        if(file.exists()&&file.length()< Long.parseLong(ab.getApkFileSize()))
        {
            file.delete();
        }

        //如果文件不存在，下载文件
        if(!file.exists())
        {
            OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
            ofm.downAsyncFile(ab.getApkOssFileUrl(),localPath + localFileName,ab.getID()+"",new CallBack() {

                //上传成功后回调，更新数据库
                @Override
                public void OnSuccess(String id) throws Exception {
                    //生成命令脚本数据记录

                    Map<String,ApkBean> apkMap = ha.getApkMap();
                    final ApkBean ab = apkMap.get(id);
                    ab.setExist(true);


                        AppUtils.installPmApk(ab.getApkLocalPath() + ab.getFileName(), ab.getID(), new CallBack() {

                            @Override
                            public void OnSuccess(String id) throws Exception {
                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("cmd", "client_apk_install");
                                jsonObject.put("action", "client");
                                jsonObject.put("mer_id", ab.getMerID());
                                jsonObject.put("apk_id", id);
                                jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));

                                OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                                MyEvent me = new MyEvent(EventTag.server_notice);
                                me.setObject(ab.getApkName()+"安装完成");
                                EventBus.getDefault().post(me);
                            }

                            @Override
                            public void OnUploading(String param) {

                            }

                            @Override
                            public void OnFail(String param)
                            {
                                MyEvent me = new MyEvent(EventTag.server_notice);
                                me.setObject("安装失败");
                                EventBus.getDefault().post(me);
                            }
                        });
                }
                @Override
                public void OnUploading(String param) {
                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject("下载"+param);
                    EventBus.getDefault().post(me);
                }

                @Override
                public void OnFail(String param)
                {
                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject("下载失败");
                    EventBus.getDefault().post(me);
                }
            });


        }
        else{

                AppUtils.installPmApk(ab.getApkLocalPath() + ab.getFileName(), ab.getID(), new CallBack() {

                    @Override
                    public void OnSuccess(String id) throws Exception {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cmd", "client_apk_install");
                        jsonObject.put("action", "client");
                        jsonObject.put("mer_id", ha.getMerID());
                        jsonObject.put("apk_id", id);
                        jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));
                        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject(ha.getApkMap().get(id).getApkName()+"安装完成");
                        EventBus.getDefault().post(me);
                    }

                    @Override
                    public void OnUploading(String param) {

                    }

                    @Override
                    public void OnFail(String param)
                    {
                        MyEvent me = new MyEvent(EventTag.server_notice);
                        me.setObject("安装失败");
                        EventBus.getDefault().post(me);
                    }
                });
            }


    }


    //删除Apk文件
    public  void deleteApkHandle(JSONObject jb) throws Exception
    {
        String apkId = jb.get("id").toString();

        String localPath  = jb.get("apk_local_path").toString();
        String fileName  = jb.get("file_name").toString();
        String packageName = jb.get("apk_package_name").toString();

        final  String apk_name = jb.get("apk_name").toString();
        final File file = new File(localPath + fileName);

        //如果文件不存在，下载文件
        if(file.exists())
        {
            file.delete();
        }
        AppUtils.uninstallSlient(packageName,apkId,new CallBack(){
            @Override
            public void OnSuccess(String id) throws Exception {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", "delete_apk_install_rel");
                jsonObject.put("action", "client");
                jsonObject.put("mer_id", ha.getMerID());
                jsonObject.put("apk_id", id);
                jsonObject.put("moblie_serial", MobileUtils.getMoblieIMEI(ha.getContext()));
                OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                MyEvent me = new MyEvent(EventTag.server_notice);
                me.setObject(apk_name+"卸载完成");
                EventBus.getDefault().post(me);
            }
            @Override
            public void OnUploading(String param) {

            }
            @Override
            public void OnFail(String param)
            {
                MyEvent me = new MyEvent(EventTag.server_notice);
                me.setObject("卸载失败");
                EventBus.getDefault().post(me);
            }
        });
    }


    public  void deleteApkFileHandle(JSONObject jb) throws Exception
    {
        JSONObject apkJson =  new JSONObject(jb.get("apk").toString());
        String apkId =  apkJson.get("id").toString();
        Map<String,ApkBean> apkMap = ha.getApkMap();
        ApkBean ab = apkMap.get(apkId);
        final File file = new File(ab.getApkLocalPath() + ab.getFileName());

        //如果文件不存在，下载文件
        if(file.exists())
        {
            file.delete();
        }
        apkMap.remove(apkId);

        ha.setApkMap(apkMap);
    }

    //获取手机列表
    private  void getPhoneListHandle(JSONObject jb) throws Exception
    {
        JSONArray jb_phone_list = new JSONArray(jb.get("phone_list").toString());

        Map<String,PhoneBean> phoneMap  = this.ha.getPhoneMap();
        for (int i=0; i < jb_phone_list.length(); i++)    {
            PhoneBean pb = new PhoneBean();
            pb.setId(Integer.parseInt(jb_phone_list.getJSONObject(i).get("id")+""));
            pb.setFd(Integer.parseInt(jb_phone_list.getJSONObject(i).get("fd")+""));
            pb.setSerila(jb_phone_list.getJSONObject(i).get("mobile_serila")+"");
            pb.setMobileCode(jb_phone_list.getJSONObject(i).get("mobile_code")+"");
            pb.setGroup(jb_phone_list.getJSONObject(i).get("group")+"");
            pb.setPhone_number(jb_phone_list.getJSONObject(i).get("phone")+"");
            pb.setLocationX(jb_phone_list.getJSONObject(i).get("location_x")+"");
            pb.setLocationY(jb_phone_list.getJSONObject(i).get("location_y")+"");
            pb.setMerID(jb_phone_list.getJSONObject(i).get("mer_id")+"");
            pb.setStatus(jb_phone_list.getJSONObject(i).get("m_status")+"");
            pb.setIsSelected(false);
            pb.setRunCmdID(jb_phone_list.getJSONObject(i).get("run_cmd_id")+"");
            pb.setVerison(jb_phone_list.getJSONObject(i).get("version")+"");


            if(pb.getPhone_number().equals("null"))
            {
                pb.setPhone_number("正在获取中");
            }

            if(pb.getRunCmdID().equals("null"))
            {
                pb.setRunCmdID("");
            }

            if(jb_phone_list.getJSONObject(i).get("m_type").toString().equals("4"))
            {
                continue;
            }
            if(jb_phone_list.getJSONObject(i).get("m_type").toString().equals("2")) {
                pb.setIsMaster(true);
                String merID  = pb.getMerID();
                this.ha.setMerID(merID);
            }
            else
                pb.setIsMaster(false);

            String serila = pb.getSerila();
            phoneMap.put(serila,pb);

            String toastText = pb.getStatus().equals("1")?pb.getMobileCode()+"上线":pb.getMobileCode()+"下线";

            MyEvent me = new MyEvent(EventTag.server_notice);
            me.setObject(toastText);
            EventBus.getDefault().post(me);
        }
        this.ha.setPhoneMap(phoneMap);

        EventBus.getDefault().post(new MyEvent(EventTag.master_device_list_changed));
        EventBus.getDefault().post(new MyEvent(EventTag.controlled_change_address));
    }

    private void getMobileGroupListHandle(JSONObject jb) throws Exception
    {
        JSONArray jb_group_list = new JSONArray(jb.get("group_list").toString());

        Map<String,GroupBean> groupMap  = this.ha.getGroupMap();
        for (int i=0; i < jb_group_list.length(); i++)    {
            GroupBean gb = new GroupBean();

            String groupID = jb_group_list.getJSONObject(i).get("id")+"";
            gb.setGroupID(Integer.parseInt(jb_group_list.getJSONObject(i).get("id")+""));
            gb.setGroupName(jb_group_list.getJSONObject(i).get("group_name")+"");
            gb.setCode(jb_group_list.getJSONObject(i).get("code")+"");
            gb.setMerID(Integer.parseInt(jb_group_list.getJSONObject(i).get("mer_id")+""));
            gb.setCreatedAt(jb_group_list.getJSONObject(i).get("created_at")+"");
            gb.setUpdatedAt(jb_group_list.getJSONObject(i).get("updated_at")+"");

            groupMap.put(groupID,gb);
        }

        Map<String, GroupBean> sortMap = new TreeMap<String, GroupBean>( new MapKeyComparator());
        sortMap.putAll(groupMap);
        this.ha.setGroupMap(sortMap);
        EventBus.getDefault().post(new MyEvent(EventTag.master_change_add_group));
    }


    private  void openAPk(JSONObject jb) throws Exception
    {
        String package_name = jb.get("package_name").toString();

        PackageManager packageManager = ha.getContext().getPackageManager();

        List<String>  packList =  new ArrayList<String>();
        packList.add(package_name);
        ha.setPackageList(packList);

        Intent intent = new Intent();
        intent =packageManager.getLaunchIntentForPackage(package_name);
        if(intent==null){
            Toast.makeText(ha.getContext(), "未安装", Toast.LENGTH_LONG).show();
        }else{
            ha.getContext().startActivity(intent);
        }

    }

    private  void autoReadApk(JSONObject jb) throws Exception
    {
        String package_name = jb.get("package_name").toString();

        PackageManager packageManager = ha.getContext().getPackageManager();

        List<String>  packList =  new ArrayList<String>();
        packList.add(package_name);
        ha.setPackageList(packList);

        ha.setCurrentApkPackName(package_name);

        Intent intent = new Intent();
        intent =packageManager.getLaunchIntentForPackage(package_name);

        execShellCmd("settings  put  secure  enabled_accessibility_services  com.hwytapp.Service/com.hwytapp.Service.AutoReadService");
        execShellCmd("settings  put  secure  accessibility_enabled  0");

        if(intent==null){
            Toast.makeText(ha.getContext(), "未安装", Toast.LENGTH_LONG).show();
        }else{
            ha.getContext().startActivity(intent);
        }

    }



    //执行命令
    private  void exeCmd(JSONObject jb) throws Exception
    {
        String strCmd = jb.get("cmd_data").toString();

        String [] cmds = strCmd.split("\\|\\|\\|");

        Handler handler = new Handler();

        for(int i=0;i<cmds.length;i++)
        {
            String [] cmd = cmds[i].split("\\$\\$\\$");

            final  String command = cmd[0];
            String execTime = cmd[1];

            if(command.equals("end"))
                continue;


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    execShellCmd(command);
                }
            }, Integer.parseInt(execTime));
        }

    }

    private void execShellCmd(String cmd) {
        Log.i("MyLog","----------------"+cmd);
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            Log.v("color","----------------"+t.getMessage());
            t.printStackTrace();
        }
    }


    //获取手机号
    public void sendPhoneNum(JSONObject jb) throws Exception
    {
        String phone = jb.get("phone").toString();
        Context context = this.ha.getContext();

        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);
        String imei = MobileUtils.getMoblieIMEI(context);
        String smsText = "grapphone|||"+imei;
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, smsText, pi, null);

    }

    //获取客户机验证码
    public void sendVerificationCode()
    {
        ConnectionInfo info = this.ha.getSocketinfo();
        Uri SMS_ALL = Uri.parse("content://sms/");
        Context context = this.ha.getContext();

        if (!SmsWriteOpUtil.isWriteEnabled(context)) {
            SmsWriteOpUtil.setWriteEnabled(context, true);
        }

        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[] {"_id","thread_id", "address", "person","body", "date", "type" ,"thread_id"};
        Cursor cur = cr.query(SMS_ALL, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("ooc","************cur == null");
            return;
        }

        int index = 0;

        while(cur.moveToNext()) {

            if(index>0)
                break;
            try {

                String number = cur.getString(cur.getColumnIndex("address"));//手机号
                String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
                String date = cur.getString(cur.getColumnIndex("date"));//联系人姓名列表
                String body = cur.getString(cur.getColumnIndex("body"));//短信内容

                if( body.contains("验证码"))
                {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "phone_ver_code");
                    jsonObject.put("action", "client");

                    jsonObject.put("body", body);

                    OkSocket.open(info).send(new TestSendData(jsonObject));

                    Log.i("sms++++++++++++",   " " + body);
                    index++;
                }
            }
            catch (Exception e)
            {
                Log.i("sms++++++++++++", e.toString());
            }

        }

    }


    class MapKeyComparator implements Comparator<String>{

        @Override
        public int compare(String str1, String str2) {

            return str2.compareTo(str1);
        }
    }

    private  void recoverMobileHandle(JSONObject jb) throws  Exception
    {
        String serial = jb.get("serial").toString();

        Map<String,PhoneBean>  phoneBeanMap = ha.getPhoneMap();

        phoneBeanMap.remove(serial);

        ha.setPhoneMap(phoneBeanMap);
        EventBus.getDefault().post(new MyEvent(EventTag.master_device_list_changed));
        EventBus.getDefault().post(new MyEvent(EventTag.controlled_change_address));
    }


    private  void getQueueTaskListHandle(JSONObject jb) throws  Exception
    {
        JSONArray jb_group_list = new JSONArray(jb.get("queue_task").toString());

        Map<String,QueueTaskBean> queueTaskMap  = this.ha.getQueueTaskMap();
        for (int i=0; i < jb_group_list.length(); i++)    {
            QueueTaskBean qt = new QueueTaskBean();

            String taskID = jb_group_list.getJSONObject(i).get("id")+"";
            qt.setID(jb_group_list.getJSONObject(i).get("id")+"");
            qt.setTaskName(jb_group_list.getJSONObject(i).get("task_name")+"");
            qt.setTaskNum(jb_group_list.getJSONObject(i).get("task_num")+"");
            qt.setTaskTime(jb_group_list.getJSONObject(i).get("task_time")+"");
            qt.setDecviceCount(jb_group_list.getJSONObject(i).get("mobile_count")+"");
            qt.setCreatedAt(jb_group_list.getJSONObject(i).get("created_at")+"");
            qt.setStatus("0");
            queueTaskMap.put(taskID,qt);
        }

        Map<String, QueueTaskBean> sortMap = new TreeMap<String, QueueTaskBean>( new MapKeyComparator());
        sortMap.putAll(queueTaskMap);
        this.ha.setQueueTaskMap(sortMap);
        EventBus.getDefault().post(new MyEvent(EventTag.master_queue_task_list_changed));
    }


    public  void pulseQueueTaskHandle(JSONObject jb) throws Exception
    {
        String taskID =  jb.get("queue_task_id").toString();
        Map<String,QueueTaskBean> queueTaskMap  = this.ha.getQueueTaskMap();
        QueueTaskBean qt = queueTaskMap.get(taskID);

        if(qt!=null)
        {
            qt.setStatus(jb.get("status").toString());
            this.ha.setQueueTaskMap(queueTaskMap);
            EventBus.getDefault().post(new MyEvent(EventTag.master_queue_task_list_changed));
        }
    }

    public  void deleteQueueTaskHandle(JSONObject jb) throws Exception
    {
        String taskID =  jb.get("task_id").toString();
        Map<String,QueueTaskBean> queueTaskMap = ha.getQueueTaskMap();

        queueTaskMap.remove(taskID);

        ha.setQueueTaskMap(queueTaskMap);

        EventBus.getDefault().post(new MyEvent(EventTag.master_queue_task_list_changed));
    }

    private  void getQueueTaskItemHandle(JSONObject jb) throws  Exception
    {
        JSONArray jb_list = new JSONArray(jb.get("queue_task_item").toString());

        Map<String,QueueTaskItemBean> queueTaskItemMap  = this.ha.getQueueTaskItemMap();

        if(!this.ha.getIsMasterPhone())
            queueTaskItemMap = new HashMap<String,QueueTaskItemBean>();

        for (int i=0; i < jb_list.length(); i++)    {
            QueueTaskItemBean qti = new QueueTaskItemBean();

            String taskID = jb_list.getJSONObject(i).get("id")+"";
            qti.setID(jb_list.getJSONObject(i).get("id")+"");

            qti.setApkName(jb_list.getJSONObject(i).get("apk_name")+"");
            qti.setPackageName(jb_list.getJSONObject(i).get("package_name")+"");
            qti.setExecTime(jb_list.getJSONObject(i).get("exec_time")+"");
            qti.setCreatedAt(jb_list.getJSONObject(i).get("created_at")+"");
            qti.setQueueTaskID(jb_list.getJSONObject(i).get("queue_task_id")+"");
            qti.setStopTime(Long.parseLong("0"));
            queueTaskItemMap.put(taskID,qti);
        }

        Map<String, QueueTaskItemBean> sortMap = new TreeMap<String, QueueTaskItemBean>( new MapKeyComparator());
        sortMap.putAll(queueTaskItemMap);
        this.ha.setQueueTaskItemMap(sortMap);
        EventBus.getDefault().post(new MyEvent(EventTag.master_queue_task_item_changed));
    }


    private  void execQueueTaskItemHandle(JSONObject jb) throws  Exception
    {
        JSONArray jb_list = new JSONArray(jb.get("queue_task_item").toString());

        Map<String,QueueTaskItemBean> queueTaskItemMap  = new HashMap<String,QueueTaskItemBean>();

        for (int i=0; i < jb_list.length(); i++)    {
            QueueTaskItemBean qti = new QueueTaskItemBean();

            String taskID = jb_list.getJSONObject(i).get("id")+"";
            qti.setID(jb_list.getJSONObject(i).get("id")+"");

            qti.setApkName(jb_list.getJSONObject(i).get("apk_name")+"");
            qti.setPackageName(jb_list.getJSONObject(i).get("package_name")+"");
            qti.setExecTime(jb_list.getJSONObject(i).get("exec_time")+"");
            qti.setCreatedAt(jb_list.getJSONObject(i).get("created_at")+"");
            qti.setQueueTaskID(jb_list.getJSONObject(i).get("queue_task_id")+"");
            qti.setStopTime(Long.parseLong("0"));
            qti.setStartTime(Long.parseLong("0"));
            qti.setRunTime(Long.parseLong(jb_list.getJSONObject(i).get("run_time")+""));
            queueTaskItemMap.put(taskID,qti);
        }

        Map<String, QueueTaskItemBean> sortMap = new TreeMap<String, QueueTaskItemBean>( new MapKeyComparator());
        sortMap.putAll(queueTaskItemMap);

        this.ha.setQueueTaskItemMap(sortMap);

        if(this.ha.getQueueTaskItemMap().size()>0&&!this.ha.getIsMasterPhone()) {
            //阅读服务
            Intent it1 = new Intent(this.ha.getContext(), com.hwytapp.Service.AccessibiliService.class);
            it1.setPackage("com.hwytapp.Service.AccessibiliService");
            this.ha.getContext().startService(it1);

            execShellCmd("settings  put  secure  enabled_accessibility_services  com.bee.yunkong/com.hwytapp.Service.AutoReadService");
            execShellCmd("settings  put  secure  accessibility_enabled  1");
        }

    }

    public  void deleteQueueTaskItemHandle(JSONObject jb) throws Exception
    {
        String taskItemID =  jb.get("task_item_id").toString();
        Map<String,QueueTaskItemBean> queueTaskItemMap = ha.getQueueTaskItemMap();

        queueTaskItemMap.remove(taskItemID);

        ha.setQueueTaskItemMap(queueTaskItemMap);

        EventBus.getDefault().post(new MyEvent(EventTag.master_queue_task_item_changed));
    }


    public  JSONObject getXPreadArticle (JSONObject jb) throws Exception
    {
        return jb;
    }





}
