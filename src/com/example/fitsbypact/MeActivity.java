package com.example.fitsbypact;

import responses.StatsResponse;
import servercommunication.LeagueCommunication;
import servercommunication.UserCommunication;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbtables.Stats;
import dbtables.User;
import widgets.NavigationBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

public class MeActivity extends Activity {

	private final static String TAG = "MeActivity";
	
	private TextView nameTV;
	private TextView joinTV;
	private TextView earningsTV;
	
	private Button logoutButton;
	private Button changePictureButton;
	private Button submitButton;
	
	private EditText emailET;
	private EditText oldPasswordET;
	private EditText newPasswordET;
	private EditText newPasswordConfirmET;
	
	private NavigationBar navigation;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        initializeNavigationBar();
        initializeTextViews();
        initializeButtons();
        initializeEditTexts();
        
        new StatsAsyncTask().execute(mUser.getID());
    }

    /**
     * called when optionsmenu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_me, menu);
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
	    FlurryAgent.logEvent("Me Activity");
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
		navigation = (NavigationBar)findViewById(R.id.me_navigation_bar);
		navigation.setParentActivity(this);
		navigation.turnOffTV("me");
	}
	
	/**
	 * initialize the textviews
	 */
	public void initializeTextViews() {
		nameTV = (TextView)findViewById(R.id.me_textview_name);
		nameTV.setText(mUser.getFirstName() + " " + mUser.getLastName());
		
		joinTV = (TextView)findViewById(R.id.me_textview_join_date);
		//TODO add join date
		
		earningsTV = (TextView)findViewById(R.id.me_textview_total_money_earned_money);
		//add earnings for user
	}
	
	/**
	 * initializes the editTexts
	 */
	private void initializeEditTexts() {
		emailET = (EditText)findViewById(R.id.me_settings_et_change_email);
		oldPasswordET = (EditText)findViewById(R.id.me_settings_et_old_password);
		newPasswordET = (EditText)findViewById(R.id.me_settings_et_new_password);
		newPasswordConfirmET = (EditText)findViewById(R.id.me_settings_et_new_password_confirm);
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons() {
		logoutButton = (Button)findViewById(R.id.me_settings_button_logout);
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});
		
		changePictureButton = (Button)findViewById(R.id.me_settings_button_change_picture);
		changePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changePicture();
			}
		});
		
		submitButton = (Button)findViewById(R.id.me_settings_button_submit); 
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		
	}
	
	/**
	 * logs the user out
	 */
	private void logout() {
		//TODO implement
	}
	
	/**
	 * change picture
	 */
	private void changePicture() {
		//TODO figure out how to do and then implement
	}
	
	/**
	 * submits user input info
	 */
	private void submit() {
		//TODO figure out what need to be submit and then submit it
	}
	
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class StatsAsyncTask extends AsyncTask<Integer, Void, StatsResponse> {
        protected StatsResponse doInBackground(Integer... params) {
        	StatsResponse response = UserCommunication.getStats(params[0]);
        	return response;
        }

        protected void onPostExecute(StatsResponse response) {
        	if (response.wasSuccessful()) {
        		Stats stats = response.getStats();
        		earningsTV.setText(stats.getMoneyEarned());
        	} else {
        		Toast.makeText(getApplicationContext(), "stats failed", Toast.LENGTH_LONG).show();
        	}
        	
        }
    }
}
