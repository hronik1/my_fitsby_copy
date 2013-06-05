package com.fitsby;

import responses.CreatorResponse;
import responses.PrivateLeagueResponse;
import responses.StatusResponse;
import servercommunication.LeagueCommunication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;
import dbtables.League;
import dbtables.User;

/**
 * LeagueJoinDetailActivity shows a detailed version of a selected league.
 * 
 * @author brenthronk
 *
 */
public class LeagueJoinDetailActivity extends KiipFragmentActivity {

	/**
	 * Tag used for logcat messages.
	 */
	private final static String TAG = "LeagueJoinDetailActivity";
	
	/**
	 * Displays whether the game is private or public.
	 */
	private TextView typeTV;
	/**
	 * Displays the wager amount.
	 */
	private TextView wagerTV;
	/**
	 * Displays the number of goal days.
	 */
	private TextView goalTV;
	/**
	 * Displays the number of players in the league.
	 */
	private TextView playersTV;
	/**
	 * Displays the duration of the league.
	 */
	private TextView durationTV;
	/**
	 * Displays the id of the league.
	 */
	private TextView leagueIdTV;
	/**
	 * Displays the start date.
	 */
	private TextView startDateTV;
	/**
	 * Displays the creator's first name.
	 */
	private TextView firstNameTV;
	/**
	 * Displays the creator's last name.
	 */
	private TextView lastNameTV;
	
	/**
	 * Pressed to join the league.
	 */
	private Button joinButton;
	/**
	 * Pressed to display open up faq in browser.
	 */
	private Button faqButton;
	
	/**
	 * Displays image of league creator.
	 */
	private ImageView mImageView;
	
	/**
	 * Stores session information.
	 */
	private ApplicationUser mApplicationUser;
	
	/**
	 * Id of the league.
	 */
	private int leagueId;
	/**
	 * Goal number of days.
	 */
	private int goal;
	/**
	 * True if league is private, false if public.
	 */
	private boolean isPrivate;
	/**
	 * The amount being wagered.
	 */
	private int wager;
	/**
	 * The number of players.
	 */
	private int players;
	/**
	 * Flag used to set whether information was properly passed in from the
	 * previous activity.
	 */
	private boolean isValid;
	/**
	 * Duration of the league.
	 */
	private int duration;
	/**
	 * Bitmap of the league creator.
	 */
	private Bitmap bitmap;
	/**
	 * Stores information of currently loggged in user.
	 */
	private User mUser;
	
	/**
	 * Used to show ongoing background activity.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Callback for the creation of the activity, initializes views.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_join_detail);
        Log.i(TAG, "onCreate");
        
        parseBundle(getIntent());
        
        initializeTextViews();
        initializeButtons();
        initializeImageView();
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        
        new GameInfoAsyncTask().execute(leagueId);
        
    }

    /**
     * Callback for when options menu is created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_join_detail, menu);
        Log.i(TAG, "onCreateOptionsMent");
        return true;
    }
    
    /**
 	 * Callback for when activity is restarted.
 	 */
 	@Override
 	public void onRestart() {
 	    super.onRestart();
 	
 	    Log.i(TAG, "onRestart");
 	}
 	
 	/**
 	 * Callback for when activity is starting, starts flurry session.
 	 */
 	@Override
 	public void onStart() {
 	    super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Game Join Detail Activity");
 	    Log.i(TAG, "onStart");
 	}
 	
 	/**
 	 * Callback for stopping of the activity, stops flurry session.
 	 */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
 	
 	/**
 	 * Callback when activity resumes.
 	 */
 	@Override
 	public void onResume() {
 	    super.onResume();
 	    
 	    Log.i(TAG, "onResume");
 	}
 	
 	/**
 	 * Callback for when activity is paused.
 	 */
 	@Override
 	public void onPause() {
 	    super.onPause();
 	
 	    Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
 	}
 	
 	/**
 	 * Callback for when activity is destroyed.
 	 */
 	@Override
 	public void onDestroy() {
 		super.onDestroy();
 		
 		Log.i(TAG, "onDestroy");
 		
 	}
 	
