package com.example.fitsbypact;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LeagueJoinDetailActivity extends Activity {

	private final static String TAG = "LeagueJoinDetailActivity";
	
	private TextView typeTV;
	private TextView wagerTV;
	private TextView potTV;
	private TextView playersTV;
	private Button joinButton;
	
	/**
	 * called when activtiy is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_join_detail);
        Log.i(TAG, "onCreate");
        
        initializeTextViews();
        initializeButtons();
    }

    /**
     * called when options menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_join_detail, menu);
        Log.i(TAG, "onCreateOptionsMent");
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
 	 * initializes the TextViews
 	 */
 	private void initializeTextViews() {
 		typeTV = (TextView)findViewById(R.id.league_join_detail_type);
 		wagerTV = (TextView)findViewById(R.id.league_join_detail_wager);
 		potTV = (TextView)findViewById(R.id.league_join_detail_pot);
 		playersTV = (TextView)findViewById(R.id.league_join_detail_players);
 	}
 	
 	/**
 	 * initializes buttons
 	 */
 	private void initializeButtons() {
 		joinButton = (Button)findViewById(R.id.league_join_detail_button_join);
 		joinButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				join();
			}
 			
 		});
 	}
 	
 	/**
 	 * join the game selected by user
 	 */
 	private void join() {
 		//TODO actually join this game
 	}
}
