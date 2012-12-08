package com.example.fitsbypact;

import android.content.Context;
import android.content.Intent;

public class GCMIntentService extends  com.google.android.gcm.GCMBaseIntentService {

	public GCMIntentService() {
		
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		// TODO get message from intent and display as a push notification
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		// TODO send message to danny so that he can map the person to push notifications to 
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		// TODO send message to danny to unregister
	}

}
