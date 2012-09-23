package com.example.fitsbypact;

import java.util.List;

import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CheckInActivity extends Activity {

	private final static String TAG = "CheckInActivity";
	
	private NavigationBar navigation;
	private int userID;
	
	private Button checkinButton;
	private Button checkoutButton;
	
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private List<LeagueMember> mLeagueMemberList;
	private int userId;
	/**
	 * called when Activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Log.i(TAG, "onCreate");
        
        Intent intent = getIntent();
        if(intent == null || intent.getExtras() == null)
        	userID = -1;
        else
        	userID = intent.getExtras().getInt(User.ID_KEY);
        
        initializeNavigationBar();
        initializeButtons();
        
        mdbHandler = DatabaseHandler.getInstance(this);
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mLeagueMemberList = mLeagueMemberTableHandler.getAllLeagueMembersByUserId(userId);
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
	 * initialized NavigationBar for use
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.games_navigation_bar);
		navigation.setParentActivity(this);
		navigation.setUserID(userID);
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
		for(LeagueMember member: mLeagueMemberList) {
			if(member.getCheckins() == member.getCheckouts()) {
				member.setCheckins(member.getCheckins() + 1);
				mLeagueMemberTableHandler.updateLeagueMember(member);
			}
		}
	}
	
	/**
	 * checks out user
	 */
	public void checkout() {
		for(LeagueMember member: mLeagueMemberList) {
			if(member.getCheckins() == member.getCheckouts() + 1) {
				member.setCheckouts(member.getCheckins());
				mLeagueMemberTableHandler.updateLeagueMember(member);
			}
		}
	}
}
