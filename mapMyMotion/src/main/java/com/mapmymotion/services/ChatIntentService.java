package com.mapmymotion.services;



import android.app.IntentService;
import android.content.Intent;

public class ChatIntentService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";

	public ChatIntentService() {
		super("ChatIntentService");
	}
	@Override
	public void onCreate(){
		//Startmy thing
		super.onCreate();
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
//		String msg = intent.getStringExtra(PARAM_IN_MSG);
//		 while (true) {
//		 SystemClock.sleep(3000); // 30 seconds
//		 String resultTxt = msg
//		 + " "
//		 + DateFormat.format("MM/dd/yy h:mmaa",
//		 System.currentTimeMillis());
//		
//		 // processing done here?.
//		 Intent broadcastIntent = new Intent();
//		 broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
//		 broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//		 broadcastIntent.putExtra(PARAM_OUT_MSG, resultTxt);
//		 sendBroadcast(broadcastIntent);
//		 }
	
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
	}
}
