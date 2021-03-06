package com.fitsby;

import servercommunication.UserCommunication;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fitsby.applicationsubclass.ApplicationUser;

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
		
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			//TODO handle not having a bundle more elegantly
			return;
		}
		String messageText = bundle.getString("message_text");
		
		mBuilder.setContentText(messageText);
		Intent clickedIntent = new Intent(GCMIntentService.this, LoggedinActivity.class);
		String collapseKey = bundle.getString("collapse_key");
	    if ("newsfeed".equals(collapseKey)) {
    	    intent.putExtra(LoggedinActivity.POSITION_KEY,
    	    		LoggedinActivity.NEWSFEED_POSITION);
    	    mBuilder.setContentTitle("New comment");
	    } else if ("game_start".equals(collapseKey)) {
	    	mBuilder.setContentTitle("Fitsby");
	    } else if ("no_check_in".equals(collapseKey)) {
	    	//TODO this could be changed
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
