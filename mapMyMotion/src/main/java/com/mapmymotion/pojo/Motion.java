/**
 * 
 */
package com.mapmymotion.pojo;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Rick
 * 
 */
public class Motion{
	private int count;
	private long currentTime;
	private long timeElapsed;
	private long timePaused;

	private long totalTimePaused;
	private double currentSpeed;
	private double latitude;
	private String activityid;

	private double longitude;
	private double averageSpeed;
//	private double pace;
	private double currentAltitude;
	private double currentDistance;
	private double totalDistance;
	private boolean animateToPoint;
	private boolean isPaused;
	private long id;
	private int activityType;
	private double weight;
	private String note;


	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public String getActivityid() {
		return activityid;
	}

	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	private LatLng latLng;

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the currentTime
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * @param currentTime
	 *            the currentTime to set
	 */
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * @return the timeElapsed
	 */
	public long getTimeElapsed() {
		return timeElapsed;
	}

	/**
	 * @param timeElapsed
	 *            the timeElapsed to set
	 */
	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	/**
	 * @return the timePaused
	 */
	public long getTimePaused() {
		return timePaused;
	}

	/**
	 * @param timePaused
	 *            the timePaused to set
	 */
	public void setTimePaused(long timePaused) {
		this.timePaused = timePaused;
	}

	/**
	 * @return the totalTimePaused
	 */
	public long getTotalTimePaused() {
		return totalTimePaused;
	}

	/**
	 * @param totalTimePaused
	 *            the totalTimePaused to set
	 */
	public void setTotalTimePaused(long totalTimePaused) {
		this.totalTimePaused = totalTimePaused;
	}

	/**
	 * @return the currentSpeed
	 */
	public double getCurrentSpeed() {
		return currentSpeed;
	}

	/**
	 * @param currentSpeed
	 *            the currentSpeed to set
	 */
	public void setCurrentSpeed(double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	/**
	 * @return the averageSpeed
	 */
	public double getAverageSpeed() {
		return averageSpeed;
	}

	/**
	 * @param averageSpeed
	 *            the averageSpeed to set
	 */
	public void setAverageSpeed(double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

//	/**
//	 * @return the pace
//	 */
//	public double getPace() {
//		return pace;
//	}

//	/**
//	 * @param pace
//	 *            the pace to set
//	 */
//	public void setPace(double pace) {
//		this.pace = pace;
//	}

	/**
	 * @return the currentAltitude
	 */
	public double getCurrentAltitude() {
		return currentAltitude;
	}

	/**
	 * @param currentAltitude
	 *            the currentAltitude to set
	 */
	public void setCurrentAltitude(double currentAltitude) {
		this.currentAltitude = currentAltitude;
	}

	/**
	 * @return the currentDistance
	 */
	public double getCurrentDistance() {
		return currentDistance;
	}

	/**
	 * @param currentDistance
	 *            the currentDistance to set
	 */
	public void setCurrentDistance(double currentDistance) {
		this.currentDistance = currentDistance;
	}

	/**
	 * @return the totalDistance
	 */
	public double getTotalDistance() {
		return totalDistance;
	}

	/**
	 * @param totalDistance
	 *            the totalDistance to set
	 */
	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	/**
	 * @return the animateToPoint
	 */
	public boolean isAnimateToPoint() {
		return animateToPoint;
	}

	/**
	 * @param animateToPoint
	 *            the animateToPoint to set
	 */
	public void setAnimateToPoint(boolean animateToPoint) {
		this.animateToPoint = animateToPoint;
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
	
	
	//This is much simpler than Parcelable
	public JSONObject convertMotionToJSON(boolean isBasic) throws JSONException{
		JSONObject jMotion=new JSONObject();
		jMotion.put("currentTime", currentTime);
		jMotion.put("currentAltitude", currentAltitude);
		jMotion.put("currentDistance", currentDistance);
		jMotion.put("latitude", latitude);
		jMotion.put("longitude", longitude);
		jMotion.put("activityid", activityid);
		if(isBasic){
			return jMotion;
		}
		
		jMotion.put("count", count);
		jMotion.put("id", id);
		jMotion.put("timeElapsed", timeElapsed);
		jMotion.put("timePaused", timePaused);
		jMotion.put("totalTimePaused", totalTimePaused);
		jMotion.put("currentSpeed", currentSpeed);
		jMotion.put("totalDistance", totalDistance);
		jMotion.put("averageSpeed", averageSpeed);
		jMotion.put("activitytype", activityType);
//		jMotion.put("pace", pace);
		jMotion.put("isPaused", isPaused);
		jMotion.put("animateToPoint", animateToPoint);
		jMotion.put("weight", weight);
		jMotion.put("note", note);
		
		return jMotion;}

	//Import motion info from server
	public void populateMotionDBFromJSON(String activityId, JSONObject jMotion) throws JSONException{
		this.currentTime = jMotion.getLong("epochtime");
		this.currentDistance = jMotion.getDouble("distance");
		this.latitude = jMotion.getDouble("latitude");
		this.longitude = jMotion.getDouble("longitude");
		this.currentAltitude=jMotion.getDouble("altitude");
		this.activityid = activityId;
	}	
	
	public void populateMotionFromJSON(JSONObject jMotion) throws JSONException{
		this.count = jMotion.getInt("count");
		this.id = jMotion.getLong("id");
		this.currentTime = jMotion.getLong("currentTime");
		this.timeElapsed = jMotion.getLong("timeElapsed");
		this.timePaused = jMotion.getLong("timePaused");
		this.totalTimePaused = jMotion.getLong("totalTimePaused");
		this.currentSpeed = jMotion.getDouble("currentSpeed");
		this.latitude = jMotion.getDouble("latitude");
		this.longitude = jMotion.getDouble("longitude");
		this.activityid = jMotion.getString("activityid");
		this.averageSpeed = jMotion.optDouble("averageSpeed");
//		this.pace = jMotion.getDouble("pace");
		this.currentAltitude = jMotion.getDouble("currentAltitude");
		this.currentDistance = jMotion.getDouble("currentDistance");
		this.totalDistance = jMotion.getDouble("totalDistance");
		this.isPaused = jMotion.getBoolean("isPaused");
		this.animateToPoint = jMotion.getBoolean("animateToPoint");
		setLatLng(new LatLng(getLatitude(),
				getLongitude()));
		this.activityType = jMotion.getInt("activitytype");
		this.weight = jMotion.getDouble("weight");
		this.note = jMotion.optString("note");

	}

}
