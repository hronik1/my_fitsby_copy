package com.example.fitsbypact;

import responses.PrivateLeagueResponse;
import responses.StatusResponse;
import servercommunication.LeagueCommunication;
import servercommunication.NewsfeedCommunication;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import dbtables.LeagueMember;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
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
	private TextView durationTV;
	private TextView leagueIdTV;
	private TextView startDateTV;
	
	private Button joinButton;
	private Button faqButton;
	
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
	private int duration;
	
	private ProgressDialog mProgressDialog;
	
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
        
        new GameInfoAsyncTask().execute(leagueId);
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
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Game Join Detail Activity");
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
 		duration = extras.getInt(LeagueDetailBundleKeys.KEY_DURATION);

 	}
 	/**
 	 * initializes the TextViews
 	 */
 	private void initializeTextViews() {
 		typeTV = (TextView)findViewById(R.id.league_join_detail_type_data);
 		typeTV.setText(isPrivate ? " Private" : " Public");
 		
 		wagerTV = (TextView)findViewById(R.id.league_join_detail_wager_data);
 		wagerTV.setText(" $" + wager);
 		
 		potTV = (TextView)findViewById(R.id.league_join_detail_pot_data);
 		potTV.setText(" $" + pot);
 		
 		playersTV = (TextView)findViewById(R.id.league_join_detail_players_data);
 		playersTV.setText(" " + players);
 		
 		durationTV = (TextView)findViewById(R.id.league_join_detail_duration_data);
 		durationTV.setText(" " + duration + " days");
 		
 		leagueIdTV = (TextView)findViewById(R.id.league_join_detail_id_data);
 		leagueIdTV.setText(" " + leagueId);
 		
 		startDateTV = (TextView)findViewById(R.id.game_start_date_value);
 	}

 	/**
 	 * initializes buttons
 	 */
 	private void initializeButtons() {
 		joinButton = (Button)findViewById(R.id.league_join_detail_button_join);
 		joinButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				join();
			}
 			
 		});
 		
 		faqButton = (Button)findViewById(R.id.league_join_detail_button_faq);
 		faqButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showFaqBrowser();
			}
 		});
 	}
 	
 	/**
 	 * join the game selected by user
 	 */
 	private void join() {
 		if(!isValid) {
 			//TODO really have to change this, not sure how to properly handle a bad bundle being passed though
 			Toast toast = Toast.makeText(getApplicationContext(), "sorry invalid bundle", Toast.LENGTH_LONG);
 			toast.setGravity(Gravity.CENTER, 0, 0);
 			toast.show();
 			return;
 		}
 		
 		//TODO add checking that user is not already in league
 		
//		LeagueMember member = new LeagueMember(leagueId, mApplicationUser.getUser().getID());
//		mLeagueMemberTableHandler.addLeagueMember(member);
// 		new JoinLeagueAsyncTask().execute(mApplicationUser.getUser().getID(), leagueId);
 		ApplicationUser appData = (ApplicationUser)getApplicationContext();
 		appData.setJoin();
 		appData.setUserId(mApplicationUser.getUser().getID());
 		appData.setLeagueId(leagueId);
 		Intent intent = new Intent(LeagueJoinDetailActivity.this, CreditCardActivity.class);
 		intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
 		startActivity(intent);
 	}
 	
 	/**
 	 * opens up the faq browser
 	 */
 	private void showFaqBrowser() {
 		//TODO change url to point to faq url
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fitsby.com/faq.html"));
 		startActivity(browserIntent);
 	}
 	
 	
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class GameInfoAsyncTask extends AsyncTask<Integer, Void, PrivateLeagueResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
                    "Gathering game data...");
		}
		
        protected PrivateLeagueResponse doInBackground(Integer... params) {
        	PrivateLeagueResponse response = LeagueCommunication.getSingleGame(params[0] + "");
        	return response;
        }

        @SuppressLint("NewApi")
		protected void onPostExecute(PrivateLeagueResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response.wasSuccessful()) {
        		League league = response.getLeague();
        		startDateTV.setText(" " + league.getStartDate());
        	}
        		

        }
    }
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
//    private class JoinLeagueAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
//        protected StatusResponse doInBackground(Integer... params) {
//        	StatusResponse response = LeagueCommunication.joinLeague(params[0], params[1]);
//        	return response;
//        }
//
//        protected void onPostExecute(StatusResponse response) {
//        	if (response.wasSuccessful()) {
//        		try {
//        			Intent intent = new Intent(LeagueJoinDetailActivity.this, CreditCardActivity.class);
//        			intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
//        			startActivity(intent);
//        		} catch(Exception e) {
//        			//TODO handle failure more robustly
//        			Toast toast = Toast.makeText(getApplicationContext(), "could not start credit card activity", Toast.LENGTH_LONG);
//        			toast.setGravity(Gravity.CENTER, 0, 0);
//        			toast.show();
//        		}
//        	} else {
//        		Toast.makeText(getApplicationContext(), "You are already in this game", Toast.LENGTH_LONG).show();
//        	}
//        }
//    }
}
