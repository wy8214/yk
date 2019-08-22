package com.bee.yunkong.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by yuanshenghong on 16/3/16.
 * 操作sp的简化类
 * 默认只用到一个sp文件，既defaultSp
 */
public class SPUtils {
	private static volatile SharedPreferences sp;

	private SPUtils() {
	}

	public static SharedPreferences init(@NonNull Context context) {
		sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		return sp;
	}

	public static void putString(@NonNull SpConstant key, String value) {
		sp.edit().putString(key.getName(), value).apply();
	}

	public static void putInt(@NonNull SpConstant key, int value) {
		sp.edit().putInt(key.getName(), value).apply();
	}

	public static void putBoolean(@NonNull SpConstant key, boolean value) {
		sp.edit().putBoolean(key.getName(), value).apply();
	}

	public static void putFloat(@NonNull SpConstant key, float value) {
		sp.edit().putFloat(key.getName(), value).apply();
	}

	public static void putLong(@NonNull SpConstant key, long value) {
		sp.edit().putLong(key.getName(), value).apply();
	}

	public static String getString(@NonNull SpConstant key, String defaultValue) {
		return sp.getString(key.getName(), defaultValue);
	}

	public static String getString(@NonNull SpConstant key) {
		return sp.getString(key.getName(), "");
	}

	public static int getInt(@NonNull SpConstant key, int defaultValue) {
		return sp.getInt(key.getName(), defaultValue);
	}

	public static int getInt(@NonNull SpConstant key) {
		return sp.getInt(key.getName(), 0);
	}

	public static boolean getBoolean(@NonNull SpConstant key, boolean defaultValue) {
		return sp.getBoolean(key.getName(), defaultValue);
	}

	public static boolean getBoolean(@NonNull SpConstant key) {
		return sp.getBoolean(key.getName(), false);
	}

	public static float getFloat(@NonNull SpConstant key, float defaultValue) {
		return sp.getFloat(key.getName(), defaultValue);
	}

	public static float getFloat(@NonNull SpConstant key) {
		return sp.getFloat(key.getName(), 0F);
	}

	public static long getLong(@NonNull SpConstant key, long defaultValue) {
		return sp.getLong(key.getName(), defaultValue);
	}

	public static long getLong(@NonNull SpConstant key) {
		return sp.getLong(key.getName(), 0L);
	}

}
