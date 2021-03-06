package com.fitsby.fragments;

import java.util.ArrayList;
import java.util.List;

import responses.CountdownResponse;
import responses.PrivateLeagueResponse;
import responses.ProgressResponse;
import responses.UsersGamesResponse;
import servercommunication.GamesLeaderCommunication;
import servercommunication.LeagueCommunication;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import bundlekeys.CreditCardBundleKeys;

import com.fitsby.FriendInviteActivity;
import com.fitsby.LeagueJoinActivity;
import com.fitsby.R;
import com.fitsby.applicationsubclass.ApplicationUser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.Leader;
import dbtables.League;
import dbtables.User;

public class GamesFragment extends Fragment {

	private static final String TAG = "GamesActivity";

	private final static int PROGRESS_MAX = 100;
	private Activity parent;
	
	private TextView playersPromptTV;
	private TextView wagerPromptTV;
	private TextView goalPromptTV;
	private TextView noGamesPromptTV;
	
	private TextView playersTV;
	private TextView wagerTV;
	private TextView durationTV;
	private TextView goalTV;
	private TextView startTV;
	private TextView endTV;
	private TextView daysLeftTV;
	
	private ProgressBar progressBar;
	private PullToRefreshListView leadersLV;
	private boolean refreshFinished = true;
	private Spinner gamesSpinner;
	private Button inviteButton;
	private Button newGamesButton;
	
	private ArrayAdapter<String> spinnerDataAdapter;
	private List<String> spinnerData;
	
	private SimpleCursorAdapter mAdapter;
	public final static String[] fromArgs = {UserTableHandler.KEY_FIRST_NAME, UserTableHandler.KEY_LAST_NAME,
			LeagueMemberTableHandler.KEY_CHECKINS, "_id", Leader.KEY_BITMAP, 
			LeagueMemberTableHandler.KEY_CHECKINS, LeagueMemberTableHandler.KEY_CHECKINS};
	public final static int[] toArgs = {R.id.list_item_game_leader_name, R.id.list_item_game_leader_last_name,
			R.id.list_item_game_leader_checkins, R.id.rank, R.id.list_item_game_leader_imageview,
			R.id.winner, R.id.game_leader_goal_progress};
	private int spinnerPosition;
	
	private ApplicationUser mApplicationUser;
	
	private User user;
	
	private ProgressDialog mProgressDialog;
	
	private int mGoal;
	
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
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * initialize the text views
	 */
	private void initializeTextViews(View viewer) {
		playersTV = (TextView)viewer.findViewById(R.id.input_players);
		wagerTV = (TextView)viewer.findViewById(R.id.input_wager);
		durationTV = (TextView)viewer.findViewById(R.id.input_duration);
		goalTV = (TextView)viewer.findViewById(R.id.input_goal);
		startTV = (TextView)viewer.findViewById(R.id.input_date);
		endTV = (TextView)viewer.findViewById(R.id.input_end_date);
		daysLeftTV = (TextView)viewer.findViewById(R.id.days_left_prompt);
		
		playersPromptTV = (TextView)viewer.findViewById(R.id.games_player_prompt);
		wagerPromptTV = (TextView)viewer.findViewById(R.id.games_wager_prompt);
		goalPromptTV = (TextView)viewer.findViewById(R.id.games_goal_prompt);
		noGamesPromptTV = (TextView)viewer.findViewById(R.id.games_no_games_prompt);
		noGamesPromptTV.setVisibility(View.INVISIBLE);

	}
	
	/**
	 * initializes the progress bar
	 */
	private void initializeProgressBar(View viewer) {
		progressBar = (ProgressBar)viewer.findViewById(R.id.games_progress_bar);
		progressBar.setMax(PROGRESS_MAX);
	}
	
