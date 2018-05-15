package com.monyrama.log;

import android.util.Log;

public class MyLog {
	private final static String LOG_TAG = "pbvp";
	
	public static void info(String msg) {
		Log.i(LOG_TAG, msg);
	}
	
	public static void warning(String msg) {
		Log.w(LOG_TAG, msg);
	}	
	
	public static void error(String msg) {
		Log.e(LOG_TAG, msg);
	}
	
	public static void debug(String msg) {
		Log.d(LOG_TAG, msg);
	}
	
	public static void info(String msg, Throwable t) {
		Log.i(LOG_TAG, msg, t);
	}
	
	public static void warning(String msg, Throwable t) {
		Log.w(LOG_TAG, msg, t);
	}	
	
	public static void error(String msg, Throwable t) {
		Log.e(LOG_TAG, msg, t);
	}
	
	public static void debug(String msg, Throwable t) {
		Log.d(LOG_TAG, msg, t);
	}	
}
