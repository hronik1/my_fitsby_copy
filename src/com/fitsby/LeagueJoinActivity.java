package com.fitsby;

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
import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

public class LeagueJoinActivity extends KiipFragmentActivity 
	implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private final static String TAG = "LeagueJoinActivity";
	
	private Button buttonSubmit;
	private Button buttonCreate;
	
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etInviteCode;
	private ListView leagueLV;
	private User mUser;
	private SimpleCursorAdapter mAdapter;
	private PublicLeaguesCursorLoader mPublicLeaguesCursorLoader;
	private int[] toArgs = { R.id.list_item_public_leagues_creator, R.id.list_item_public_leagues_id, R.id.list_item_public_leagues_players,
			R.id.list_item_public_leagues_wager, R.id.list_item_public_leagues_goal, R.id.list_item_public_leagues_duration };
	

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
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
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
    	
    	buttonCreate = (Button)findViewById(R.id.league_join_button_create);
    	buttonCreate.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			create();
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
    		Toast toast = Toast.makeText(this, "Please fill out both Game Host's First Name and Game ID", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else
    		new FindGamesAsyncTask().execute(id, firstName, mUser.getID() + "");
    }
    
    /**
     * goes to create activity
     */
    private void create() {
    	Intent intent = new Intent(this, LeagueCreateActivity.class);
    	startActivity(intent);
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
     * 
     * @param id
     * @param args
     * @return
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	Log.d(TAG, "inited loader");
    	mPublicLeaguesCursorLoader = new PublicLeaguesCursorLoader(this, mUser.getID());
    	return mPublicLeaguesCursorLoader;
    }
    
    /**
     * callback for finishing of loader
     * @param loader
     * @param data
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

