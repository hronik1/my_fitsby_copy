package com.fitsby;

import com.fitsby.AccelerometerActivity.IncomingHandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class WalkingFragment extends Fragment {

	private final String TAG = getClass().getName();
	
	private Button mStartButton;
	private Button mStopButton;
	
	private static TextView mStepsTv;
	
	private boolean mIsBound = false;
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
    
	public WalkingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewer = inflater.inflate(R.layout.fragment_walking, container, false);
		
		initializeButtons(viewer);
		initializeTextView(viewer);
		
		return viewer;
	}

	private void initializeButtons(View viewer) {
		mStartButton = (Button) viewer.findViewById(R.id.fragment_walking_start_button);
		mStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAccelerometer();
			}
		});
		
		mStopButton = (Button) viewer.findViewById(R.id.fragment_walking_stop_button);
		mStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAccelerometer();
			}
		});
	}
	
	private void initializeTextView(View viewer) {
		mStepsTv = (TextView) viewer.findViewById(R.id.fragment_walking_tv);
	}
	
	private void startAccelerometer() {
		Intent intent = new Intent(getActivity(), AccelerometerService.class);
		getActivity().startService(intent);
		doBindService();
	}
	
	private void stopAccelerometer() {
		Intent intent = new Intent(getActivity(), AccelerometerService.class);
		doUnbindService();
		getActivity().stopService(intent);
	}
	
    private void doBindService() {
    	Log.d(TAG, "bindService");
        getActivity().bindService(new Intent(getActivity(), AccelerometerService.class), 
        		mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
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
        if (mIsBound) {
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
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }
    
	static class IncomingHandler extends Handler {
		
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AccelerometerService.MSG_SET_VALUE:
                	mStepsTv.setText(msg.arg1+"");
	            break;
                default:
                    super.handleMessage(msg);
            }
        }
	}
}
