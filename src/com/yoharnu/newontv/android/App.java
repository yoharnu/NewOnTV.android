package com.yoharnu.newontv.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application{

	public static final String MIRRORPATH = "http://thetvdb.com";
	public static final String LANGUAGE = "en";
	public static final String API_KEY = "768A3A72ACDABC4A";
	protected static Context context;
	public static SharedPreferences preferences;
	
	public void onCreate(){
		super.onCreate();
		
		context = getApplicationContext();
		preferences = context.getSharedPreferences(getString(R.string.pref_default), Context.MODE_PRIVATE);
	}
	
	public static Context getContext(){
		return context;
	}
	
}
