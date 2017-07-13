package com.mapmymotion.sql;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.MotionCalculator;
import com.mapmymotion.pojo.ActivityAssignment;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.pojo.Motion;
import com.securitylibrary.SecurityLibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MotionDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MotionSQLHelper dbHelper;
	private String[] allActivityColumns = { MotionSQLHelper.COLUMN_ACTIVITYID,
			MotionSQLHelper.COLUMN_LATITUDE, MotionSQLHelper.COLUMN_LONGITUDE,
			MotionSQLHelper.COLUMN_DISTANCE, MotionSQLHelper.COLUMN_TIME,
			MotionSQLHelper.COLUMN_ALTITUDE };

	public MotionDataSource(Context context) {
		dbHelper = new MotionSQLHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void deleteTable() {// LATER CREATE TRIGGER TO REMOVE ALL ACTIVITIES
								// AND RELATED ITEMS WHEN AN ACTIVITY IS DELETED
		database.delete(MotionSQLHelper.TABLE_ACTIVITY, null, null);
		database.delete(MotionSQLHelper.TABLE_ACTIVITYASSIGNMENT, null, null);
		database.delete(MotionSQLHelper.TABLE_EVENTS, null, null);

	}

	public Events createEvent(Events event) {
		if (!database.isOpen()) {
			this.open();
		}
		// Can only have one weight and not for an activity
		if (event.getEventType() == Constants.WEIGHTTYPE) {
			int rowsDeleted = deleteEvent(event.getActivityId(),
					event.getEventType(),event.getEventTime());
		}

		ContentValues values = new ContentValues();
		values.put(MotionSQLHelper.COLUMN_ACTIVITYID, event.getActivityId());
		values.put(MotionSQLHelper.COLUMN_TYPE, event.getEventType());
		values.put(MotionSQLHelper.COLUMN_SUB_TYPE, event.getEventSubType());
		values.put(MotionSQLHelper.COLUMN_TIME, event.getEventTime());
		values.put(MotionSQLHelper.COLUMN_WEIGHT, event.getWeight());
		values.put(MotionSQLHelper.COLUMN_NOTE, event.getNote());
		long row = database.insert(MotionSQLHelper.TABLE_EVENTS, null, values);
		this.close();
		return event;
	}

	// List of events for an activity such as RUN,BIKE,WALK,etc
	public List<Events> getEventsForActivity(String activityId, int eventType) {
		this.open();

		// Contains a list of all activities
		List<Events> events = new ArrayList<Events>();

		String selectStatement = "select * from activity_events where "
				+ MotionSQLHelper.COLUMN_ACTIVITYID + "=\"" + activityId
				+ "\" and " + MotionSQLHelper.COLUMN_TYPE + "=" + eventType
				+ " order by " + MotionSQLHelper.COLUMN_TIME + " asc";
		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Events event = cursorToEvent(cursor);
			events.add(event);
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		return events;
	}

	// List of ActivityAssignments
	public List<ActivityAssignment> getActivityAssignmentForActivity(String activityId) {
		this.open();

		// Contains a list of all activities
		List<ActivityAssignment> assignments = new ArrayList<ActivityAssignment>();

		String selectStatement = "select * from activity_assignment where "
				+ MotionSQLHelper.COLUMN_ACTIVITYID + "=\'" + activityId +"=\'";
		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ActivityAssignment assignment = cursorToActivityAssignment(cursor);
			assignments.add(assignment);
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		return assignments;
	}
	
	
	// Create a activity assignment entry
	public long createActivityAssignment(ActivityAssignment activity) {
		ContentValues values = new ContentValues();
		values.put(MotionSQLHelper.COLUMN_ACTIVITYID, activity.getActivityId());
		values.put(MotionSQLHelper.COLUMN_MEMBERID, activity.getMemberId());
		values.put(MotionSQLHelper.COLUMN_SYNC_DATE, activity.getSync_date());
		values.put(MotionSQLHelper.COLUMN_ROUTE_TYPE, activity.getRoute_type());

		long rowid =database.insert(MotionSQLHelper.TABLE_ACTIVITYASSIGNMENT, null, values);

		return rowid;
	}

	// Create a activity assignment entry
	public int updateActivityAssignment(ActivityAssignment activity) {
		ContentValues values = new ContentValues();
		String where_filter=MotionSQLHelper.COLUMN_ACTIVITYID +"=\'"+activity.getActivityId()+"\'";
		values.put(MotionSQLHelper.COLUMN_ACTIVITYID, activity.getActivityId());
		values.put(MotionSQLHelper.COLUMN_MEMBERID, activity.getMemberId());
		values.put(MotionSQLHelper.COLUMN_SYNC_DATE, activity.getSync_date());
		values.put(MotionSQLHelper.COLUMN_ROUTE_TYPE, activity.getRoute_type());
		int status = database.update(MotionSQLHelper.TABLE_ACTIVITYASSIGNMENT, values,where_filter,null);
		return status;
	}
	
	public void startTransaction(){
		database.beginTransaction();
	}
	public void endTransaction(){
		database.endTransaction();
	}
	
	// Used to execute individual sql statements
	public void importSQL(String sql) {
		// database.beginTransaction();
		database.execSQL(sql);
		// database.endTransaction();
	}

	// Create an entry for each valid GPS reading
	public Motion createMotion(Motion motion) {
		if (motion.getCurrentTime() < 1) {
			return motion;
		}
		ContentValues values = new ContentValues();
		values.put(MotionSQLHelper.COLUMN_ACTIVITYID, motion.getActivityid());
		values.put(MotionSQLHelper.COLUMN_LATITUDE, motion.getLatitude());
		values.put(MotionSQLHelper.COLUMN_LONGITUDE, motion.getLongitude());
		values.put(MotionSQLHelper.COLUMN_DISTANCE, motion.getCurrentDistance());
		values.put(MotionSQLHelper.COLUMN_TIME, motion.getCurrentTime());
		values.put(MotionSQLHelper.COLUMN_ALTITUDE, motion.getCurrentAltitude());
		this.open();
		database.insert(MotionSQLHelper.TABLE_ACTIVITY, null, values);
		this.close();
		return motion;
	}

	// Delete an Activity
	public int deleteActivity(String activityId) {
		this.open();
		int rowsDeleted = database.delete(MotionSQLHelper.TABLE_ACTIVITY,
				MotionSQLHelper.COLUMN_ACTIVITYID + "=" + "\"" + activityId
						+ "\"", null);
		this.close();
		return rowsDeleted;
	}

	// Delete an Event
	public int deleteEvent(String activityId,int eventType,long eventTime) {
		this.open();
		int rowsDeleted = database.delete(MotionSQLHelper.TABLE_EVENTS,
				MotionSQLHelper.COLUMN_ACTIVITYID + "=" + "\"" + activityId
						+ "\" AND "+ MotionSQLHelper.COLUMN_TYPE+"="+eventType + " AND "+ MotionSQLHelper.COLUMN_TIME+"="+eventTime , null);
		this.close();
		return rowsDeleted;
	}
	
	
	// Delete all Activity
	public int deleteAllActivities() {
		this.open();
		int rowsDeleted = database.delete(MotionSQLHelper.TABLE_ACTIVITY, null,
				null);
		this.close();
		return rowsDeleted;
	}

	// Delete events of a particular type from an activity
	private int deleteEventsForActivity(String activityId, int eventType) {
		int rowsDeleted = database.delete(MotionSQLHelper.TABLE_EVENTS,
				MotionSQLHelper.COLUMN_ACTIVITYID + "=" + "\"" + activityId
						+ "\" and " + MotionSQLHelper.COLUMN_TYPE + "="
						+ eventType, null);
		return rowsDeleted;
	}

	// Get the last activity - used when we want to resume last activity
	public String getLastActivity() {
		String activity;
		String selectStatement = "select activityid from activity  order by epochtime desc LIMIT 1";
		// Open table
		this.open();
		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);
		cursor.moveToFirst();
		activity = cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_ACTIVITYID));
		cursor.close();
		this.close();
		return activity;
	}

	// Get a list of all activities
	public List<Motion> getAllActivites() {
		List<Motion> activities = new ArrayList<Motion>();
		this.open();
		Cursor cursor = database.query(MotionSQLHelper.TABLE_ACTIVITY,
				allActivityColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Motion motion = cursorToMotion(cursor);
			activities.add(motion);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		this.close();
		return activities;
	}

	// Breaks activity information based on the set interval like 1/10
	// mile,.25miles,etc
	public List<Motion> getIntervalsForActivity(String activityId) {
		List<Motion> activities = new ArrayList<Motion>();
		double intervalUnits=Constants.UNITS[AppSettings.getUOM()];
		int intervalCount = 1;
		double totalDistance = 0;
		// Interval to distance conversion i.e. .1 mile to
		double intervalValue = AppSettings.getNotificationDistanceInterval()
				* intervalUnits;// Constants.UNITS[AppSettings.getUOM()]*interval;

		// double intervalValue = kmmi * interval;
		boolean mFirstTime = true;
		String selectStatement = "select * from activity where activityid="
				+ "\"" + activityId + "\" order by epochtime asc";
		// Open table
		this.open();
		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);
//		long initialTime = 0; // Inital Time for Activity
		long lastIntervalTime = 0;
		double lastIntervalDistance = 0;
		double intervalDistance = 0;
		long intervalTime = 0;
		long time = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			// get distance and time from the database
			double distance = cursor.getDouble(cursor
					.getColumnIndex(MotionSQLHelper.COLUMN_DISTANCE));
			time = cursor.getLong(cursor
					.getColumnIndex(MotionSQLHelper.COLUMN_TIME));

			// On first time we need to set initial time values
			if (mFirstTime && time > 0) {
//				initialTime = time;
				lastIntervalTime = time;
				mFirstTime = false;
				continue;
			}

			// Calculate total distance in correct uom as we want to check our
			// interval against this value
			totalDistance += distance;
			// elapsedTime = time - initialTime;
			intervalDistance = totalDistance - lastIntervalDistance;
			// intervals are equidistant and therefore we just need to compare
			// with # total intervals
			if (intervalDistance >= intervalValue) {
				intervalTime = time - lastIntervalTime;
				Motion motion = new Motion();
				motion.setCount(intervalCount);
				motion.setTimeElapsed(intervalTime);
				motion.setCurrentTime(time);
				motion.setCurrentDistance(intervalDistance);
				motion.setActivityid(activityId);
				lastIntervalTime = time;
				lastIntervalDistance = totalDistance;
				intervalDistance = 0;
				activities.add(motion);
				intervalCount++;
			}

			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		this.close();
		// Now to get the last and partial interval
		intervalTime = time - lastIntervalTime;

		Motion motion = new Motion();
		motion.setCount(intervalCount);
		motion.setActivityid(activityId);
		motion.setCurrentTime(intervalTime);
		motion.setCurrentDistance(intervalDistance);
		activities.add(motion);
		return activities;
	}

	// Used to get a list of motions for activity
	public List<Motion> getMotionListForActivity(String activityId) {
		// //This is used to get elapsed time,total distance for an activity
		MotionCalculator motionCalculator = new MotionCalculator();

		this.open();

		// Contains a list of all activities
		List<Motion> activities = new ArrayList<Motion>();

		String selectStatement = "select activity_events.activityid,activity.epochtime,eventtype,eventsubtype,weight,note,latitude,longitude,"
				+ "distance,altitude from activity_events left outer join activity on activity_events.activityid=activity.activityid where eventtype=14 "
				+ "and activity.epochtime>=activity_events.epochtime and activity_events.activityid=\""
				+ activityId
				+ "\""
				+ " group by activity.epochtime order by activity.epochtime";
		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Motion motion = cursorToMotion(cursor);
			Events event = cursorToEvent(cursor);
			// NOW CALCULATE ALL THE OTHER MOTION INFO LIKE
			// TOTALDISTANCE,TOTALTIME BEFORE ADDING MOTION
			motion = motionCalculator.replayCalculations(motion);
			motion.setActivityType(event.getEventSubType());
			activities.add(motion);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		this.close();
		return activities;
	}

	// Make a jsonobject of activity info to send to server
	public JSONObject syncData(String activityId) throws JSONException {
		JSONObject jActivity = new JSONObject();

		jActivity.put("_id", SecurityLibrary.generateUniqueId(24));
		jActivity.put("memberId", AppSettings.getMemberid());
		jActivity.put("activityId", activityId);

		JSONArray jArrayOfEvents = new JSONArray();
		List<Events> eventList = getEventsForActivity(activityId,
				Constants.MAINACTIVITYTYPE);
		for (Events event : eventList) {
			JSONObject jEvent = event.convertEventToJSON();
			jArrayOfEvents.put(jEvent);
		}

		JSONArray jArrayOfMotions = new JSONArray();

		// Get a list of all activity info - same as in replay- bit of overkill
		// for this
		List<Motion> motionList = getMotionListForActivity(activityId);
		for (Motion motion : motionList) {
			if (Double.isInfinite(motion.getAverageSpeed())) {
				motion.setAverageSpeed(0);
			}
			JSONObject jMotion = motion.convertMotionToJSON(true);
			jArrayOfMotions.put(jMotion);
		}
		jActivity.put("motions", jArrayOfMotions);
		jActivity.put("events", jArrayOfEvents);
		return jActivity;
	}

	// public JSONObject syncData(String activityId) throws JSONException {
	// this.open();
	// JSONObject jActivity = new JSONObject();
	// jActivity.put("_id", SecurityLibrary.generateUniqueId(24));
	// jActivity.put("memberId", AppSettings.getMemberid());
	// jActivity.put("activityType", AppSettings.getActivityType());
	// jActivity.put("activityId", activityId);
	//
	// JSONArray jArrayOfMotions = new JSONArray();
	//
	// String selectStatement = "select * from activity where "
	// + MotionSQLHelper.COLUMN_ACTIVITYID + "=\"" + activityId
	// + "\" order by " + MotionSQLHelper.COLUMN_TIME + " asc";
	// // Set the query so it sorts by time
	// Cursor cursor = database.rawQuery(selectStatement, null);
	//
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast()) {
	// JSONObject jMotion = new JSONObject();
	// // jMotion.put(MotionSQLHelper.COLUMN_ACTIVITYID,
	// // cursor.getString(cursor
	// // .getColumnIndex(MotionSQLHelper.COLUMN_ACTIVITYID)));
	// jMotion.put("currentTime", cursor.getLong(cursor
	// .getColumnIndex(MotionSQLHelper.COLUMN_TIME)));
	// jMotion.put("currentAltitude", cursor.getDouble(cursor
	// .getColumnIndex(MotionSQLHelper.COLUMN_ALTITUDE)));
	// jMotion.put("currentDistance", cursor.getDouble(cursor
	// .getColumnIndex(MotionSQLHelper.COLUMN_DISTANCE)));
	// jMotion.put("latitude",
	// cursor.getColumnIndex(MotionSQLHelper.COLUMN_LATITUDE));
	// jMotion.put("longitude",
	// cursor.getColumnIndex(MotionSQLHelper.COLUMN_LONGITUDE));
	// jArrayOfMotions.put(jMotion);
	// cursor.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor.close();
	// this.close();
	// jActivity.put("motionData", jArrayOfMotions);
	// return jActivity;
	// }

	// This gets min max times and other info for the OLD activitysummary table.
	// Not going to need this as table is going to change
	public List<Motion> getActivitiesList() {
		this.open();

		List<Motion> activities = new ArrayList<Motion>();
		// String selectStatement =
		// "SELECT activityid,sum(distance) as totaldistance,min(epochtime) as mintime,max(epochtime) as maxtime,(max(epochtime)-min(epochtime))/1000 as totaltime FROM activity group by activityid order by mintime desc";
		String selectStatement = "SELECT activity.activityid,sum(distance) as totaldistance,min(epochtime) as mintime,max(epochtime) as maxtime,(max(epochtime)-min(epochtime))/1000 as totaltime,(select max(weight) from activity_events where activity_events.activityid=\'"+AppSettings.getMemberid()+"\' and eventtype=16 AND activity.epochtime>=activity_events.epochtime ) as weight,(select note from activity_events where activity_events.activityid=activity.activityid and eventtype=16) as note,route_type FROM activity,activity_assignment where activity_assignment.memberid=\'"+AppSettings.getMemberid()+"\'  and activity_assignment.activityid=activity.activityid group by activity.activityid order by mintime desc";
		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Motion motion = new Motion();
			motion.setActivityid(cursor.getString(cursor
					.getColumnIndex("activityid")));
			motion.setTimeElapsed(cursor.getLong(cursor
					.getColumnIndex("totaltime")));
			motion.setTotalDistance(cursor.getDouble(cursor
					.getColumnIndex("totaldistance")));
			motion.setCurrentTime((cursor.getLong(cursor
					.getColumnIndex("mintime"))));
			motion.setWeight((cursor.getDouble(cursor.getColumnIndex("weight"))));
			motion.setNote((cursor.getString(cursor.getColumnIndex("note"))));

			activities.add(motion);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		this.close();
		return activities;
	}

	// Summarizes various activity related information
	public Motion getTotalsOfActivity(String activityId) {
		// Only will require one set of data as this is a sum for this activity
		Motion sumOfActivity = new Motion();

		String selectStatement = "SELECT sum(distance) as totaldistance,"
				+ "min(epochtime) as mintime,max(epochtime) as maxtime,"
				+ "(max(epochtime)-min(epochtime))/1000 as totaltime "
				+ "FROM activity where activityid=" + "\"" + activityId + "\"";

		// Set the query so it sorts by time
		Cursor cursor = database.rawQuery(selectStatement, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			sumOfActivity.setActivityid(activityId);
			sumOfActivity.setTimeElapsed(cursor.getLong(cursor
					.getColumnIndex("totaltime")));
			sumOfActivity.setTotalDistance(cursor.getDouble(cursor
					.getColumnIndex("totaldistance")));
			sumOfActivity.setCurrentTime((cursor.getLong(cursor
					.getColumnIndex("mintime"))));

			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return sumOfActivity;
	}

	// Takes a cursor and creates and Events pojo
	private Events cursorToEvent(Cursor cursor) {
		Events event = new Events();
		// Need to be sure of order
		event.setActivityId(cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_ACTIVITYID)));
		event.setEventType(cursor.getInt(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_TYPE)));
		event.setEventSubType(cursor.getInt(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_SUB_TYPE)));

		event.setEventTime(cursor.getLong(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_TIME)));
		event.setWeight(cursor.getDouble(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_WEIGHT)));
		event.setNote(cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_NOTE)));
		return event;
	}

	// Takes a cursor and creates and ActivityAssignment pojo
	private ActivityAssignment cursorToActivityAssignment(Cursor cursor) {
		ActivityAssignment assignment = new ActivityAssignment();
		// Need to be sure of order
		assignment.setActivityId(cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_ACTIVITYID)));
		assignment.setMemberId(cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_MEMBERID)));
		assignment.setRoute_type(cursor.getInt(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_ROUTE_TYPE)));
		assignment.setSync_date(cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_SYNC_DATE)));
		return assignment;
	}
	
	// Takes a cursor and creates a Motion pojo
	private Motion cursorToMotion(Cursor cursor) {
		Motion motion = new Motion();
		// Need to be sure of order
		// motion.setId(cursor.getLong(cursor
		// .getColumnIndex(MotionSQLHelper.COLUMN_ID)));
		motion.setActivityid(cursor.getString(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_ACTIVITYID)));
		motion.setLatitude(cursor.getDouble(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_LATITUDE)));
		motion.setLongitude(cursor.getDouble(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_LONGITUDE)));
		motion.setCurrentDistance(cursor.getDouble(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_DISTANCE)));
		motion.setCurrentTime(cursor.getLong(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_TIME)));
		motion.setCurrentAltitude(cursor.getDouble(cursor
				.getColumnIndex(MotionSQLHelper.COLUMN_ALTITUDE)));

		return motion;
	}
}
