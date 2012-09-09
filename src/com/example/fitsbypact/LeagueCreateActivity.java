package com.example.fitsbypact;

import dbtables.User;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

public class LeagueCreateActivity extends Activity {

	private final static String TAG = "LeagueCreateActivity";
	
	private final static int MIN_WAGER = 5;
	private final static int MAX_WAGER = Integer.MAX_VALUE;
	private final static int MIN_DAYS = 3;
	private final static int MAX_DAYS = 7;
	
	private NumberPicker wagerNP;
	private NumberPicker daysNP;
	private Button createButton;
	
	/**
	 * called when activity is first created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_create);
        Log.i(TAG, "onCreate");
        
        initializeNumberPickers();
        initializeButtons();
        
        int userID = savedInstanceState != null ? savedInstanceState.getInt(User.ID_KEY) : -1;
        
        Toast.makeText(this, "Hello user:" + userID, Toast.LENGTH_LONG).show();
        
    }

    /**
     * called when menu is first create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_create, menu);
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
	 * initializes the days and wager number pickers
	 */
	private void initializeNumberPickers() {
    	wagerNP = (NumberPicker)findViewById(R.id.league_create_np_wager);
    	wagerNP.setMaxValue(MAX_WAGER);
    	wagerNP.setMinValue(MIN_WAGER);
    	wagerNP.setValue(MIN_WAGER);
    	
    	daysNP = (NumberPicker)findViewById(R.id.league_create_np_days);
    	daysNP.setMaxValue(MAX_DAYS);
    	daysNP.setMinValue(MIN_DAYS);
    	daysNP.setValue(MIN_DAYS);
	}
	
	/**
	 * initializes create button
	 */
	private void initializeButtons() {
		createButton = (Button)findViewById(R.id.league_create_button_create);
	   	createButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			create();
    		}
    	});
	}
	
	/**
	 * Creates the game specified by the wager and days
	 */
	private void create() {
		Toast.makeText(LeagueCreateActivity.this,
				"Wager: " + wagerNP.getValue() + "  Days: " + daysNP.getValue(),
				Toast.LENGTH_LONG).show();
		//TODO actually create the given game then move to next activity
	}
}
