package com.mapmymotion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.mapmymotion.services.ChatService;
import com.mapmymotion.services.LocationIntentService;
import com.mapmymotion.utilities.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gov.nasa.worldwind.util.EGM96;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//import android.content.IntentFilter;
//import android.support.v4.widget.DrawerLayout;
//import com.mapproject.services.LocationUpdateService;
//import com.mapproject.services.TCPClient;

//import com.securitylibrary.SecurityLibrary;

@SuppressLint("NewApi")
public class MainActivity extends CustomFragmentActivity {
    private static EGM96 egm96;
    // private JSONObject jActivity;
    private MqttAndroidClient client;
    private String mStatus;
    String clientId = MqttClient.generateClientId();

    public static Handler mUiHandler = null;
    String mMessage;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static EGM96 getEgm96() {
        return egm96;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sContext = this;

//		try {
//			DBUtils.importData(getApplicationContext(), "motion");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}


        // DBUtils.importDB(getContext());
        // MotionDataSource mds=new MotionDataSource(this);
        // mds.deleteAllActivities();

        getPreferences(this); // MOVED TO MOTIONAPPLICATION

        // Creates initial memberid if it does not exist
        AppSettings.createMemberid();

        try {
            String egmFile = Utils.copyFileFromAssetandSave(
                    getApplicationContext(), "geo", "WW15MGH.DAC",
                    "mapmymotion", "WW15MGH.DAC");
            egm96 = new EGM96(egmFile);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // String
        // jack="{\"to\":[183,2],\"reply\":true,\"self\":true,\"NS\":\"mapmymotion\",\"memberid\":\"5\",\"type\":\"login\",\"_id\":\"1348877065\"}";
        // mTcpClient.sendMessage(jack);
        // Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization
        // completes.
        // mTts = new TextToSpeech(this, this);

        Drawable b = Utils.writeOnCircle(getResources(),
                getString(R.string.app_name), 80, 80, 20, Color.WHITE);


        // Create the handler to update user interface from the service
        mUiHandler = new Handler() // Receive messages from service class
        {
            @Override
            public void handleMessage(Message msg) {
                // Bundle bundle = new Bundle(msg.getData());
                if (msg.getData() == null) {
                    return;
                }
                // mMenu.findItem(R.id.replaySpeed).setVisible(false);
                switch (msg.what) {

                    case 0:
                        // return;
                        break;
                    case Constants.LOCATIONNOTENABLED:
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        break;
                    case Constants.START:
                        sendDataToTopDrawer(msg);
                        break;
                    case Constants.STOP:
                        sendDataToTopDrawer(msg);
                        break;

                    case Constants.AUTOSTART:
                        sendDataToTopDrawer(msg);
                        break;
                    case Constants.AUTOSTOP:
                        sendDataToTopDrawer(msg);
                        break;
                    case Constants.CHARTVIEW:
                        sendDataToRightDrawer(msg);
                        ;
                        break;
                    case Constants.SYNCDATA:
                        sendDataToRightDrawer(msg);
                        break;
                    case Constants.UPDATEDATA:
                        sendChatMessage(msg);
                        sendPayload(msg.toString(), 1);
                        // sendDataToRightDrawer(msg);
                        // String message = bundle.getString("message");
                        // message="{\"to\":[183,2,124],\"reply\":true,\"self\":true,\"NS\":\"mapmymotion\",\"memberid\":\"5\",\"type\":\"getmessage\",\"_id\":\"1348877065\"}";
                        // Token token = new Token();
                        // message = token.createUpdateToken(
                        // AppSettings.getMemberid(), "MOTIONACTIVITY",
                        // message);
                        // sendMessage(message);
                        // Utils.setToast(MainActivity.this, message,
                        // Toast.LENGTH_LONG);
                        break;
                    case Constants.SETTINGS:
                        sendDataToTopDrawer(msg);
                        break;
                    case Constants.REPLAY:
                        closeAllDrawers();
                        sendDataToTopDrawer(msg);
                        // mMenu.findItem(R.id.replaySpeed).setVisible(true);

                        break;
                    case Constants.ACTIVITIES:
                        sendDataToTopDrawer(msg);
                        // Dont need to update the right drawer as will populate
                        // from database
                        // sendDataToRightDrawer(msg);
                        break;
                }
            }
        };

        setContentView(R.layout.activity_main);
        initializeActionBar();


        // Start Location Service
        Intent locationIntent = new Intent(getApplicationContext(),
                LocationIntentService.class);

        startService(locationIntent);

        // Set up MQTT
        String MQTTHOST = "tcp://mapmymotion.com";
        String USERNAME = "rbarnes";
        String PASSWORD = "sasha23";

        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
                clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    mStatus = getString(R.string.connected);
                    Toast.makeText(MainActivity.this, mStatus, Toast.LENGTH_LONG).show();
                    sendPayload(mStatus, 0);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    mStatus = getString(R.string.notconnected);
                    Toast.makeText(MainActivity.this, mStatus, Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // DBUtils.exportDB(sContext);

    }

    @Override
    protected void onStop() {
        super.onStop();

        // // Start Chat Service
        // Intent chatIntent = new Intent(this, ChatService.class);
        // stopService(chatIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // LocationManager locationManager = (LocationManager)
        // getSystemService(LOCATION_SERVICE);
        // if (!locationManager.isProviderEnabled("gps")) {
        // startActivityForResult(new Intent(
        // android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
        // Constants.LOCATIONNOTENABLED);
        // } else {
        // }
        // // Start Chat Service
        // Intent chatIntent = new Intent(getApplicationContext(),
        // ChatService.class);
        // chatIntent.putExtra("serverip", "192.168.1.6");
        // chatIntent.putExtra("serverport", 2002);
        // chatIntent.putExtra("memberid", AppSettings.getMemberid());
        // startService(chatIntent);

        // String strInputMsg = "I was here";
        // mChatIntentService = new Intent(this, ChatIntentService.class);
        // mChatIntentService
        // .putExtra(ChatIntentService.PARAM_IN_MSG, strInputMsg);
        // startService(mChatIntentService);
        // IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        // filter.addCategory(Intent.CATEGORY_DEFAULT);
        // mResponseReceiver = new ResponseReceiver();
        // registerReceiver(mResponseReceiver, filter);
        //
        //
        // Instantiate tcp - listener in the customfragment
        // ExecutorService executor = Executors.newSingleThreadExecutor();
        // new connectTask().executeOnExecutor(executor, "");
        // executor.shutdown();
        // if (AppSettings.getEmailAddress().length() > 0) {
        // // setToast(mEmail, SHORT);
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // Token loginToken = new Token();
        // String mMemberid = AppSettings.getMemberid();
        // // String message = loginToken.createLoginToken(mMemberid);
        // // String mMemberid="5";
        // sendNewMessage(loginToken.createLoginToken(mMemberid));
        // }

    }

    public static Context getContext() {
        return sContext;
    }

    public void ClickHandler(View v) {
        // Utils.setToast(getContext(), "POS: " + v.getId(),
        // Toast.LENGTH_SHORT);
        // FragmentManager fm = getSupportFragmentManager();
        // FragmentTransaction ft = fm.beginTransaction();
        // Fragment currentFragment = fm.findFragmentById(R.id.right_drawer);
        // DrawerLayout drawerLayout = (DrawerLayout)
        // findViewById(R.id.drawer_layout);
        closeAllDrawers();
        // mMenu.findItem(R.id.save).setVisible(false);

        switch (v.getId()) {

            case R.id.quit:
                // Quit Application
                onBackPressed();
                break;

        }

    }

    void sendPayload(String payload, int qos) {
        String topic = "BARNES123";
        //byte[] encodedPayload; //= new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(payload.getBytes("UTF-8"));
            message.setQos(qos);
            client.publish(topic, message);
            //client.publish(topic, message.getBytes(), 0, false);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    // send message to service
    public void sendChatMessage(Message msg) {
        // only we need a handler to send message to any component.
        // here we will get the handler from the service first, then
        // we will send a message to the service.

        if (null != ChatService.mServiceHandler) {
            Message msgToChat = ChatService.mServiceHandler
                    .obtainMessage(Constants.UPDATEDATA);
            msgToChat.setData(msg.getData());
            ChatService.mServiceHandler.sendMessage(msgToChat);
        }
    }

}
