package com.monyrama.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PrefsManager {
	
	private static String PREFS_NAME = "com.monyrama.prefs";
	
	public static void saveBoolean(Context context, PrefKeys key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(keyToString(key), value);
		editor.commit();
	}
	
	public static boolean getBoolean(Context context, PrefKeys key, boolean def) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(keyToString(key), def);
	}	
	
	public static void saveInt(Context context, PrefKeys key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(keyToString(key), value);
		editor.commit();
	}
	
	public static int getInt(Context context, PrefKeys key, int def) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(keyToString(key), def);
	}

	public static void saveLong(Context context, PrefKeys key, long value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putLong(keyToString(key), value);
		editor.commit();
	}

	public static long getLong(Context context, PrefKeys key, int def) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(keyToString(key), def);
	}

	private static String keyToString(PrefKeys key) {
		return key.toString();
	}

}