package org.mannsverk.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import org.mannsverk.R;
import org.mannsverk.activity.CalendarActivity;
import org.mannsverk.activity.SettingsActivity;
import org.mannsverk.common.enom.CalendarEnum;
import org.mannsverk.common.exception.ManndroidException;
import org.mannsverk.common.util.FileUtil;
import org.mannsverk.common.util.Util;
import org.mannsverk.common.util.XMLParser;
import org.mannsverk.common.vo.Event;
import org.mannsverk.common.vo.EventList;
import org.mannsverk.dao.rest.RestDao;
import org.xml.sax.SAXException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends IntentService {
	private Timer timer = new Timer();
	private static final String TAG = "UpdateService";

	public UpdateService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public UpdateService() {
		super("UpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Starting intent service");
		updateService();		
	}	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		Log.i(TAG, "Starting service");
		updateService();
		stopSelf();
		 */
	}

	private void updateService() {
		//timer.scheduleAtFixedRate(new TimerTask() {			

		//	@Override
		//	public void run() {
		Thread thread = new Thread() {
			public void run() {
				createNotification();
			}
		};
		thread.start();

		//	}
		//}, interval(), interval());
		//;
	}

	public static long interval(Context context) {
		String pref = SettingsActivity.getUpdateInterval(context);
		long hour = 3600000;

		if(pref.equalsIgnoreCase("1hr")) {
			return hour;
		} else if(pref.equalsIgnoreCase("3hr")) {
			return hour * 3;
		} else if(pref.equalsIgnoreCase("12hr")) {
			return hour * 12;
		} else if(pref.equalsIgnoreCase("24hr")) {
			return hour * 24;
		}
		return Long.MIN_VALUE;
	}

	private void createNotification() {
		Context context = getApplicationContext();
		List<Event> eventList = null;

		if (Util.isOnline(context)) {
			eventList = fetchOnline(context);
		}

		if (eventList != null) {
			FileUtil.updateCache(context);

			CharSequence contentText = "";
			CharSequence ticker = "";
			Bundle bundle = new Bundle();
			EventList events = new EventList();
			for (Event event : eventList) {
				events.add(event);
				if (event.isChanged()) {
					contentText = event.getEventName() + " er lagt til/endret";
					ticker = contentText;
				}
			}

			NotificationManager notificationManager;
			final int NOTIFY_ID = 1100;

			String notificationStatus = Context.NOTIFICATION_SERVICE;
			notificationManager = (NotificationManager) getSystemService(notificationStatus);
			final Notification notification = new Notification(
					R.drawable.status_bar, ticker, System.currentTimeMillis());

			CharSequence contentTitle = getText(R.string.notification_header);

			Intent notificationIntent = new Intent(this, CalendarActivity.class);

			bundle.putParcelable("events", events);
			notificationIntent.putExtras(bundle);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.defaults |= Notification.FLAG_AUTO_CANCEL;

			notificationManager.notify(NOTIFY_ID, notification);
		}
	}

	private List<Event> fetchOnline(Context context) {
		// restdao for å hente online fil og få liste med events i retur
		// deretter hente cached fil, sende til xmlparser og få events i retur
		// sammenlikne listene. er de ulik, lag notification.

		RestDao dao = new RestDao(context);
		XMLParser parser = new XMLParser(context);

		try {
			File cache = FileUtil.existingCache(context, CalendarEnum.EVENTS);
			List<Event> onlineList = dao.fetchCalendar("SERVICE_UPDATE");

			List<Event> different = null;
			List<Event> cachedList = null;

			if(cache != null) {
				cachedList = (List<Event>) parser.parse(cache, CalendarEnum.EVENTS);
				different = new ArrayList<Event>(cachedList);
				different.addAll(onlineList);
				different.removeAll(cachedList);
			} else {
				different = new ArrayList<Event>(onlineList);
			}

			Log.i(TAG, "New events: " + different);

			if (different.size() > 0) {
				for (Event event : different) {
					for (Event online : onlineList) {
						if (event.getEventId() == online.getEventId()) {
							Log.i(TAG,
									"Setting changed on event id "
									+ online.getEventId());
							online.setChanged(true);
						}
					}
				}
				return onlineList;
			}
		} catch (ManndroidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
