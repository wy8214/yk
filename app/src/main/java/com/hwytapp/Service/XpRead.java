package com.hwytapp.Service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.bee.yunkong.App;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XpRead implements IXposedHookLoadPackage {
    private String TAG = "xposed hook ";

    private  Object articleWebInstance = null;
    private JSONObject jbArticle = new JSONObject();
    private ConcurrentHashMap<String , JSONObject> articleList =  new ConcurrentHashMap<String , JSONObject>();
    private ConcurrentHashMap<String , Object> objList =  new ConcurrentHashMap<String , Object>();
    private ConcurrentHashMap<String , Class<?>> appObjList =  new ConcurrentHashMap<String , Class<?>>();
    private ConcurrentHashMap<String , Class<?>> articleObjList =  new ConcurrentHashMap<String , Class<?>>();

    public void callLoop(){


        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                try {
//                    XposedBridge.log(TAG + " callLoop ing ... ");

                    if(articleList.size()>0)
                    {
                        for(JSONObject jb : articleList.values()) {

                            String packageName = jb.get("package_name").toString();

                            if(packageName.equals("cn.youth.news")) {
                                Object objectWebView = objList.get(packageName);
                                XposedBridge.log(TAG + " objectWebView "+objectWebView.toString());

                                Class<?> articleObjClazz = articleObjList.get(packageName);
                                Object artileBean    = articleObjClazz.getConstructor(int.class).newInstance(4);

                                Field field_id = artileBean.getClass().getDeclaredField("id");
                                field_id.set(artileBean, jb.get("id"));
                                Field field_url = artileBean.getClass().getDeclaredField("url");
                                field_url.set(artileBean, jb.get("url"));
                                Field field_title = artileBean.getClass().getDeclaredField("title");
                                field_title.set(artileBean, jb.get("title"));

                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("item", (Parcelable) artileBean);
                                bundle.putString("article_look_from", "home");
                                bundle.putLong("time", System.currentTimeMillis());
                                intent.putExtras(bundle);

                                XposedHelpers.callMethod(objectWebView, "a", intent);
                            }
                            articleList.remove(jb.get("id"));
                        }

                    }
                }catch (Exception e) {
                    XposedBridge.log(TAG +" error "+e.toString());
                    return;
                }


            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1,3000);

    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(TAG + " XP已检测到 "+ loadPackageParam.packageName);
        callLoop();

        final ClassLoader classLoader = loadPackageParam.classLoader;
        if (("cn.youth.news").equals(loadPackageParam.packageName)) {

//            JSONObject articleJson = new JSONObject();
//            articleJson.put("package_name","cn.youth.news");
//            articleJson.put("id","14407127");
//            articleJson.put("url","https://kd.youth.cn/n?timestamp=1552120098&signature=jEVpO2W7mqG5JBQKz8ZBq8JpBhQg3Vw1yRlrdXoAkYeaL34g90&device_type=android&app_version=1.4.2&from=home");
//            articleJson.put("title","喝水长肉的人，这个穴位都不通，饭后按一按，肚子变平腰变细");
//            articleList.put("14407127",articleJson);

            Class<?> articleWebClazz = classLoader.loadClass("com.weishang.wxrd.activity.WebViewActivity");
            appObjList.put("cn.youth.news",articleWebClazz);
            Class<?> articleObjClazz= classLoader.loadClass("com.weishang.wxrd.bean.Article");
            articleObjList.put("cn.youth.news",articleObjClazz);

            XposedHelpers.findAndHookMethod(articleWebClazz, "a", Intent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        articleWebInstance = param.thisObject;
                        objList.put("cn.youth.news",articleWebInstance);

                        Intent intent = (Intent)param.args[0];
                        Bundle var2 = intent.getExtras();

                        Set<String> keySet = var2.keySet();
                        for(String key : keySet) {
                            Object value = var2.get(key);
                            XposedBridge.log(TAG+ " key " + key);
                            XposedBridge.log(TAG+ " value " + value.toString());

                            if(key.equals("item"))
                            {
                                Object cls =  value;
                                Field[] fields = cls.getClass().getDeclaredFields();
                                for(int i=0; i<fields.length; i++){
                                    Field f = fields[i];
                                    f.setAccessible(true);
                                    if(f.getName().equals("id")||f.getName().equals("url")||f.getName().equals("title"))
                                    {
                                        jbArticle.put(f.getName(),f.get(cls));
                                    }
                                    jbArticle.put("package_name","cn.youth.news");
                                }
                            }

                        }


                        XposedBridge.log(TAG+ " jbArticle " +"XP已检测到");
                    } catch (Exception e) {
                        XposedBridge.log(TAG+e.toString());
                        return;
                    }

                }
            });


