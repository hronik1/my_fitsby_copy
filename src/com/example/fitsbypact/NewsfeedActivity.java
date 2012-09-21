package com.example.fitsbypact;

import loaders.NewsfeedCursorLoader;
import loaders.PublicLeaguesCursorLoader;
import dbtables.User;
import widgets.NavigationBar;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Button;

public class NewsfeedActivity extends Activity 
	implements OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "NewsfeedActivity";
	
	private NavigationBar navigation;
	private int userID;

	private Spinner gamesSpinner;
	private ListView newsfeedLV;
	private EditText commentET;
	private Button submitButton;
	
	private SimpleCursorAdapter mAdapter;
	private int[] toArgs = { R.id.list_item_newsfeed_first_name, 
			R.id.list_item_newsfeed_last_name, R.id.list_item_newsfeed_timestamp,
			R.id.list_item_newsfeed_message };
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        
        Log.i(TAG, "onCreate");
        
        Intent intent = getIntent();
        if(intent == null || intent.getExtras() == null)
        	userID = -1;
        else
        	userID = intent.getExtras().getInt(User.ID_KEY);
        
        //TODO loadermanager stuffs
        initializeNavigationBar();
        initializeButtons();
        initializeListView();
    }

    /**
     * called when optionsmenu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_newsfeed, menu);
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
	 * initialized NavigationBar for use
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.games_navigation_bar);
		navigation.setParentActivity(this);
		navigation.setUserID(userID);
		navigation.turnOffTV("newsfeed");
	}
	
	/**
	 * initialize the EditText
	 */
	private void initializeEditText() {
		commentET = (EditText)findViewById(R.id.newsfeed_et_comment);
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons() {
		submitButton = (Button)findViewById(R.id.newsfeed_button_submit);
		submitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				submit();
			}
		});
	}
	
	/**
	 * method which gets the data from the edit text and submits that
	 */
	private void submit() {
		//TODO submit comment to newsfeed
	}
	/**
	 * initialize the listview
	 */
	private void initializeListView() {
		newsfeedLV = (ListView)findViewById(R.id.newsfeed_list_view);
		newsfeedLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		//TODO add manager, adapter and loader
    	mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_newsfeed, null,
    			NewsfeedCursorLoader.FROM_ARGS, toArgs, 0);
    	newsfeedLV.setAdapter(mAdapter);
	}
	
	/**
	 * initialize the spinner
	 */
	private void initializeSpinner() {
		gamesSpinner = (Spinner)findViewById(R.id.newsfeed_spinner);
		gamesSpinner.setOnItemSelectedListener(this);
	}
	
	/** OnItemSelected callbacks **/
	
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
    
    /** end OnItemSelected callbacks **/
    
    
    /** LoaderManager callBacks **/
    
    /**
     * 
     * @param id
     * @param args
     * @return
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	return new NewsfeedCursorLoader(this);
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
}
