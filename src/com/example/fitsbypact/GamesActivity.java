package com.example.fitsbypact;

import dbtables.User;
import widgets.NavigationBar;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class GamesActivity extends Activity implements OnItemSelectedListener {

	private static final String TAG = "GamesActivity";
	
	private NavigationBar navigation;
	private int userID;
	
	private TextView playersTV;
	private TextView wagerTV;
	private TextView durationTV;
	private ProgressBar progressBar;
	private ListView leadersLV;
	private Spinner gamesSpinner;
	
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        
        Log.i(TAG, "onCreate");
        
        Intent intent = getIntent();
        if(intent == null || intent.getExtras() == null)
        	userID = -1;
        else
        	userID = intent.getExtras().getInt(User.ID_KEY);
        
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
		navigation.setUserID(userID);
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
		//TODO add Adapter, Manager, and Loader
	}
	
	/**
	 * initializes the spinner
	 */
	private void initializeSpinner() {
		gamesSpinner = (Spinner)findViewById(R.id.games_spinner);
		gamesSpinner.setOnItemSelectedListener(this);
		//TODO add Adapter
	}
	
	/**
	 * callback to be implemented by onItemSelectedListener interface
	 * called when item is selected
	 */
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	
    	//TODO do something with retrieved item
    }

    /**
     * callback to be implemented by onItemSelectedListener interface
     * called when nothing is selected
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    	
    	//TODO verify that I should indeed do nothing
    }
}

