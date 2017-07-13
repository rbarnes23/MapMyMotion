package com.mapmymotion.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MotionSQLHelper extends SQLiteOpenHelper {
	public static final String TABLE_ACTIVITY = "activity";
	// public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ACTIVITYID = "activityid";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_ALTITUDE = "altitude";
	public static final String COLUMN_TIME = "epochtime";
	public static final String COLUMN_ACTIVITY_TYPE = "activitytype";
	public static final String COLUMN_MEMBERID = "memberid";
	public static final String COLUMN_TYPE = "eventtype";
	public static final String COLUMN_SUB_TYPE = "eventsubtype";
	public static final String COLUMN_WEIGHT = "weight";
	public static final String COLUMN_NOTE = "note";
	public static final String COLUMN_SYNC_DATE = "sync_date";
	public static final String COLUMN_ROUTE_TYPE = "route_type";

	public static final String TABLE_ACTIVITYASSIGNMENT = "activity_assignment";
	public static final String TABLE_EVENTS = "activity_events";

	private static final String DATABASE_NAME = "mapmymotion.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation activity table
	private static final String DATABASE_ACTIVITY = "create table "
			+ TABLE_ACTIVITY + "(" + COLUMN_ACTIVITYID + " text not null,"
			+ COLUMN_LATITUDE + " real not null," + COLUMN_LONGITUDE
			+ " real not null," + COLUMN_DISTANCE + " real not null,"
			+ COLUMN_TIME + " integer not null," + COLUMN_ALTITUDE
			+ " real not null," + "PRIMARY KEY (" + COLUMN_ACTIVITYID + ","
			+ COLUMN_TIME + "))";

	// Database creation activity summary table
	private static final String DATABASE_ACTIVITY_ASSIGNMENT = "create table "
			+ TABLE_ACTIVITYASSIGNMENT + "(" + COLUMN_ACTIVITYID
			+ " text not null," + COLUMN_MEMBERID + " text not null,"+ COLUMN_SYNC_DATE + " text null ,"+ COLUMN_ROUTE_TYPE + " integer DEFAULT 0,"
			+ "PRIMARY KEY (" + COLUMN_ACTIVITYID + "," + COLUMN_MEMBERID + ")"
			+ ")";

	// Database creation events table
	private static final String DATABASE_EVENTS = "create table "
			+ TABLE_EVENTS + "(" + COLUMN_ACTIVITYID + " text not null,"
			+ COLUMN_TYPE + " integer not null," + COLUMN_SUB_TYPE
			+ " integer not null," + COLUMN_TIME + " integer not null,"
			+ COLUMN_WEIGHT + " real," + COLUMN_NOTE + " text null,"
			+ "PRIMARY KEY (" + COLUMN_ACTIVITYID + "," + COLUMN_TIME + ")"
			+ ")";

	public MotionSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITYASSIGNMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

		db.execSQL(DATABASE_ACTIVITY);
		db.execSQL(DATABASE_ACTIVITY_ASSIGNMENT);
		db.execSQL(DATABASE_EVENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MotionSQLHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		//BACKUP INFO FIRST
		//THEN DROP OLD TABLE
		//CREATE NEW TABLE STRUCTURE
		//IMPORT OLD TABLE DATA INTO NEW STRUCTURE
		//NOT IMPLEMENTED YET
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITYASSIGNMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

		onCreate(db);
	}

}
