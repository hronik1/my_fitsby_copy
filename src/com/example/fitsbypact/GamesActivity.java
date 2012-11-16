package com.example.fitsbypact;

import java.util.ArrayList;
import java.util.List;

import responses.CountdownResponse;
import responses.CreatorResponse;
import responses.PrivateLeagueResponse;
import responses.UserResponse;
import responses.UsersGamesResponse;
import servercommunication.GamesLeaderCommunication;
import servercommunication.LeagueCommunication;
import servercommunication.NewsfeedCommunication;
import servercommunication.UserCommunication;

import bundlekeys.CreditCardBundleKeys;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import loaders.GameLeaderCursorLoader;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.League;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

public class GamesActivity extends Activity {
//	implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "GamesActivity";
	
	private NavigationBar navigation;
	
	private TextView playersPromptTV;
	private TextView durationPromptTV;
	private TextView wagerPromptTV;
	private TextView potPromptTV;
	private TextView startPromptTV;
	private TextView noGamesPromptTV;
	
	private TextView playersTV;
	private TextView wagerTV;
	private TextView durationTV;
	private TextView potTV;
	private TextView startTV;
	private TextView daysLeftTV;
	
	private ProgressBar progressBar;
	private ListView leadersLV;
	private Spinner gamesSpinner;
	private Button inviteButton;
	private Button newGamesButton;
	
	private ArrayAdapter<String> spinnerDataAdapter;
	private List<String> spinnerData;
	
	private SimpleCursorAdapter mAdapter;
	private final static String[] fromArgs = {UserTableHandler.KEY_FIRST_NAME, UserTableHandler.KEY_LAST_NAME, LeagueMemberTableHandler.KEY_CHECKINS, "_id"};
	private final static int[] toArgs = {R.id.list_item_game_leader_name, R.id.list_item_game_leader_last_name,
			R.id.list_item_game_leader_checkins, R.id.rank};
	private int spinnerPosition;
	
	private ApplicationUser mApplicationUser;
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private LeagueTableHandler mLeagueTableHandler;
	
	private List<LeagueMember> listLeagueMember;
	private User user;
	
	private ProgressDialog mProgressDialog;
	private String creatorFirstName;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        user = mApplicationUser.getUser();
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mLeagueTableHandler = mdbHandler.getLeagueTableHandler();
        listLeagueMember = mLeagueMemberTableHandler.getAllLeagueMembersByUserId(user.getID());
        
        //TODO loadermanager stuffs
        initializeSpinner();
        initializeNavigationBar();
        initializeTextViews();
        initializeProgressBar();
        initializeListView();
        initializeButtons();

