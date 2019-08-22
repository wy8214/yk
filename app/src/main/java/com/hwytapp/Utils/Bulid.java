package com.hwytapp.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Bulid {

    //当一个版本属性不知道时所设定的值。
    public static final String UNKNOWN = "unknown";
    //修订版本列表码
    public static final String Device_ID = getString("ro.build.id");
    //显示屏参数
    public static final String DISPLAY = getString("ro.build.display.id");
    //整个产品的名称
    public static final String PRODUCT = getString("ro.product.name");
    //设备参数
    public static final String DEVICE = getString("ro.product.device");
    //主板
    public static final String BOARD = getString("ro.product.board");
    //cpu指令集
    public static final String CPU_ABI = getString("ro.product.cpu.abi");
    //cpu指令集2
    public static final String CPU_ABI2 = getString("ro.product.cpu.abi2");
    //硬件制造商
    public static final String MANUFACTURER = getString("ro.product.manufacturer");
    //系统定制商
    public static final String BRAND = getString("ro.product.brand");
    //版本即最终用户可见的名称
    public static final String MODEL = getString("ro.product.model");
    //系统启动程序版本号
    public static final String BOOTLOADER = getString("ro.bootloader");
    //硬件名称
    public static final String HARDWARE = getString("ro.hardware");
    //硬件序列号
    public static final String SERIAL = getString("ro.serialno");
    //build的类型
    public static final String TYPE = getString("ro.build.type");
    //描述build的标签,如未签名，debug等等。
    public static final String TAGS = getString("ro.build.tags");
    //唯一识别码
    public static final String FINGERPRINT = getString("ro.build.fingerprint");

    public static final String TIME = getLong("ro.build.date.utc") ;
    public static final String USER = getString("ro.build.user");
    public static final String HOST = getString("ro.build.host");

    public static final String INCREMENTAL = getString("ro.build.version.incremental");
    public static final String RELEASE = getString("ro.build.version.release");
    //        public static final int SDK_INT = SystemProperties.getInt( "ro.build.version.sdk", 0);
    public static final String CODENAME = getString("ro.build.version.codename");
    //获取无线电固件版本
//    public static String getRadioVersion(Context context) {
//
////        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
////
////        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
////
////        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
////        try{
////            String imei = mTm.getImei();
////
////        }catch (Exception e)
////        {
////
////        }
//        return SystemProperties.get(TelephonyProperties.PROPERTY_BASEBAND_VERSION, null);
//    }



    private static String getString(String property) {


        String result="";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);
            result=(String)get.invoke(c, property);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

//        return SystemProperties.get(property, UNKNOWN);
    }

    private static String getLong(String property) {
        String result="";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);

            result=(String)get.invoke(c, property);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }

    public HashMap bulidInfo()
    {
        HashMap<String,String> bulidbject = new HashMap<String, String>() ;
        try {
            bulidbject.put("Device_ID", Device_ID);
            bulidbject.put("DISPLAY", DISPLAY);
            bulidbject.put("PRODUCT", PRODUCT);
            bulidbject.put("DEVICE", DEVICE);
            bulidbject.put("BOARD", BOARD);
            bulidbject.put("CPU_ABI", CPU_ABI);
            bulidbject.put("CPU_ABI2", CPU_ABI2);
            bulidbject.put("MANUFACTURER", MANUFACTURER);
            bulidbject.put("BRAND", BRAND);
            bulidbject.put("MODEL", MODEL);
            bulidbject.put("BOOTLOADER", BOOTLOADER);
            bulidbject.put("HARDWARE", HARDWARE);
            bulidbject.put("SERIAL", SERIAL);
            bulidbject.put("TYPE", TYPE);
            bulidbject.put("TAGS", TAGS);
            bulidbject.put("FINGERPRINT", FINGERPRINT);
            bulidbject.put("USER", USER);
            bulidbject.put("HOST", HOST);
            bulidbject.put("INCREMENTAL", INCREMENTAL);
            bulidbject.put("RELEASE", RELEASE);
            bulidbject.put("CODENAME", CODENAME);
            bulidbject.put("TIME", TIME);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bulidbject;
    }

    public  int getBatteryCap(Context context){

        BatteryManager batteryManager = (BatteryManager)context.getSystemService(context.BATTERY_SERVICE);
        int batteryCap = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return batteryCap;
    }



    //目前已知的版本代码的枚举类
    public static class VERSION_CODES {
        public static final int CUR_DEVELOPMENT = 10000;

        /** * October 2008: The original, first, version of Android. Yay! */
        public static final int BASE = 1;

        /** * February 2009: First Android update, officially called 1.1. */
        public static final int BASE_1_1 = 2;

        /** * May 2009: Android 1.5. */
        public static final int CUPCAKE = 3;

        /** * September 2009: Android 1.6. */
        public static final int DONUT = 4;

        /** * November 2009: Android 2.0 */
        public static final int ECLAIR = 5;

        /** * December 2009: Android 2.0.1 */
        public static final int ECLAIR_0_1 = 6;

        /** * January 2010: Android 2.1 */
        public static final int ECLAIR_MR1 = 7;

        /** * June 2010: Android 2.2 */
        public static final int FROYO = 8;

        /** * November 2010: Android 2.3 */
        public static final int GINGERBREAD = 9;

        /** * February 2011: Android 2.3.3. */
        public static final int GINGERBREAD_MR1 = 10;

        /** * February 2011: Android 3.0. */
        public static final int HONEYCOMB = 11;

        /** * May 2011: Android 3.1. */
        public static final int HONEYCOMB_MR1 = 12;

        /** * June 2011: Android 3.2. */
        public static final int HONEYCOMB_MR2 = 13;

        /** * October 2011: Android 4.0. */
        public static final int ICE_CREAM_SANDWICH = 14;

        /** * December 2011: Android 4.0.3. */
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;

        /** * June 2012: Android 4.1. */
        public static final int JELLY_BEAN = 16;

        /** * Android 4.2: Moar jelly beans! */
        public static final int JELLY_BEAN_MR1 = 17;

        /** * Android 4.3: Jelly Bean MR2, the revenge of the beans. */
        public static final int JELLY_BEAN_MR2 = 18;

        /** * Android 4.4: KitKat, another tasty treat. */
        public static final int KITKAT = 19;
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

}
