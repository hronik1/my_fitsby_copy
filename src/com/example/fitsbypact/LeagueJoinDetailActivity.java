package com.example.fitsbypact;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbtables.LeagueMember;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;

public class LeagueJoinDetailActivity extends Activity {

	private final static String TAG = "LeagueJoinDetailActivity";
	
	private TextView typeTV;
	private TextView wagerTV;
	private TextView potTV;
	private TextView playersTV;
	private Button joinButton;
	
	private ApplicationUser mApplicationUser;
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private LeagueTableHandler mLeagueTableHandler;
	
	private int leagueId;
	private int pot;
	private boolean isPrivate;
	private int wager;
	private int players;
	private boolean isValid;
	
	/**
	 * called when activtiy is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_join_detail);
        Log.i(TAG, "onCreate");
        
        parseBundle(getIntent());
        
        initializeTextViews();
        initializeButtons();
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mLeagueTableHandler = mdbHandler.getLeagueTableHandler();
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
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
 	
 	public void parseBundle(Intent intent) {
 		//TODO robustly handle bad bundle
 		if (intent == null) {
 			isValid = false;
 			return;
 		}
 		
 		Bundle extras = intent.getExtras();
 		if (extras == null) {
 			isValid = false;
 			return;
 		}
 		
 		leagueId = extras.getInt(LeagueDetailBundleKeys.KEY_LEAGUE_ID);
 		players = extras.getInt(LeagueDetailBundleKeys.KEY_PLAYERS);
 		wager = extras.getInt(LeagueDetailBundleKeys.KEY_WAGER);
 		pot = extras.getInt(LeagueDetailBundleKeys.KEY_POT);
 		if (extras.getInt(LeagueDetailBundleKeys.KEY_TYPE) == 0)
 			isPrivate = false;
 		else
 			isPrivate = true;
 		isValid = true;
 	}
 	/**
 	 * initializes the TextViews
 	 */
 	private void initializeTextViews() {
 		typeTV = (TextView)findViewById(R.id.league_join_detail_type);
 		typeTV.append(isPrivate ? "private" : "public");
 		
 		wagerTV = (TextView)findViewById(R.id.league_join_detail_wager);
 		wagerTV.append("" + wager);
 		
 		potTV = (TextView)findViewById(R.id.league_join_detail_pot);
 		potTV.append("" + pot);
 		
 		playersTV = (TextView)findViewById(R.id.league_join_detail_players);
 		playersTV.append("" + players);
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
 		if(!isValid) {
 			//TODO really have to change this, not sure how to properly handle a bad bundle being passed though
 			Toast.makeText(getApplicationContext(), "sorry invalid bundle", Toast.LENGTH_LONG).show();
 			return;
 		}
 		
 		//TODO add checking that user is not already in league
		LeagueMember member = new LeagueMember(leagueId, mApplicationUser.getUser().getID());
		mLeagueMemberTableHandler.addLeagueMember(member);
		try {
			Intent intent = new Intent(this, CreditCardActivity.class);
			intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
			startActivity(intent);
		} catch(Exception e) {
			//TODO handle failure more robustly
			Toast.makeText(getApplicationContext(), "could not start credit card activity", Toast.LENGTH_LONG).show();
		}
 	}
}
