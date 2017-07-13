/**
 * 
 */
package com.mapmymotion;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.util.EGM96;

import com.mapmymotion.pojo.Motion;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;

/**
 * @author Rick
 * 
 */
public class MotionCalculator {
	private int mMeasurementIndex = AppSettings.getUOM();
	private long mStartTime;
	private long mElapsedTime;
	private long mTotalPauseTime;
	private int mCount = 1;
	private Location mLastLocation;
	private double mTotalDistance = 0.00;
	private boolean mPause;
	private long mPauseStartTime;
	private EGM96 egm96;
	private String mActivityId;

	// Set NASA altitude adjustments...wasn't able to set from here so did in
	// main activity
	public void initalize(EGM96 egm) {
		egm96 = egm;
	}

	public void setPaused(boolean pause) {
		mPause = pause;
	}

	public Motion replayCalculations(Motion motion) {
		// Calculate speed in m/s
		if (motion.getCurrentDistance() > 0) {
			motion.setCurrentSpeed(motion.getCurrentDistance()
					/ (Constants.MINIMUMTIMEBETWEENUPDATES / 1000));// motion.getCurrentTime());
		}
		// The first item will be the starttime as cursor is sorted by epochtime
		if (mTotalDistance == 0) {
			mStartTime = motion.getCurrentTime();
		}
		mTotalDistance += motion.getCurrentDistance();
		motion.setTotalDistance(mTotalDistance);
		if (mTotalDistance >= (AppSettings.getNotificationDistanceInterval()
				* Constants.UNITS[mMeasurementIndex] * mCount)
				&& AppSettings.getNotificationDistanceInterval() > 0) {
			motion.setAnimateToPoint(true);
			mCount++;
		}
		mElapsedTime = (motion.getCurrentTime() - mStartTime) / 1000;
		// Set thee pause starttime to 0
		mPauseStartTime = (mPause == true) ? motion.getCurrentTime() : 0;
		if (mPauseStartTime > 0) {
			mTotalPauseTime = (motion.getCurrentTime() - mPauseStartTime) / 1000;
			motion.setTotalTimePaused(mTotalPauseTime);
		}
		motion.setTimeElapsed(mElapsedTime - mTotalPauseTime);
		// Calculate average speed
		double avgSpeed = 0;
		if (mTotalDistance > 0) {
			avgSpeed = mTotalDistance / (mElapsedTime - mTotalPauseTime);
		}
		// This is mostly for emulator, but fix anyway
		avgSpeed = Double.isInfinite(avgSpeed) ? 0 : avgSpeed;

		// Update Average Speed
		// motion.setAverageSpeed(convertSpeed(avgSpeed));
		motion.setAverageSpeed(avgSpeed);

		motion.setCount(mCount);
		// Set LatLng
		motion.setLatLng(new LatLng(motion.getLatitude(), motion.getLongitude()));

		motion.setTimePaused(mTotalPauseTime);

		return motion;
	}

	public void setActivityId(String activityId) {
		mActivityId = activityId;
	}

	// Keeps the telematics up to date but does not convert between Metric /US
	// until later
	public Motion updateCalculations(Location location) {
		if (egm96 == null) {
			egm96 = MainActivity.getEgm96();
		}
		// Initialize POJO
		Motion motion = new Motion();

		// This is used to initialize a new trip
		if (mLastLocation == null && location != null) {
			mLastLocation = location;
			// mActivityId = SecurityLibrary.generateUniqueId(10);
			mTotalDistance = 0.00; // in meters per second
			mElapsedTime = 1; // Set to 1 so no divide by 0 error
			mStartTime = location.getTime(); // This is start of trip time in
												// epochtime

			return motion;
		}

		// Set the ActivityId
		motion.setActivityid(mActivityId);
		// Set Activity Type
		motion.setActivityType(AppSettings.getActivityType());

		// Set the Current Speed - need to keep in m/s
		// motion.setCurrentSpeed(convertSpeed(location.getSpeed()));
		motion.setCurrentSpeed(location.getSpeed());

		// // If mCount is 10 then we will move MAP -might want to change this
		// // count based on speed
		// motion.setAnimateToPoint(mCount - 5 == 0 ? true : false);
		// // If we are animating MAP then we want to reset the count
		// mCount = motion.isAnimateToPoint() ? 0 : mCount;
		// mCount++;

		// Set the Altitude - Currently in meters only
		double offset = 0;
		if (egm96 != null) {
			offset = egm96.getOffset(Angle.fromDegrees(location.getLatitude()),
					Angle.fromDegrees(location.getLongitude()));
		}
		// motion.setCurrentAltitude(convertMetric((location.getAltitude()-offset)));
		motion.setCurrentAltitude((location.getAltitude() - offset));

		// Need to subtract endTime from mStartTime and then
		// mTotalDistance/totalTime
		mElapsedTime = (location.getTime() - mStartTime) / 1000;
		// Set the pause starttime to 0
		mPauseStartTime = (mPause == true) ? location.getTime() : 0;
		if (mPauseStartTime > 0) {
			mTotalPauseTime = (location.getTime() - mPauseStartTime) / 1000;
			motion.setTotalTimePaused(mTotalPauseTime);
		}
		motion.setTimeElapsed(mElapsedTime - mTotalPauseTime);

		// Get latest distance
		double currentDistance = location.distanceTo(mLastLocation);
		motion.setCurrentDistance(currentDistance);

		// Set the LastLocation for next calculation
		mLastLocation = location;

		// Calculate total distance
		mTotalDistance += currentDistance;

		if (mTotalDistance >= (AppSettings.getNotificationDistanceInterval()
				* Constants.UNITS[mMeasurementIndex] * mCount)
				&& AppSettings.getNotificationDistanceInterval() > 0) {
			motion.setAnimateToPoint(true);
			mCount++;
		}

		// Set distance in either km or miles
		// motion.setTotalDistance(mTotalDistance *
		// Constants.UNIT_MULTIPLIERS[mMeasurementIndex]);
		motion.setTotalDistance(mTotalDistance);

		// Calculate average speed
		double avgSpeed = mTotalDistance / (mElapsedTime - mTotalPauseTime);
		// This is mostly for emulator, but fix anyway
		avgSpeed = Double.isNaN(avgSpeed) ? 0 : avgSpeed;

		// Update Average Speed
		// motion.setAverageSpeed(convertSpeed(avgSpeed));
		motion.setAverageSpeed(avgSpeed);

		// Calculate and display Pace
		// double pace = (60 / convertSpeed(avgSpeed));
		// double pace = (60 / avgSpeed);
		//
		// // This is a kludge just in case we get a wacky reading
		// pace=(pace>100)?0:pace;
		//
		// // Set Pace
		// motion.setPace(pace);

		// Set Current Time
		motion.setCurrentTime(location.getTime());

		// Set LatLng
		motion.setLatLng(new LatLng(location.getLatitude(), location
				.getLongitude()));
		// Set Latitude
		motion.setLatitude(location.getLatitude());
		// Set Longitude
		motion.setLongitude(location.getLongitude());
		;

		return motion;
	}

	// // Converts speed to mph or kmph
	// private double convertSpeed(double speed) {
	// return ((speed * Constants.HOUR_MULTIPLIER) *
	// Constants.UNIT_MULTIPLIERS[mMeasurementIndex]);
	// }
	// // Converts from to mph or kmph
	// private double convertMetric(double meters) {
	// return ((meters * Constants.METERSANDFEET[mMeasurementIndex]));
	// }

}
