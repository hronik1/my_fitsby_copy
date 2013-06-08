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
import android.util.Log;

/**
 * MessengerService is a service responsible for keeping track of the length of
 * time and where a user is checked in, as well as publishing updates to
 * notifications and the checkinfragment.
 * 
 * @author brenthronk
 *
 */
public class MessengerService extends Service {

	/**
	 * Tag used for logcat messages.
	 */
	private final static String TAG = "MessengerService";
	/**
	 * Id of the notification, used to publish updates to it.
	 */
	private final int NOTIFICATION_ID = 1;
	
	private NotificationManager mNotificationManager;
	private static Messenger mClient;
	private static Timer mTimer;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	/**
	 * Passed by client to signify it wants to register.
	 */
	public final static int MSG_REGISTER_CLIENT = 1;
	/**
	 * Passed by client to signify it wants to unregister.
	 */
    public static final int MSG_UNREGISTER_CLIENT = 2;
    /**
     * Passed to client to signify that it should set the value passed.
     */
    public static final int MSG_SET_VALUE = 3;
    /**
     * Passed by client to signify that this should start the timer.
     */
    public static final int MSG_START_TIMER = 4;
    /**
     * Passed by client to signify that this should start the timer.
     */
    public static final int MSG_STOP_TIMER = 5;
    /**
     * Passed by client to signify that this should set the gym.
     */
    public static final int MSG_SET_GYM = 6;
    /**
     * Key for passing in the gym name.
     */
    public static final String GYM_NAME_KEY = "gymName";
    
    /**
     * Used to keep track of the minutes since checking in.
     */
    private static int minutes = 0;
    /**
     * Used to keep track of the seconds within a minute since checking in.
     */
    private static int seconds = 0;
    
    /**
     * The name of the gym where the user is checked in.
     */
    private static String gym;
    
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
    

    /**
     * Callback for recieving the start command.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }
    
    /**
     * Callback for the creation of the service.
     */
    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    
    /**
     * Callback for client trying to bind to service.
     */
	@Override
	public IBinder onBind(Intent arg0) {
		return mMessenger.getBinder();
	}

	/**
	 * This registers a client and stores the gym name.
	 * 
	 * @param message	contains client information for registering
	 */
	private void registerClient(Message message) {
		mClient = message.replyTo;
		try {
			Bundle data = new Bundle();
			data.putString(GYM_NAME_KEY, gym);
			Message msg = Message.obtain(null, MSG_SET_GYM);
			msg.setData(data);
        	mClient.send(msg);
		} catch (RemoteException e) {
			mClient = null;
		}
	}
	
	/**
	 * Starts the timer and starts sending messages to the client.
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
        	    intent.putExtra(LoggedinActivity.POSITION_KEY,
        	    		LoggedinActivity.CHECK_IN_POSITION);
        	    PendingIntent pendingIntent = PendingIntent.getActivity(MessengerService.this, 1, intent, 0);
        	    // Set the Notification UI parameters
        	    Notification notification =  new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Checked in at " + gym) 
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
	 * Stops the timer and stops sending updates to client.
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
	 * Sets the gym name.
	 * 
	 * @param message	message containing the gym name
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
	 * Creates the notification for the placing in the foreground.
	 */
	private void createForegroundNotification() {
	    // Create an Intent that will open the main Activity
	    // if the notification is clicked.
	    Intent intent = new Intent(this, LoggedinActivity.class);
	    intent.putExtra(LoggedinActivity.POSITION_KEY,
	    		LoggedinActivity.CHECK_IN_POSITION);
	    
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
