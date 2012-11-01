package org.mannsverk.activity;

import org.mannsverk.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {
	//settings
	private static final String SETTING_USERNAME = "username";
	private static final String SETTING_USERNAME_DEF = "NA";	
	private static final String SETTING_PASSWORD = "password";
	private static final String SETTING_PASSWORD_DEF = "NA";
	private static final String SETTING_CALENDARS = "pref_calendar";
	private static final String SETTING_CALENDAR_DEF = "all";
	private static final String SETTING_CACHE = "pref_cache";
	private static final String SETTING_CACHE_DEF = "never";
	private static final String SETTING_UPDATE = "pref_update";
	private static final String SETTING_UPDATE_DEF = "never";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);		
	}	
	
	@Override
	public void onBackPressed() {
		Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
		startActivity(menu);
	}
	
	public static String getUsername(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTING_USERNAME, SETTING_USERNAME_DEF).trim();
	}
	
	public static String getPassword(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTING_PASSWORD, SETTING_PASSWORD_DEF).trim();
	}
	
	public static String getCalendars(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTING_CALENDARS, SETTING_CALENDAR_DEF).trim();
	}
	
	public static String getCacheTime(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTING_CACHE, SETTING_CACHE_DEF).trim();
	}
	
	public static String getUpdateInterval(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTING_UPDATE, SETTING_UPDATE_DEF).trim();
	}
}
