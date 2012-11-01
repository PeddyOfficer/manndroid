package org.mannsverk.activity;

import org.mannsverk.R;
import org.mannsverk.common.util.Util;
import org.mannsverk.service.UpdateService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.androidquery.util.AQUtility;

/**
 * This class holds the menu
 * and should trigger other
 * activities 
 * @author roger
 *
 */
public class MenuActivity extends Activity implements OnClickListener {
	private static final String TAG = "MenuActivity";
	private PendingIntent pendingIntent;
	private OnSharedPreferenceChangeListener listener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// create listeners for all buttons
		View calendarButton = findViewById(R.id.button_calendar);
		calendarButton.setOnClickListener(this);

		View settingsButton = findViewById(R.id.button_settings);
		settingsButton.setOnClickListener(this);	

		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences(this);

		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				if(key.equalsIgnoreCase("pref_update") ){					
					Util.registerAlarm(getApplicationContext(), prefs.getString(key, "N/A").equalsIgnoreCase("never") ? true : false);
				}				
			}
		};	
		prefs.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_calendar:
			Intent calendarIntent = new Intent(this, CalendarActivity.class);
			if(Util.isOnline(this)){
				startActivity(calendarIntent);
			}else {
				Toast.makeText(MenuActivity.this, R.string.toast_is_online, Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.button_settings:			
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			Toast.makeText(MenuActivity.this, R.string.toast_settings_intent, Toast.LENGTH_SHORT).show();			
			break;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AQUtility.cleanCacheAsync(this, 3000000, 2000000);
	}
}