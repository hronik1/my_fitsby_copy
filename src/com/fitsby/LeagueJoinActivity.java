package com.fitsby;

import loaders.PublicLeaguesCursorLoader;
import responses.PrivateLeagueResponse;
import servercommunication.LeagueCommunication;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bundlekeys.LeagueDetailBundleKeys;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import dbtables.User;

/**
 * LeagueJoinActivity is the activity where users will look at the
 * leagues that are available to join.
 *  
 * @author brenthronk
 *
 */
public class LeagueJoinActivity extends KiipFragmentActivity 
	implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	/**
	 * Tag used for logcat messages.
	 */
	private final static String TAG = "LeagueJoinActivity";
	
	/**
	 * Pressed to submit search.
	 */
	private Button buttonSubmit;
	/**
	 * Pressed to open up create activity.
	 */
	private Button buttonCreate;
	/**
	 * EditText where users will enter the first name of the creator of the
	 * game that they are searching for.
	 */
	private EditText etFirstName;
	/**
	 * EditText where users will enter the id of the game that they are 
	 * searching for.
	 */
	private EditText etInviteCode;
	/**
	 * The listview which will contain the leagues.
	 */
	private ListView leagueLV;
	
	/**
	 * The logged in user.
	 */
	private User mUser;
	
	/**
	 * Adapter responsible for mapping data to the listview.
	 */
	private SimpleCursorAdapter mAdapter;
	/**
	 * CursorLoader responsible for gathering underlying data.
	 */
	private PublicLeaguesCursorLoader mPublicLeaguesCursorLoader;
	/**
	 * Array of ids to be mapped to be adapter.
	 */
	private int[] toArgs = { R.id.list_item_public_leagues_creator, R.id.list_item_public_leagues_id, R.id.list_item_public_leagues_players,
			R.id.list_item_public_leagues_wager, R.id.list_item_public_leagues_goal, R.id.list_item_public_leagues_duration };
	

	/**
	 * Callback for when activity is first created, initializes the views and
	 * gathers session information.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_join);
        
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeEditTexts();
        initializeListView();
        
        mUser = ((ApplicationUser)getApplicationContext()).getUser();
        
        getSupportLoaderManager().initLoader(0, null, (android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>) this);
    }

	/**
	 * Callback for when menu is created.
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_join, menu);
        Log.i(TAG, "onCreateOptionsMenu");
        return true;
    }
    
    /**
     * Callback for when activity is restarted.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        Log.i(TAG, "onRestart");
    }

    /**
     * Callback for when activity is starting, starts flurry session.
     */
    @Override
    public void onStart() {
        super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Game Join Activity");
        Log.i(TAG, "onStart");
    }
    
    /**
     * Callback for the when activity is stopped, stops flurry session.
     */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
    /**
     * Callback when activity resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        
        Log.i(TAG, "onResume");
    }
    
    /**
     * Callback when activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
       
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * Callback when activity is destroyed.
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	Log.i(TAG, "onDestroy");
    	
    }
    
    /**
     * Registers buttons from layout and adds listeners.
     */
    private void initializeButtons() {
    	buttonSubmit = (Button)findViewById(R.id.league_join_button_submit);
    	buttonSubmit.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			submit();
    		}
    	});
    	
    	buttonCreate = (Button)findViewById(R.id.league_join_button_create);
    	buttonCreate.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			create();
    		}
    	});
    }
    
    /**
     * Submits private league query entered by user.
     */
    private void submit() {
    	String firstName = etFirstName.getText().toString();
    	String id = etInviteCode.getText().toString();
    	
    	if(firstName.equals("") || id.equals("")) {
    		Toast toast = Toast.makeText(this, "Please fill out both Game Host's First Name and Game ID", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else
    		new FindGamesAsyncTask().execute(id, firstName, mUser.getID() + "");
    }
    
    /**
     * Goes to create activity.
     */
    private void create() {
    	Intent intent = new Intent(this, LeagueCreateActivity.class);
    	startActivity(intent);
    }
    
    /**
     * Registers EditTexts from the layout.
     */
    private void initializeEditTexts() {
    	etFirstName = (EditText)findViewById(R.id.league_join_et_first_name);
    	etInviteCode = (EditText)findViewById(R.id.league_join_et_invite_code);
    }
    
    /**
     * Initialize the list view of leagues.
     */
    private void initializeListView() {
    	leagueLV = (ListView)findViewById(R.id.league_join_list);
    	leagueLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
				int leagueId = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_ID));
				int goal = cursor.getInt(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_GOAL));
				int players = cursor.getInt(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_NUM_PLAYERS));
				int wager = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_WAGER));
				int duration = cursor.getInt(cursor.getColumnIndex(LeagueTableHandler.KEY_DURATION));
				boolean isPrivate = false;
            	byte[] bytes = cursor.getBlob(cursor.getColumnIndex(PublicLeaguesCursorLoader.KEY_BITMAP));
            	Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				gotoLeagueDetails(leagueId, players, wager, goal, isPrivate, duration, bitmap);
			}
    		
    	});
    	
    	mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_public_leagues, null,
    			PublicLeaguesCursorLoader.FROM_ARGS, toArgs, 0);
    	mAdapter.setViewBinder(new MyViewBinder());
    	leagueLV.setAdapter(mAdapter);
    }
    
    /**
     * Opens up the detail view of the selceted league.
     * 
     * @param leagueId	id of the selected league
     * @param players	number of players
     * @param wager		the amount being wagered
     * @param goal		the goal number of days
     * @param isPrivate	whether the league is private
     * @param duration	the length of the league
     * @param bitmap	the image corresponding to the league creator
     */
    private void gotoLeagueDetails(int leagueId, int players, int wager, int goal, boolean isPrivate, int duration, Bitmap bitmap) {
    	try {
    		Intent intent = new Intent(this, LeagueJoinDetailActivity.class);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_LEAGUE_ID, leagueId);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_PLAYERS, players);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_WAGER, wager);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_GOAL, goal);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_TYPE, isPrivate ? 1 : 0);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_DURATION, duration);
    		intent.putExtra(LeagueDetailBundleKeys.KEY_BITMAP, bitmap);
    		startActivity(intent);
    	} catch(Exception e) {
    		//TODO add robustness, remove from production code.
    		Toast toast = Toast.makeText(getApplicationContext(), "Can't perform operation", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	}
    }
    
    /** LoaderManager callBacks **/
    
    /**
     * Callback for creation of the loader.
     * 
     * @param id	not used
     * @param args	not used
     * @return		initialized loader for the leagues
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	Log.d(TAG, "inited loader");
    	mPublicLeaguesCursorLoader = new PublicLeaguesCursorLoader(this, mUser.getID());
    	return mPublicLeaguesCursorLoader;
    }
    
    /**
     * Callback for finishing of loader.
     * 
     * @param loader	not used
     * @param data		data to be loaded
     */
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    	if (data != null) {
    		mAdapter.swapCursor(data);
    	} else {
    		Toast toast = Toast.makeText(LeagueJoinActivity.this, getString(R.string.timeout_message), Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	}
    	try {
    		mPublicLeaguesCursorLoader.getProgressDialog().dismiss();
    	} catch (Exception e) { }
    }
    
    /**
     * Callback for resetting of loader.
     * 
     * @param loader	not used
     */
    public void onLoaderReset(Loader<Cursor> loader) {
    	mAdapter.swapCursor(null);
    }
    
    /** end LoaderManager callbacks **/
   
    /**
     * FindGamesAsyncTask submits, on a background thread, the query for a
     * league.
     * 
     * @author brenthronk
     *
     */
    private class FindGamesAsyncTask extends AsyncTask<String, Void, PrivateLeagueResponse> {
    	//TODO possible add onPreExecute to start a progressdialog
        protected PrivateLeagueResponse doInBackground(String... params) {
        	PrivateLeagueResponse response = LeagueCommunication.getPrivateLeague(params[0], params[1], params[2]);
        	return response;
        }

        protected void onPostExecute(PrivateLeagueResponse response) {
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Limited or no internet connectivity", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}  else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(LeagueJoinActivity.this, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "No games with those credentials exist", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		League league = response.getLeague();
        		boolean isPrivate = (league.isPrivate() == 0 ? false : true);
        		gotoLeagueDetails(league.getId(), league.getPlayers(), league.getWager(),
        				league.getGoal(), isPrivate, league.getDuration(), league.getBitmap());
 
        	}
        }
    }
    
    /**
     * MyViewBinder provides custom mapping of specific elements within
     * specific elements of a list row, specifically it handles the bitmap.
     * 
     * @author brenthronk
     *
     */
    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewId = view.getId();
            if(viewId == R.id.list_item_public_leagues_creator) {
            	ImageView profilePic = (ImageView) view;
            	byte[] bytes = cursor.getBlob(columnIndex);
            	profilePic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            } else {
            	TextView name = (TextView) view;
            	name.setText(cursor.getString(columnIndex));
            }
            
            return true;
        }
    }
    
}

