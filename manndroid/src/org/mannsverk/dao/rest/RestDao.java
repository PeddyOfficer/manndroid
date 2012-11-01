package org.mannsverk.dao.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.mannsverk.activity.SettingsActivity;
import org.mannsverk.common.enom.CalendarEnum;
import org.mannsverk.common.exception.ManndroidException;
import org.mannsverk.common.util.FileUtil;
import org.mannsverk.common.util.Util;
import org.mannsverk.common.util.XMLParser;
import org.mannsverk.common.vo.Event;
import org.mannsverk.common.vo.User;

import android.content.Context;
import android.util.Log;

public class RestDao {
	private static final String TAG = "RestDao";
	private Context context;
	private static final String BASE_URL = "http://api.mannsverk.org/rest/calendar/calendar.php/";

	public RestDao(Context context){
		this.context = context;
	}

	/**
	 * This method should fetch the calendar
	 * from mannsverk.org. Return object must
	 * be changed and also the signature
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<Event> fetchCalendar(String action) throws ManndroidException{
		List<Event> events = null;

		File cache = null;
		
		try {
			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

			if(!Util.useCache(context, CalendarEnum.EVENTS) || action.equalsIgnoreCase("REFRESH") || action.equalsIgnoreCase("SERVICE_UPDATE")) {
				Log.i(TAG, "Not using cache");				
				cache = onlineConnect(null, action.equalsIgnoreCase("SERVICE_UPDATE") ? CalendarEnum.SERVICE_UPDATE : CalendarEnum.EVENTS);
			}else {
				Log.i(TAG, "Using cache");
				cache = FileUtil.existingCache(context, CalendarEnum.EVENTS);
			}

			// parse
			XMLParser parser = new XMLParser(context);
			try {				
				if(cache != null) {
					Log.i(TAG, "Got file - send to parser");
					events = (List<Event>)parser.parse(cache, CalendarEnum.EVENTS);
				} else {
					Log.i(TAG, "File is null");
					events = (List<Event>)parser.parse(new File(FileUtil.getCachePath(context), cache.getName()), CalendarEnum.EVENTS);
				}
			} catch (ManndroidException e) {
				Log.i(TAG, "Throwing");
				throw e;
			}

			Log.i(TAG, events.toString());			

			// if we need the xml as string
			//BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			//String payload = reader.readLine();
			//reader.close();

			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

		}catch(IOException e){
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d(TAG, "Got: " + events);
		return events;
	}
	
	private File onlineConnect(String eventId, CalendarEnum type) {
		HttpURLConnection connection = null;
		InputStream input = null;
		File response = null;

		try {
			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}
			
			eventId = eventId != null && eventId != "" ? eventId : "";
			
			// build RESTful query
			URL url = new URL(BASE_URL + eventId + "?u=" + SettingsActivity.getUsername(context) + "&p=" + SettingsActivity.getPassword(context));
			Log.i(TAG, "URL is " + url.toString());

			connection = (HttpURLConnection)url.openConnection();
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			// start
			connection.connect();

			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

			// read results
			input = connection.getInputStream();
			
			// save to file
			response = FileUtil.cacheXML(input, context, type);

			if(Thread.interrupted()){
				throw new InterruptedException();
			}			
		}catch(IOException e){
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(connection != null){
				connection.disconnect();
			}
		}
		return response;
	}

	public List<User> fetchEvent(String eventId) throws ManndroidException{
		List<User> users = null;
		File cache = null;

		try {
			cache = onlineConnect(eventId, CalendarEnum.EVENT);

			// parse
			XMLParser parser = new XMLParser(context);

			users = (List<User>) parser.parse(cache, CalendarEnum.EVENT);

			Log.i(TAG, users.toString());			

			// if we need the xml as string
			//BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			//String payload = reader.readLine();
			//reader.close();

			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

		}catch(IOException e){
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "Got: " + users);
		return users;
	}

	public boolean signUpForEvent(String eventType, String eventId, boolean signUp){
		boolean response = true;
		HttpURLConnection connection = null;

		try {
			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

			// build RESTful query
			URL url = new URL(BASE_URL + eventId + "/" + eventType + "?u=" + SettingsActivity.getUsername(context) + "&p=" + SettingsActivity.getPassword(context));
			Log.i(TAG, "URL is " + url.toString());

			connection = (HttpURLConnection)url.openConnection();
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod(signUp ? "DELETE" : "POST");
			connection.setDoInput(true);

			// start
			connection.connect();

			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

			connection.getInputStream();

			// parse
			//XMLParser parser = new XMLParser();
			//users = (List<User>)parser.parse(input, CalendarEnum.EVENT);

			//Log.i(TAG, users.toString());			

			// if we need the xml as string
			//BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			//String payload = reader.readLine();
			//reader.close();

			// check if task has been interrupted
			if(Thread.interrupted()){
				throw new InterruptedException();
			}

		}catch(IOException e){
			e.printStackTrace();
			response = false;
		}catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
			response = false;
		}finally {
			if(connection != null){
				connection.disconnect();
			}
		}
		Log.d(TAG, "Got: " + response);
		return response;
	}
}
