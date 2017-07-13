package com.mapmymotion.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
//import org.json.JSONObject;

import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.MainActivity;
import com.mapmymotion.MotionCalculator;
import com.mapmymotion.R;
import com.mapmymotion.pojo.ActivityAssignment;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.pojo.Motion;
import com.mapmymotion.sql.MotionDataSource;
import com.mapmymotion.utilities.Utils;
import com.securitylibrary.SecurityLibrary;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class LocationIntentService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";
	LocationManager mLocationManager;

	private MotionDataSource mds;
	private MotionCalculator mMotionCalculator;
	// private JSONObject jActivity;
	public static Handler mServiceHandler;
	private int mOnOffStatus = Constants.STOP;
	private int mPause = Constants.RESUME;
	private int mActivityType;
	private ExecutorService executor = Executors.newFixedThreadPool(5);
	private int mLastActivityType = Constants.SETTINGS;
	private boolean mFirst = true;

	public LocationIntentService() {
		super("LocationIntentService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mds = new MotionDataSource(this);
		mds.open();
		// Create the handler to update user interface from the service
		mServiceHandler = new Handler() // Receive messages from service class
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {

				case Constants.START:
					mOnOffStatus = Constants.START;
					break;
				case Constants.STOP:
					mOnOffStatus = Constants.STOP;
					break;
				case Constants.NEWACTIVITY:
					mActivityType = msg.what;
					mOnOffStatus = Constants.STOP;
					break;
				case Constants.LASTACTIVITY:
					mActivityType = msg.what;
					mOnOffStatus = Constants.STOP;
					break;

				case Constants.PAUSE: // If we pause then we need to indicate so
					mOnOffStatus = Constants.STOP;
					mPause = Constants.PAUSE;
					break;

				}
			}
		};

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (enabled) {
			// mMotionCalculator = new MotionCalculator();
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					Constants.MINIMUMTIMEBETWEENUPDATES, 0,
					new GeoUpdateHandler());

		} else {
			Utils.setToast(getBaseContext(),
					getString(R.string.locationnotenabled), Toast.LENGTH_LONG);
//			sendStatusMessage(Constants.LOCATIONNOTENABLED);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		// executor.shutdown();
		mds.close();
		mds = null;
		mMotionCalculator = null;

	}

	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			if (mOnOffStatus == Constants.STOP && mPause == Constants.RESUME) {
				// Checking to see if autostart is expensive so only want to do
				// it if we are stopped
				if ((Utils.roundDecimal(
						Utils.convertSpeed(location.getSpeed()), 2) >= AppSettings
						.getAutoStartInterval())
						&& AppSettings.getAutoStartInterval() != 0) {
					// Send a message to autostart the onoff button
					mOnOffStatus = Constants.AUTOSTART;
					sendStatusMessage(mOnOffStatus);
				}
			} // only populate database and update view if we are started
			else if (location.getLatitude() > 0
					&& mOnOffStatus == Constants.START) {
				if (mMotionCalculator == null) {
					mMotionCalculator = new MotionCalculator();

					if (mds == null) {
						mds = new MotionDataSource(getBaseContext());
						if (mActivityType == Constants.LASTACTIVITY) {
							mMotionCalculator.setActivityId(mds
									.getLastActivity());
						} else {
							mMotionCalculator.setActivityId(SecurityLibrary
									.generateUniqueId(10));
						}

						mds.open();
						// This sets the activity id to use as either a new one
						// or the last one used
					}
				}
				new PopulateTelematics().executeOnExecutor(executor, location);
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			if (!provider.contentEquals("gps")) {
				Utils.setToast(getBaseContext(), getString(R.string.info),
						Toast.LENGTH_SHORT);
				sendStatusMessage(Constants.LOCATIONNOTENABLED);

			}
			;

		}
	}

	// Does necessary calculation to update the motion pojo, the database
	// and send data to the UI
	private class PopulateTelematics extends AsyncTask<Location, Void, Void> {
		// @Override
		// protected void onPostExecute(Motion motion) {
		// super.onPostExecute(motion);
		// }

		@Override
		protected Void doInBackground(Location... params) {
			Location location = params[0];
			// The mMotionCalculator keeps a running sum of calculations
			Motion motion = mMotionCalculator.updateCalculations(location);
			// Update data
			if (motion.getCurrentTime() > 0) {
				// The first time thru we need to create an assignment to
				// memberid
				if (mFirst) {
					ActivityAssignment activityAssignment = new ActivityAssignment();
					activityAssignment.setActivityId(motion.getActivityid());
					activityAssignment.setMemberId(AppSettings.getMemberid());
					mds.createActivityAssignment(activityAssignment);
					mFirst = false;
				}

				// createActivityData(motion);
				// Each time the activity type changes we need to update the
				// table
				if (motion.getActivityType() != mLastActivityType) {
					Events event = new Events();
					event.setActivityId(motion.getActivityid());
					event.setEventType(Constants.MAINACTIVITYTYPE);
					event.setEventSubType(motion.getActivityType());
					event.setEventTime(motion.getCurrentTime());
					mds.createEvent(event);
					mLastActivityType = motion.getActivityType();
				}
				motion = mds.createMotion(motion);

				if (motion != null) {
					sendUpdateMessage(motion);
				}
			}
			return null;
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// String msg = intent.getStringExtra(PARAM_IN_MSG);
		// NOT USING THIS CURRENTLY AS I HAVE HANDLERS TO DO THIS
	}

	// Send a message to autostart, autostop,etc
	private boolean sendStatusMessage(int status) {
		Message msgToActivity = MainActivity.mUiHandler.obtainMessage(status);
		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

	//Updates data to the user interface
	private boolean sendUpdateMessage(Motion motion) {

		Message msgToActivity = MainActivity.mUiHandler
				.obtainMessage(Constants.ACTIVITIES);
		// msgToActivity.what = Constants.ACTIVITIES;
		Bundle bundle = new Bundle();
		try {
			bundle.putString("motion", motion.convertMotionToJSON(false).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		msgToActivity.setData(bundle);

		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

}
