package org.mannsverk.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mannsverk.R;
import org.mannsverk.common.exception.ManndroidException;
import org.mannsverk.common.util.Util;
import org.mannsverk.common.vo.User;
import org.mannsverk.dao.rest.RestDao;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

public class EventActivity extends ListActivity{

	private static final String TAG = "EventActivity";
	private List<User> restResponse = null;
	// handler for callback to the UI thread
	final Handler restHandler = new Handler();
	private EventAdapter eventAdapter;
	ProgressDialog dialog;
	boolean signedUp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);	

		startRestCall();
	}

	@Override
	public void onBackPressed() {
		Intent calendar = new Intent(getApplicationContext(), CalendarActivity.class);
		if(Util.isOnline(this)){
			startActivity(calendar);
		} else {
			Toast.makeText(this, R.string.toast_is_online, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		signedUp = isSignedUp();
		boolean active = getIntent().getExtras().getBoolean("eventActive");

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_event, menu);
		if(active){
			if(signedUp){
				menu.setGroupVisible(R.id.menu_group_sign_up, false);
			} else {
				menu.setGroupVisible(R.id.menu_group_sign_off, false);
			}
		} else {
			menu.setGroupVisible(R.id.menu_group_sign_up, false);
			menu.setGroupVisible(R.id.menu_group_sign_off, false);
		}

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
			startActivity(getRefreshedIntent());
			return true;
		case R.id.menu_sign_up:
			signUpOff();
			return true;
		case R.id.menu_sign_off:
			signUpOff();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void signUpOff(){
		Thread thread = new Thread(){
			public void run(){
				RestDao restDao = new RestDao(EventActivity.this);

				String eventType = getIntent().getExtras().getString("footballId") != null ? "footballevent" : "pokerevent";
				boolean ok = false;

				ok = restDao.signUpForEvent(eventType, getIntent().getExtras().getString("footballId") != null ? getIntent().getExtras().getString("footballId") : getIntent().getExtras().getString("pokerId"), signedUp);

				if(ok && Util.isOnline(getApplicationContext())){					
					startActivity(getRefreshedIntent());
				} else {
					Toast.makeText(getApplicationContext(), R.string.toast_is_online, Toast.LENGTH_LONG).show();
				}
			}
		};
		thread.start();
	}

	//this will post back to the UI thread
	final Runnable updateCalendar = new Runnable() {		
		@Override
		public void run() {
			updateUIThread();			
		}
	};

	private void updateUIThread(){		
		eventAdapter = new EventAdapter(this, R.layout.event_row, (ArrayList<User>)restResponse);
		setListAdapter(this.eventAdapter);
		dialog.dismiss();
	}

	protected void startRestCall(){
		dialog = ProgressDialog.show(this, "Jobber...", "Henter data fra mannsverk.org", true, false);
		//Create new thread since this is to expensive to do in the UI thread
		Thread thread = new Thread(){
			public void run(){				
				// get the rest payload
				RestDao restDao = new RestDao(EventActivity.this);
				try {					
					restResponse = restDao.fetchEvent(getIntent().getExtras().getString("eventId"));
				} catch (ManndroidException e) {
					Toast.makeText(EventActivity.this, e.getMsg(), Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
					startActivity(intent);
				} catch (Exception e){
					Toast.makeText(EventActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
					startActivity(intent);
				}
				// call the runnable that should post back to the UI thread
				restHandler.post(updateCalendar);								
			}
		};
		thread.start();
	}

	private class EventAdapter extends ArrayAdapter<User> {

		private ArrayList<User> users;

		public EventAdapter(Context context, int textViewResourceId, ArrayList<User> users) {
			super(context, textViewResourceId, users);
			this.users = users;
			Bundle bundle = getIntent().getExtras();
			boolean active = bundle.getBoolean("eventActive");

			TextView headerText = new TextView(EventActivity.this);
			if(active){				
				headerText.setText("Det er " + users.size() + " påmeldte til ");
				headerText.append(bundle.getString("eventType").toLowerCase() + " " + bundle.getString("eventDate") + " kl " + bundle.getString("eventTime") + ".");
			} else {
				headerText.setText("Denne påmeldingen er stengt. Om aktiviteten er fram i tid vil den bli åpnet senere.");
			}
			headerText.setLines(2);
			headerText.setPadding(1, 5, 1, 10);			
			getListView().addHeaderView(headerText);
		}		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			User user = users.get(position);

			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.event_row, null);				
			}

			if (user != null) {					
				TextView topText = (TextView) view.findViewById(R.id.toptext);
				TextView bottomText = (TextView) view.findViewById(R.id.bottomtext);
				if (topText != null) {
					topText.setText(user.getName());					
				}
				if(bottomText != null){
					bottomText.setText("Påmeldt " + user.getRegistered());
				}
				ImageView image = (ImageView) view.findViewById(R.id.event_imgview);

				if(user.getAvatarUrl() == null){
					image.setImageResource(R.drawable.avatar);
				} else {
					AQuery aquery = new AQuery(view);
					File avatar = aquery.getCachedFile(user.getAvatarUrl());
					
					if(avatar == null){
						Log.i(TAG, "Avatar not cached - fetching and caching");
						aquery.id(image.getId()).image(user.getAvatarUrl(), true, true, 0, 0, myCustomBitmapCallback());
					} else {
						Log.i(TAG, "Avatar cached - fetching from file: " + avatar.getAbsolutePath());
						aquery.id(image.getId()).image(avatar, false, 0, myCustomBitmapCallback());
					}

					//Drawable avatar = aquery.getCachedFile(user.getAvatarUrl());
					//Drawable avatar = Util.ImageOperations(getApplicationContext(), user.getAvatarUrl(), Util.windowSize(getApplicationContext()));
					//					if(avatar == null){
					//						image.setImageResource(R.drawable.avatar);
					//					}
					//					image.setImageDrawable(avatar);
				}				
			}
			return view;
		}
	}
	
	private BitmapAjaxCallback myCustomBitmapCallback(){
		return new BitmapAjaxCallback(){
			@Override
			public void callback(String url, ImageView imageView, Bitmap bitmap, AjaxStatus ajaxStatus){								
				bitmap = Util.resize(bitmap, Util.windowSize(getApplicationContext()));								
				imageView.setImageBitmap(bitmap);
			}
		};
	}

	private boolean isSignedUp(){
		for(User user : restResponse){
			if(user.getName().equalsIgnoreCase(SettingsActivity.getUsername(EventActivity.this))){
				return true;
			}
		}
		return false;
	}

	private Intent getRefreshedIntent(){
		Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
		eventIntent.putExtra("eventId", getIntent().getExtras().getString("eventId"));
		eventIntent.putExtra("eventType", getIntent().getExtras().getString("eventType"));
		eventIntent.putExtra("eventDate", getIntent().getExtras().getString("eventDate"));
		eventIntent.putExtra("eventTime", getIntent().getExtras().getString("eventTime"));
		eventIntent.putExtra("footballId", getIntent().getExtras().getString("footballId"));
		eventIntent.putExtra("pokerId", getIntent().getExtras().getString("pokerId"));
		eventIntent.putExtra("eventActive", getIntent().getExtras().getBoolean("eventActive"));

		return eventIntent;
	}	
}