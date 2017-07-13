package com.mapmymotion.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.MainActivity;
import com.mapmymotion.utilities.Utils;
//import com.securitylibrary.SecurityLibrary;
import com.tokenlibrary.Token;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * This service is for communications.
 * 
 * @author Rick Barnes
 * @version 1.0
 */
public class ChatService extends Service implements Runnable {
	private  String mServerHostName;
	private  int mServerPort;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private static StringBuffer toSend = new StringBuffer("");
	private static Socket mSocket;
	private String mErrorMessage;
	private boolean mConnected = false;
	private  String mMemberid;
	private static Looper looper;
	// used for getting the handler from other class for sending messages
	public static Handler mServiceHandler = null;
	// used for keep track on Android running status
	public static Boolean mIsServiceRunning = false;
	private String encryptionDecryptionKey = "4455414744176343";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		if (mServiceHandler == null) {
			// Handle incoming messages
			mServiceHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case Constants.UPDATEDATA:
						if (mConnected) {
							// create token
							Bundle bundle = msg.getData();
							String msgFromActivity = bundle
									.getString("message");
							Token token = new Token();
							String msgtoSend = token.createUpdateToken(
									AppSettings.getMemberid(), "MOTIONACTIVITY",
									msgFromActivity);
							sendData(msgtoSend);
						}
						break;

					default:
						break;
					}
				}
			};
		}

		// Toast.makeText(this, "ChatService Created",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Utils.setToast(this, "Chat Service Started", Toast.LENGTH_SHORT); 
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		mMemberid = bundle.getString("memberid");
		mServerHostName = bundle.getString("serverip");
		mServerPort = bundle.getInt("serverport");

		// Try to connect to Network if fails then thread will die
		if (!mConnected) {
			// Starts the rUn Thread
			Thread networkingThread = new Thread(this);
			networkingThread.start();
		}

		mIsServiceRunning = true; // set service running status = true
	Utils.setToast(this,"Communication Service Started",
				Toast.LENGTH_SHORT);
		// We need to return if we want to handle this service explicitly.
		return START_STICKY;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cleanUp();
		Utils.setToast(this,"Chat Service Stopped", Toast.LENGTH_SHORT);
		// Toast.makeText(this, "Chat Service Stopped",
		// Toast.LENGTH_SHORT).show();
		mIsServiceRunning = false; // make it false, as the service is already
									// destroyed.

	}

	/*
	 * This method Updates Main Activity with the message.
	 * 
	 * @param message
	 *            - The message to be updated.
	 * @return true false.
	 * @exception None.
	 * @see None
	 */
	private boolean sendUpdateMessage(Message msgToActivity) {

		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

	/**
	 * This method gets Status of the chat service.
	 * 
	 * @param None
	 *            .
	 * @return String Status Message
	 * @exception None.
	 * @see None
	 */

	public String getStatus() {
		return mErrorMessage;
	}

	public Boolean isConnected() {
		mConnected = false;
		if (mSocket != null) {
			mConnected = mSocket.isConnected();
		}
		return mConnected;
	}

	public String cleanUp() {
		mConnected = false;
		try {
			if (in != null) {
				// mSocket.shutdownInput();
				in.close();
				mErrorMessage = "Input Stream Closed";
			}
		} catch (IOException e) {
			mErrorMessage = e.getMessage();
		}

		if (out != null) {
			// mSocket.shutdownOutput();
			out.close();
			mErrorMessage = "OutPut Stream Closed";
		}

		try {
			if (mSocket != null) {
				mSocket.close();
				mErrorMessage = "DISCONNECTED";
			}
		} catch (IOException e) {
			mErrorMessage = e.getMessage();
		}
		try {
			stopSelf();

			this.finalize();
		} catch (Throwable e) {
			mErrorMessage = e.getMessage();
		}
		looper.quit();

		return mErrorMessage;
	}

	/**
	 * This method adds text to send-buffer.
	 * 
	 * @param String
	 *            Message to send
	 * @return None.
	 * @exception None.
	 * @see None
	 */

	// Add text to send-buffer
	public static void sendData(String str) {
		synchronized (toSend) {
			toSend.append(str + "\n");
		}

	}

	// ///////////////////////////////////////////////////////////////

	@Override
	public void run() {
		connectToNetwork();
		Looper.prepare();
		// if (mServiceHandler==null){
		// // Handle incoming messages
		// mServiceHandler = new Handler() {
		// public void handleMessage(Message msg) {
		// switch (msg.what) {
		// case Constant.X204:case Constant.X990:case Constant.X997:case
		// Constant.X210:
		// if (mConnected) {
		// // add the status which came from service and show on
		//
		// String msgFromActivity = msg.obj.toString();
		// // String ivs = "12345678";
		// // String test=SecurityLibrary.encryptString(msgFromActivity,
		// encryptionDecryptionKey, ivs);
		// // test=SecurityLibrary.decryptString(msgFromActivity,
		// encryptionDecryptionKey, ivs);
		// sendData(msgFromActivity); // Send data to server
		// }
		// //sendUpdateMessage(msg);// Notify//
		// break;
		// default:
		// break;
		// }
		// }
		// };
		// }
		while (mConnected) {
			try {
				Thread.sleep(100);
				// Send data
				if (toSend.length() != 0) {
					out.print(toSend);
					out.flush();
					toSend.setLength(0);
				}

				// Receive data
				if (in.ready()) {
					String message = in.readLine();
					String ivs = "12345678";
					// String mac =SecurityLibrary.getMACAddress();

					// String test=SecurityLibrary.encryptString(message,
					// encryptionDecryptionKey, ivs);
					// test=SecurityLibrary.decryptString(test,
					// encryptionDecryptionKey, ivs);
					// Do the encryption stuff at the token level so we can get
					// the isa6&8 info first
					// byte[]
					// testmessage=SecurityLibrary.encrypt(message.getBytes("UTF-8"),
					// encryptionDecryptionKey.getBytes("UTF-8"),
					// ivs.getBytes("UTF-8"));
					// String encmessage = new String(testmessage, "UTF-8");
					// testmessage = SecurityLibrary.decrypt(testmessage,
					// encryptionDecryptionKey.getBytes("UTF-8"),
					// ivs.getBytes("UTF-8"));
					// message = new String(testmessage, "UTF-8");
					if (message.length() > 0) {
						Message msgToActivity = mServiceHandler.obtainMessage();
						JSONObject jMessage = new JSONObject(message);
						String type = jMessage.getString("type");
						if (type.contentEquals("edi")) {
//							msgToActivity.what = Constant.LOADDATA;
						} else {
							msgToActivity.what = Constants.MESSAGE;
						}
						Bundle bundle = new Bundle();
						bundle.putString("message", message);
						msgToActivity.setData(bundle);
						sendUpdateMessage(msgToActivity);
					}
				}
			} catch (IOException e) {
				mErrorMessage = e.getMessage();// "Connection to server broken.";
				connectToNetwork();
				break;
			} catch (InterruptedException e) {
				mErrorMessage = "Interrrupted";
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Looper.loop();
	}

	// Connect to Network and Login
	private boolean connectToNetwork() {
		for (int i = 0; i < 10; i++) {
			try {
				// Connect to Chat Server
				mSocket = new Socket(mServerHostName, mServerPort);

				if (mSocket.isConnected()) {

					in = new BufferedReader(new InputStreamReader(
							mSocket.getInputStream()));
					out = new PrintWriter(new OutputStreamWriter(
							mSocket.getOutputStream()));
					mErrorMessage = "Connected to " + " " + mServerHostName;
					mConnected = true;
					// Login
					Token loginToken = new Token();
					sendData(loginToken.createLoginToken(mMemberid));
					break;
				}
			} catch (IOException ioe) {
				// mErrorMessage = ioe.getMessage() + " " + SERVER_HOSTNAME;// +
				mErrorMessage = "Network not available.  Try Again in a few minutes.";
				mConnected = false;
			}
		}
		// Message msgToActivity =
		// mServiceHandler.obtainMessage(Constant.CONNECTED,mErrorMessage);
		// sendUpdateMessage(msgToActivity);
		return mConnected;
	}
}