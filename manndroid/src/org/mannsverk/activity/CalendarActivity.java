package org.mannsverk.activity;

import java.util.ArrayList;
import java.util.List;

import org.mannsverk.R;
import org.mannsverk.common.enom.ErrorEnum;
import org.mannsverk.common.exception.ManndroidException;
import org.mannsverk.common.util.Util;
import org.mannsverk.common.vo.Event;
import org.mannsverk.common.vo.EventList;
import org.mannsverk.dao.rest.RestDao;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class should display
 * the calendar(s) based on
 * users preferences. The calendar
 * is fetched remotely
 * @author roger
 *
 */

public class CalendarActivity extends ListActivity {
	private static final String TAG = "CalendarActivity";
	private List<Event> restResponse = null;
	// handler for callback to the UI thread
	final Handler restHandler = new Handler();
	private CalendarAdapter calendarAdapter;
	private ListView listView;
	private ManndroidException manndroidException;
	private ProgressDialog dialog;
	private EventList events;
	private boolean isDialogActive = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		Bundle bundle = getIntent().getExtras();		

		if(bundle != null && bundle.getParcelable("events") != null) {
			events = bundle.getParcelable("events");
		}

		if(events != null) {
			restResponse = new ArrayList<Event>();
			restResponse.addAll(events);
			isDialogActive = false;
			NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(1100);
			updateUIThread();
		} else {
			startRestCall();
		}
	}

	@Override
	public void onBackPressed() {
		Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
		startActivity(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_cal, menu);		

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if(!Util.isOnline(this)){
			Toast.makeText(this, R.string.toast_is_online, Toast.LENGTH_LONG).show();
			return false;
		}
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_home:
			Intent home = new Intent(this, MenuActivity.class);
			startActivity(home);
			return true;
		case R.id.menu_refresh:
			Intent refresh = new Intent(this, CalendarActivity.class);
			refresh.setAction("REFRESH");
			startActivity(refresh);
			return true;		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//this will post back to the UI thread
	final Runnable updateCalendar = new Runnable() {		
		@Override
		public void run() {
			updateUIThread();			
		}
	};

	private void updateUIThread(){
		if(restResponse != null){
			calendarAdapter = new CalendarAdapter(this, R.layout.calender_row, (ArrayList<Event>)restResponse);
			setListAdapter(this.calendarAdapter);

			if(isDialogActive) {
				dialog.dismiss();
			}

			listView = getListView();			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {	
					Intent eventIntent = null;
					if(!restResponse.get((int)id).getEventType().equalsIgnoreCase("Avstemning")){
						eventIntent = new Intent(getApplicationContext(), EventActivity.class);
						eventIntent.putExtra("eventId", restResponse.get((int)id).getEventId());
						eventIntent.putExtra("eventType", restResponse.get((int)id).getEventName());
						eventIntent.putExtra("eventDate", restResponse.get((int)id).getEventDate());
						eventIntent.putExtra("eventTime", restResponse.get((int)id).getEventTime());
						eventIntent.putExtra("eventActive", restResponse.get((int)id).isActive());
						eventIntent.putExtra("footballId", restResponse.get((int)id).getFootballEventId());
						eventIntent.putExtra("pokerId", restResponse.get((int)id).getPokerEventId());
					}

					if(Util.isOnline(getApplicationContext())){
						if(eventIntent != null){
							startActivity(eventIntent);
						}else {
							Toast.makeText(getApplicationContext(), R.string.toast_illegal_event, Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), R.string.toast_is_online, Toast.LENGTH_LONG).show();
					}
					//Toast.makeText(getApplicationContext(), "Eventen er " + restResponse.get((int)id).isActive(), Toast.LENGTH_SHORT).show();					
				}
			});	
		}else {
			dialog.dismiss();
			Intent intent = null;
			if(manndroidException.getErrorEnum().equals(ErrorEnum.AUTENTICATION_FAILURE)){
				intent = new Intent(getApplicationContext(), SettingsActivity.class);
				Toast.makeText(getApplicationContext(), manndroidException.getMsg(), Toast.LENGTH_LONG).show();
			}
			if(manndroidException.getErrorEnum().equals(ErrorEnum.XML_PARSE_FAILURE)){
				intent = new Intent(getApplicationContext(), MenuActivity.class);
				Toast.makeText(getApplicationContext(), manndroidException.getMsg(), Toast.LENGTH_LONG).show();
			}
			startActivity(intent);
		}

	}

	protected void startRestCall(){
		dialog = ProgressDialog.show(this, "Jobber...", "Henter data fra mannsverk.org", true, false);
		//Create new thread since this is to expensive to do in the UI thread
		Thread thread = new Thread(){
			public void run(){
				// get the rest payload
				RestDao restDao = new RestDao(CalendarActivity.this);

				try {
					String forceUpdate = getIntent().getAction() != null ? getIntent().getAction() : "";

					Log.i(TAG, "Action: " + forceUpdate);
					restResponse = restDao.fetchCalendar(forceUpdate);
				} catch (ManndroidException e) {
					e.printStackTrace();
					restResponse = null;
					manndroidException = e;
				} 

				// call the runnable that should post back to the UI thread
				restHandler.post(updateCalendar);								
			}
		};
		thread.start();		
	}

	private class CalendarAdapter extends ArrayAdapter<Event> {

		private ArrayList<Event> events;

		public CalendarAdapter(Context context, int textViewResourceId, ArrayList<Event> events) {
			super(context, textViewResourceId, events);
			this.events = events;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			Event event = events.get(position);

			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.calender_row, null);				
			}

			if (event != null) {

				TextView topText = (TextView) view.findViewById(R.id.toptext);
				TextView bottomText = (TextView) view.findViewById(R.id.bottomtext);
				ImageView image = (ImageView) view.findViewById(R.id.calendar_imgview);

				if(event.isChanged()) {
					view.setBackgroundColor(Color.GRAY);
				}

				if (topText != null) {					
					topText.setText(event.getEventName());

				}
				if(bottomText != null){					
					bottomText.setText(event.getEventDate() + " " + event.getEventTime());
				}

				image.setImageResource(event.getEventType().equalsIgnoreCase("Poker") ? R.drawable.poker : R.drawable.football);				
			}
			return view;
		}
	}
}
