package com.example.fitsbypact;

import bundlekeys.LeagueDetailBundleKeys;
import dbhandlers.LeagueTableHandler;
import loaders.PublicLeaguesCursorLoader;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import com.flurry.android.FlurryAgent;

public class LeagueJoinActivity extends Activity 
	implements LoaderManager.LoaderCallbacks<Cursor> {

	private final static String TAG = "LeagueJoinActivity";
	
	private Button buttonSubmit;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etInviteCode;
	private ListView leagueLV;
	
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
	    FlurryAgent.logEvent("Game Create Activity");
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
    	leagueLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
				int leagueId = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_ID));
				int pot = cursor.getInt(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_POT));
				int players = cursor.getInt(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_NUM_PLAYERS));
				int wager = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_WAGER));
				boolean isPrivate = false;
				gotoLeagueDetails(leagueId, players, wager, pot, isPrivate);
			}
    		
    	});
    	
    	mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_public_leagues, null,
    			PublicLeaguesCursorLoader.FROM_ARGS, toArgs, 0);
    	leagueLV.setAdapter(mAdapter);
    }
    
    private void gotoLeagueDetails(int leagueId, int players, int wager, int pot, boolean isPrivate) {
    	try {
    		Intent intent = new Intent(this, LeagueJoinDetailActivity.class);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_LEAGUE_ID, leagueId);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_PLAYERS, players);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_WAGER, wager);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_POT, pot);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_TYPE, isPrivate ? 1 : 0);
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
    	return new PublicLeaguesCursorLoader(this);
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

