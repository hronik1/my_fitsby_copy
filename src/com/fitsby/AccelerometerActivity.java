package com.fitsby;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AccelerometerActivity extends Activity {

	private final String TAG = getClass().getName();
	
	private Button startButton;
	private Button stopButton;
	
	private static TextView stepsTv;
	
	private boolean isBound = false;
	private Messenger mService;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	Log.d(TAG, "service connected");
            mService = new Messenger(service);

            try {
                Message msg = Message.obtain(null,
                        AccelerometerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            	Log.e(TAG, e.toString());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        	Log.d(TAG, "service disconnected");
            mService = null;

        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accelerometer);
		
		Log.i(TAG, "onCreate");
		
		initializeButtons();
		initializeTextView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accelerometer, menu);
		return true;
	}

	private void initializeButtons() {
		startButton = (Button) findViewById(R.id.accelerometer_start_button);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAccelerometer();
			}
		});
		
		stopButton = (Button) findViewById(R.id.accelerometer_stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAccelerometer();
			}
		});
	}
	
	private void initializeTextView() {
		stepsTv = (TextView) findViewById(R.id.accelerometer_steps_tv);
	}
	
	private void startAccelerometer() {
		Intent intent = new Intent(this, AccelerometerService.class);
		startService(intent);
		doBindService();
	}
	
	private void stopAccelerometer() {
		Intent intent = new Intent(this, AccelerometerService.class);
		doUnbindService();
		stopService(intent);
	}
	
    private void doBindService() {
    	Log.d(TAG, "bindService");
        bindService(new Intent(this, AccelerometerService.class), 
        		mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
        if (mService != null) {
        	try {
        		Message msg = Message.obtain(null,
        				AccelerometerService.MSG_REGISTER_CLIENT);
        		msg.replyTo = mMessenger;
        		mService.send(msg);
        	} catch (RemoteException e) {
        		Log.e(TAG, e.toString());
        	}
        }
          
    }

    private void doUnbindService() {
        if (isBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            AccelerometerService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            isBound = false;
        }
    }
	static class IncomingHandler extends Handler {
		
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AccelerometerService.MSG_SET_VALUE:
                	stepsTv.setText(msg.arg1+"");
	            break;
                default:
                    super.handleMessage(msg);
            }
        }
	}
}
