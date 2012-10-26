package com.example.fitsbypact;

import java.util.ArrayList;
import java.util.List;

import servercommunication.NewsfeedCommunication;
import servercommunication.UserCommunication;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import loaders.NewsfeedCursorLoader;
import loaders.PublicLeaguesCursorLoader;
import dbhandlers.CommentTableHandler;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbtables.Comment;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;


public class NewsfeedActivity extends Activity 
	implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "NewsfeedActivity";
	
	private NavigationBar navigation;


	private Spinner gamesSpinner;
	private ListView newsfeedLV;
	private EditText commentET;
	private Button submitButton;
	
	private SimpleCursorAdapter mAdapter;
	private int[] toArgs = { R.id.list_item_newsfeed_first_name, 
			R.id.list_item_newsfeed_last_name, R.id.list_item_newsfeed_timestamp,
			R.id.list_item_newsfeed_message };
	
	private ApplicationUser mApplicationUser;
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private CommentTableHandler mCommentTableHandler;
	
	private List<LeagueMember> listLeagueMember;
	private User user;
	private int spinnerPosition;
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        user = mApplicationUser.getUser();
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mCommentTableHandler = mdbHandler.getCommentTableHandler();
        listLeagueMember = mLeagueMemberTableHandler.getAllLeagueMembersByUserId(user.getID());
        
        //TODO loadermanager stuffs
        initializeNavigationBar();
        initializeButtons();
        initializeListView();
        initializeEditText();
        initializeSpinner();

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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");	    
	    FlurryAgent.onPageView();	    
	    FlurryAgent.logEvent("Newsfeed Activity");
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
	 * initialized NavigationBar for use
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.newsfeed_navigation_bar);
		navigation.setParentActivity(this);
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
		if(listLeagueMember == null || listLeagueMember.size() == 0) {
			//TODO maybe do something more robust
			Toast toast = Toast.makeText(getApplicationContext(), "sorry no leagues", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		else {
			LeagueMember member = listLeagueMember.get(spinnerPosition);
			Comment comment = new Comment(member.getId(), member.getLeagueId(), commentET.getText().toString());
			//mCommentTableHandler.addComment(comment);
			new AddCommentAsyncTask().execute(member.getId()+"", comment.getMessage());
		}
		
	}
	
	/**
	 * initialize the listview
	 */
	@SuppressLint("NewApi")
	private void initializeListView() {
		newsfeedLV = (ListView)findViewById(R.id.newsfeed_list_view);
		newsfeedLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// TODO add appropriate click functionality here
			}
		});
    	mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_newsfeed, null,
    			NewsfeedCursorLoader.FROM_ARGS, toArgs, 0);
    	newsfeedLV.setAdapter(mAdapter);
	}
	
	/**
	 * initialize the spinner
	 */
	private void initializeSpinner() {
		gamesSpinner = (Spinner)findViewById(R.id.newsfeed_spinner);
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
				//TODO show game states of element clicked on
				//TODO change comments showing to be those of this league
				spinnerPosition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				/** do nothing **/
			}
			
		});
	}
    
    /** LoaderManager callBacks **/
    
    /**
     * 
     * @param id
     * @param args
     * @return
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	return new NewsfeedCursorLoader(this, id);
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
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class AddCommentAsyncTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
        	String string = NewsfeedCommunication.addComment(Integer.parseInt(params[0]), params[1]);
        	return string;
        }

        protected void onPostExecute(String string) {
        	Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        }
    }
}