 	/**
 	 * Parses the incoming bundle, sets isValid flag true if bundle was valid.
 	 * 
 	 * @param intent	contains the to-be-parsed bundle
 	 */
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
 		goal = extras.getInt(LeagueDetailBundleKeys.KEY_GOAL);
 		Log.i(TAG, goal +"");
 		if (extras.getInt(LeagueDetailBundleKeys.KEY_TYPE) == 0)
 			isPrivate = false;
 		else
 			isPrivate = true;
 		isValid = true;
 		duration = extras.getInt(LeagueDetailBundleKeys.KEY_DURATION);
 		bitmap = extras.getParcelable(LeagueDetailBundleKeys.KEY_BITMAP);

 	}
 	
 	/**
 	 * Connects the TextViews to their corresponding view in layout.
 	 */
 	private void initializeTextViews() {
 		typeTV = (TextView)findViewById(R.id.league_join_detail_type_data);
 		typeTV.setText(isPrivate ? "Private" : "Public");
 		
 		wagerTV = (TextView)findViewById(R.id.league_join_detail_wager_data);
 		wagerTV.setText("$" + wager);
 		
 		goalTV = (TextView)findViewById(R.id.league_join_detail_goal_data);
 		goalTV.setText(goal+"");
 		
 		playersTV = (TextView)findViewById(R.id.league_join_detail_players_data);
 		playersTV.setText(players+"");
 		
 		durationTV = (TextView)findViewById(R.id.league_join_detail_duration_data);
 		durationTV.setText(" (" + duration + " days)");
 		
 		leagueIdTV = (TextView)findViewById(R.id.league_join_detail_id_data);
 		leagueIdTV.setText("" + leagueId);
 		
 		startDateTV = (TextView)findViewById(R.id.game_start_date_value);
 		
 		firstNameTV = (TextView)findViewById(R.id.league_join_detail_name_first);
 		firstNameTV.setText(" ");
 		lastNameTV = (TextView)findViewById(R.id.league_join_detail_name_last);
 		lastNameTV.setText(" ");

 	}

 	/**
 	 * Connects the buttons to their corresponding view in layout, and adds
 	 * listeners.
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
 	 * Connects the imageview to its corresponding view in layout, and sets
 	 * the bitmap.
 	 */
 	private void initializeImageView() {
 		mImageView = (ImageView)findViewById(R.id.league_join_detail_imageview);
 		mImageView.setImageBitmap(bitmap);
 	}
 	
  	/**
 	 * Joins the game selected by user.
 	 */
 	private void join() {
 		if(!isValid) {
 			//TODO really have to change this, not sure how to properly handle a bad bundle being passed though
 			Toast toast = Toast.makeText(getApplicationContext(), "Invalid bundle", Toast.LENGTH_LONG);
 			toast.setGravity(Gravity.CENTER, 0, 0);
 			toast.show();
 			return;
 		}
 		
 		if (wager > 0) {
 			ApplicationUser appData = (ApplicationUser)getApplicationContext();
 			appData.setJoin();
 			appData.setUserId(mApplicationUser.getUser().getID());
 			appData.setLeagueId(leagueId);
 			Intent intent = new Intent(LeagueJoinDetailActivity.this, CreditCardActivity.class);
 			intent.putExtra(CreditCardBundleKeys.KEY_LEAGUE_ID, leagueId);
 			intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
 			startActivity(intent);
 		} else {
 			showConfirmation();
 		}
 	}
 	
 	/**
 	 * Opens up the faq in browser.
 	 */
 	private void showFaqBrowser() {
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fitsby.com/faq.html"));
 		startActivity(browserIntent);
 	}
 	
    /**
     * Shows a confirmation dialog, confirming that user really wants to join
     * the league.
     */
    private void showConfirmation() {
	  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setMessage("Are you sure you want to join this game?")
    			.setCancelable(false)
    			.setPositiveButton("Join", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					new JoinLeagueAsyncTask().execute(mUser.getID(), leagueId);
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /**
     * GameInfoAsyncTask gathers extra information about the league, that
     * wasn't passed in from the previous activity, and does so on a
     * background thread.
     * 
     * @author brenthronk
     *
     */
    private class GameInfoAsyncTask extends AsyncTask<Integer, Void, PrivateLeagueResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
						"Gathering game data...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						GameInfoAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected PrivateLeagueResponse doInBackground(Integer... params) {
        	PrivateLeagueResponse response = LeagueCommunication.getSingleGame(params[0] + "");
        	return response;
        }

		protected void onPostExecute(PrivateLeagueResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response.wasSuccessful()) {
        		League league = response.getLeague();
        		startDateTV.setText(" " + league.getStartDate());

        	}
        		
        	new CreatorAsyncTask().execute();
        }
    }
    
    /**
     * CreatorAsyncTask gathers information about the league creator, that
     * wasn't passed in from the previous activity, and does so on a
     * background thread.
     * 
     * @author brenthronk
     *
     */
    private class CreatorAsyncTask extends AsyncTask<String, Void, CreatorResponse> {
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
						"Gathering the game host's data...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						CreatorAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected CreatorResponse doInBackground(String... params) {
        	CreatorResponse response = LeagueCommunication.getCreator(leagueId+"");
        	return response;
        }

		protected void onPostExecute(CreatorResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response.wasSuccessful()) {
            	firstNameTV.setText(" " + response.getCreatorFirstName());
        	}
        }
    }
    
    /**
     * JoinLeagueAsyncTask sends the information needed for the user to join
     * the selected league, and does so on a background thread.
     * 
     * @author brenthronk
     *
     */
    private class JoinLeagueAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
						"Creating your game...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						JoinLeagueAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = LeagueCommunication.joinLeague(params[0], params[1],
        			"", "", "", "");
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response.wasSuccessful()) {
        		try {
        			ApplicationUser appData = (ApplicationUser)getApplicationContext();
        			Intent intent = new Intent(LeagueJoinDetailActivity.this, FriendInviteActivity.class);
            		intent.putExtra(CreditCardBundleKeys.KEY_LEAGUE_ID, appData.getLeagueId());
            		startActivity(intent);
        		} catch(Exception e) {
        		}
        	} else {
        		Toast toast = Toast.makeText(LeagueJoinDetailActivity.this, "The game could not be joined at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        }
    }
    
}
