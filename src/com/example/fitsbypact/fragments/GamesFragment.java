package com.example.fitsbypact.fragments;

import java.util.ArrayList;
import java.util.List;

import responses.CountdownResponse;
import responses.PrivateLeagueResponse;
import responses.UsersGamesResponse;
import servercommunication.GamesLeaderCommunication;
import servercommunication.LeagueCommunication;

import widgets.NavigationBar;

import bundlekeys.CreditCardBundleKeys;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.fitsbypact.FriendInviteActivity;
import com.example.fitsbypact.GamesActivity;
import com.example.fitsbypact.LeagueJoinActivity;
import com.example.fitsbypact.LeagueLandingActivity;
import com.example.fitsbypact.R;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.League;
import dbtables.LeagueMember;
import dbtables.User;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class GamesFragment extends SherlockFragment {

	private static final String TAG = "GamesActivity";
	
	private Activity parent;
	
	private TextView playersPromptTV;
	private TextView durationPromptTV;
	private TextView wagerPromptTV;
	private TextView potPromptTV;
	private TextView startPromptTV;
	private TextView endPromptTV;
	private TextView noGamesPromptTV;
	
	private TextView playersTV;
	private TextView wagerTV;
	private TextView durationTV;
	private TextView potTV;
	private TextView startTV;
	private TextView endTV;
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
	 * callback to add in the stats fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View viewer = (View) inflater.inflate(R.layout.activity_games, container, false);
	    Log.i(TAG, "onCreateView");
	    
	    initializeSpinner(viewer);
	    initializeTextViews(viewer);
	    initializeProgressBar(viewer);
	    initializeListView(viewer);
	    initializeButtons(viewer);
	    
	    new SpinnerDataAsyncTask().execute();
	    
	    return viewer;
	}
	
	/**
	 * callback for when this fragment is attached to a view
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");
		parent = activity;

		mApplicationUser = ((ApplicationUser)parent.getApplicationContext());
		user = mApplicationUser.getUser();
	}
	
	/**
	 * initialize the text views
	 */
	private void initializeTextViews(View viewer) {
		playersTV = (TextView)viewer.findViewById(R.id.input_players);
		wagerTV = (TextView)viewer.findViewById(R.id.input_wager);
		durationTV = (TextView)viewer.findViewById(R.id.input_duration);
		potTV = (TextView)viewer.findViewById(R.id.input_pot);
		startTV = (TextView)viewer.findViewById(R.id.input_date);
		endTV = (TextView)viewer.findViewById(R.id.input_end_date);
		daysLeftTV = (TextView)viewer.findViewById(R.id.days_left_prompt);
		
		playersPromptTV = (TextView)viewer.findViewById(R.id.games_player_prompt);
		wagerPromptTV = (TextView)viewer.findViewById(R.id.games_wager_prompt);
		durationPromptTV = (TextView)viewer.findViewById(R.id.games_duration_prompt);
		potPromptTV = (TextView)viewer.findViewById(R.id.games_pot_prompt);
		noGamesPromptTV = (TextView)viewer.findViewById(R.id.games_no_games_prompt);
		noGamesPromptTV.setVisibility(View.INVISIBLE);
		endPromptTV = (TextView)viewer.findViewById(R.id.games_end_date_prompt);
		startPromptTV = (TextView)viewer.findViewById(R.id.games_start_date_prompt);
	}
	
	/**
	 * initializes the progress bar
	 */
	private void initializeProgressBar(View viewer) {
		progressBar = (ProgressBar)viewer.findViewById(R.id.games_progress_bar);
	}
	
	/**
	 * initializes the list view
	 */
	private void initializeListView(View viewer) {
		leadersLV = (ListView)viewer.findViewById(R.id.games_leader_list);
		mAdapter = new SimpleCursorAdapter(parent, R.layout.list_item_game_leader, null, fromArgs, toArgs, 0);
		leadersLV.setAdapter(mAdapter);
	}
	
	/**
	 * initializes the spinner
	 */
	private void initializeSpinner(View viewer) {
		gamesSpinner = (Spinner)viewer.findViewById(R.id.games_spinner);
		spinnerData =  new ArrayList<String>();

		spinnerDataAdapter = new ArrayAdapter<String>(parent,
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
	private void initializeButtons(View viewer) {
		inviteButton = (Button)viewer.findViewById(R.id.invite_friends_button);
		inviteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoInviteActivity();
				
			}
		});
		
		newGamesButton = (Button)viewer.findViewById(R.id.games_button_newgame);
		newGamesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoLeagueJoinActivity();
			}
		});
	}
	
	private void gotoInviteActivity() {
		
		if (spinnerData.isEmpty()) {
	   		Toast toast = Toast.makeText(parent, "Sorry, but you can't invite friends since you aren't in any games", Toast.LENGTH_LONG);
	   		toast.setGravity(Gravity.CENTER, 0, 0);
	   		toast.show();
	   		return;
		}
			
		Intent intent = new Intent(parent, FriendInviteActivity.class);
		intent.putExtra(CreditCardBundleKeys.KEY_LEAGUE_ID, UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));

		startActivity(intent);

	}
	
	/**
	 * opens up the LeagueLandingActivity
	 */
	private void gotoLeagueJoinActivity() {
		try {
			Intent intent = new Intent(parent, LeagueJoinActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			//remove in deployment
			String stackTrace = android.util.Log.getStackTraceString(e);
			Toast toast = Toast.makeText(parent, stackTrace,
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
		endPromptTV.setVisibility(View.INVISIBLE);
		noGamesPromptTV.setVisibility(View.VISIBLE);
	}
	
	private void disableNoGamesPrompts() {
		//noGamesPromptTV.setText("");
	}
	
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class SpinnerDataAsyncTask extends AsyncTask<String, Void, UsersGamesResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Finding your games...");
		}
		
        protected UsersGamesResponse doInBackground(String... params) {
        	UsersGamesResponse response = LeagueCommunication.getUsersLeagues(user.getID());
        	return response;
        }

        protected void onPostExecute(UsersGamesResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(parent, "Sorry, but there doesn't appear to be an internet connection at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Sorry, but we weren't able to grab the data for your game", Toast.LENGTH_LONG);
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
        	new GameInfoAsyncTask().execute();

        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class GameInfoAsyncTask extends AsyncTask<String, Void, PrivateLeagueResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
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
        		endTV.setText(" " + league.getEndDate());
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
            mProgressDialog = ProgressDialog.show(parent, "",
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