	/**
	 * initializes the list view
	 */
	private void initializeListView(View viewer) {
		leadersLV = (PullToRefreshListView)viewer.findViewById(R.id.games_leader_list);
		mAdapter = new SimpleCursorAdapter(parent, R.layout.list_item_game_leader, null, fromArgs, toArgs, 0);
		mAdapter.setViewBinder(new MyViewBinder());
		leadersLV.setAdapter(mAdapter);
		
	   	leadersLV.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {

				@Override
				public void onPullEvent(PullToRefreshBase<ListView> refreshView,
						State state, Mode direction) {
					if (refreshFinished) {
						new SpinnerDataAsyncTask().execute();
						refreshFinished = false;
					}
				}
	    		
	    	});
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
	   		Toast toast = Toast.makeText(parent, "You can't invite friends since you aren't in any games", Toast.LENGTH_LONG);
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
		goalPromptTV.setVisibility(View.INVISIBLE);
		noGamesPromptTV.setVisibility(View.VISIBLE);
	}
	
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class SpinnerDataAsyncTask extends AsyncTask<String, Void, UsersGamesResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Finding your games...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						SpinnerDataAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected UsersGamesResponse doInBackground(String... params) {
        	UsersGamesResponse response = LeagueCommunication.getUsersLeagues(user.getID());
        	return response;
        }

        protected void onPostExecute(UsersGamesResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(parent, "There doesn't appear to be an internet connection at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Error grabbing the data for your game", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		disableGamesPrompts();
        	} else {
        		//TODO switch to next page
        		List<String> games = response.getGames();
        		if (games == null || games.size() == 0) {
        			disableGamesPrompts();
        		}
        		spinnerData.clear();
        		spinnerData.addAll(games);
        		spinnerDataAdapter.notifyDataSetChanged();
        	}
        	refreshFinished = true;
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
        	if (cursor != null) {
        		Log.d(TAG, "cursor rows: " + cursor.getCount());
        		mAdapter.swapCursor(cursor);
        		mAdapter.notifyDataSetChanged();
        	} else {
        		Log.d(TAG, "cursor is null");
        		Toast toast = Toast.makeText(parent, parent.getString(R.string.timeout_message), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} 
        	new DaysRemainingAsyncTask().execute();	

        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class GameInfoAsyncTask extends AsyncTask<String, Void, PrivateLeagueResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Gathering game data...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						GameInfoAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected PrivateLeagueResponse doInBackground(String... params) {
        	PrivateLeagueResponse response = LeagueCommunication.getSingleGame(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)) + "");
        	return response;
        }

		protected void onPostExecute(PrivateLeagueResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response.wasSuccessful()) {
        		League league = response.getLeague();
        		playersTV.setText("" + league.getPlayers());
        		goalTV.setText("" + league.getGoal());
        		mGoal = league.getGoal();
        		durationTV.setText("" + league.getDuration() + " days");
        		wagerTV.setText("$" + league.getWager());
        		startTV.setText(" (" + league.getStartDate() + " -");
        		endTV.setText(" " + league.getEndDate() + ")");
        	} else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} 
        	
        	new CursorDataAsyncTask().execute();
        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class DaysRemainingAsyncTask extends AsyncTask<String, Void, CountdownResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Gathering game data...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						DaysRemainingAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected CountdownResponse doInBackground(String... params) {
        	CountdownResponse response = LeagueCommunication.getCountdown(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));
        	return response;
        }

		protected void onPostExecute(CountdownResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response.wasSuccessful()) {
        		daysLeftTV.setText(response.getDaysLeft());
        	} else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} 
        	new ProgressAsyncTask().execute();
        }
    }
    
    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewId = view.getId();
            Log.d(TAG, cursor.getClass().toString());
            if(viewId == R.id.list_item_game_leader_imageview) {
            	ImageView profilePic = (ImageView) view;
            	byte[] bytes = cursor.getBlob(columnIndex);
            	profilePic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            } else if (viewId == R.id.list_item_game_leader_checkins) {
            	TextView text = (TextView) view;
            	text.setText(cursor.getString(columnIndex) + "/" + mGoal);
            } else if (viewId == R.id.winner) {
            	ImageView winner = (ImageView) view;
            	int checkins = Integer.parseInt(cursor.getString(columnIndex));
            	if (checkins < mGoal)
            		winner.setVisibility(View.INVISIBLE);
            	else
            		winner.setVisibility(View.VISIBLE);
            } else if (viewId == R.id.game_leader_goal_progress) {
            	ProgressBar progress = (ProgressBar) view;
            	int checkins = Integer.parseInt(cursor.getString(columnIndex));
            	if (checkins >= mGoal)
            		progress.setProgressDrawable(parent.getResources().getDrawable(R.drawable.progress_bar_goal));
            	else 
            		progress.setProgressDrawable(parent.getResources().getDrawable(R.drawable.progress_bar_days));
            	double progressPercent = (checkins >= mGoal ? 100 : (double)checkins/(double)mGoal*100.0);
            	progress.setProgress((int)progressPercent);
            } else {
            	TextView name = (TextView) view;
            	name.setText(cursor.getString(columnIndex));
            }
            
            return true;
        }
    }
        
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class ProgressAsyncTask extends AsyncTask<String, Void, ProgressResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Gathering game progress...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						ProgressAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected ProgressResponse doInBackground(String... params) {
        	 return LeagueCommunication.getProgress(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));
			
        	
        }

		protected void onPostExecute(ProgressResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response.wasSuccessful()) {
        		progressBar.setProgress((int) (response.getProgress() * PROGRESS_MAX));
			} else if (response.getError() != null && !response.getError().equals("")) {
          		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
          		toast.setGravity(Gravity.CENTER, 0, 0);
      			toast.show();
          	} else {
        		progressBar.setProgress(0);
          	}
		}		
    }
}
