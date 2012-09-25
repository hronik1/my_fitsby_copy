package com.example.fitsbypact;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbtables.User;
import widgets.NavigationBar;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MeActivity extends Activity {

	private final static String TAG = "MeActivity";
	
	private TextView nameTV;
	private TextView joinTV;
	private TextView earningsTV;
	
	private NavigationBar navigation;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        initializeNavigationBar();
        initializeTextViews();
    }

    /**
     * called when optionsmenu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_me, menu);
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
		navigation.turnOffTV("me");
	}
	
	/**
	 * initialize the textviews
	 */
	public void initializeTextViews() {
		nameTV = (TextView)findViewById(R.id.me_textview_name);
		nameTV.setText(mUser.getFirstName() + " " + mUser.getLastName());
		
		joinTV = (TextView)findViewById(R.id.me_textview_join_date);
		//TODO add join date
		
		earningsTV = (TextView)findViewById(R.id.me_textview_total_money_earned_money);
		//add earnings for user
	}
}
