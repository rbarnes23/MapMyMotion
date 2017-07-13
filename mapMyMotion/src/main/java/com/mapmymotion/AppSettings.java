package com.mapmymotion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapmymotion.R;
import com.mapmymotion.utilities.Utils;
import com.securitylibrary.SecurityLibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

public class AppSettings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static Context sContext = MainActivity.getContext();
	// private static final String UNIT_STRING = "MeasureUnit";
	private static final String login = "login_settings";
	private static final String uom = "uom";
	private static final String networkid = "networkid";
	private static final String networkingonoff = "networkingonoff";
	private static final String notificationInterval = "notification_interval";
	private static final String notificationDistanceInterval = "notificationdistance_interval";
	private static final String notifications = "notifications";
	private static final String changepassword = "changepassword";
	private static final String autostart = "autostart";
	private static final String autostop = "autostop";
	private static final String currentspeed = "current_speed";
	private static final String averagespeed = "avg_speed";
	private static final String avgpace = "avg_pace";
	private static final String duration = "duration";
	private static final String distance = "distance";
	private static final String altitude = "altitude";
	private static final String updatesinterval = "updates_interval";
	private static final String currentspeedcue = "current_speed_cue";
	private static final String averagespeedcue = "avg_speed_cue";
	private static final String avgpacecue = "avg_pace_cue";
	private static final String durationcue = "duration_cue";
	private static final String distancecue = "distance_cue";
	private static final String current_time_cue = "current_time_cue";
	private static final String altitude_cue = "altitude_cue";
	private static final String minpace = "minpace";
	private static final String intervals = "interval";
	private static final String activitytype = "activitytype";
	private static final String replayspeed = "replayspeed";
	private static final String login_memberid = "login_memberid";
	private static Boolean mbChanged = false;
	private static String mActivityType = "0";
	private static String mReplaySpeed = "4";
	private static Menu mMenu;

	// private static TextView textLabel = new
	// TextView(MainActivity.getContext());

	// createMemberid used the new generated id paradigm
	public static String createMemberid() {
		String memberid = getMemberid();
		if (memberid.contentEquals("-1") || memberid.contentEquals("0")) {
			memberid=Secure.getString(sContext.getContentResolver(), Secure.ANDROID_ID);
			if(memberid==null){
				memberid = SecurityLibrary.generateUniqueId(16);
			}
			setMemberid(memberid);
		}

		return memberid;
	}

	public static Boolean isChanged() {
		return mbChanged;
	}

	public static int changePassword(String email, String p1, String p2) {
		int memberid = 0;

		// String email = AppSettings.login;
		if (email == null) {
			return memberid;
		}

		// Wait for keyboard to disappear before moving from preferences
		Toast.makeText(sContext, R.string.waitforconfirmation,
				Toast.LENGTH_SHORT).show();
		try {
			String[] params = new String[4];
			params[0] = "http://mapmymotion.com/changepassword.php";
			params[1] = email;// emailaddress
			params[2] = p1;// password
			params[3] = "1";// sendmail
			memberid = new MemberidTask().execute(params).get();
		} catch (InterruptedException e) {
			// Never interrupted
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return memberid;
	}

	/*
	 * public static int changePassword(String email,String p1,String p2) { int
	 * memberid = 0;
	 * 
	 * // String email = AppSettings.login; if (email == null) { return
	 * memberid; }
	 * 
	 * // Wait for keyboard to disappear before moving from preferences
	 * Toast.makeText(MainActivity.getContext(), R.string.waitforconfirmation,
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 * 
	 * nameValuePairs.add(new BasicNameValuePair("email", email));
	 * nameValuePairs.add(new BasicNameValuePair("pass", p1));
	 * nameValuePairs.add(new BasicNameValuePair("sendmail", "1")); InputStream
	 * is = null;
	 * 
	 * // http post try { HttpClient httpclient = new DefaultHttpClient();
	 * HttpPost httppost = new HttpPost(
	 * "http://mapmymotion.com/changepassword.php"); httppost.setEntity(new
	 * UrlEncodedFormEntity(nameValuePairs));
	 * 
	 * HttpResponse response = httpclient.execute(httppost);
	 * 
	 * HttpEntity entity = response.getEntity(); is = entity.getContent();
	 * 
	 * } catch (Exception e) {
	 * 
	 * // Log.e("log_tag", "Error in http connection " + e.toString()); }
	 * 
	 * String result = null; // convert response to string try {
	 * 
	 * BufferedReader reader = new BufferedReader(new InputStreamReader( is,
	 * "iso-8859-1"), 8); StringBuilder sb = new StringBuilder(); String line =
	 * null;
	 * 
	 * while ((line = reader.readLine()) != null) { sb.append(line + "\n"); }
	 * is.close();
	 * 
	 * result = sb.toString(); } catch (Exception e) {
	 * 
	 * // Log.e("log_tag", "Error converting result " + e.toString()); } //
	 * parse json data try { JSONArray jArray = new JSONArray(result);
	 * JSONObject json_data; json_data = jArray.getJSONObject(0); memberid =
	 * json_data.getInt("memberid"); //
	 * setMemberid(MainActivity.getContext(),memberid); } catch (JSONException
	 * e) { // Log.e("log_tag", "Error parsing data " + e.toString()); } return
	 * memberid; }
	 */
	public static int getmemberid(String email) {
		int memberid = 0;

		if (!Utils.checkEmail(email)) {
			memberid = 0;
			Toast.makeText(sContext, R.string.invalidemail, Toast.LENGTH_SHORT)
					.show();
			return memberid;
		}
		try {
			String[] params = new String[2];
			params[0] = "http://mapmymotion.com/getmemberid.php";
			params[1] = email;
			memberid = new MemberidTask().execute(params).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (memberid > 0) {
			setMemberid(Integer.toString(memberid));
			setEmailAddress(email);

		} else {
		}

		return memberid;
	}

	/*
	 * public static int getmemberid(String email) { int memberid = 0; if
	 * (!checkEmail(email)) { Toast.makeText(sContext, R.string.invalidemail,
	 * Toast.LENGTH_SHORT) .show(); memberid = 0; return memberid; }
	 * 
	 * 
	 * // String email = AppSettings.login; if (email == null) { return
	 * memberid; }
	 * 
	 * // Wait for keyboard to disappear before moving from preferences
	 * Toast.makeText(MainActivity.getContext(), R.string.waitforconfirmation,
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * // If member id is set no need to login again // if
	 * (getMemberid(MainActivity.getContext()) > 0){return //
	 * getMemberid(MainActivity.getContext());} // ArrayAdapter<String>
	 * activities = null; // the year data to send ArrayList<NameValuePair>
	 * nameValuePairs = new ArrayList<NameValuePair>();
	 * 
	 * nameValuePairs.add(new BasicNameValuePair("email", email)); InputStream
	 * is = null; // nameValuePairs.add(new BasicNameValuePair("type", "CYC"));
	 * // http post try { HttpClient httpclient = new DefaultHttpClient();
	 * HttpPost httppost = new HttpPost(
	 * "http://mapmymotion.com/getmemberid.php"); httppost.setEntity(new
	 * UrlEncodedFormEntity(nameValuePairs));
	 * 
	 * HttpResponse response = httpclient.execute(httppost);
	 * 
	 * HttpEntity entity = response.getEntity(); is = entity.getContent();
	 * 
	 * } catch (Exception e) {
	 * 
	 * // Log.e("log_tag", "Error in http connection " + e.toString()); }
	 * 
	 * String result = null; // convert response to string try {
	 * 
	 * BufferedReader reader = new BufferedReader(new InputStreamReader( is,
	 * "iso-8859-1"), 8); StringBuilder sb = new StringBuilder(); String line =
	 * null;
	 * 
	 * while ((line = reader.readLine()) != null) { sb.append(line + "\n"); }
	 * is.close();
	 * 
	 * result = sb.toString(); } catch (Exception e) {
	 * 
	 * // Log.e("log_tag", "Error converting result " + e.toString()); } //
	 * parse json data try { JSONArray jArray = new JSONArray(result);
	 * JSONObject json_data; json_data = jArray.getJSONObject(0); memberid =
	 * json_data.getInt("memberid"); //
	 * setMemberid(MainActivity.getContext(),memberid); } catch (JSONException
	 * e) { // Log.e("log_tag", "Error parsing data " + e.toString()); } return
	 * memberid; }
	 */

	public static int newUser(String email, String p1, String p2, String fname,
			String lname) {
		int memberid = 0;

		if (!Utils.checkEmail(email)) {
			Utils.setToast(sContext, Utils.getStringFromResource(sContext,
					R.string.invalidemail), Toast.LENGTH_SHORT);
			memberid = 0;
			return memberid;
		}

		// If any value is null return 0
		if (p1 == null || p2 == null || fname == null || lname == null) {
			return memberid;
		}
		try {
			String[] params = new String[6];
			params[0] = "http://mapmymotion.com/newmember.php";
			params[1] = email;
			params[2] = p1;
			params[3] = p2;
			params[4] = fname;
			params[5] = lname;
			memberid = new MemberidTask().execute(params).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (memberid > 0) {
			setMemberid(Integer.toString(memberid));
			setEmailAddress(email);
		}

		return memberid;
	}

	/*
	 * public static int newUser(String email, String p1, String p2, String
	 * fname, String lname) { int memberid = 0; if (!checkEmail(email)) {
	 * Toast.makeText(sContext, R.string.invalidemail, Toast.LENGTH_SHORT)
	 * .show(); memberid = 0; return memberid; }
	 * 
	 * // If any value is null return 0 if (email == null || p1 == null || p2 ==
	 * null || fname == null || lname == null) { return memberid; }
	 * 
	 * // Set up the value pairs ArrayList<NameValuePair> nameValuePairs = new
	 * ArrayList<NameValuePair>();
	 * 
	 * nameValuePairs.add(new BasicNameValuePair("email", email));
	 * nameValuePairs.add(new BasicNameValuePair("p1", p1));
	 * nameValuePairs.add(new BasicNameValuePair("p2", p2));
	 * nameValuePairs.add(new BasicNameValuePair("fname", fname));
	 * nameValuePairs.add(new BasicNameValuePair("lname", lname)); InputStream
	 * is = null;
	 * 
	 * // http post try { HttpClient httpclient = new DefaultHttpClient();
	 * HttpPost httppost = new HttpPost(
	 * "http://mapmymotion.com/newmember.php"); httppost.setEntity(new
	 * UrlEncodedFormEntity(nameValuePairs));
	 * 
	 * HttpResponse response = httpclient.execute(httppost);
	 * 
	 * HttpEntity entity = response.getEntity(); is = entity.getContent();
	 * 
	 * } catch (Exception e) {
	 * 
	 * // Log.e("log_tag", "Error in http connection " + e.toString()); }
	 * 
	 * String result = null; // convert response to string try {
	 * 
	 * BufferedReader reader = new BufferedReader(new InputStreamReader( is,
	 * "iso-8859-1"), 8); StringBuilder sb = new StringBuilder(); String line =
	 * null;
	 * 
	 * while ((line = reader.readLine()) != null) { sb.append(line + "\n"); }
	 * is.close();
	 * 
	 * result = sb.toString(); } catch (Exception e) {
	 * 
	 * // Log.e("log_tag", "Error converting result " + e.toString()); } //
	 * parse json data try { JSONArray jArray = new JSONArray(result);
	 * JSONObject json_data; json_data = jArray.getJSONObject(0); memberid =
	 * json_data.getInt("memberid"); setMemberid( Integer.toString(memberid));
	 * setEmailAddress( email); } catch (JSONException e) { // Log.e("log_tag",
	 * "Error parsing data " + e.toString()); } return memberid; }
	 */
	public static String getFriendsArray(String aMemberid) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("amemberid", aMemberid));
		InputStream is = null;

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/getfriendsarray.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {

			// Log.e("log_tag", "Error in http connection " + e.toString());
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();

			result = sb.toString();
		} catch (Exception e) {

			// Log.e("log_tag", "Error converting result " + e.toString());
			result = "Unable to retrieve List of Friends ";
		}
		return result;
	}

	public static String getEmailAddress() {
		return getString(AppSettings.login);
	}

	public static int getActivityType() {
		if (getString(AppSettings.activitytype) == null) {
			setActivityType("0");
		}
		return Integer.parseInt(getString(AppSettings.activitytype));
	}

	public static void setActivityType(String activitytype) {
		mActivityType = activitytype;
		// setActivitySpinner(mActivityType);
		putString(AppSettings.activitytype, activitytype);
	}

	public static int getReplaySpeed() {
		if (getString(AppSettings.replayspeed) == null) {
			setReplaySpeed("4");
		}
		// if (getString(AppSettings.replayspeed).contentEquals(null)) {
		// setReplaySpeed("0");
		// }
		;
		return Integer.parseInt(getString(AppSettings.replayspeed));
	}

	public static void setReplaySpeed(String replaySpeed) {
		mReplaySpeed = replaySpeed;
		putString(AppSettings.replayspeed, replaySpeed);
	}

	public static int getUOM() {
		return Integer.parseInt(getString(AppSettings.uom));
	}

	public static int getMinPace() {
		return Integer.parseInt(getString(AppSettings.minpace));

	}

	public static int getAutoStartInterval() {
		return Integer.parseInt(getString(AppSettings.autostart));

	}

	public static int getAutoStopInterval() {
		return Integer.parseInt(getString(AppSettings.autostop));

	}

	public static boolean getCurrentSpeedCue() {
		return getBoolean(AppSettings.currentspeedcue);
	}

	public static boolean getAverageSpeedCue() {
		return getBoolean(AppSettings.averagespeedcue);
	}

	public static boolean getPaceCue() {
		return getBoolean(AppSettings.avgpacecue);
	}

	public static boolean getDurationCue() {
		return getBoolean(AppSettings.durationcue);
	}

	public static boolean getDistanceCue() {
		return getBoolean(AppSettings.distancecue);
	}

	public static boolean getCurrentTimeCue() {
		return getBoolean(AppSettings.current_time_cue);
	}

	public static boolean getAltitudeCue() {
		return getBoolean(AppSettings.altitude_cue);
	}

	public static boolean getCurrentSpeedSetting() {
		return getBoolean(AppSettings.currentspeed);
	}

	public static boolean getAverageSpeedSetting() {
		return getBoolean(AppSettings.averagespeed);
	}

	public static boolean getPaceSetting() {
		return getBoolean(AppSettings.avgpace);
	}

	public static boolean getDurationSetting() {
		return getBoolean(AppSettings.duration);
	}

	public static boolean getDistanceSetting() {
		return getBoolean(AppSettings.distance);
	}

	public static boolean getAltitudeSetting() {
		return getBoolean(AppSettings.altitude);
	}

	public static boolean getIntervalSetting() {
		return getBoolean(AppSettings.intervals);
	}

	public static int getNotificationInterval() {
		return Integer.parseInt(getString(AppSettings.notificationInterval));

	}

	public static float getNotificationDistanceInterval() {
		return Float
				.parseFloat(getString(AppSettings.notificationDistanceInterval));

	}

	public static boolean getNotificationStatus() {
		return getBoolean(AppSettings.notifications);

	}

	public static int getUpdateInterval() {
		return Integer.parseInt(getString(AppSettings.updatesinterval));
	}

	public static boolean getChangePass() {
		return getBoolean(AppSettings.changepassword);
	}

	public static void setChangePass(Boolean aBool) {
		putBoolean(AppSettings.changepassword, aBool);
	}

	private static boolean getBoolean(String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return pref.getBoolean(tag, false);
	}

	private static String getString(String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return pref.getString(tag, null);
	}

	public static String getNetworkid() {
		// return getString( AppSettings.networkid);
		return "96.31.86.130";
		// return "192.168.1.6";

	}

	public static void setNetworkid(Context context, String limit) {
		putString(AppSettings.networkid, limit);
	}

	public static boolean getNetworkStatus() {
		return getBoolean(AppSettings.networkingonoff);
	}

	public static void setNetworkStatus(String tag, Boolean aNS) {
		putBoolean(AppSettings.networkingonoff, aNS);
	}

	public static String getMemberid() {
		return getString(AppSettings.login_memberid);
	}

	public static void setMemberid(String limit) {
		putString(AppSettings.login_memberid, limit);
	}

	public static void setEmailAddress(String limit) {
		putString(AppSettings.login, limit);
	}

	private static int getInt(String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);

		return pref.getInt(tag, 0);
	}

	public static void putInt(String tag, int value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		SharedPreferences.Editor editor = pref.edit();

		editor.putInt(tag, value);
		editor.commit();
	}

	public static void putString(String tag, String value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		SharedPreferences.Editor editor = pref.edit();

		editor.putString(tag, value);
		editor.commit();
	}

	public static void putBoolean(String tag, Boolean value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(tag, value);
		editor.commit();
	}

	public static String getHMS(long s) {
		int HH = (int) (s / 3600);
		int MM = (int) ((s - (HH * 3600)) / 60);
		int SS = (int) (s - (HH * 3600) - (MM * 60));
		String durationIs = "";// sContext.getResources().getString(R.string.durationis);
		return durationIs
				+ String.format(Locale.US, " %02d:%02d:%02d", HH, MM, SS);
	}

	public static String getMS(double m) {
		int MM = (int) m;
		int SS = (int) (m % 1 * 60);
		// String paceText =
		// sContext.getResources().getString(R.string.pacetext);
		return String.format(Locale.US, " %02d:%02d", MM, SS);
	}

	public void setDefault() {
		PreferenceManager.setDefaultValues(sContext, R.xml.preferences, false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// This has to go first...do not move
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		PreferenceManager.setDefaultValues(sContext, R.xml.preferences, false);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		mbChanged = true;
		if (key.equals("activitytype")) {
			setActivityType(mActivityType);
		}
		if (key.equals("replayspeed")) {
			setReplaySpeed(mReplaySpeed);
		}

		SharedPreferences.Editor editor = pref.edit();
		editor.commit();
		onContentChanged();
	}

	public static void setContext(Context context) {
		sContext = context;
		// sTextLabel = new TextView(context);

	}

	public static void setMenu(Menu menu) {
		mMenu = menu;
	}

	public static void setActivitySpinner(String activityType) {
		MenuItem mSpinnerItem = mMenu.findItem(R.id.activityTypeMenu);

		View view = mSpinnerItem.getActionView();
		if (view instanceof Spinner) {
			Spinner spinner = (Spinner) view;
			spinner.setSelection(Integer.parseInt(activityType));
		}
	}

	public static void setReplaySpinner(String replaySpeed) {
		MenuItem spinnerItem = mMenu.findItem(R.id.replaySpeed);

		View view = spinnerItem.getActionView();
		if (view instanceof Spinner) {
			Spinner spinner = (Spinner) view;
			spinner.setSelection(Integer.parseInt(replaySpeed));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		/*
		 * SharedPreferences pref = getSharedPreferences("MY_PREFS", 0);
		 * SharedPreferences.Editor editor = pref.edit(); editor.commit();
		 */
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Send appsettings
	public static boolean sendMessage(String appSettingType,
			String appSettingValue) {
		Message msgToActivity = MainActivity.mUiHandler
				.obtainMessage(Constants.SETTINGS);
		Bundle bundle = new Bundle();
		bundle.putString(appSettingType, appSettingValue);
		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

}

class MemberidTask extends AsyncTask<String, Void, Integer> {
	int myid = -1;

	@Override
	protected Integer doInBackground(String... params) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String url = params[0];
		if (url.contentEquals("http://mapmymotion.com/getmemberid.php")) {
			nameValuePairs.add(new BasicNameValuePair("email", params[1]));
		} else if (url.contentEquals("http://mapmymotion.com/newmember.php")) {
			nameValuePairs.add(new BasicNameValuePair("email", params[1]));
			nameValuePairs.add(new BasicNameValuePair("p1", params[2]));
			nameValuePairs.add(new BasicNameValuePair("p2", params[3]));
			nameValuePairs.add(new BasicNameValuePair("fname", params[4]));
			nameValuePairs.add(new BasicNameValuePair("lname", params[5]));
		} else if (url
				.contentEquals("http://mapmymotion.com/changepassword.php")) {
			nameValuePairs.add(new BasicNameValuePair("email", params[1]));
			nameValuePairs.add(new BasicNameValuePair("pass", params[2]));
			nameValuePairs.add(new BasicNameValuePair("sendmail", params[3]));

		}
		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}
		// parse json data
		try {
			/*
			 * if (result.contentEquals(null)) { return myid; }
			 */
			JSONArray jArray = new JSONArray(result);
			JSONObject json_data;
			json_data = jArray.getJSONObject(0);
			myid = json_data.getInt("memberid");
		} catch (JSONException e) {
			myid = -1;
		}
		return myid;
	}

}

class FriendsTask extends AsyncTask<String, Void, Integer> {
	int myid = -1;

	@Override
	protected Integer doInBackground(String... params) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("email", params[0]));
		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/artytheartistgetmemberid.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}
		// parse json data
		try {
			/*
			 * if (result.contentEquals(null)) { return myid; }
			 */
			JSONArray jArray = new JSONArray(result);
			JSONObject json_data;
			json_data = jArray.getJSONObject(0);
			myid = json_data.getInt("memberid");
		} catch (JSONException e) {
			myid = -1;
		}
		return myid;
	}

}