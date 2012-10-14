package com.example.fitsbypact;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbtables.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;

public class LeagueLandingActivity extends Activity {

	private final static String TAG = "LeagueLandingActivity";
	
	private Button buttonJoin;
	private Button buttonCreate;
	
	private int userID;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_landing);
        Log.i(TAG, "onCreate");
        
        initializeButtons(); 
    }

    /**
     * called when menu is created for the first time
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_landing, menu);
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Game Landing Activity");
        Log.i(TAG, "onStart");
    }
    
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
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
     * initializes the buttons 
     */
    private void initializeButtons() {
    	buttonJoin = (Button)findViewById(R.id.league_landing_button_join);
    	buttonJoin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			join();
    		}
    	});
    	
     	buttonCreate = (Button)findViewById(R.id.league_landing_button_create);
    	buttonCreate.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			goToLeagueCreatePage();
    		}
    	});
    }
    
    /**
     * sends user to join page
     */
    private void join() {
    	try {
    		Intent intent = new Intent(this, LeagueJoinActivity.class);
    		startActivity(intent);
    	} catch (Exception e) {
    		//TODO add some robustness
    		Toast toast = Toast.makeText(LeagueLandingActivity.this, "Sorry can not perform at the moment", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	}
    }
    
    /**
     * sends user to league create page
     */
    private void goToLeagueCreatePage() {
    	try {
    		Intent intent = new Intent(this, LeagueCreateActivity.class);
    		startActivity(intent);
    	} catch (Exception e) {
    		//remove in deployment
    		String stackTrace = android.util.Log.getStackTraceString(e);
    		Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	} 
    }
}