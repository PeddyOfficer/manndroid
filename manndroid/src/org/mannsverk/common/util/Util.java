package org.mannsverk.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mannsverk.activity.MenuActivity;
import org.mannsverk.activity.SettingsActivity;
import org.mannsverk.common.enom.CalendarEnum;
import org.mannsverk.service.UpdateService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Util {
	private final static String TAG = "Util";
	public static final String AVATAR_FILE = "http://mannsverk.org/forum/venneforum/download/file.php?avatar=";
	public static final String AVATAR_GALLERY = "http://mannsverk.org/forum/venneforum/images/avatars/gallery/";

	public enum Screen {LOW, MEDIUM, HIGH}

	public static Drawable ImageOperations(Context ctx, String url, int screenSize) {
		try {
			InputStream is = (InputStream) fetch(url);
			//Drawable d = Drawable.createFromStream(is, "src");

			return resize(is, screenSize);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int windowSize(Context context){
		WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay(); 
		int width = d.getWidth(); 
		int height = d.getHeight();

		if(width <= 240)return 24;
		else if(width <= 320)return 32;
		else return 48;
	}

	public static Bitmap resize(Bitmap bitmap, int screenSize) {
		if(bitmap != null){ // check because bitmap is null for some images on Desire
			//			int width = bitmap.getWidth();
			//			int height = bitmap.getHeight();
			//			int newWidth = screenSize;
			//			int newHeight = screenSize;
			//
			//			float scaleWidth = ((float) newWidth) / width;
			//			float scaleHeight = ((float) newHeight) / height;
			//
			//			Matrix matrix = new Matrix();
			//			matrix.postScale(scaleWidth, scaleHeight);

			// create the new Bitmap object
			//Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,	height, matrix, true);
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, screenSize, screenSize, false);
			//BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

			return resizedBitmap;
		}else {
			return null;
		}
	}

	private static BitmapDrawable resize(InputStream is, int screenSize){
		Bitmap bitmap = BitmapFactory.decodeStream(is);//decodeResource(getResources(), R.drawable.icon);		
		Log.i(TAG, "New size is " + screenSize);

		if(bitmap != null){ // check because bitmap is null for some images on Desire
			//			int width = bitmap.getWidth();
			//			int height = bitmap.getHeight();
			//			int newWidth = screenSize;
			//			int newHeight = screenSize;
			//
			//			float scaleWidth = ((float) newWidth) / width;
			//			float scaleHeight = ((float) newHeight) / height;
			//
			//			Matrix matrix = new Matrix();
			//			matrix.postScale(scaleWidth, scaleHeight);

			// create the new Bitmap object
			//Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,	height, matrix, true);
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, screenSize, screenSize, false);
			BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

			return bmd;
		}else {
			return null;
		}
	}

	private static Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);		
		Object content = url.getContent();

		return content;
	}

	public static String timeStamp2date(long timeStamp){		
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeStamp);
		Date date = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		String sDate = format.format(date);

		return sDate;		
	}

	public static String timeStamp2Time(long timeStamp){
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeStamp);
		Date date = cal.getTime();
		int hour = date.getHours();
		int minutes = date.getMinutes();

		return hour + ":" + minutes;
	}

	/**
	 * Takes a String and returns a long that can be used as TimeStamp
	 * @param dateTime must be (dd.MM.yyyy HH:mm)
	 * @return dateTime as long
	 */
	public static long dateTime2Timestamp(String dateTime){
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date date = null;
		final Calendar cal = Calendar.getInstance();

		try {
			date = format.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}				

		cal.setTimeInMillis(date.getTime());

		return cal.getTime().getTime();
	}

	/**
	 * Checking whether the phone has WiFi or 3G connection activated
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false; 
	}	

	public static boolean useCache(Context context, CalendarEnum type) {
		String pref = SettingsActivity.getCacheTime(context);
		long hour = 3600000;
		long now = System.currentTimeMillis();
		long cache;

		if(FileUtil.existingCache(context, type) != null){			
			cache = Long.valueOf(FileUtil.existingCache(context, type).getName().substring(0, 13));

			Log.i(TAG, "Cache exist, timestamp is " + cache);
		} else return false;

		if(pref.equalsIgnoreCase("never")){
			Log.i(TAG, "Pref is never");
			return false;
		} else if(pref.equalsIgnoreCase("1hr")){
			Log.i(TAG, "Pref is 1hr");			
			if(cache > (now - hour)  ) return true;
			return false;
		} else if(pref.equalsIgnoreCase("3hr")){
			Log.i(TAG, "Pref is 3hr");
			if(cache > (now - hour*3)  ) return true;
			return false;
		}else if(pref.equalsIgnoreCase("12hr")){
			Log.i(TAG, "Pref is 12hr");
			if(cache > (now - hour*12)  ) return true;
			return false;
		}else if(pref.equalsIgnoreCase("24hr")){
			Log.i(TAG, "Pref is 24hr");
			if(cache > (now - hour*24)  ) return true;
			return false;
		}else return false;

	}

	public static void registerAlarm(Context context, boolean cancel) {
		Log.i(TAG, cancel ? "Deregistering AlarmManager" : "Registering AlarmManager");

		Intent service = new Intent(context, UpdateService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);

		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		long interval = UpdateService.interval(context);

		if(cancel) {
			alarmManager.cancel(pendingIntent);
			return;
		}

		if(interval > 0) {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
		}
	}
}
