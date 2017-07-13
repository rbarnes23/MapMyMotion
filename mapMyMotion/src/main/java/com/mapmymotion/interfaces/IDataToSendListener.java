package com.mapmymotion.interfaces;

import android.os.Message;
 
public interface IDataToSendListener {
    public void sendDataToBottomDrawer(Message msg);
	public void sendDataToRightDrawer(Message msg);
    public void sendDataToTopDrawer(Message msg);
    public void sendDataToLeftDrawer(Message msg);

}