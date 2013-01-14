package com.fitsby;

import java.util.List;

import responses.CreatorResponse;
import responses.PrivateLeagueResponse;
import responses.StatusResponse;
import responses.UsersGamesResponse;
import servercommunication.GamesLeaderCommunication;
import servercommunication.LeagueCommunication;
import servercommunication.NewsfeedCommunication;


import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import dbtables.LeagueMember;
import dbtables.User;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.fitsby.fragments.GamesFragment;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

public class LeagueJoinDetailActivity extends KiipFragmentActivity {

	private final static String TAG = "LeagueJoinDetailActivity";
	
	private TextView typeTV;
	private TextView wagerTV;
	private TextView potTV;
	private TextView playersTV;
	private TextView durationTV;
	private TextView leagueIdTV;
	private TextView startDateTV;
	private TextView firstNameTV;
	private TextView lastNameTV;
	private TextView numberWinnersTV;
	
	private Button joinButton;
	private Button faqButton;
	
	private ImageView mImageView;
//	private ListView mListView;
	
	private ApplicationUser mApplicationUser;
	
	private int leagueId;
	private int pot;
	private boolean isPrivate;
	private int wager;
	private int players;
	private boolean isValid;
	private int duration;
	private Bitmap bitmap;
	private User mUser;
	
	private ProgressDialog mProgressDialog;
	private List<LeagueMember> listLeagueMember;
//	private SimpleCursorAdapter mAdapter;
	private int structure;
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
        initializeImageView();
        initializeListView();
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        
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
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
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
 		bitmap = extras.getParcelable(LeagueDetailBundleKeys.KEY_BITMAP);

 	}
 	/**
 	 * initializes the TextViews
 	 */
 	private void initializeTextViews() {
 		typeTV = (TextView)findViewById(R.id.league_join_detail_type_data);
 		typeTV.setText(isPrivate ? "Private" : "Public");
 		
 		wagerTV = (TextView)findViewById(R.id.league_join_detail_wager_data);
 		wagerTV.setText("$" + wager);
 		
 		potTV = (TextView)findViewById(R.id.league_join_detail_pot_data);
 		potTV.setText("$" + pot);
 		
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
 		
 		numberWinnersTV = (TextView)findViewById(R.id.game_number_of_winners_value);
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
 	 * initialize the imageview
 	 */
 	private void initializeImageView() {
 		mImageView = (ImageView)findViewById(R.id.league_join_detail_imageview);
 		mImageView.setImageBitmap(bitmap);
 	}
 	
 	/**
 	 * initializes the listview
 	 */
 	private void initializeListView() {
// 		mListView = (ListView)findViewById(R.id.league_join_detail_members_lv);
//		mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_game_leader, null, GamesFragment.fromArgs, GamesFragment.toArgs, 0);
//		mAdapter.setViewBinder(new MyViewBinder());
//		mListView.setAdapter(mAdapter);
 	}
 	
  	/**
 	 * join the game selected by user
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
 	 * opens up the faq browser
 	 */
 	private void showFaqBrowser() {
 		//TODO change url to point to faq url
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fitsby.com/faq.html"));
 		startActivity(browserIntent);
 	}
 	
    /**
     * 
     */
    private void showConfirmation() {
	  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setMessage("Are you sure you want to join this game?")
    			.setCancelable(false)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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

		protected void onPostExecute(PrivateLeagueResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response.wasSuccessful()) {
        		League league = response.getLeague();
        		startDateTV.setText(" " + league.getStartDate());
        		structure = league.getStructure();
        		numberWinnersTV.setText(" " + league.getStructure());
        	}
        		
        	new CreatorAsyncTask().execute();
        }
    }
    
    private class CreatorAsyncTask extends AsyncTask<String, Void, CreatorResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
                    "Gathering the game host's data...");
		}
		
        protected CreatorResponse doInBackground(String... params) {
        	CreatorResponse response = LeagueCommunication.getCreator(leagueId+"");
        	return response;
        }

		protected void onPostExecute(CreatorResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response.wasSuccessful()) {
            	firstNameTV.setText(" " + response.getCreatorFirstName());
        	}	
//        	new CursorDataAsyncTask().execute();
        }
    }
    
    private class JoinLeagueAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
                    "Creating your game...");
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = LeagueCommunication.joinLeague(params[0], params[1],
        			"", "", "", "");
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	if (response.wasSuccessful()) {
        		try {
        			ApplicationUser appData = (ApplicationUser)getApplicationContext();
//            		Intent intent = new Intent(LeagueJoinDetailActivity.this, LoggedinActivity.class);
        			Intent intent = new Intent(LeagueJoinDetailActivity.this, FriendInviteActivity.class);
            		intent.putExtra(CreditCardBundleKeys.KEY_LEAGUE_ID, appData.getLeagueId());
            		startActivity(intent);
        		} catch(Exception e) {
        		}
        	} else {
        		Toast toast = Toast.makeText(LeagueJoinDetailActivity.this, "The game could not be joined at the moment.", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
//    private class CursorDataAsyncTask extends AsyncTask<String, Void, Cursor> {
//    	
//		protected void onPreExecute() {
//            mProgressDialog = ProgressDialog.show(LeagueJoinDetailActivity.this, "",
//                    "Getting game members...");
//		}
//		
//        protected Cursor doInBackground(String... params) {
//        	Cursor cursor = GamesLeaderCommunication.getGamesLeader(leagueId);
//        	return cursor;
//        }
//
//		protected void onPostExecute(Cursor cursor) {
//			mProgressDialog.cancel();
////        	mAdapter.swapCursor(cursor);
////        	mAdapter.notifyDataSetChanged();
//
//        }
//    }
    
    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewId = view.getId();
            Log.d(TAG, cursor.getClass().toString());
            if(viewId == R.id.list_item_game_leader_imageview) {
            	ImageView profilePic = (ImageView) view;
            	byte[] bytes = cursor.getBlob(columnIndex);
            	profilePic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            } else if (viewId == R.id.winner) {
            	int rank = cursor.getInt(columnIndex);
            	if (rank > structure) {
            		view.setVisibility(View.INVISIBLE);
            		Log.d(TAG, "setting cup invisible: " + rank);
            	} else {
            		view.setVisibility(View.VISIBLE);
            		Log.d(TAG, "setting cup visible: " + rank);

            	}
            		
            		
            } else {
            	TextView name = (TextView) view;
            	name.setText(cursor.getString(columnIndex));
            }
            
            return true;
        }
    }
    
}
