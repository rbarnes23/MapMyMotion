package com.mapmymotion;


import com.mapmymotion.R;
import com.mapmymotion.services.ChatService;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MotionApplication extends Application {
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		AppSettings.setContext(getApplicationContext());
		//Initialize the preference manager
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// start the service from here //ChatService is your service class name
		Bundle bundle = new Bundle();
		bundle.putString("memberid", AppSettings.getMemberid());
		bundle.putString("serverip", "96.31.86.129");//AppSettings.getNetworkid());
		bundle.putInt("serverport", 2002);
		Intent commIntent = new Intent(getApplicationContext(), ChatService.class);
		commIntent.putExtras(bundle);
		startService(commIntent);

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Intent commIntent = new Intent(getApplicationContext(),
				ChatService.class);
		if(commIntent!=null){
	
		stopService(commIntent);
		commIntent=null;
		}
	}
	

}