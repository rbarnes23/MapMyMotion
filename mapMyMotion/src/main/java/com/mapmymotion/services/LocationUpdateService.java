package com.mapmymotion.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.MainActivity;
import com.mapmymotion.MotionCalculator;
import com.mapmymotion.R;
import com.mapmymotion.pojo.Motion;
import com.mapmymotion.sql.MotionDataSource;
import com.mapmymotion.utilities.Utils;
import com.securitylibrary.SecurityLibrary;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class LocationUpdateService extends Service {
	private MotionDataSource mds;
	private MotionCalculator motionCalculator;
	private JSONObject jActivity;
	public static Handler mServiceHandler = null;
	private int mOnOffStatus=Constants.STOP;
	private int mPause = Constants.RESUME;
	private ExecutorService executor = Executors.newFixedThreadPool(5);

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
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
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					Constants.MINIMUMTIMEBETWEENUPDATES, 0,
					new GeoUpdateHandler());
			motionCalculator = new MotionCalculator();
		} else {
			Utils.setToast(getBaseContext(),
					getString(R.string.locationnotenabled),
					Toast.LENGTH_LONG);
		}
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		executor.shutdown();
		mds.close();
		mds = null;
		motionCalculator = null;

	}

	public class GeoUpdateHandler implements LocationListener {
		// Keep track of our markers
		// private List<MarkerOptions> markerOptions = new
		// ArrayList<MarkerOptions>();
		// Animated Count to display on marker icon
		// Motion motionInfo;

		@Override
		public void onLocationChanged(Location location) {
			if (mOnOffStatus == Constants.STOP && mPause == Constants.RESUME) {
				// Checking to see if autostart is expensive so only want to do
				// it if we are stopped
				if ((Utils.roundDecimal(Utils.convertSpeed(location.getSpeed()),
						2) >= AppSettings.getAutoStartInterval())
						&& AppSettings.getAutoStartInterval() != 0) {
					// Send a message to autostart the onoff button
					mOnOffStatus = Constants.AUTOSTART;
					sendStatusMessage(mOnOffStatus);
				}
			} // only populate database and update view if we are started
			else if (location.getLatitude() > 0
					&& mOnOffStatus == Constants.START) {
				new PopulateTelematics().executeOnExecutor(executor, location);
			}
		}

		// Does necessary calculation to update the motion pojo, the database
		// and send data to the UI
		private class PopulateTelematics extends
				AsyncTask<Location, Void, Void> {
			// @Override
			// protected void onPostExecute(Motion motion) {
			// super.onPostExecute(motion);
			// }

			@Override
			protected Void doInBackground(Location... params) {
				Location location = params[0];

				// The motionCalculator keeps a running sum of calculations
				Motion motion = motionCalculator.updateCalculations(location);
				// Update data
				if (motion.getCurrentTime() > 0) {
					// createActivityData(motion);
					motion = mds.createMotion(motion);
					if (motion != null) {
						sendUpdateMessage(motion);
					}
				}
				return null;
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
			// TODO Auto-generated method stub

		}

	}

	private JSONObject createActivityData(Motion motion) throws JSONException {
		if (jActivity == null) {
			createActivity();
		}
		;
		JSONObject jData = new JSONObject();
		jData.put("latlng", motion.getLatLng());
		jData.put("epochtime", motion.getCurrentTime());
		jData.put("distance", motion.getCurrentDistance());
		jData.put("altitude", motion.getCurrentAltitude());
		jData.put("paused", motion.isPaused());
		jActivity.put("data", jData);
		return jActivity;
	}

	private void createActivity() throws JSONException {
		jActivity = new JSONObject();
		jActivity.put("_id", SecurityLibrary.generateUniqueId(24));
		jActivity.put("memberId", AppSettings.getMemberid());

		// jActivity.put("activityType",
		// Constants.ACTIVITYTYPE.valueOf(AppSettings.getActivityType())
		// .getID());
		jActivity.put("activityType", AppSettings.getActivityType());
	}

	// Gets activity name i.e. "RUN" not currently used
//	private String getActivityName(int typeOfActivity) {
//		ACTIVITYTYPE[] arrayOfTypes = Constants.ACTIVITYTYPE.values();
//		return arrayOfTypes[typeOfActivity].name();
//	}

	// Send a message to autostart, autostop,etc
	private boolean sendStatusMessage(int status) {
		Message msgToActivity = MainActivity.mUiHandler.obtainMessage(status);
		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

	private boolean sendUpdateMessage(Motion motion) {

		Message msgToActivity = MainActivity.mUiHandler.obtainMessage();
		msgToActivity.what = Constants.ACTIVITIES;
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