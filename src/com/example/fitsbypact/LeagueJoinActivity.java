package com.example.fitsbypact;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;

public class LeagueJoinActivity extends ListActivity 
	implements OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

	private final static String TAG = "LeagueJoinActivity";
	
	private Button buttonSubmit;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etInviteCode;
	private ListView leagueLV;
	
	/**
	 * called when activity is first created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_join);
        
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeEditTexts();
        initializeListView();
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
    	//TODO submit form to database
    }
    
    /**
     * registers EditTexts from the layout
     */
    private void initializeEditTexts() {
    	etFirstName = (EditText)findViewById(R.id.league_join_et_first_name);
    	etLastName =(EditText)findViewById(R.id.league_join_et_last_name);
    	etInviteCode = (EditText)findViewById(R.id.league_join_et_invite_code);
    }
    
    /**
     * initialize the list view
     */
    private void initializeListView() {
    	leagueLV = (ListView)findViewById(R.id.league_join_list);
    	//TODO set Loader, Adapter, and manager
    	//set onItemClickListener
    	leagueLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// TODO actually do something cool
				
			}
    		
    	});
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
    	//TODO initialize proper cursor and return that
    	return null;
    }
    
    /**
     * callback for finishing of loader
     * @param loader
     * @param data
     */
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    	//TODO swap cursor in
    	//TODO show list
    }
    
    /**
     * callback for resetting of loader
     * @param loader
     */
    public void onLoaderReset(Loader<Cursor> loader) {
    	//TODO make sure that adapter is no longer using cursor
    }
    
    /** end LoaderManager callbacks **/
}