//            XposedHelpers.findAndHookMethod(articleWeb, "a", Intent.class, new XC_MethodHook() {
//
//                @Override
//                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//
//                    super.beforeHookedMethod(param);
//
//
//                    try {
//                        XposedBridge.log(TAG);
//                        Intent intent = (Intent)param.args[0];
//                        Bundle var2 = intent.getExtras();
//
//                        Set<String> keySet = var2.keySet();
//                        for(String key : keySet) {
//                            Object value = var2.get(key);
//                            XposedBridge.log(TAG+ " key " + key);
//                            XposedBridge.log(TAG+ " value " + value.toString());
//                        }
//
//
//                        XposedBridge.log(TAG+ var2.get("item").toString());
//
//
//                        Object cls = (Object) var2.get("item");
//                        Field field_id = cls.getClass().getDeclaredField("id");
//                        field_id.set(cls,"14407127");
//                        Field field_url = cls.getClass().getDeclaredField("url");
//                        field_url.set(cls,"https://kd.youth.cn/n?timestamp=1552120098&signature=jEVpO2W7mqG5JBQKz8ZBq8JpBhQg3Vw1yRlrdXoAkYeaL34g90&device_type=android&app_version=1.4.2&from=home");
//
//                        Field field_title = cls.getClass().getDeclaredField("title");
//                        field_title.set(cls,"喝水长肉的人，这个穴位都不通，饭后按一按，肚子变平腰变细");
//
//                        var2.putParcelable("item", (Parcelable)cls);
//
//
//
//                        Field[] fields = cls.getClass().getDeclaredFields();
//                        JSONObject jsonObject = new JSONObject();
//                        for(int i=0; i<fields.length; i++){
//                            Field f = fields[i];
//                            f.setAccessible(true);
//                            XposedBridge.log(TAG+ "属性名:" + f.getName() + " 属性值:" + f.get(cls));
//
//                            jsonObject.put(f.getName(),f.get(cls));
//
//                        }
//                        XposedBridge.log(TAG+ "jsonObject:"  + jsonObject.toString());
//                        intent.putExtras(var2);
//                        param.args[0] = intent;
//
//                    }
//                    catch (Exception e) {
//                        XposedBridge.log(TAG +" error "+e.toString());
//                        return;
//                    }
//
//                }
//            });


            if (("com.bee.yunkong").equals(loadPackageParam.packageName)) {

                Class<?> handlecmdClazz = classLoader.loadClass("com.hwytapp.Common.HandleCmd");
                XposedHelpers.findAndHookMethod(handlecmdClazz, "getXPreadArticle", JSONObject.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            XposedBridge.log(TAG + " getXPreadArticle " +  jbArticle.toString());
                            Object result = param.getResult();
                            JSONObject articleJson = (JSONObject)result;
                            articleList.put(articleJson.get("id").toString(),articleJson);
                        } catch (Exception e) {
                            XposedBridge.log(TAG + e.toString());
                            return;
                        }

                    }
                });
                Class<?> mmClazz = classLoader.loadClass("com.hwytapp.Common.MasterMethod");
                XposedHelpers.findAndHookMethod(mmClazz, "inputReadArticle", JSONObject.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            XposedBridge.log(TAG + " inputReadArticle " +  jbArticle.toString());
                            param.args[0] = jbArticle;

                        } catch (Exception e) {
                            XposedBridge.log(TAG + e.toString());
                            return;
                        }

                    }
                });
            }

            }

    }
}