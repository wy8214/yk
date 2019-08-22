package com.hwytapp.Service;


import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.bee.yunkong.App;
import com.bee.yunkong.util.logger.MyLog;
import com.hwytapp.Utils.AppUtils;


public class AutoReadService extends AccessibilityService {

    private Handler handler = new Handler();

    private boolean isClick = false;

    private boolean isLanuch = false;
    private App app;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

//        Log.d("getClassName  -----",event.getClassName().toString()+"  eventType:"+event.getEventType());

        int eventType = event.getEventType();

        if(eventType==AccessibilityEvent.TYPE_VIEW_SCROLLED||eventType==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            if(isClick)
                return;

            final AccessibilityNodeInfo rootNode = getRootInActiveWindow();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handlerEvent(rootNode);
                }
            }, 100);

        }

    }


    private void handlerEvent(AccessibilityNodeInfo rootNode)
    {
        app = (App) getApplication();

//        app.setCurrentApkPackName("com.ldzs.zhangxin");

        String currentApkPanckName = app.getCurrentApkPackName();

        if (!currentApkPanckName.equals("null")) {

            switch (currentApkPanckName) {
                case "com.jifen.qukan":
                    findQttLanuchText(rootNode);
                    break;
                case "com.yanhui.qktx":
                    findQKTTLanuchText(rootNode);
                    break;

                case "cn.weli.story":
                    findWLKKLanuchText(rootNode);
                    break;

                case "com.songheng.eastnews":
                    findDFTTLanuchText(rootNode);
                    break;

                case "com.ifeng.kuaitoutiao":
                    findKTTLanuchText(rootNode);
                    break;
                case "com.sohu.infonews":
                    findSHZXanuchText(rootNode);
                    break;
                case "cn.youth.news":
                    findZQKDLanuchText(rootNode);
                    break;
                case "com.ss.android.article.news":
                    findTodayLanuchText(rootNode);
                    break;

                case "com.expflow.reading":
                    findYTTLanuchText(rootNode);
                    break;

                case "com.koramgame.xianshi.kl":
                    findXJWZLanuchText(rootNode);
                    break;

                case "com.toutiao.news":
                    findXttLanuchText(rootNode);
                    break;

                case "com.yingliang.clicknews":
                    findDDXWLanuchText(rootNode);
                    break;

                case "com.dzkandian":
                    findDZKDLanuchText(rootNode);
                    break;

                case "com.zhangku.qukandian":
                    findQKDLanuchText(rootNode);
                    break;
                case "com.songshu.jucai":
                    findSSZXLanuchText(rootNode);
                    break;
                case "com.xcm.huasheng":
                    findHSTTLanuchText(rootNode);
                    break;
                case "com.billionstech.grassbook":
                    findHCGSLanuchText(rootNode);
                    break;
                case "com.yooee.headline":
                    findJHTTLanuchText(rootNode);
                    break;
                case "com.qudu.weiqukan":
                    findWQKLanuchText(rootNode);
                    break;
                case "com.ldzs.zhangxin":
                    findMYTTLanuchText(rootNode);
                    break;
                case "com.caishi.cronus":
                    findWLTTLanuchText(rootNode);
                    break;
                case "com.cashtoutiao":
                    findHTTLanuchText(rootNode);
                    break;
                case "com.xiangzi.jukandian":
                    findJKDLanuchText(rootNode);
                    break;

                case "com.netease.news.lite":
                    findWYXWJSBLanuchText(rootNode);
                    break;
                default:
                    break;
            }

        }

        isClick=false;
    }

    // 网易新闻急速版
    private void findWYXWJSBLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("查看")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("继续赚钱")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("停止播放")) {
                        nodeClick(node);
                    }




                    if(text.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("查看全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.netease.news.lite:id/u5")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }



                findWYXWJSBLanuchText(node);
            }

        }

    }

    // 聚看点
    private void findJKDLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("查看")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("继续赚钱")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("停止播放")) {
                        nodeClick(node);
                    }




                    if(text.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+(r.centerY()-100));
                    }

                    if(des.toString().trim().contains("版权说明")) {
                        AccessibilityNodeInfo node1 = rootNode.getChild(i+1);
                        Rect r1 = new Rect();
                        node1.getBoundsInScreen(r1);

                        if( r1.centerX()>0&&r1.centerY()>150&&r1.centerY()<800)
                        {
                            if(app.getIsShellRun())
                                return;
                            app.setIsShellRun(true);

                            AppUtils.execShellCmd("input tap "+" "+r1.centerX() +" "+(r1.centerY()-100));
                        }

                    }


                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/time_period_sign_in")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }



                findJKDLanuchText(node);
            }

        }

    }


    // 惠头条
    private void findHTTLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("查看")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("停止播放")) {
                        nodeClick(node);
                    }




                    if(text.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+(r.centerY()-100));
                    }

                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800) {
                        nodeClick(node);
                    }


                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/time_period_sign_in")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }



                findHTTLanuchText(node);
            }

        }

    }


    // 唔哩头条
    private void findWLTTLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("停止播放")) {
                        nodeClick(node);
                    }




                    if(text.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+(r.centerY()-100));
                    }

                    if(des.toString().trim().contains("阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800) {
                        nodeClick(node);
                    }


                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/time_period_sign_in")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }



                findWLTTLanuchText(node);
            }

        }

    }

    // 蚂蚁头条
    private void findMYTTLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }


                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("open")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+(r.centerY()-100));
                    }


                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/time_period_sign_in")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.ldzs.zhangxin:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }



                findMYTTLanuchText(node);
            }

        }

    }
    // 微趣看
    private void findWQKLanuchText(AccessibilityNodeInfo rootNode) {



        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.qudu.weiqukan:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.billionstech.grassbook:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                findWQKLanuchText(node);
            }

        }

    }

    // 聚合头条
    private void findJHTTLanuchText(AccessibilityNodeInfo rootNode) {



        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.yooee.headline:id/close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.billionstech.grassbook:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                findJHTTLanuchText(node);
            }

        }

    }
    // 海草公社
    private void findHCGSLanuchText(AccessibilityNodeInfo rootNode) {



        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.billionstech.grassbook:id/iv_ranking_card_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.billionstech.grassbook:id/iv_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }


                findHCGSLanuchText(node);
            }

        }

    }

    // 花生头条
    private void findHSTTLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.xcm.huasheng:id/iv_public_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                findHSTTLanuchText(node);
            }
        }

    }


    // 松鼠资讯
    private void findSSZXLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();



                if (text != null ) {

                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("先逛逛")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("查看全文")) {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {

                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+(r.centerY()+100));
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.zhangku.qukandian:id/header_information_read_all_btn")) {

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    if(r.centerX()>0&&r.centerY()>150&&r.centerY()<800) {

                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.songshu.jucai:id/close")) {

                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                findSSZXLanuchText(node);
            }
        }

    }



    // 网易新闻急速版
    private void findQKDLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();



                if (text != null ) {

                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("继续阅读")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("先逛逛")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("狠心拒绝")) {
                        nodeClick(node);
                    }
                }



                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("查看全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.zhangku.qukandian:id/header_information_read_all_btn")) {

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    if(r.centerX()>0&&r.centerY()>150&&r.centerY()<800) {

                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
                findQKDLanuchText(node);
            }
        }

    }


    // 大众看点
    private void findDZKDLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {

                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+(r.centerY()+100));
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                findDZKDLanuchText(node);
            }
        }

    }

    // 点点新闻
    private void findDDXWLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);


                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("查看全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                findDDXWLanuchText(node);
            }
        }

    }

    // 薪头条
    private void findXttLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                Log.i("rootNode -----",rootNode.toString());
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {

                    Log.i("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("任务大厅")) {

                        if(app.getIsAbRun())
                            return;

                        app.setIsAbRun(true);
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
                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("去看看")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("查看全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.i("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());


                    if(des.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                findXttLanuchText(node);
            }
        }

    }

    // 小桔文摘
    private void findXJWZLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {

                    Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                findXJWZLanuchText(node);
            }
        }

    }


    //  悦头条
    private void findYTTLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {

                    Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("阅读领取")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("点击阅读全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                findYTTLanuchText(node);
            }
        }

    }


    //今日头条
    private void findTodayLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {

                    Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("百度一下")) {
                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input keyevent 4");
                    }

                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                findTodayLanuchText(node);
            }
        }

    }

    //中青看点
    private void findZQKDLanuchText(AccessibilityNodeInfo rootNode) {


        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {


                    Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("取消")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("残忍拒绝")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("去拆现金")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("跳过")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("百度一下")) {
                        if(app.getIsShellRun())
                            return;
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input keyevent 4");
                    }
                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.youth.news:id/tv_pass")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                findZQKDLanuchText(node);
            }
        }

    }


    //搜狐资讯
    private void findSHZXanuchText(AccessibilityNodeInfo rootNode) {


        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {


                    Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("停止播放")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }


                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.sohu.infonews:id/normaldlg_btn_close")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Rect r = new Rect();
//                    node.getBoundsInScreen(r);
//                    Log.d("getViewIdResourceName -", node.getViewIdResourceName()  + "   Rect:" + r.toString() );
//
//                    app.setIsShellRun(true);
//                    AppUtils.execShellCmd("input tap " + " " + r.centerX() + " " + r.centerY());

                }


                findSHZXanuchText(node);
            }
        }

    }

    //快头条
    private void findKTTLanuchText(AccessibilityNodeInfo rootNode) {


        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
                    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {


                    Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());

                    Rect r = new Rect();
                    node.getBoundsInScreen(r);

                    if(text.toString().trim().contains("否")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }
                    if(text.toString().trim().contains("允许")) {
                        nodeClick(node);
                    }
                }

                if(des!=null)
                {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                    if(des.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }
                    if(des.toString().trim().contains("展开全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                    {
                        nodeClick(node);
                    }

                }
                findKTTLanuchText(node);
            }
        }

    }

    //东方头条
    private void findDFTTLanuchText(AccessibilityNodeInfo rootNode) {


            if (rootNode != null) {
                //从最后一行开始找起
                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                    //Log.d("rootNode index -----",i+"");
                    AccessibilityNodeInfo node = rootNode.getChild(i);
                    //如果node为空则跳过该节点
                    if (node == null) {
                        continue;
                    }
                    CharSequence text = node.getText();

                    CharSequence des = node.getContentDescription();
                    String resID = node.getViewIdResourceName();

                    if(resID!=null)
                    {
    //                    Log.d("Resource id -----", node.getViewIdResourceName());
                    }

                    if (text != null ) {

                        Log.d("text -----",text.toString() + " index:"+i+" Resource id:"+ node.getViewIdResourceName());
                        if(text.toString().trim().contains("查  看")) {
                            nodeClick(node);
                        }
                        if(text.toString().trim().contains("关闭")) {
                            nodeClick(node);
                        }
                        if(text.toString().trim().contains("确定")) {
                            nodeClick(node);
                        }
                        if(text.toString().trim().contains("允许")) {
                            nodeClick(node);
                        }

                    }

                    if(des!=null)
                    {
                        Rect r = new Rect();
                        node.getBoundsInScreen(r);
                        Log.d("des -----",des.toString() + " ret:"+r.toString()+" Resource id:"+ node.getViewIdResourceName());

                        if(des.toString().trim().contains("关闭")) {
                            nodeClick(node);
                        }
                        if(des.toString().trim().contains("点击查看全文")&& r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                        {
                            nodeClick(node);
                        }

                    }
                    findDFTTLanuchText(node);
                }
            }

    }

    //微鲤看看
    private void findWLKKLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();
                CharSequence des = node.getContentDescription();

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.weli.story:id/ll_height_more")) {
                    Rect r = new Rect();
                    node.getBoundsInScreen(r);
                    Log.d("getViewIdResourceName -", node.getViewIdResourceName()  + "   Rect:" + r.toString() );
                    if (r.centerX() > 0 && r.centerY() > 150 && r.centerY() < 800) {
                        app.setIsShellRun(true);
                        AppUtils.execShellCmd("input tap " + " " + r.centerX() + " " + r.centerY());
                    }
                }

                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.weli.story:id/text_ok")) {
                    nodeClick(node);
                }


                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("cn.weli.story:id/iv_close")) {
                    nodeClick(node);

                }
                findWLKKLanuchText(node);
            }
        }

    }

    //趣看天下
    private void findQKTTLanuchText(AccessibilityNodeInfo rootNode) {

        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();
                CharSequence des = node.getContentDescription();

//                Log.d("getViewIdResourceName -",node.getViewIdResourceName()+ " index:"+i);

                if(app.getCurrentApkPackName().equals("com.yanhui.qktx")&&!app.getIsShellRun()&&node.getViewIdResourceName()!=null&&node.getViewIdResourceName().equals("com.yanhui.qktx:id/start"))
                {
                    app.setIsShellRun(true);
                    AppUtils.execShellCmd("input keyevent 4");
                    app.getHandler().removeCallbacksAndMessages(null);

                    Handler handler =  app.getHandler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            app.setIsAbRun(false);

                        }
                    }, 2000);
                }




                if (text != null ) {

                    if(text.toString().trim().equals("领取")&&!app.getIsShellRun()){
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }

                }

                if(des!=null)
                {

                    if(des.toString().trim().contains("展开查看全文")) {
                        if(app.getIsShellRun())
                            return;

                        Rect r = new Rect();
                        node.getBoundsInScreen(r);
                        Log.d("getViewIdResourceName -",node.getViewIdResourceName()+ " index:"+i+"   Rect:"+ r.toString());
                        if(!app.getIsShellRun()&&r.centerX()>0&&r.centerY()>150&&r.centerY()<800)
                        {
                            app.setIsShellRun(true);
                            AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+r.centerY());

                        }
                    }

                    if(des.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }


                }
                findQKTTLanuchText(node);
            }
        }
    }




    //趣头条
    private void findQttLanuchText(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            //从最后一行开始找起
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {

                //Log.d("rootNode index -----",i+"");
                AccessibilityNodeInfo node = rootNode.getChild(i);
                //如果node为空则跳过该节点
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();

                CharSequence des = node.getContentDescription();
                String resID = node.getViewIdResourceName();

                if(resID!=null)
                {
//                    Log.d("Resource id -----", node.getViewIdResourceName());
                }

                if (text != null ) {

//                    Log.d("text -----",text.toString() + " index:"+i);

                    if(text.toString().trim().equals("领取")&&!app.getIsShellRun()){
                        nodeClick(node);
                    }


                    if(text.toString().trim().contains("查看详情")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                    if(text.toString().trim().contains("确定")) {
                        nodeClick(node);
                    }


                }

                if(des!=null)
                {
                    if(des.toString().trim().contains("领取")) {
                        nodeClick(node);
                    }

                    if(des.toString().trim().contains("关闭")) {
                        nodeClick(node);
                    }

                }
                if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals("com.jifen.qukan:id/oy")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                findQttLanuchText(node);
            }
        }
    }



    private void nodeClick(AccessibilityNodeInfo node)
    {
        if(app.getIsShellRun())
            return;
        app.setIsShellRun(true);
        Rect r = new Rect();
        node.getBoundsInScreen(r);
        AppUtils.execShellCmd("input tap "+" "+r.centerX() +" "+r.centerY());


    }


    /**
     * 服务连接
     */
    @Override
    protected void onServiceConnected() {

        MyLog.d("自动阅读服务已被开启...");

        Toast.makeText(this, "自动服务已被开启...", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {

        Toast.makeText(this, "onInterrupt-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务断开
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "自动服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
}
