package com.fitsby;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class MessengerService extends Service {

	private final static String TAG = "MessengerService";
	private final int NOTIFICATION_ID = 1;
	
	private NotificationManager mNotificationManager;
	private static Messenger mClient;
	private static Timer mTimer;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	public final static int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_VALUE = 3;
    public static final int MSG_START_TIMER = 4;
    public static final int MSG_STOP_TIMER = 5;
    public static final int MSG_SET_GYM = 6;
    public static final String GYM_NAME_KEY = "gymName";
    
    private static int minutes = 0;
    private static int seconds = 0;
    
    private static String gym;
    private static int count = 0;
    
    /**
     * Handler of incoming messages from clients.
     */
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                	registerClient(msg);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClient = null;
                    break;
                case MSG_START_TIMER:
                	startTimer();
                	break;
                case MSG_STOP_TIMER:
                	stopTimer();
                	stopForeground(true);
                	break;
                case MSG_SET_GYM:
                	setGym(msg);
                	createForegroundNotification();
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }
    
    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    
    /**
     * method to provide a binder object for clients to use
     */
	@Override
	public IBinder onBind(Intent arg0) {
		return mMessenger.getBinder();
	}

	
	private void registerClient(Message message) {
		mClient = message.replyTo;
		try {
			Bundle data = new Bundle();
			count++;
			data.putString(GYM_NAME_KEY, gym);
			Message msg = Message.obtain(null, MSG_SET_GYM);
			msg.setData(data);
        	mClient.send(msg);
		} catch (RemoteException e) {
			mClient = null;
		}
	}
	
	/**
	 * starts the timer sending messages to the client
	 */
	private void startTimer() {
		Log.d(TAG, "startTimer");
       	minutes = 0;
    	seconds = 0;
    	mTimer = new Timer();
    	mTimer.scheduleAtFixedRate(new TimerTask() {
    		@Override
    		public void run() {
    			if (seconds >= 59) {
    				seconds = 0;
    				minutes++;
    			} else {
    				seconds++;
    			}
            	if (mClient != null) {
            		try {
                    	mClient.send(Message.obtain(null, MSG_SET_VALUE,
                    			minutes, seconds));
            		} catch (RemoteException e) {
            			mClient = null;
            		}
            	}
            	String minutesText = (minutes < 10 ? "0" + minutes : minutes + "");
            	String secondsText = (seconds < 10 ? "0" + seconds : seconds + "");
        	    Intent intent = new Intent(MessengerService.this, LoggedinActivity.class);
        	    PendingIntent pendingIntent = PendingIntent.getActivity(MessengerService.this, 1, intent, 0);
        	    // Set the Notification UI parameters
        	    Notification notification =  new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Check in at " + gym) 
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentText(minutesText + " : " + secondsText)
                .build();

        	    // Set the Notification as ongoing
        	    notification.flags = notification.flags |
        	                         Notification.FLAG_ONGOING_EVENT;
        	    mNotificationManager.notify(
        	            NOTIFICATION_ID,
        	            notification);
    		}
    	}, 0, 1000);
	}
	
	/**
	 * stops the timer
	 */
	private void stopTimer() {
		Log.d(TAG, "stopTimer");
		if (mTimer != null) {
			mTimer.cancel();
			minutes = 0;
			seconds = 0;
			gym = null;
			try {
				mClient.send(Message.obtain(null, MSG_SET_VALUE,
						minutes, seconds));
			} catch (RemoteException e) {
				mClient = null;
			}
		}
	}
	
	/**
	 * sets the gym name
	 * @param message
	 */
	private void setGym(Message message) {
		Bundle data = message.getData();
		gym = data.getString(GYM_NAME_KEY);
		if (gym != null)
			Log.i(TAG, gym);
		else
			Log.i(TAG, "gym is null");

		
	}
	
	/**
	 * creates the notification for the placing in the foreground
	 */
	private void createForegroundNotification() {
	    // Create an Intent that will open the main Activity
	    // if the notification is clicked.
	    Intent intent = new Intent(this, LoggedinActivity.class);
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);

	    // Set the Notification UI parameters
	    Notification notification =  new NotificationCompat.Builder(this)
        .setContentTitle("Checked in at " + gym) 
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pendingIntent)
        .build();

	    // Set the Notification as ongoing
	    notification.flags = notification.flags |
	                         Notification.FLAG_ONGOING_EVENT;

	    // Move the Service to the Foreground
	    startForeground(NOTIFICATION_ID, notification);
	}
}
