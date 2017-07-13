package com.mapmymotion.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class Events {
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public int getEventSubType() {
		return eventSubType;
	}

	public void setEventSubType(int eventSubType) {
		this.eventSubType = eventSubType;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	// This is much simpler than Parcelable
	public JSONObject convertEventToJSON() throws JSONException {
		JSONObject jEvent = new JSONObject();
		jEvent.put("activityid", activityId);
		jEvent.put("eventtype", eventType);
		jEvent.put("eventsubtype", eventSubType);
		jEvent.put("eventtime", eventTime);
		jEvent.put("weight", weight);
		jEvent.put("note", note);
		return jEvent;
	}

	String activityId;
	int eventType;
	int eventSubType;
	long eventTime;
	double weight;
	String note;

	// //Event Types Supported
	// public static final int STOP = -1;
	// public static final int START = 1;
	// public static final int NEWACTIVITY = 3;
	// public static final int LASTACTIVITY = 4;
	// public static final int ACTIVITY_TYPE = 5;
	//
	// //Event Sub Types
	// public static final int NOSUBTYPE = 0;
	// public static final int AUTOSTART = 7;
	// public static final int AUTOSTOP = 8;
	//
	// // Activity Sub Types
	// public static final int RUN = 0;
	// public static final int BIKE = 1;
	// public static final int WALK = 2;
	// public static final int WHEELCHAIR = 3;
	// public static final int SWIM = 4;
	// public static final int SKI = 5;
	// public static final int ROBOT = 6;
	// public static final int OTHER = 7;

}
