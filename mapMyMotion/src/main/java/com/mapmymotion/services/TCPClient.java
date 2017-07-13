/**
 * 
 */
package com.mapmymotion.services;

/**
 * @author Rick
 *
 */
//import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import com.mapmymotion.interfaces.IMessageReceivedListener;
 
 
public class TCPClient {
     private String serverMessage;
//	public static final String SERVERIP = AppSettings.getNetworkid();

    public static final String SERVERIP = "96.31.86.129"; //your computer IP address
//     public static final String SERVERIP = "192.168.1.6"; //your computer IP address

     public static final int SERVERPORT = 2002;
    private IMessageReceivedListener mMessageListener = null;
    private boolean mRun = false;
    private Socket mSocket;
 
    PrintWriter out;
    BufferedReader in;

 
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(IMessageReceivedListener listener) {
        mMessageListener = listener;
    }
 
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }
 
    public void stopClient(){
        mRun = false;
    }
    
    //The local port is transmitted to the server to assist in identification of this user in ClientInfo
	public String getLocalSocketId() {
		return Integer.toHexString(mSocket.getLocalPort());
	}

    public void run() {
 
        mRun = true;
 
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);
 
 //           Log.e("TCP Client", "C: Connecting...");
 
            //create a socket to make the connection with the server
            mSocket = new Socket(serverAddr, SERVERPORT);

            try {
 
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
 
 //               Log.e("TCP Client", "C: Sent.");
 
 //               Log.e("TCP Client", "C: Done.");
 
                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
 
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();
 
                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
 
                }
 
 
   //             Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
 
 
            } catch (Exception e) {
 e.getMessage();
//                Log.e("TCP", "S: Error", e);
 
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                mSocket.close();
            }
 
        } catch (Exception e) {
 e.getMessage();
//            Log.e("TCP", "C: Error", e);
 
        }
 
    }
 
}
