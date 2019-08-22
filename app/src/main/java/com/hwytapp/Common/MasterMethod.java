package com.hwytapp.Common;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.network.DataManager;
import com.hwytapp.Bean.ApkBean;
import com.hwytapp.Bean.CmdBean;
import com.hwytapp.Bean.GroupBean;
import com.hwytapp.Bean.PhoneBean;
import com.hwytapp.Bean.TestSendData;
import com.hwytapp.Interface.CallBack;
import com.hwytapp.Utils.AppUtils;
import com.hwytapp.Utils.Bulid;
import com.hwytapp.Utils.MobileUtils;
import com.hwytapp.Utils.SmsWriteOpUtil;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MasterMethod{

    private App ha ;

    public  MasterMethod(App ha )
    {
        this.ha = ha;
    }

    public  void initDevice()
    {
        Context context = ha.getContext();
        try {
            JSONObject jsonObject = new JSONObject();
            Bulid bulid = new Bulid();
            Map bulidInfo = bulid.bulidInfo();
            String batteryCap = String.valueOf(bulid.getBatteryCap(context));
            jsonObject.put("BRAND", bulidInfo.get("BRAND").toString());
            jsonObject.put("Device_ID", bulidInfo.get("Device_ID").toString());
            jsonObject.put("HARDWARE", bulidInfo.get("HARDWARE").toString());
            jsonObject.put("MODEL", bulidInfo.get("MODEL").toString());
            jsonObject.put("BatteryCap", batteryCap);

            jsonObject.put("cmd", "init");
            jsonObject.put("action", "client");
            TelephonyManager tm = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            jsonObject.put("Serial", imei);

            String version = bulid.getVerName(context);
            jsonObject.put("Version", version);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        } catch (Exception e) {
        }
    }

    public  void getClientPhoneNum() throws  Exception
    {
        ConnectionInfo info = this.ha.getSocketinfo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "send_phone_num");
        jsonObject.put("action", "client");

        Map<String,PhoneBean> phoneMap  = ha.getPhoneMap();
        for (PhoneBean pb : phoneMap.values()) {
            if(!pb.isMaster()&&pb.isSelected()&&pb.getFd()>0)
            {
                jsonObject.put("rec_fd", pb.getFd());
                OkSocket.open(info).send(new TestSendData(jsonObject));
            }
        }

    }

    //发送获取手机号的短信
    public void sendPhoneNum()
    {
        String phone = this.ha.getServerPhoneNum();
        Context context = this.ha.getContext();

        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);
        String imei = MobileUtils.getMoblieIMEI(context);
        String smsText = "grapphone|||"+imei;
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, smsText, pi, null);
    }

    //输入手机号
    public void inputReadArticle(JSONObject jb) throws  Exception
    {
        if(!jb.has("id")||jb.get("id")==null)
        {
            Toast.makeText(ha.getContext(), "文章信息未获取到", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i("xposed hook",jb.toString());
        jb.put("cmd", "input_read_article");
        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jb));
    }

    //输入手机号
    public void inputPhoneNum() throws  Exception
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "input_phone_num");
        jsonObject.put("action", "client");
        jsonObject.put("mer_id", ha.getMerID());
        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
    }


    public  void getClientPhoneVerCode()
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "send_ver_code");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            OkSocket.open(info).send(new TestSendData(jsonObject));

        }catch (Exception e)
        {}


    }

    public void getSMSInfo() {

        ConnectionInfo info = this.ha.getSocketinfo();

        if(info==null)
            return;

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

        while(cur.moveToNext()) {
            try {

                String number = cur.getString(cur.getColumnIndex("address"));//手机号
                String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
                String date = cur.getString(cur.getColumnIndex("date"));//联系人姓名列表
                String body = cur.getString(cur.getColumnIndex("body"));//短信内容

                if( System.currentTimeMillis()- Long.parseLong(date) < 1000*60*10 &&body.contains("grapphone"))
                {
                    String [] content = {};
                    if(body.contains("?"))
                    {
                        body = body.replace(" ","");
                        content = body.split("\\?\\?\\?");
                    }
                    if(body.contains("|"))
                    {
                        content = body.split("\\|\\|\\|");
                    }

                    String phone = number;
                    String Serial = content[1];
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "update_mobile_phonenum");
                    jsonObject.put("action", "client");

                    jsonObject.put("mobile_serila", Serial);
                    jsonObject.put("phone",  phone);

                    OkSocket.open(info).send(new TestSendData(jsonObject));


                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject(phone+"已发送");
                    EventBus.getDefault().post(me);

                    Log.i("sms++++++++++++",   " " + body);
                }
            }
            catch (Exception e)
            {
                Log.i("sms++++++++++++", e.toString());
            }
        }
    }



    //处理录制的命令
    public void editRecordeCmd(final String cmdName)
    {
        List<String> cmdList = this.ha.getCmdList();

        if(cmdList.size()==0)
            return;

        Map<Long,String>  cmdstrMap = new HashMap<Long, String>();

        for (String cmd : cmdList)
        {
            String [] cmds = cmd.split("\\$\\$\\$");
            cmdstrMap.put(Long.parseLong(cmds[1]),cmd);
        }

        Map<Long,String>  cmdMap = new TreeMap<Long,String>(new MapKeyComparator());
        cmdMap.putAll(cmdstrMap);

        String cmdConent = "";
        for (String cmd : cmdMap.values())
        {
            if(!cmdConent.equals(""))
                cmdConent +="|||";
            cmdConent += cmd;


        }

        String appPath = AppUtils.appPath();
        final String localPath = appPath + Config.APPDIR + "/cmd/" ;
        final String localFileName =  System.currentTimeMillis()+".txt";
        //保存文件到本地
        Boolean saveSuccsess =AppUtils.saveFile(cmdConent,localPath + localFileName);

        if(saveSuccsess)
        {
            String mer_id = ha.getMerID();
            final  String ossFilePath  = mer_id + "/" + "cmd"+"/" + localFileName;
            OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
            ofm.uploadAsyncFile(ossFilePath,localPath + localFileName,new CallBack() {

                //上传成功后回调，更新数据库
                @Override
                public void OnSuccess(String param) {
                    //生成命令脚本数据记录
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cmd", "cmd_recorde");
                        jsonObject.put("action", "client");
                        jsonObject.put("mer_id", ha.getMerID());
                        jsonObject.put("oss_file_path", ossFilePath);
                        jsonObject.put("file_name", localFileName);
                        jsonObject.put("local_path", localPath);
                        jsonObject.put("name", cmdName);
                        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                        File file =  new File(localPath + localFileName);
                        file.delete();
                    }catch (Exception e)
                    {
                    }
                }

                @Override
                public void OnUploading(String param) {

                }

                @Override
                public void OnFail(String param)
                {
                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject("上传失败");
                    EventBus.getDefault().post(me);
                }
            });
        }
    }

    class MapKeyComparator implements Comparator<Long> {

        @Override
        public int compare(Long str1, Long str2) {

            return str1.compareTo(str2);
        }
    }


    //上传apk，并通知客户端下载安装
    public void uploadApkFile(String apkFilePath,final String apkName,final String apkPackageName,final String apkIcon,String version)
    {
        File apkFile  = new File(apkFilePath);
        if(!apkFile.exists())
            return;

        String appPath = AppUtils.appPath();
        final String localPath = appPath + Config.APPDIR + "/apk/" ;
        final String localFileName =  apkFile.getName();
        final String apkFileSize = apkFile.length()+"";

        String mer_id = ha.getMerID();
        final  String ossFilePath  = mer_id + "/" + "apk"+"/" + localFileName;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "upload_apk");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("apk_name", apkName);
            jsonObject.put("apk_version", version);
            jsonObject.put("apk_package_name", apkPackageName);
            jsonObject.put("apk_icon", apkIcon);
            jsonObject.put("apk_oss_file_url", ossFilePath);
            jsonObject.put("apk_local_path", localPath);
            jsonObject.put("file_name", localFileName);
            jsonObject.put("apk_file_size", apkFileSize);
            jsonObject.put("status", "1");
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
        }catch (Exception e)
        {
        }

        OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
        ofm.uploadAsyncFile(ossFilePath,apkFilePath,new CallBack() {

            //上传成功后回调，更新数据库
            @Override
            public void OnSuccess(String param) throws Exception {

                Map<String ,ApkBean>  apkBeanMap = ha.getApkMap();

                for (ApkBean ab : apkBeanMap.values())
                {
                    if(ab.getApkPackageName().equals(apkPackageName))
                    {
                        ab.setUploadProgress(param);
                        ab.setState(2);
                        ab.setStateName("正在安装应用");
                        ab.setInstalledNum("0/"+ha.getPhoneMap().size());


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cmd", "apk_upload_complete");
                        jsonObject.put("action", "client");
                        jsonObject.put("mer_id", ha.getMerID());
                        jsonObject.put("apk_id", ab.getID());

                        OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
                    }


                }
                EventBus.getDefault().post(new MyEvent(EventTag.master_change_add_apks));
            }

            @Override
            public void OnUploading(String param) {

                Map<String ,ApkBean>  apkBeanMap = ha.getApkMap();

                for (ApkBean ab : apkBeanMap.values())
                {
                    if(ab.getApkPackageName().equals(apkPackageName))
                    {
                        ab.setUploadProgress(param);
                        ab.setState(1);
                        ab.setStateName("正在上传");
                    }
                }
                EventBus.getDefault().post(new MyEvent(EventTag.master_change_add_apks));
            }

            @Override
            public void OnFail(String param)
            {
                MyEvent me = new MyEvent(EventTag.server_notice);
                me.setObject("上传失败");
                EventBus.getDefault().post(me);
            }
        });
    }

    public void deleteCmd(String cmdID)
    {
        try {

            CmdBean cb = this.ha.getCmdMap().get(cmdID);

            final  String ossFilePath  = cb.getOssFilePath();

            OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
            ofm.deleteOssFile(ossFilePath,cmdID,new CallBack() {

                //删除成功后回调，更新数据库
                @Override
                public void OnSuccess(String param) throws Exception {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "delete_cmd");
                    jsonObject.put("action", "client");
                    jsonObject.put("mer_id", ha.getMerID());
                    jsonObject.put("cmd_id",param);
                    OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
                }

                @Override
                public void OnUploading(String param) {

                }
                @Override
                public void OnFail(String param)
                {
                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject("删除失败");
                    EventBus.getDefault().post(me);
                }
            });

        }catch (Exception e)
        {
        }

        Map<String,CmdBean> cbMap = ha.getCmdMap();

        cbMap.remove(cmdID);
        ha.setCmdMap(cbMap);

    }


    public void uninstallApk(String apkID)
    {
        try {

            ApkBean ab = this.ha.getApkMap().get(apkID);

            final String localFileName =  ab.getFileName();

            String mer_id = ha.getMerID();
            final  String ossFilePath  = mer_id + "/" + "apk"+"/" + localFileName;

            OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
            ofm.deleteOssFile(ossFilePath,apkID,new CallBack() {

                //删除成功后回调，更新数据库
                @Override
                public void OnSuccess(String param) throws Exception {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "uninstall_apk");
                    jsonObject.put("action", "client");
                    jsonObject.put("mer_id", ha.getMerID());

                    jsonObject.put("apk_id",param);
                    OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject("卸载成功");
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

        }catch (Exception e)
        {
        }
        Map<String,ApkBean> apkMap = ha.getApkMap();
        apkMap.remove(apkID);
        ha.setApkMap(apkMap);

        EventBus.getDefault().post(new MyEvent(EventTag.master_change_add_apks));
    }

    public void reInstallApk(String apkID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "re_install_apk");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("apk_id",apkID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        {
        }

    }

    public void deleteApk(String apkID)
    {
        try {

            ApkBean ab = this.ha.getApkMap().get(apkID);

            final String localFileName =  ab.getFileName();

            String mer_id = ha.getMerID();
            final  String ossFilePath  = mer_id + "/" + "apk"+"/" + localFileName;

            OssFileMethod ofm = new OssFileMethod(this.ha.getContext());
            ofm.deleteOssFile(ossFilePath,apkID,new CallBack() {

                //删除成功后回调，更新数据库
                @Override
                public void OnSuccess(String param) throws Exception {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "delete_apk");
                    jsonObject.put("action", "client");
                    jsonObject.put("mer_id", ha.getMerID());

                    jsonObject.put("apk_id",param);
                    OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));
                }

                @Override
                public void OnUploading(String param) {

                }

                @Override
                public void OnFail(String param)
                {
                    MyEvent me = new MyEvent(EventTag.server_notice);
                    me.setObject("删除失败");
                    EventBus.getDefault().post(me);
                }
            });

        }catch (Exception e)
        {
        }
        Map<String,ApkBean> apkMap = ha.getApkMap();

         File file = new File(apkMap.get(apkID).getApkLocalPath() + apkMap.get(apkID).getFileName());

        //如果文件不存在，下载文件
        if(file.exists())
        {
            file.delete();
        }
        apkMap.remove(apkID);
        ha.setApkMap(apkMap);

        EventBus.getDefault().post(new MyEvent(EventTag.master_change_add_apks));
    }


    public void clientPhoneRegister(String imei,String clientFd,String group,String location_x,String location_y,String mer_id)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();

            Map<String,GroupBean> groupBeanMap = this.ha.getGroupMap();

            String code = "";
            for (GroupBean gb : groupBeanMap.values()){
                if(gb.getGroupName().equals(group)){
                    code = gb.getCode();
                }
            }

            String code_x = location_x;
            String code_y = location_y;
            if(Integer.parseInt(location_x)<10)
                code_x = "0"+location_x;

            if(Integer.parseInt(location_y)<10)
                code_y = "0"+location_y;

            code = code+code_y+code_x;

            JSONObject jsonObject = new JSONObject();
            Bulid bulid = new Bulid();
            jsonObject.put("Serial", imei);
            jsonObject.put("client_fd", clientFd);
            String version = bulid.getVerName(context.getApplicationContext());
            jsonObject.put("Version", version);
            jsonObject.put("m_type", 1);

            jsonObject.put("group", group);
            jsonObject.put("mobile_code", code);
            jsonObject.put("location_x", location_x);
            jsonObject.put("location_y", location_y);
            jsonObject.put("mer_id", mer_id);

            jsonObject.put("cmd", "client_register");
            jsonObject.put("action", "client");

            OkSocket.open(info).send(new TestSendData(jsonObject));
        }catch (Exception e)
        {}


    }


    //注册手机 m_type=1 客户机，=2主控机
    public void  masterPhoneRegister(String code,String phone) throws Exception
    {
        ConnectionInfo info = this.ha.getSocketinfo();
        Context context = this.ha.getContext();
        JSONObject jsonObject = new JSONObject();
        Bulid bulid = new Bulid();
        Map bulidInfo = bulid.bulidInfo();
        String batteryCap = String.valueOf(bulid.getBatteryCap(context));
        jsonObject.put("BRAND", bulidInfo.get("BRAND").toString());
        jsonObject.put("Device_ID", bulidInfo.get("Device_ID").toString());
        jsonObject.put("HARDWARE", bulidInfo.get("HARDWARE").toString());
        jsonObject.put("MODEL", bulidInfo.get("MODEL").toString());
        jsonObject.put("BatteryCap", batteryCap);

        String imei = MobileUtils.getMoblieIMEI(context);
        jsonObject.put("Serial", imei);

        String version = bulid.getVerName(context.getApplicationContext());
        jsonObject.put("Version", version);
        jsonObject.put("phone", phone);
        jsonObject.put("code", code);
        jsonObject.put("m_type", 2);
        jsonObject.put("cmd", "client_register");
        jsonObject.put("action", "client");

        OkSocket.open(info).send(new TestSendData(jsonObject));
    }


    public  void addMobileGroup(String groupName,String code ,String merID)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("group_name", groupName);
            jsonObject.put("code", code);
            jsonObject.put("mer_id", merID);
            jsonObject.put("cmd", "add_mobile_group");
            jsonObject.put("action", "client");

            OkSocket.open(info).send(new TestSendData(jsonObject));
        }catch (Exception e)
        {}
    }

    public  void updateMobileGroup(String groupName,String code,String groupID)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("group_name", groupName);
            jsonObject.put("code", code);
            jsonObject.put("group_id", groupID);
            jsonObject.put("cmd", "update_mobile_group");
            jsonObject.put("action", "client");

            OkSocket.open(info).send(new TestSendData(jsonObject));
        }catch (Exception e)
        {}
    }

    public  void deleteMobileGroup(String groupID,String merID)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("group_id", groupID);
            jsonObject.put("mer_id", merID);
            jsonObject.put("cmd", "delete_mobile_group");
            jsonObject.put("action", "client");

            OkSocket.open(info).send(new TestSendData(jsonObject));

            Map<String,GroupBean> groupMap =  ha.getGroupMap();

            groupMap.remove(groupID);
        }catch (Exception e)
        {}
    }


    public  void updateMobileGroupInfo(String serial,String code,String groupNanme,String location_x,String location_y)
    {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "client_update");
            jsonObject.put("action", "client");


            JSONObject jsonWhere = new JSONObject();
            jsonWhere.put("mobile_serila", serial);
            jsonObject.put("where", jsonWhere.toString());

            JSONObject jsonData = new JSONObject();

            String code_x = location_x;
            String code_y = location_y;
            if(Integer.parseInt(location_x)<10)
                code_x = "0"+location_x;

            if(Integer.parseInt(location_y)<10)
                code_y = "0"+location_y;

            code = code+code_y+code_x;

            jsonData.put("group", groupNanme);
            jsonData.put("location_x", location_x);
            jsonData.put("location_y", location_y);
            jsonData.put("mobile_code", code);
            jsonObject.put("data", jsonData.toString());

            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

            ha.getPhoneMap().get(serial).setGroup(groupNanme);
            ha.getPhoneMap().get(serial).setLocationY(location_x);
            ha.getPhoneMap().get(serial).setLocationY(location_y);
        }catch (Exception e)
        {

        }

    }


    public  void recoverMobile(String serial)
    {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "recover_mobile");
            jsonObject.put("action", "client");

            jsonObject.put("Serial", serial);

            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        {

        }

    }


    public  void updateCmd(String cmdID,JSONObject jsonData)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd_id", cmdID);
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("cmd", "update_cmd");
            jsonObject.put("action", "client");

            jsonObject.put("data", jsonData.toString());

            OkSocket.open(info).send(new TestSendData(jsonObject));
        }catch (Exception e)
        {}


    }

    public  void updateMobile(String mobileID,JSONObject jsonData)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile_id", mobileID);
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("cmd", "update_mobile");
            jsonObject.put("action", "client");

            jsonObject.put("data", jsonData.toString());

            OkSocket.open(info).send(new TestSendData(jsonObject));
        }catch (Exception e)
        {}


    }

    public void  openApk(String apk_id,String packageName)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("apk_id", apk_id);
            jsonObject.put("apk_package_name", packageName);
            jsonObject.put("cmd", "open_apk");
            jsonObject.put("action", "client");

            Map<String,PhoneBean>  phoneBeanMap = ha.getPhoneMap();

            for (PhoneBean pb : phoneBeanMap.values())
            {
                if(pb.getStatus().equals("1")&&pb.isSelected())
                {
                    jsonObject.put("rec_fd", pb.getFd());
                    OkSocket.open(info).send(new TestSendData(jsonObject));
                }
            }

            jsonObject.put("rec_fd", ha.getFd());
            OkSocket.open(info).send(new TestSendData(jsonObject));

        }catch (Exception e)
        {}
    }

    public void autoRunApk(String apk_id,String packageName)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            Context context = this.ha.getContext();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("apk_id", apk_id);
            jsonObject.put("apk_package_name", packageName);
            jsonObject.put("cmd", "auto_read_apk");
            jsonObject.put("action", "client");

            Map<String,PhoneBean>  phoneBeanMap = ha.getPhoneMap();

            for (PhoneBean pb : phoneBeanMap.values())
            {
                if(pb.getStatus().equals("1")&&pb.isSelected())
                {
                    jsonObject.put("rec_fd", pb.getFd());
                    OkSocket.open(info).send(new TestSendData(jsonObject));
                }
            }

            jsonObject.put("rec_fd", ha.getFd());
            OkSocket.open(info).send(new TestSendData(jsonObject));

        }catch (Exception e)
        {}
    }

    public void  runCmd(String cmdID,String runNum)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("cmd_id", cmdID);
            jsonObject.put("run_num", runNum);
            jsonObject.put("cmd", "run_cmd_script");
            jsonObject.put("action", "client");


            Map<String,PhoneBean>  phoneBeanMap = ha.getPhoneMap();

            for (PhoneBean pb : phoneBeanMap.values())
            {
                if(pb.getStatus().equals("1")&&pb.isSelected())
                {
                    jsonObject.put("rec_fd", pb.getFd());
                    OkSocket.open(info).send(new TestSendData(jsonObject));
                }
            }



        }catch (Exception e)
        {}


    }

    public void  stopCmd(String cmdID,PhoneBean pb)
    {
        try {
            ConnectionInfo info = this.ha.getSocketinfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("cmd_id", cmdID);
            jsonObject.put("cmd", "stop_cmd_script");
            jsonObject.put("action", "client");

            jsonObject.put("rec_fd", pb.getFd());
            OkSocket.open(info).send(new TestSendData(jsonObject));


        }catch (Exception e)
        {}
    }

    public void editQueueTask(String taskName,String taskID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "edit_queue_task");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("task_id",taskID);
            jsonObject.put("task_name",taskName);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }


    public void deleteQueueTask(String taskID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "delete_queue_task");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("task_id",taskID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }

    public void execQueueTask(String taskID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "exec_queue_task");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("queue_task_id",taskID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }

    public void stopQueueTask(String taskID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "stop_queue_task");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());
            jsonObject.put("queue_task_id",taskID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }




    public void editQueueTaskItem(String taskID,String itemID,String apkName,String packageName,String execTime)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "edit_queue_task_item");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("queue_task_id",taskID);
            jsonObject.put("id",itemID);
            jsonObject.put("apk_name",apkName);
            jsonObject.put("package_name",packageName);
            jsonObject.put("exec_time",execTime);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }


    public void deleteQueueTaskItem(String itemID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "delete_queue_task_item");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("id",itemID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }

    public void deleteQueueTaskDevice(String taskID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "delete_queue_task_device");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("queue_task_id",taskID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }

    public void addQueueTaskDevice(String taskID,String mobileID)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "add_queue_task_device");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("queue_task_id",taskID);
            jsonObject.put("mobile_serila",mobileID);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }


    public void updateRunningTaskItem(String Serial,String run_time,String task_item_id,String queue_task_id)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "update_running_task_item");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("Serial",Serial);
            jsonObject.put("run_time",run_time);
            jsonObject.put("task_item_id",task_item_id);
            jsonObject.put("queue_task_id",queue_task_id);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }

    public void removeRunningTaskItem(String Serial,String run_time,String task_item_id,String queue_task_id)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "remove_running_task_item");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("Serial",Serial);
            jsonObject.put("run_time",run_time);
            jsonObject.put("task_item_id",task_item_id);
            jsonObject.put("queue_task_id",queue_task_id);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }
    public void pulseQueueTask(String queue_task_id)
    {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "pulse_queue_task");
            jsonObject.put("action", "client");
            jsonObject.put("mer_id", ha.getMerID());

            jsonObject.put("queue_task_id",queue_task_id);
            OkSocket.open(ha.getSocketinfo()).send(new TestSendData(jsonObject));

        }catch (Exception e)
        { }

    }




}
