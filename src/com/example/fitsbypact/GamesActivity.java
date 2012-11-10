package com.example.fitsbypact;

import java.util.ArrayList;
import java.util.List;

import responses.UserResponse;
import responses.UsersGamesResponse;
import servercommunication.GamesLeaderCommunication;
import servercommunication.LeagueCommunication;
import servercommunication.NewsfeedCommunication;
import servercommunication.UserCommunication;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import loaders.GameLeaderCursorLoader;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
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

public class GamesActivity extends Activity {
//	implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "GamesActivity";
	
	private NavigationBar navigation;
	
	private TextView playersTV;
	private TextView wagerTV;
	private TextView durationTV;
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
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
		playersTV = (TextView)findViewById(R.id.games_player_prompt);
		wagerTV = (TextView)findViewById(R.id.games_wager_prompt);
		durationTV = (TextView)findViewById(R.id.games_duration_prompt);
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
	@SuppressLint("NewApi")
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
		Intent intent = new Intent(this, FriendInviteActivity.class);
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
        protected UsersGamesResponse doInBackground(String... params) {
        	UsersGamesResponse response = LeagueCommunication.getUsersLeagues(user.getID());
        	return response;
        }

        protected void onPostExecute(UsersGamesResponse response) {
        	if (response == null ) {
        		Toast.makeText(getApplicationContext(), "Sorry, there appears to be no internet connection at the moment", Toast.LENGTH_LONG).show();
        	} else if (!response.wasSuccessful()){
        		Toast.makeText(getApplicationContext(), "Sorry, but could not get game data", Toast.LENGTH_LONG).show();
        	} else {
        		//TODO switch to next page
        		spinnerData.addAll(response.getGames());
        		spinnerDataAdapter.notifyDataSetChanged();
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
        	Cursor cursor = GamesLeaderCommunication.getGamesLeader(Integer.parseInt(spinnerData.get(spinnerPosition)));
        	return cursor;
        }

        @SuppressLint("NewApi")
		protected void onPostExecute(Cursor cursor) {
        	mAdapter.swapCursor(cursor);
        	mAdapter.notifyDataSetChanged();

        }
    }
}

