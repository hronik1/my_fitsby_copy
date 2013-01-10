package com.fitsby;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

	private final static String TAG = "MessengerService";
	
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
    static class IncomingHandler extends Handler {
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
                	break;
                case MSG_SET_GYM:
                	setGym(msg);
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
       // mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    
    /**
     * method to provide a binder object for clients to use
     */
	@Override
	public IBinder onBind(Intent arg0) {
		return mMessenger.getBinder();
	}

	
	static private void registerClient(Message message) {
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
	static private void startTimer() {
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
    		}
    	}, 0, 1000);
	}
	
	/**
	 * stops the timer
	 */
	static private void stopTimer() {
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
	static private void setGym(Message message) {
		Bundle data = message.getData();
		gym = data.getString(GYM_NAME_KEY);
		if (gym != null)
			Log.i(TAG, gym);
		else
			Log.i(TAG, "gym is null");

		
	}
}
