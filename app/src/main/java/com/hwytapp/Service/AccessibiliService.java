package com.hwytapp.Service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.bee.yunkong.App;
import com.bee.yunkong.core.EventTag;
import com.bee.yunkong.core.MyEvent;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Bean.QueueTaskItemBean;
import com.hwytapp.Common.MasterMethod;
import com.hwytapp.Utils.AppUtils;
import com.hwytapp.Utils.MobileUtils;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class AccessibiliService extends IntentService {

    private Handler handler = new Handler();

    private int backTime = 0;
    private int mainTime = 0;
    private int newsTime = 0;
    private int openTime = 0;
    private final String TAG = "Accessibili";


    private String currentActivityName = "";

    private App app;

    private int threadTap = 1500;

    private IConnectionManager mManager;

    //必须实现父类的构造方法
    public AccessibiliService() {
        super("AccessibiliService");
    }

    //必须重写的核心方法
    @Override
    protected void onHandleIntent(Intent intent) {

        app = (App) getApplication();
        app.setHandler(handler);

        MasterMethod mm = new MasterMethod(app);
        MyEvent me = new MyEvent(EventTag.server_notice);
        while (true) {
            try {
                Thread.sleep(threadTap);

                app.setCurrentApkPackName("null");

                for (QueueTaskItemBean qti : app.getQueueTaskItemMap().values())
                {
                    if(qti.getStopTime().toString().equals("0"))
                    {
                        me.setObject(qti.getApkName()+"开始执行");
                        EventBus.getDefault().post(me);
                        qti.setStopTime( System.currentTimeMillis() + Long.parseLong(qti.getExecTime())*60*1000 -  qti.getRunTime());
                        qti.setStartTime(System.currentTimeMillis());
                        app.setCurrentApkPackName(qti.getPackageName());
                        break;
                    }else if(System.currentTimeMillis()>qti.getStopTime())
                    {

                        AppUtils.execShellCmd("am force-stop "+qti.getPackageName());

                        me.setObject(qti.getApkName()+"执行完毕");
                        EventBus.getDefault().post(me);

                        app.getQueueTaskItemMap().remove(qti.getID());
                        mm.removeRunningTaskItem(MobileUtils.getMoblieIMEI(app),"",qti.getID(),qti.getQueueTaskID());
                        continue;
                    }else
                    {
                        Log.i("QueueTaskItem++++++++", "  StopTime:" + System.currentTimeMillis() + "   getStartTime:"+  qti.getStartTime()+"  getRunTime:"+qti.getRunTime());
                        Long runTime = System.currentTimeMillis() - qti.getStartTime() + qti.getRunTime();

                        app.setCurrentApkPackName(qti.getPackageName());

                        if(Math.random()*10>8)
                        {
                            me.setObject(qti.getApkName()+"执行"+qti.getExecTime()+"分钟,已执行"+runTime/(1000*60)+"分钟");
                            EventBus.getDefault().post(me);
                            mm.updateRunningTaskItem(MobileUtils.getMoblieIMEI(app),runTime+"",qti.getID(),qti.getQueueTaskID());
                        }

                        break;
                    }
                }

                threadTap = 1500;

                String currentPackName = app.getCurrentApkPackName();

                if (currentPackName.equals("null"))
                {
                    handler.removeCallbacksAndMessages(null);
                    AppUtils.execShellCmd("settings  put  secure  enabled_accessibility_services  com.bee.yunkong/com.hwytapp.Service.AutoReadService");
                    AppUtils.execShellCmd("settings  put  secure  accessibility_enabled  0");
                    openApp("com.bee.yunkong");
                    break;
                }

                String topActivityName = AppUtils.getTopActivity();
                Log.i("topActivityName++++++", topActivityName+"  getIsAbRun:"+app.getIsAbRun());
                String[] strs = topActivityName.split(" ");

                String topActivity = "";

                if (strs.length > 0) {
                    topActivity = strs[3];
                } else {
                    continue;
                }

                Log.i("topActivityName++++++++", topActivityName+"  getIsAbRun:"+app.getIsAbRun());

                if (!topActivityName.contains(currentPackName)) {
                    app.setIsAbRun(false);
                    handler.removeCallbacksAndMessages(null);

                    AppUtils.execShellCmd("input keyevent 4");
                    AppUtils.execShellCmd("input keyevent 4");
                    openApp(currentPackName);
                    currentActivityName="";
                    threadTap=8000;
                    continue;
                }

                if (!currentActivityName.equals("") && !currentActivityName.equals(topActivity)&&topActivityName.contains(currentPackName)) {
                    handler.removeCallbacksAndMessages(null);
                    app.setIsAbRun(false);
                }

                if (app.getIsAbRun())
                    continue;

                currentActivityName = topActivity;



//                if (topActivity.contains("com.bee.yunkong") || topActivity.contains("hwyt.com.watch")) {
//                    AppUtils.execShellCmd("input keyevent 3");
//                    continue;
//                }

                Log.i("dumpsys++++++++", topActivityName + "  topActivity:" + topActivity + "   getIsAbRun:" + app.getIsAbRun()+ "   currentPackName:"+currentPackName);
                switch (currentPackName) {
                    case "com.jifen.qukan":
                        accessQTT(topActivity);
                        break;
                    case "com.yanhui.qktx":
                        accessQKTX(topActivity);
                        break;

                    case "cn.weli.story":
                        accessWLKK(topActivity);
                        break;
                    case "com.songheng.eastnews":
                        accessDFTT(topActivity);
                        break;
                    case "com.ifeng.kuaitoutiao":
                        accessKTT(topActivity);
                        break;

                    case "com.sohu.infonews":
                        accessSHZX(topActivity);
                        break;

                    case "cn.youth.news":
                        accessZQKD(topActivity);
                        break;

                    case "com.ss.android.article.news":
                        accessToday(topActivity);
                        break;

                    case "com.expflow.reading":
                        accessYTT(topActivity);
                        break;

                    case "com.koramgame.xianshi.kl":
                        accessXJWZ(topActivity);
                        break;

                    case "com.toutiao.news":
                        accessXTT(topActivity);
                        break;
                    case "com.hodanet.handnews":
                        accessZSTT(topActivity);
                        break;
                    case "com.yingliang.clicknews":
                        accessDDXW(topActivity);
                        break;

                    case "com.dzkandian":
                        accessDZKD(topActivity);
                        break;

                    case "com.zhangku.qukandian":
                        accessQKD(topActivity);
                        break;

                    case "com.songshu.jucai":
                        accessSSZX(topActivity);
                        break;

                    case "com.xcm.huasheng":
                        accessHSTT(topActivity);
                        break;

                    case "com.billionstech.grassbook":
                        accessHCGS(topActivity);
                        break;

                    case "com.yooee.headline":
                        accessJHTT(topActivity);
                        break;
                    case "com.qudu.weiqukan":
                        accessWQK(topActivity);
                        break;
                    case "com.ldzs.zhangxin":
                        accessMYTT(topActivity);
                        break;

                    case "com.caishi.cronus":
                        accessWLTT(topActivity);
                        break;
                    case "com.cashtoutiao":
                        accessHTT(topActivity);
                        break;
                    case "com.xiangzi.jukandian":
                        accessJKD(topActivity);
                        break;
                    case "com.netease.news.lite":
                        accessWYXWJSB(topActivity);
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
            }

        }

    }
    //网易新闻急速版
    private  void accessWYXWJSB(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.netease.news.lite/com.netease.nr.phone.main.MainActivity";
        String newsActivity = "com.netease.news.lite/com.netease.nr.biz.news.detailpage.NewsPageActivity";

        if (backTime > 3) {
            openApp("com.netease.news.lite");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.netease.news.lite");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }


    //聚看点
    private  void accessJKD(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.xiangzi.jukandian/.activity.MainActivity";
        String newsActivity = "com.xiangzi.jukandian/.activity.WebViewActivity";

        if (backTime > 3) {
            openApp("com.xiangzi.jukandian");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.xiangzi.jukandian");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //惠头条头条
    private  void accessHTT(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.cashtoutiao/.account.ui.main.MainTabActivity";
        String newsActivity = "com.cashtoutiao/.news.ui.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.cashtoutiao");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.cashtoutiao");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }



    //唔哩头条
    private  void accessWLTT(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.caishi.cronus/.ui.main.MainActivity";
        String newsActivity = "com.caishi.cronus/.ui.news.view.DetailsActivity";


        if (backTime > 3) {
            openApp("com.caishi.cronus");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.caishi.cronus");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }


    //蚂蚁头条
    private  void accessMYTT(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.ldzs.zhangxin/com.weishang.wxrd.activity.MainActivity";
        String newsActivity = "com.ldzs.zhangxin/com.weishang.wxrd.activity.WebViewActivity";


        if (backTime > 3) {
            openApp("com.ldzs.zhangxin");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.ldzs.zhangxin");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }




    //微趣看
    private  void accessWQK(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.qudu.weiqukan/.Home";
        String newsActivity = "com.qudu.weiqukan/.detail.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.qudu.weiqukan");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.qudu.weiqukan");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //聚合头条
    private  void accessJHTT(String topActivity)
    {
        app.setIsAbRun(true);


        String mainActivity = "com.yooee.headline/.ui.activity.MainActivity";
        String newsActivity = "com.yooee.headline/.ui.activity.ArticleDetailActivity";


        if (backTime > 3) {
            openApp("com.yooee.headline");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.yooee.headline");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //海草公社
    private  void accessHCGS(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.billionstech.grassbook/.business.main.MainActivity";
        String newsActivity = "com.billionstech.grassbook/.business.main.find.findDetail.FindDetailActivity";


        if (backTime > 3) {
            openApp("com.billionstech.grassbook");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.billionstech.grassbook");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppUtils.execShellCmd("input swipe 150 800 150 300 1000");
                }
            }, 1000);


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    webSwipe();

                }
            }, 3000);

            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(11, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //花生头条
    private  void accessHSTT(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.xcm.huasheng/.ui.activity.MainActivity";
        String newsActivity = "com.xcm.huasheng/.ui.activity.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.xcm.huasheng");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.xcm.huasheng");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }
    //松鼠资讯
    private  void accessSSZX(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.songshu.jucai/.app.main.MainAc";
        String newsActivity = "com.songshu.jucai/.app.detail.ArticleDetailActivity";


        if (backTime > 3) {
            openApp("com.songshu.jucai");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.songshu.jucai");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            if(newsTime>0)
            {
                AppUtils.execShellCmd("input tap 30 90");
                newsTime = 0;
                app.setIsAbRun(false);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        app.setIsAbRun(false);

                    }
                }, 2000);
                return;
            }

            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //趣看点
    private  void accessQKD(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.zhangku.qukandian/.activitys.MainActivity";
        String newsActivity = "com.zhangku.qukandian/.activitys.information.InformationDetailsAtivity";


        if (backTime > 3) {
            openApp("com.zhangku.qukandian");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.zhangku.qukandian");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            if(newsTime>0)
            {
                AppUtils.execShellCmd("input tap 30 90");
                newsTime = 0;
                app.setIsAbRun(false);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        app.setIsAbRun(false);

                    }
                }, 2000);
                return;
            }

            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }




    //大众看点
    private  void accessDZKD(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.dzkandian/.mvp.common.ui.activity.MainActivity";
        String newsActivity = "com.dzkandian/.mvp.news.ui.activity.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.dzkandian");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.dzkandian");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            if(newsTime>0)
            {
                AppUtils.execShellCmd("input tap 30 90");
                newsTime = 0;
                app.setIsAbRun(false);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        app.setIsAbRun(false);

                    }
                }, 2000);
                return;
            }

            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }


    //点点新闻
    private  void accessDDXW(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.yingliang.clicknews/.MainActivity";
        String newsActivity = "com.yingliang.clicknews/.activity.NewsActivity";


        if (backTime > 3) {
            openApp("com.yingliang.clicknews");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.yingliang.clicknews");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {

            if(newsTime>0)
            {
                AppUtils.execShellCmd("input tap 30 90");
                newsTime = 0;
                app.setIsAbRun(false);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        app.setIsAbRun(false);

                    }
                }, 2000);
                return;
            }

            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //掌上头条
    private  void accessZSTT(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.hodanet.handnews/com.hodanet.news.bussiness.MainActivity";
        String newsActivity = "com.hodanet.handnews/com.hodanet.news.detail.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.hodanet.handnews");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.hodanet.handnews");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }




    //薪头条
    private  void accessXTT(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.toutiao.news/.page.MainActivity";
        String newsActivity = "com.toutiao.news/.page.news.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.toutiao.news");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.toutiao.news");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }

    //小桔文摘
    private  void accessXJWZ(String topActivity)
    {
        app.setIsAbRun(true);

        String mainActivity = "com.koramgame.xianshi.kl/.ui.home.MainActivity";
        String newsActivity = "com.koramgame.xianshi.kl/.ui.feed.detail.NewsDetailActivity";


        if (backTime > 3) {
            openApp("com.expflow.reading");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.expflow.reading");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

    }
    //悦头条
    private void accessYTT(String topActivity) {
        app.setIsAbRun(true);
        String mainActivity = "com.expflow.reading/.activity.MainActivity";
        String newsActivity = "com.expflow.reading/.activity.DetailNewsActivity";

        if (backTime > 3) {
            openApp("com.expflow.reading");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.expflow.reading");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }


    }



    //今日头条
    private void accessToday(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "com.ss.android.article.news/.activity.MainActivity";
        String newsActivity = "com.ss.android.article.news/com.ss.android.detail.feature.detail2.view.NewDetailActivity";


        if (backTime > 3) {
            openApp("com.ss.android.article.news");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("com.ss.android.article.news");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) ) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }


    }



    //中青看点
    private void accessZQKD(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "cn.youth.news/com.weishang.wxrd.activity.MainActivity";
        String newsActivity = "cn.youth.news/com.weishang.wxrd.activity.WebViewActivity";
        String openActivity =   "cn.youth.news/com.baidu.mobads.AppActivity";


        if (backTime > 3) {
            openApp("cn.youth.news");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            openApp("cn.youth.news");
        }

        if(openTime>3)
        {
            openTime=0;
            AppUtils.execShellCmd("input keyevent 4");

            app.setIsAbRun(false);
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity) && !topActivity.equals(openActivity)) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");
        }

        if (topActivity.equals(openActivity)) {
            openTime++;
            app.setIsAbRun(false);
            Log.i("openTime++++++++", openTime + "");
        }
    }

    //搜狐资讯
    private void accessSHZX(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "com.sohu.infonews/com.sohu.quicknews.homeModel.activity.HomeActivity";
        String newsActivity = "com.sohu.infonews/com.sohu.quicknews.articleModel.activity.DetailActivity";

        if (backTime > 3) {
            openApp("com.sohu.infonews");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 6 || newsTime > 3) {
            openApp("com.sohu.infonews");
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity)) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(8, 5000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");

        }
    }


    //快头条
    private void accessKTT(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "com.ifeng.kuaitoutiao/com.ifeng.news2.activity.IfengTabMainActivity";
        String newsActivity = "com.ifeng.kuaitoutiao/com.ifeng.news2.activity.DocDetailActivity";

        if (backTime > 3) {
            openApp("com.ifeng.kuaitoutiao");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 6 || newsTime > 3) {
            openApp("com.ifeng.kuaitoutiao");
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity)) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 4000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");

        }
    }




    //东方头条
    private void accessDFTT(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "com.songheng.eastnews/com.songheng.eastfirst.common.view.activity.MainActivity";
        String newsActivity = "com.songheng.eastnews/com.songheng.eastfirst.business.newsdetail.view.activity.NewsDetailH5Activity";

        if (backTime > 3) {
            openApp("com.songheng.eastnews");
            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 6 || newsTime > 3) {
            openApp("com.songheng.eastnews");
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity)) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(8, 5000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");

        }
    }

    //微鲤看看
    private void accessWLKK(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "cn.weli.story/cn.etouch.ecalendar.MainActivity";
        String newsActivity = "cn.weli.story/cn.etouch.ecalendar.tools.life.LifeDetailsActivity";

        if (backTime > 3) {
            openApp("cn.weli.story");


            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 6 || newsTime > 3) {
            mainTime = 0;
            newsTime = 0;
            openApp("cn.weli.story");
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity)) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(8, 5000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;


            Log.i("newsTime++++++++", newsTime + "");

        }
    }

    //趣看天下
    private void accessQKTX(String topActivity) {
        app.setIsAbRun(true);

        String mainActivity = "com.yanhui.qktx/.activity.MainActivity";

        String newsActivity = "com.yanhui.qktx/.processweb.NewsProcessWebViewActivity";

        if (backTime > 3) {
            openApp("com.yanhui.qktx");


            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 6 || newsTime > 6) {
            mainTime = 0;
            newsTime = 0;
            openApp("com.yanhui.qktx");
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity)) {
            Log.i("not activity++++++++", topActivity);
            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(8, 5000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");

        }
    }

    //趣头条
    private void accessQTT(String topActivity) {
        app.setIsAbRun(true);
        // handler.removeCallbacksAndMessages(null);

        String mainActivity = "com.jifen.qukan/com.jifen.qkbase.main.MainActivity";
        String newsActivity = "com.jifen.qukan/.content.newsdetail.news.NewsDetailActivity";

        if (backTime > 3) {
            openApp("com.jifen.qukan");

            backTime = 0;
            mainTime = 0;
            newsTime = 0;
        }

        if (mainTime > 3 || newsTime > 3) {
            mainTime = 0;
            newsTime = 0;

            AppUtils.execShellCmd("input keyevent 4");
            AppUtils.execShellCmd("input keyevent 4");
            openApp("com.jifen.qukan");
        }

        if (!topActivity.equals(mainActivity) && !topActivity.equals(newsActivity)) {
            Log.i("not activity++++++++", topActivity);

            backActivity();
            backTime++;
        }

        if (topActivity.equals(mainActivity)) {
            webSwipe();
            backTime = 0;
            mainTime++;
            newsTime = 0;

            Log.i("mainTime++++++++", mainTime + "");
        }

        if (topActivity.equals(newsActivity)) {
            NewsDetailActivitySwipe(10, 3000, 3000);
            backTime = 0;
            newsTime++;
            mainTime = 0;

            Log.i("newsTime++++++++", newsTime + "");

        }
    }

    private void backActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUtils.execShellCmd("input keyevent 4");
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                app.setIsAbRun(false);
            }
        }, 3000);
    }

    private void webSwipe() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUtils.execShellCmd("input swipe 150 800 150 300 1000");
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUtils.execShellCmd("input tap 230 830");
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                app.setIsAbRun(false);

            }
        }, 5500);
    }

    private void NewsDetailActivitySwipe(int swipeInt, int tap, int abrun) {

        for (int i = 1; i < swipeInt; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    app.setIsShellRun(true);
                    AppUtils.execShellCmd("input swipe 150 800 150 300 1000");
                }
            }, tap * i);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    app.setIsShellRun(false);
                }
            }, tap * i + 1500);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUtils.execShellCmd("input keyevent 4");
            }
        }, tap * swipeInt);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                app.setIsAbRun(false);

            }
        }, tap * swipeInt + abrun);

    }

    private void openApp(String packageName)
    {
        PackageManager packageManager = app.getContext().getPackageManager();
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage(packageName);
        app.getContext().startActivity(intent);

//        Intent intent = new Intent();
//        //第一种方式
//        ComponentName cn = new ComponentName("com.example.fm", "com.example.fm.MainFragmentActivity");
//        try {
//            intent.setComponent(cn);
//            //第二种方式
//            //intent.setClassName("com.example.fm", "com.example.fm.MainFragmentActivity");
//            intent.putExtra("test", "intent1");
//            startActivity(intent);
//        } catch (Exception e) {
//            //TODO  可以在这里提示用户没有安装应用或找不到指定Activity，或者是做其他的操作
//        }

//        Context pkgContext = getPackageContext(app.getContext(), packageName);
//        Intent intent = getAppOpenIntentByPackageName(app.getContext(), packageName);
//        if (pkgContext != null && intent != null) {
//            pkgContext.startActivity(intent);
//        }
    }

    public static Intent getAppOpenIntentByPackageName(Context context,String packageName){
        //Activity完整名
        String mainAct = null;
        //根据包名寻找
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;

    }


    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
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
