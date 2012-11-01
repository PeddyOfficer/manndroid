package org.mannsverk.dao.db.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomSqliteOpenHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "mannsverk";
	public static final int DB_VERSION = 1;
	
	public static final String TABLE_EVENT = "event";
	public static final String TABLE_IMG = "image";
	public static final String TABLE_UPDATE = "update";
	
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_NAME = "event_name";
	public static final String EVENT_DATE_TIME = "event_date_time";
	public static final String EVENT_TYPE = "event_type";
	public static final String EVENT_FOOTBALL_ID = "event_football_id";
	public static final String EVENT_POKER_ID = "event_poker_id";
	public static final String EVENT_SIGNED_UP = "event_signed_up";
	
	public static final String IMAGE_USERNAME = "image_username";
	public static final String IMAGE_IMAGE = "image_image";
	
	public static final String UPDATE_TABLE = "table";
	public static final String UPDATE_TIMESTAMP = "timestamp";
	

	public CustomSqliteOpenHelper(Context context, String name,	CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder("CREATE TABLE ");
		sql.append(TABLE_EVENT + " (");
		sql.append(EVENT_ID + " INTEGER PRIMARY KEY NOT NULL, ");
		sql.append(EVENT_NAME + " TEXT, ");
		sql.append(EVENT_TYPE + " TEXT, ");
		sql.append(EVENT_DATE_TIME + " TIMESTAMP, ");
		sql.append(EVENT_FOOTBALL_ID + " INTEGER, ");
		sql.append(EVENT_POKER_ID + " INTEGER);");
		
		db.execSQL(sql.toString());
		sql = null;
		
		sql = new StringBuilder("CREATE TABLE ");
		sql.append(TABLE_IMG + " (");
		sql.append(IMAGE_USERNAME + " TEXT PRIMARY KEY NOT NULL, ");
		sql.append(IMAGE_IMAGE + " BLOB");
		
		db.execSQL(sql.toString());
		sql = null;
		
		sql = new StringBuilder("CREATE TABLE ");
		sql.append(TABLE_UPDATE + " (");
		sql.append(UPDATE_TABLE + " TEXT");
		sql.append(UPDATE_TIMESTAMP + " TIMESTAMP");
		
		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