        new SpinnerDataAsyncTask().execute();
    }

    /**
     * called when options bar is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_games, menu);
        Log.i(TAG, "onCreate");
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
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Games Activity");
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
	 * initializes the navigation bar
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.games_navigation_bar);
		navigation.setParentActivity(this);
		navigation.turnOffTV("games");
	}
	
	/**
	 * initialize the text views
	 */
	private void initializeTextViews() {
		playersTV = (TextView)findViewById(R.id.input_players);
		wagerTV = (TextView)findViewById(R.id.input_wager);
		durationTV = (TextView)findViewById(R.id.input_duration);
		potTV = (TextView)findViewById(R.id.input_pot);
		startTV = (TextView)findViewById(R.id.input_date);
		daysLeftTV = (TextView)findViewById(R.id.days_left_prompt);
		
		playersPromptTV = (TextView)findViewById(R.id.games_player_prompt);
		wagerPromptTV = (TextView)findViewById(R.id.games_wager_prompt);
		durationPromptTV = (TextView)findViewById(R.id.games_duration_prompt);
		potPromptTV = (TextView)findViewById(R.id.games_pot_prompt);
		noGamesPromptTV = (TextView)findViewById(R.id.games_no_games_prompt);
		noGamesPromptTV.setVisibility(View.INVISIBLE);
		startPromptTV = (TextView)findViewById(R.id.games_start_date_prompt);
	}
	
	/**
	 * initializes the progress bar
	 */
	private void initializeProgressBar() {
		progressBar = (ProgressBar)findViewById(R.id.games_progress_bar);
	}
	
	/**
	 * initializes the list view
	 */
	private void initializeListView() {
		leadersLV = (ListView)findViewById(R.id.games_leader_list);
		mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_game_leader, null, fromArgs, toArgs, 0);
		leadersLV.setAdapter(mAdapter);
	}
	
	/**
	 * initializes the spinner
	 */
	private void initializeSpinner() {
		gamesSpinner = (Spinner)findViewById(R.id.games_spinner);
		spinnerData =  new ArrayList<String>();

		spinnerDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerData);
		spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gamesSpinner.setAdapter(spinnerDataAdapter);
		gamesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View view,
					int position, long id) {
				//TODO show game states of element clicked on
				spinnerPosition = position;
				new CursorDataAsyncTask().execute();
				new GameInfoAsyncTask().execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				/** do nothing **/
			}
			
		});
	}
    
	/**
	 * initializes Buttons
	 */
	private void initializeButtons() {
		inviteButton = (Button)findViewById(R.id.invite_friends_button);
		inviteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoInviteActivity();
				
			}
		});
		
		newGamesButton = (Button)findViewById(R.id.games_button_newgame);
		newGamesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoLeagueLandingActivity();
			}
		});
	}
	
	private void gotoInviteActivity() {
		
		if (spinnerData.isEmpty()) {
	   		Toast toast = Toast.makeText(this, "Sorry, but you can't invite friends since you aren't in any games", Toast.LENGTH_LONG);
	   		toast.setGravity(Gravity.CENTER, 0, 0);
	   		toast.show();
	   		return;
		}
			
		Intent intent = new Intent(GamesActivity.this, FriendInviteActivity.class);
		intent.putExtra(CreditCardBundleKeys.KEY_LEAGUE_ID, UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));

		startActivity(intent);

	}
	
	/**
	 * opens up the LeagueLandingActivity
	 */
	private void gotoLeagueLandingActivity() {
		try {
			Intent intent = new Intent(this, LeagueLandingActivity.class);
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
	
	private void disableGamesPrompts() {
		playersPromptTV.setVisibility(View.INVISIBLE);
		wagerPromptTV.setVisibility(View.INVISIBLE);
		durationPromptTV.setVisibility(View.INVISIBLE);
		potPromptTV.setVisibility(View.INVISIBLE);
		startPromptTV.setVisibility(View.INVISIBLE);
		noGamesPromptTV.setVisibility(View.VISIBLE);
	}
	
	private void disableNoGamesPrompts() {
		//noGamesPromptTV.setText("");
	}
	
    /** LoaderManager callBacks **/
    
    /**
     * 
     * @param id
     * @param args
     * @return
     */
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//    	return new GameLeaderCursorLoader(this);
//    }
//    
//    /**
//     * callback for finishing of loader
//     * @param loader
//     * @param data
//     */
//	@SuppressLint("NewApi")
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//    	mAdapter.swapCursor(data);
//    }
//    
//    /**
//     * callback for resetting of loader
//     * @param loader
//     */
//    @SuppressLint("NewApi")
//	public void onLoaderReset(Loader<Cursor> loader) {
//    	mAdapter.swapCursor(null);
//    }
    
    /** end LoaderManager callbacks **/
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class SpinnerDataAsyncTask extends AsyncTask<String, Void, UsersGamesResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(GamesActivity.this, "",
                    "Finding your games...");
		}
		
        protected UsersGamesResponse doInBackground(String... params) {
        	UsersGamesResponse response = LeagueCommunication.getUsersLeagues(user.getID());
        	return response;
        }

        protected void onPostExecute(UsersGamesResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but there doesn't appear to be an internet connection at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but we weren't able to grab the data for your game", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		disableGamesPrompts();
        	} else {
        		//TODO switch to next page
        		List<String> games = response.getGames();
        		if (games == null || games.size() == 0) {
        			disableGamesPrompts();
        		}
        		spinnerData.addAll(games);
        		spinnerDataAdapter.notifyDataSetChanged();
        		disableNoGamesPrompts();
        	}
        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class CursorDataAsyncTask extends AsyncTask<String, Void, Cursor> {
    	
        protected Cursor doInBackground(String... params) {
        	Cursor cursor = GamesLeaderCommunication.getGamesLeader(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));
        	return cursor;
        }

		protected void onPostExecute(Cursor cursor) {
        	mAdapter.swapCursor(cursor);
        	mAdapter.notifyDataSetChanged();

        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class GameInfoAsyncTask extends AsyncTask<String, Void, PrivateLeagueResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(GamesActivity.this, "",
                    "Gathering game data...");
		}
		
        protected PrivateLeagueResponse doInBackground(String... params) {
        	PrivateLeagueResponse response = LeagueCommunication.getSingleGame(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)) + "");
        	return response;
        }

		protected void onPostExecute(PrivateLeagueResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response.wasSuccessful()) {
        		League league = response.getLeague();
        		playersTV.setText("   " + league.getPlayers());
        		potTV.setText(" $" + league.getStakes());
        		durationTV.setText("   " + league.getDuration() + " days");
        		wagerTV.setText(" $" + league.getWager());
        		startTV.setText(" " + league.getStartDate());
        	}
        	new DaysRemainingAsyncTask().execute();	
        	
        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class DaysRemainingAsyncTask extends AsyncTask<String, Void, CountdownResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(GamesActivity.this, "",
                    "Gathering game data...");
		}
		
        protected CountdownResponse doInBackground(String... params) {
        	CountdownResponse response = LeagueCommunication.getCountdown(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));
        	return response;
        }

		protected void onPostExecute(CountdownResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response.wasSuccessful()) {
        		daysLeftTV.setText(response.getDaysLeft());
        	}
        		
        }
    }
    


}

