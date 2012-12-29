package com.example.fitsbypact;

import java.util.Set;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import servercommunication.UserCommunication;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends  com.google.android.gcm.GCMBaseIntentService {

	private final static String SENDER_ID = "979881390942";
	private String TAG = "GCMINTENTSERVICE";
	
	public GCMIntentService() {
		super(SENDER_ID);
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// TODO get message from intent and display as a push notification
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.fitsby_logo)
		        .setContentTitle("Fitsby update");
		
		Bundle bundle = intent.getExtras();
		Set<String> keySet = bundle.keySet();
		String content = "";
		for (String key: keySet) {
			content += (" " + bundle.getString(key));
		}
		mBuilder.setContentText(content);
		
		//TODO maybe add intent
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(100, mBuilder.build());
		//TODO maybe switch up id for different purposes
		
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		// TODO Auto-generated method stub
		// TODO send message to danny so that he can map the person to push notifications to 
		Log.v(TAG, "registered " + regId);
		UserCommunication.registerDevice(regId, ((ApplicationUser)getApplicationContext()).getUser().getID()+"");
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		// TODO Auto-generated method stub
		// TODO send message to danny to unregister, possibly
	}

}
