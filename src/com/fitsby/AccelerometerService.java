package com.fitsby;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.hardware.SensorEventListener;

public class AccelerometerService extends Service {
	
	private final String TAG = getClass().getName();
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private StepListener mStepListener;
	
	private int stepCount;
	
	private final int STEP_MAGNITUDE_THRESHOLD = 1;
	private final int CONSECUTIVE_THRESHOLD = 10;
	
	private boolean inStep = false;
	private int consecutiveHigh = 0;
	private int consecutiveLow = 0;

	public final static int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SET_VALUE = 3;
	
	private static Messenger mClient;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	public AccelerometerService() {
		Log.i(TAG, "ctor");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i(TAG, "onDestroy");
		
		mSensorManager.unregisterListener(mStepListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int ret = super.onStartCommand(intent, flags, startId);

		Log.i(TAG, "onStartCommand");

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mStepListener = new StepListener();
		mSensorManager.registerListener(mStepListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		
		return ret;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	private class StepListener implements SensorEventListener {

		private final String TAG = getClass().getName();
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			double magnitude = magnitude(new float[]{x,y,z});
			if (inStep) {
				if (magnitude > STEP_MAGNITUDE_THRESHOLD) {
					consecutiveLow = 0;
				} else {
					consecutiveLow++;
					if (consecutiveLow == CONSECUTIVE_THRESHOLD) {
						inStep = false;
						consecutiveLow = 0;
						consecutiveHigh = 0;
					}
				}
			} else {
				if (magnitude > STEP_MAGNITUDE_THRESHOLD) {
					consecutiveHigh++;
					consecutiveLow++;
					if (consecutiveHigh == CONSECUTIVE_THRESHOLD) {
						inStep = true;
						consecutiveLow = 0;
						consecutiveHigh = 0;
						stepCount++;
		            	if (mClient != null) {
		            		try {
		                    	mClient.send(Message.obtain(null, MSG_SET_VALUE,
		                    			stepCount, stepCount));
		            		} catch (RemoteException e) {
		            			mClient = null;
		            		}
		            	}
					}
				} else {
					consecutiveHigh = 0;
				}
			}
		}
			
		private double magnitude(float[] values) {
			double squaredSum = 0;
			for (float value: values)
				squaredSum += (value*value);
			return Math.sqrt(squaredSum);
		}
		
	}
		
	private void registerClient(Message message) {
		mClient = message.replyTo;
	}
	
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
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
}
