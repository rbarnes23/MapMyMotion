package com.tokenlibrary;

//import java.util.ArrayList;
//import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Token {
	protected static JSONObject mToken;
	protected static String mMessage = null;
	protected static JSONArray jArrayFriends = null;
	protected static JSONArray jArrayLogin = null;
	private final String JSONOBJECTFAILED = "-1";

	public String createToken(String aMessage) {
		try {
			mToken = new JSONObject(aMessage);
			if(mToken.optString("_id").length()<1){ 
			setToken("_id", "" + (System.currentTimeMillis() / 1000L));};
		} catch (JSONException e) {
			mMessage = JSONOBJECTFAILED;
		}
		return aMessage;
	}

	public void createNewToken() {
		mToken = new JSONObject();
		setToken("_id", "" + (System.currentTimeMillis() / 1000L));
	}

	
	
	public JSONObject getToken() {
		return mToken;
	}

	public String getID() {
		try {
			mMessage = mToken.getString("_id");
		} catch (JSONException e) {
			mMessage = "Unable to obtain Token";// e.getMessage();
		}
		return mMessage;
	}

	public String getTokenString() {
		return mToken.toString() + "\n";
	}

	public String getType() {
		try {
			mMessage = mToken.getString("type");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getNS() {
		try {
			mMessage = mToken.getString("NStype");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getData() {
		try {
			mMessage = mToken.getString("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getMessage() {
		try {
			mMessage = mToken.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}
	
	
	public String getTo() {
		try {
			mMessage = mToken.getString("to");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getNames() {
		try {
			mMessage = mToken.getString("names");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getMembers() {
		try {
			mMessage = mToken.getString("members");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	// getFrom same as getMemberid
	public String getFrom() {
		return getMemberid();
	}

	public String getMemberid() {
		try {
			mMessage = mToken.getString("memberid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getLongitude() {
		try {
			mMessage = mToken.getString("longitude");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getLatitude() {
		try {
			mMessage = mToken.getString("latitude");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getTime() {
		try {
			mMessage = mToken.getString("epochtime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getDistance() {
		try {
			mMessage = mToken.getString("distance");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getAltitude() {
		try {
			mMessage = mToken.getString("memberid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getActivityType() {
		try {
			mMessage = mToken.getString("activitytype");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getContinue() {
		try {
			mMessage = (mToken.getBoolean("continue") == true) ? "Y" : "N";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public void setToken(String aTokentype, String aContent) {
		try {
			mToken.put(aTokentype, aContent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
	}

	public void setTokenInt(String aTokentype, Integer aContent) {
		try {
			mToken.put(aTokentype, aContent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
	}

	public void setTokenArray(String aTokentype, String aContent) {
		try {
			JSONArray jArray = new JSONArray(aContent);
			mToken.put(aTokentype, jArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
	}

	// Take a JSONArray and assign it a type
	public void setTokenArray(String aTokentype, JSONArray jContent) {
		try {
			mToken.put(aTokentype, jContent);
		} catch (JSONException e) {
			mMessage = e.getMessage();
		}
	}

	public void setTokenBoolean(String aTokentype, boolean aContent) {
		try {
			mToken.put(aTokentype, aContent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
	}

	public String getReply() {
		try {
			mMessage = (mToken.getBoolean("reply") == true) ? "Y" : "N";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getSelf() {
		try {
			mMessage = (mToken.getBoolean("self") == true) ? "Y" : "N";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String isMessage() {
		try {
			mMessage = (mToken.getBoolean("MSG") == true) ? "Y" : "N";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getStartTime() {
		try {
			mMessage = mToken.getString("starttime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getNewMemberId() {
		try {
			mMessage = mToken.getString("newmemberid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getCourseId() {
		try {
			mMessage = new JSONObject(mToken.getString("courseid"))
					.getString("$oid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mMessage = e.getMessage();
		}
		return mMessage;
	}

	public String getString(String aType) {
		try {
			mMessage = mToken.getString(aType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			mMessage = null;// e.getMessage();
		}
		return mMessage;
	}

	public String createMessagetoFriendsToken(String aMemberid,
			JSONObject jMessage, Boolean aSelf, Boolean aMsg, JSONArray aTo) {
		mToken = jMessage;
		setToken("_id", "" + (System.currentTimeMillis() / 1000L));
		setToken("type", "getmessage");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setTokenBoolean("MSG", aMsg);
		setTokenArray("mStat", aTo);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}

	public String createEdiMessage(String aMemberid, JSONObject jMessage,
			Boolean aSelf, Boolean aMsg, JSONArray aTo) {
		mToken = jMessage;
		setToken("_id", "" + (System.currentTimeMillis() / 1000L));
		setToken("type", "edi");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setTokenArray("mStat", aTo);
		setTokenBoolean("MSG", aMsg);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}

	public String createStopInfoMessage(String aMemberid, JSONObject jMessage,
			Boolean aSelf, Boolean aMsg, JSONArray aTo) {
		mToken = jMessage;
		setToken("_id", "" + (System.currentTimeMillis() / 1000L));
		setToken("type", "stopinfo");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setTokenArray("mStat", aTo);
		setTokenBoolean("MSG", aMsg);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}
	
	
	public String createMessagetoFriendsToken(String aMemberid,
			CharSequence aMessage, Boolean aSelf, Boolean aMsg, JSONArray aTo) {
		createNewToken();
		setToken("type", "getmessage");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("message", aMessage);
		setTokenBoolean("MSG", aMsg);
		setTokenArray("mStat", aTo);
		// setTokenArray("names", aFriendsArray[1]);
		// setToken("to", "[0]");
		// String mMembers="0";
		// setTokenArray("to", "[" + mMembers + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}

	public String createMessagetoFriendsToken(String aMemberid,
			CharSequence aMessage, Boolean aSelf, Boolean aMsg) {
		createNewToken();
		setToken("type", "getmessage");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("message", aMessage);
		setTokenBoolean("MSG", aMsg);
		// setTokenArray("to", aFriendsArray[0]);
		// setTokenArray("names", aFriendsArray[1]);
		// setToken("to", "[0]");
		// String mMembers="0";
		// setTokenArray("to", "[" + mMembers + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}

	public String createMessagetoFriendsToken(String aMemberid,
			CharSequence aMessage, Boolean aSelf) {
		return createMessagetoFriendsToken(aMemberid, aMessage, aSelf, false);
		/*
		 * createNewToken(); setToken("type", "getmessage"); setToken("NS",
		 * "mapmymotion"); setToken("memberid", aMemberid); setToken("message",
		 * aMessage); // setTokenArray("to", aFriendsArray[0]); //
		 * setTokenArray("names", aFriendsArray[1]); //setToken("to", "[0]");
		 * //String mMembers="0"; //setTokenArray("to", "[" + mMembers + "]");
		 * setTokenBoolean("reply", true); setTokenBoolean("self", aSelf);
		 * return getTokenString();
		 */}

	/*
	 * public String createMessagetoFriendsToken(String aMemberid, CharSequence
	 * aMessage, Boolean aSelf, Boolean aMsg) { createNewToken();
	 * setToken("type", "getmessage"); setToken("NS", "mapmymotion");
	 * setToken("memberid", aMemberid); setToken("message", aMessage);
	 * setTokenBoolean("MSG", aMsg); // setTokenArray("to", aFriendsArray[0]);
	 * // setTokenArray("names", aFriendsArray[1]); // setToken("to", "[0]"); //
	 * String mMembers="0"; setTokenArray("to", "[" + aMemberid + "]");
	 * setTokenBoolean("reply", true); setTokenBoolean("self", aSelf); return
	 * getTokenString(); }
	 */
	public String createSessionsToken(String aMemberid, CharSequence aMessage,
			Boolean aSelf, Boolean aMsg) {
		createNewToken();
		setToken("type", "getsessions");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("message", aMessage);
		setTokenBoolean("MSG", aMsg);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}

	public String createCompanyToken(String aMemberid, CharSequence aMessage,
			Boolean aSelf, Boolean aMsg) {
		createNewToken();
		setToken("type", "company");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("message", aMessage);
		setTokenBoolean("MSG", aMsg);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}

	public String createEmployeeToken(String aMemberid, CharSequence aMessage,
			Boolean aSelf, Boolean aMsg) {
		createNewToken();
		setToken("type", "employee");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("message", aMessage);
		setTokenBoolean("MSG", aMsg);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}
	
	public String createVehicleToken(String aMemberid, CharSequence aMessage,
			Boolean aSelf, Boolean aMsg) {
		createNewToken();
		setToken("type", "vehicle");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("message", aMessage);
		setTokenBoolean("MSG", aMsg);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("self", aSelf);
		return getTokenString();
	}
	
	
	public void setToken(String aTokentype, CharSequence aMessage) {
		try {
			mToken.put(aTokentype, aMessage);
		} catch (JSONException e) {
			mMessage = e.getMessage();
		}
	}

	public String createFriendsToken(String aMemberid, String aAcceptedOption) {
		createNewToken();
		setToken("type", "getfriends");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("acceptedoption", aAcceptedOption);
		// token.setTokenArray("names", mFriendsArray[1]);
		setTokenArray("to", "[" + aMemberid + "]"); // mFriendsArray[0]);
		// token.setTokenArray("to", mFriendsArray[0]);
		setTokenBoolean("reply", true);
		jArrayFriends = null;
		return getTokenString();
	}

	public JSONArray getFriends() {
		return jArrayFriends;
	}

	public String createUpdateFriendStatusToken(String aMemberid,
			String aFriendsid, String aTrantype, String aEmail,
			String aFirstname, String aLastname) {
		createNewToken();
		setToken("type", "getpoints");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("friendsid", aFriendsid);
		setToken("type", aTrantype);
		setToken("email", aEmail);
		setToken("fname", aFirstname);
		setToken("lname", aLastname);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		return getTokenString();
	}

	public void updateJson(int position, JSONObject json_data) {
		try {

			jArrayFriends.put(position, json_data);
		} catch (JSONException e) {
			mMessage = e.getMessage();
		}
	}

	public String createSessionToken(String aMemberid, String sessionName,
			String sessionNo) {
		createNewToken();
		setToken("type", "sessionupdate");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("sessionname", sessionName);
		setToken("sessionno", sessionNo);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", true);
		setTokenArray("members", "[]");
		return getTokenString();
	}

	public String createSessionUpDateToken(String _senderid, String _courseid,
			String _newmemberid) {
		createNewToken();
		setToken("type", "session");
		setToken("memberid", _senderid);
		setToken("newmemberid", _newmemberid);
		setToken("courseid", _courseid);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", true);
		setTokenArray("to", "[" + _senderid + "]");
		return getTokenString();
	}

	public String findMember(String _knownas, String _memberid) {
		createNewToken();
		setToken("type", "findmember");
		setToken("memberid", _memberid);
		setToken("knownas", _knownas);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", true);
		setTokenArray("to", "[" + _memberid + "]");
		return getTokenString();
	}

	protected int findIndexInJsonArray(String key, JSONArray array) {
		int len = array.length();
		int index = -1;
		for (int i = 0; i < len; i++) {
			boolean b = array.optJSONObject(i).has(key);
			if (b) {
				index = i;
				break;
			}
		}
		return index;

	}

	public String createLoginToken(String aMemberid) {
		createNewToken();
		setToken("type", "login");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", true);
		setTokenArray("to", "[" + aMemberid + "]");
		return getTokenString();
	}

	public String createUpdateToken(String memberid,String type,String message) {
		createNewToken();
		setToken("type", type);
		setToken("NS", "mapmymotion");
		setToken("memberid", memberid);
		setToken("message", message);
		setTokenBoolean("reply", true);
		setTokenBoolean("self", true);
		setTokenArray("to", "[" + memberid + "]");
		return getTokenString();
	}

	
	public String[] createFriendsArray(String aResponse, Token aToken) {

		String[] mFriendsArray = new String[2];
		JSONArray jArray;
		try {
			jArray = new JSONArray(aResponse);
			mFriendsArray[0] = "[";
			mFriendsArray[1] = "[";
			if (aToken.getSelf().contentEquals("Y")) {
				// mFriendsArray[0]+= aToken.getMemberid()+",";
				// mFriendsArray[1]+="\"me\",";
			}
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject mFriends = jArray.getJSONObject(i);
				mFriendsArray[0] += mFriends.get("memberid") + ",";
				mFriendsArray[1] += "\"" + mFriends.get("fullname") + "\",";
			}
			mFriendsArray[0] = mFriendsArray[0].substring(0,
					mFriendsArray[0].length() - 1)
					+ "]";
			mFriendsArray[1] = mFriendsArray[1].substring(0,
					mFriendsArray[1].length() - 1)
					+ "]";

		} catch (JSONException e) {
			mFriendsArray[0] = "[-1]";
			mFriendsArray[1] = "[\"No Friends\"]";
		}

		return mFriendsArray;

	}
	
	// Create a New Activity
	public String createNewActivityToken(String aMemberid,
			String aActivitytype, String aStartTime, boolean aContinue) {
		createNewToken();
		setToken("type", "ZNW");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("starttime", "" + aStartTime);
		setToken("activitytype", aActivitytype);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		setTokenBoolean("continue", aContinue);

		return getTokenString();

	}

	// Create an Activitiy
	public String createActivityToken(String aMemberid, String aLongitude,
			String aLatitude, String aTime, String aCurrentDistance,
			String aAltitude, String aActivitytype) {
		createNewToken();
		setToken("type", "TEL");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("longitude", aLongitude);
		setToken("latitude", aLatitude);
		setToken("epochtime", aTime);
		setToken("distance", aCurrentDistance);
		setToken("altitude", aAltitude);
		setToken("activitytype", aActivitytype);
		setTokenArray("to", "[" + aMemberid + "]");
		setTokenBoolean("reply", true);
		return getTokenString();
	}

	// Get List of Activities
	public String createActivitiesToken(String aMemberid, int aUOM) {
		createNewToken();
		setToken("type", "getactivities");
		setToken("NS", "mapmymotion");
		setToken("memberid", aMemberid);
		setToken("UOM", Integer.toString(aUOM));
		setTokenArray("to", "[" + aMemberid + "]"); // mFriendsArray[0]);
		setTokenBoolean("reply", true);
		return getTokenString();
	}
	
}