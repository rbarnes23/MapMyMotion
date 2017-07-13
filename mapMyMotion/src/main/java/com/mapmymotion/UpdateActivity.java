package com.mapmymotion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.os.AsyncTask;

public class UpdateActivity {
	private String mId;
	public String UpdateActivity(final String listToUpdate) {
		try {
			ExecutorService executor = Executors.newFixedThreadPool(5);

			mId = new UpdateActivityTask().executeOnExecutor(executor,
					listToUpdate).get();
			executor.shutdown();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mId;
	}
}

class UpdateActivityTask extends AsyncTask<String, Void, String> {
	String myid = "-1";

	@Override
	protected String doInBackground(String... params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("updatedata", params[0]));

		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/ActivityUpdate.php");
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
				sb.append(line);
			}
			is.close();

			result = sb.toString();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}
		myid = result;
		return myid;
	}

}
