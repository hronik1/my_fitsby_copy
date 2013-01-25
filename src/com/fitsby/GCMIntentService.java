package com.fitsby;

import java.util.Set;

import com.fitsby.applicationsubclass.ApplicationUser;

import servercommunication.UserCommunication;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
				.setDefaults(Notification.DEFAULT_SOUND)

		        .setSmallIcon(R.drawable.ic_launcher);
		
//		Notification notification = mBuilder.build();
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
//
		Bundle bundle = intent.getExtras();
//		Set<String> keySet = bundle.keySet();
//		String content = "";
//		for (String key: keySet) {
//			Log.i(TAG, key + " " + bundle.getString(key));
//			content += (" " + bundle.getString(key));
//		}
		mBuilder.setContentText(bundle.getString("message_text"));
		Intent clickedIntent = new Intent(GCMIntentService.this, LoggedinActivity.class);
	    if ("newsfeed".equals(bundle.getString("collapse_key"))) {
    	    intent.putExtra(LoggedinActivity.POSITION_KEY,
    	    		LoggedinActivity.NEWSFEED_POSITION);
    	    mBuilder.setContentTitle("New comment");
	    } else if ("game_start".equals(bundle.getString("collapse_key"))) {
	    	mBuilder.setContentTitle("Fitsby");
	    } else {
	    	mBuilder.setContentTitle("Position change");
	    }

	    PendingIntent pendingIntent = PendingIntent.getActivity(GCMIntentService.this, 2, clickedIntent, 0);
	    mBuilder.setContentIntent(pendingIntent);
	    
		Notification notification = mBuilder.build();
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		//TODO maybe add intent
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(100, notification);
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
