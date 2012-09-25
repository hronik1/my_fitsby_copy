package com.example.fitsbypact;

import java.util.ArrayList;
import java.util.List;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import loaders.GameLeaderCursorLoader;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GamesActivity extends Activity
	implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "GamesActivity";
	
	private NavigationBar navigation;
	
	private TextView playersTV;
	private TextView wagerTV;
	private TextView durationTV;
	private ProgressBar progressBar;
	private ListView leadersLV;
	private Spinner gamesSpinner;
	
	private SimpleCursorAdapter mAdapter;
	private final static String[] fromArgs = {UserTableHandler.KEY_FIRST_NAME, UserTableHandler.KEY_LAST_NAME, LeagueMemberTableHandler.KEY_CHECKINS};
	private final static int[] toArgs = {R.id.list_item_game_leader_name, R.id.list_item_game_leader_last_name,
			R.id.list_item_game_leader_checkins};
	
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
		List<String> list = new ArrayList<String>();
		for(LeagueMember member: listLeagueMember) {
			list.add("league " + member.getLeagueId());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gamesSpinner.setAdapter(dataAdapter);
		gamesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//TODO show game states of element clicked on
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				/** do nothing **/
			}
			
		});
		//TODO add Adapter
	}

    
    /** LoaderManager callBacks **/
    
    /**
     * 
     * @param id
     * @param args
     * @return
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	//TODO initialize proper cursor and return that
    	return new GameLeaderCursorLoader(this);
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
    	//TODO make sure that adapter is no longer using cursor
    	mAdapter.swapCursor(null);
    }
    
    /** end LoaderManager callbacks **/
}

