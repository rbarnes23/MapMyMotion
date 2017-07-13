package com.mapmymotion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class UpdateActivityTest {
private static String mId;
	public static String UpdateActivityTest(List<NameValuePair> nameValuePairs){
		try {
			mId=new UpdateActivityTaskTest().execute(nameValuePairs).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mId;
	}
}

class UpdateActivityTaskTest extends AsyncTask<List<NameValuePair>, Void, String> {
	String myid = "-1";

	@Override
	protected String doInBackground(List<NameValuePair>... params) {
		List<NameValuePair> a= params[0];
		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/myactivities2.php");
			httppost.setEntity(new UrlEncodedFormEntity(params[0]));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, "iso-8859-1"), 8);
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
			myid = json_data.getString("id");
		} catch (JSONException e) {
			myid = "-1";
		}
		return myid;
	}

}
