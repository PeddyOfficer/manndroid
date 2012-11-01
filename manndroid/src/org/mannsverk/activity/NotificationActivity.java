package org.mannsverk.activity;

import org.mannsverk.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class NotificationActivity extends Activity {
	private NotificationManager notificationManager;
	private static final int NOTIFY_ID = 1100;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String notificationStatus = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager)getSystemService(notificationStatus);
		final Notification message = new Notification(R.drawable.status_bar, "manndroidmelding", System.currentTimeMillis());
		
		notificationManager.notify(NOTIFY_ID, message);
	}
}
