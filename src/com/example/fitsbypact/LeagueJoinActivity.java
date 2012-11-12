package com.example.fitsbypact;

import responses.PrivateLeagueResponse;
import responses.UserResponse;
import servercommunication.LeagueCommunication;
import servercommunication.UserCommunication;
import bundlekeys.LeagueDetailBundleKeys;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import dbtables.User;
import loaders.PublicLeaguesCursorLoader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

public class LeagueJoinActivity extends FragmentActivity 
	implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private final static String TAG = "LeagueJoinActivity";
	
	private Button buttonSubmit;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etInviteCode;
	private ListView leagueLV;
	private User mUser;
	private SimpleCursorAdapter mAdapter;
	private int[] toArgs = { R.id.list_item_public_leagues_id, R.id.list_item_public_leagues_players,
			R.id.list_item_public_leagues_wager, R.id.list_item_public_leagues_duration, R.id.list_item_public_leagues_pot };
	

	/**
	 * called when activity is first created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_join);
        
        Log.i(TAG, "onCreate");
        
        //TODO loadermanager stuffs
        initializeButtons();
        initializeEditTexts();
        initializeListView();
        
        mUser = ((ApplicationUser)getApplicationContext()).getUser();
        
        getSupportLoaderManager().initLoader(0, null, (android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>) this);
    }

	/**
	 * called when menu is created
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_join, menu);
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Game Join Activity");
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
     * registers buttons from layout and adds listeners
     */
    private void initializeButtons() {
    	buttonSubmit = (Button)findViewById(R.id.league_join_button_submit);
    	buttonSubmit.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			submit();
    		}
    	});
    }
    
    /**
     * submits data entered by user
     */
    private void submit() {
    	String firstName = etFirstName.getText().toString();
    	String id = etInviteCode.getText().toString();
    	
    	if(firstName.equals("") || id.equals("")) {
    		Toast toast = Toast.makeText(this, "Sorry, but you must fill out both fields of Game Creator's First Name and Game ID", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else
    		new FindGamesAsyncTask().execute(id, firstName);
    }
    
    /**
     * registers EditTexts from the layout
     */
    private void initializeEditTexts() {
    	etFirstName = (EditText)findViewById(R.id.league_join_et_first_name);
    	etInviteCode = (EditText)findViewById(R.id.league_join_et_invite_code);
    }
    
    /**
     * initialize the list view
     */
    private void initializeListView() {
    	leagueLV = (ListView)findViewById(R.id.league_join_list);
    	leagueLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
				int leagueId = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_ID));
				int pot = cursor.getInt(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_POT));
				int players = cursor.getInt(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_NUM_PLAYERS));
				int wager = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_WAGER));
				int duration = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_DURATION));
				boolean isPrivate = false;
				gotoLeagueDetails(leagueId, players, wager, pot, isPrivate, duration);
			}
    		
    	});
    	
    	mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_public_leagues, null,
    			PublicLeaguesCursorLoader.FROM_ARGS, toArgs, 0);
    	leagueLV.setAdapter(mAdapter);
    }
    
    private void gotoLeagueDetails(int leagueId, int players, int wager, int pot, boolean isPrivate, int duration) {
    	try {
    		Intent intent = new Intent(this, LeagueJoinDetailActivity.class);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_LEAGUE_ID, leagueId);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_PLAYERS, players);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_WAGER, wager);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_POT, pot);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_TYPE, isPrivate ? 1 : 0);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_DURATION, duration);
    		startActivity(intent);
    	} catch(Exception e) {
    		//TODO add robustness, remove from production code.
    		Toast toast = Toast.makeText(getApplicationContext(), "sorry cant perform operation", Toast.LENGTH_LONG);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	Log.d(TAG, "inited loader");
    	return new PublicLeaguesCursorLoader(this, mUser.getID());
    }
    
    /**
     * callback for finishing of loader
     * @param loader
     * @param data
     */
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    	mAdapter.swapCursor(data);
    }
    
    /**
     * callback for resetting of loader
     * @param loader
     */
    public void onLoaderReset(Loader<Cursor> loader) {
    	mAdapter.swapCursor(null);
    }
    
    /** end LoaderManager callbacks **/
    
    /**
     * AsyncTask class to login the user
     * @author brent
     *
     */
    private class FindGamesAsyncTask extends AsyncTask<String, Void, PrivateLeagueResponse> {
        protected PrivateLeagueResponse doInBackground(String... params) {
        	PrivateLeagueResponse response = LeagueCommunication.getPrivateLeague(params[0], params[1]);
        	return response;
        }

        protected void onPostExecute(PrivateLeagueResponse response) {
        	if (response == null ) {
        		Toast.makeText(getApplicationContext(), "Sorry, you don't have a connection to the internet", Toast.LENGTH_LONG).show();
        	} else if (!response.wasSuccessful()){
        		Toast.makeText(getApplicationContext(), "Sorry, but no games with those credentials exist", Toast.LENGTH_LONG).show();
        	} else {
        		League league = response.getLeague();
        		boolean isPrivate = (league.isPrivate() == 0 ? false : true);
        		gotoLeagueDetails(league.getId(), league.getPlayers(), league.getWager(),
        				league.getStakes(), isPrivate, league.getDuration());
        	}
        }
    }
}

