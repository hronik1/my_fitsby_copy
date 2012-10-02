package com.example.fitsbypact;

import java.util.List;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CheckInActivity extends Activity {

	private final static int MESSAGE_START_TIMER = 0;
	private final static int MESSAGE_UPDATE_TIMER = 1;
	private final static int MESSAGE_STOP_TIMER = 2;
	private final static int UPDATE_TIME_MILLIS = 1000; //one second
	
	private final static String TAG = "CheckInActivity";
	
	private NavigationBar navigation;
	private int userID;
	
	private Button checkinButton;
	private Button checkoutButton;
	
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private List<LeagueMember> mLeagueMemberList;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	private Handler mHandler;
	private int timeSeconds;
	private int timeMinutes;

	/**
	 * called when Activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        
        initializeHandler();
        initializeTime();        
        initializeNavigationBar();
        initializeButtons();
        
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mLeagueMemberList = mLeagueMemberTableHandler.getAllLeagueMembersByUserId(mUser.getID());
    }

    /**
     * called when options menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_check_in, menu);
        Log.i(TAG, "onCreateOptionsMenu");
        return true;
    }
    
    /**
	 * called when activity is restarted
	 */
	@Override
	public void onRestart() {
	    super.onRestart();
	
	    Log.i(TAG, "onRestart");
	}
	
	/**
	 * called when activity is starting
	 */
	@Override
	public void onStart() {
	    super.onStart();
	    
	    Log.i(TAG, "onStart");
	}
	
	/**
	 * called when activity resumes
	 */
	@Override
	public void onResume() {
	    super.onResume();
	    
	    Log.i(TAG, "onResume");
	}
	
	/**
	 * called when activity is paused
	 */
	@Override
	public void onPause() {
	    super.onPause();
	
	    Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
	}
	
	/**
	 * called when activity is destroyed
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i(TAG, "onDestroy");
		
	}
	
	/**
	 * 
	 */
	public void initializeTime() {
		timeSeconds = 0;
		timeMinutes = 0;
	}
	
	/**
	 * initializes handler
	 */
	public void initializeHandler() {
		mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message message) {
	            super.handleMessage(message);
	            switch (message.what) {
	            case MESSAGE_START_TIMER:
	                mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIMER, UPDATE_TIME_MILLIS);
	                break;

	            case MESSAGE_UPDATE_TIMER:
	                mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIMER,UPDATE_TIME_MILLIS);
	                //TODO hope jitter isn't a big problem
	                if (++timeSeconds == 60){
	                	timeSeconds = 0;
	                	timeMinutes++;
	                }
	                //TODO update TextView
	                break;
	                
	            case MESSAGE_STOP_TIMER:
	                mHandler.removeMessages(MESSAGE_UPDATE_TIMER);
	                break;

	            default:
	                break;
	            }
	        }
		};
	}
	
	/**
	 * initialized NavigationBar for use
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.games_navigation_bar);
		navigation.setParentActivity(this);
		navigation.turnOffTV("checkin");
	}
	
	/**
	 * initializes buttons on this activity
	 */
	public void initializeButtons() {
		checkinButton = (Button)findViewById(R.id.check_in_button_check_in);
		checkinButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkin();
			}
		});
		
		checkoutButton = (Button)findViewById(R.id.check_in_button_check_out);
		checkoutButton.setOnClickListener(new OnClickListener () {
			public void onClick(View v) {
				checkout();
			}
		});
	}
	
	/**
	 * checks in user
	 */
	public void checkin() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean gpsEnabled = service
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!gpsEnabled) {
			//TODO make alert dialog to prompt user to turn on GPS 
		} else {
			mHandler.sendEmptyMessage(MESSAGE_START_TIMER);
			for(LeagueMember member: mLeagueMemberList) {
				if(member.getCheckins() == member.getCheckouts()) {
					member.setCheckins(member.getCheckins() + 1);
					mLeagueMemberTableHandler.updateLeagueMember(member);
				}
			}
		}
	}
	
    /**
     * shows AlertDialog
     */
    public void showAlertDialog() {
    	Log.i(TAG, "onCreateDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("GPS location is required to make sure you are at a gym(not like you would lie)./nWould you like to turn it on?")
    			.setCancelable(false)
    			.setPositiveButton("Yup!", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					try {
    					  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    					  startActivity(intent);
    					} catch (Exception e) {
    						//TODO make a more better error message
    						Toast.makeText(CheckInActivity.this, "Sorry could not change your GPS at the moment", Toast.LENGTH_LONG).show();
    					}
    				}
    			})
    			.setNegativeButton("I'll pass", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
	/**
	 * checks out user
	 */
	public void checkout() {
		mHandler.sendEmptyMessage(MESSAGE_STOP_TIMER);
		for(LeagueMember member: mLeagueMemberList) {
			if(member.getCheckins() == member.getCheckouts() + 1) {
				member.setCheckouts(member.getCheckins());
				mLeagueMemberTableHandler.updateLeagueMember(member);
			}
		}
		
	}
}
