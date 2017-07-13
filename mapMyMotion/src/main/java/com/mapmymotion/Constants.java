package com.mapmymotion;

import com.mapmymotion.R;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public interface Constants {
	//General
	public static final int STOP = -1;
	public static final int START = 1;
	public static final int PAUSE = 2;
	public static final int NEWACTIVITY = 3;
	public static final int LASTACTIVITY = 4;
	public static final int ACTIVITIES = 6;
	public static final int AUTOSTART = 7;
	public static final int AUTOSTOP = 8;
	public static final int CHARTVIEW = 9;
	public static final int REPLAY = 10;
	public static final int SYNCDATA = 11;
	public static final int UPDATEDATA = 12;
	public static final int DELETEDATA = 13;
	public static final int MAINACTIVITYTYPE = 14;
	public static final int LOCATIONNOTENABLED = 15;
	public static final int WEIGHTTYPE = 16;

	// Use for which polyline to use ,etc
	public static final int ACTIVITYLINE = 0;
	public static final int REPLAYLINE = 1;

	// Preferences
	public static final int ERROR = -1;
	public static final int ABOUT = -2;
	public static final int MESSAGE = -3;
	public static final int RESUME = -5;
	public static final int CHANGEPASS = -6;
	public static final int SETTINGS = -10;
	public static final int INDEX_KM = 0;
	public static final int INDEX_MILES = 1;
	public static final int DEFAULT_SPEED_LIMIT = 80;

	// Conversions from metric to US
	public static final int HOUR_MULTIPLIER = 3600;
	public static final double UNIT_MULTIPLIERS[] = { 0.001, 0.000621371192 };
	public static final double METERSPERSEC_MULTIPLIERS[] = { 1.0, 2.23693629 };
	public static final double UNITS[] = { 1000, 1609.344 };
	public static final double METERSPERSSEC_TOPACE[] = { 16.6666667, 26.8224 };
	public static final double METERSANDFEET[] = { 1.0, 3.28084 };
	public static final int MINIMUMTIMEBETWEENUPDATES = 3000;// 3000
	public static final int MINIMUMPACE = 200;

	// Chart Types
	public static final int LINECHART = 0;
	public static final int BARCHART = 1;
	public static final int TIMESERIESCHART = 2;

	// ActivityTypes
	public static final int[] ACTIVITYTYPES = { R.drawable.runner,
			R.drawable.bicycle, R.drawable.walk_green, R.drawable.wheelchair,
			R.drawable.swimming, R.drawable.skiing, R.drawable.car_36,
			R.drawable.boat_36, R.drawable.snowmobile, R.drawable.robot,
			R.drawable.circle };
	public static final int ACTIVITY_TITLES[] = { R.string.run, R.string.bike,
			R.string.walk, R.string.wheel_chair, R.string.swim, R.string.ski,
			R.string.auto, R.string.boat, R.string.snow_mobile, R.string.robot,
			R.string.other };

	// Speed to Replay
	public static final int[] SPEED_TITLES = { R.string.stop, R.string.x1,
			R.string.x5, R.string.x10, R.string.x100 };

	public static enum SPEED {
		STOP(0), X1(1), X5(5), X10(10), X100(250);

		private int id;

		SPEED(int id) {
			this.id = id;
		}

		public int getReplayValue() {
			return id;
		}

	}
	
	//Email Address Pattern to check for valid email addresses
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	public static final String DATE_YYYYMMDD_REGEX = "^\\d{4}/((0[1-9])|(1[012]))/((0[1-9]|[12]\\d)|3[01])$";

	
	public static final SimpleDateFormat TODATEFORMAT = new SimpleDateFormat("yyyy/MM/dd");
}
