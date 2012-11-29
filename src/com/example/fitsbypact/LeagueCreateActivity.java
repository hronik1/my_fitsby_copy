package com.example.fitsbypact;

import responses.LeagueCreateResponse;
import responses.StatusResponse;
import servercommunication.LeagueCommunication;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import bundlekeys.CreditCardBundleKeys;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import dbtables.LeagueMember;
import dbtables.User;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

public class LeagueCreateActivity extends Activity {
	
	private final static String TAG = "LeagueCreateActivity";
	
	private final static int WAGER_INCREMENT = 1;
	private final static int MIN_WAGER = 0;
	private final static int START_WAGER = 5;
	private final static int DAYS_INCREMENT = 7;
	private final static int MAX_DAYS = 28;
	
	private Button createButton;
	private CheckBox createCheckBox;
	private Button wagerPlusButton;
	private Button wagerMinusButton;
	private Button daysPlusButton;
	private Button daysMinusButton;
	private TextView wagerTV;
	private TextView daysTV;
	private Button faqButton;
	private RadioButton top3RB;
	private RadioButton takeAllRB;
	
	private User mUser;
	private int userID;
	
	private ProgressDialog mProgressDialog;
	
	private DatabaseHandler dbHandler;
	private LeagueTableHandler leagueTableHandler;
	private LeagueMemberTableHandler leagueMemberTableHandler;
	
	private ApplicationUser mApplicationUser;
	
	/**
	 * called when activity is first created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_create);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeRadioButtons();
        initializeCheckBoxes();  
        initializeTextViews();
        
        dbHandler = DatabaseHandler.getInstance(getApplicationContext());
        leagueTableHandler = dbHandler.getLeagueTableHandler();
        leagueMemberTableHandler = dbHandler.getLeagueMemberTableHandler();
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        userID = mApplicationUser.getUser().getID();
        
    }

    /**
     * called when menu is first create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_create, menu);
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
	 * initializes the checkbox
	 */
	private void initializeCheckBoxes() {
		createCheckBox = (CheckBox)findViewById(R.id.league_create_checkbox);
	}
	
	/**
	 * initializes the text views
	 */
	private void initializeTextViews() {
		wagerTV = (TextView)findViewById(R.id.league_create_wager);
		wagerTV.setText(START_WAGER + "");
		daysTV = (TextView)findViewById(R.id.league_create_days);
	}
	
	/**
	 * initializes create button
	 */
	private void initializeButtons() {
		createButton = (Button)findViewById(R.id.league_create_button_create);
	   	createButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			create();
    		}
    	});
	   	
		wagerPlusButton = (Button)findViewById(R.id.league_create_ib_wager_plus);
		wagerPlusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				incrementWager();
			}
		});
		
		wagerMinusButton = (Button)findViewById(R.id.league_create_ib_wager_minus);
		wagerMinusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementWager();
			}	
		});
		
		daysPlusButton = (Button)findViewById(R.id.league_create_ib_days_plus);
		daysPlusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				incrementDays();
			}
		});
		
		daysMinusButton = (Button)findViewById(R.id.league_create_ib_days_minus);
		daysMinusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementDays();
			}
		});
		
		faqButton = (Button)findViewById(R.id.league_create_faq_button);
		faqButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showFaqBrowser();
			}
		});
	}
	
	/**
	 * initializes the radiobuttons
	 */
	private void initializeRadioButtons() {
		top3RB = (RadioButton)findViewById(R.id.league_create_rb_top3);
		top3RB.setChecked(true);
		
		takeAllRB = (RadioButton)findViewById(R.id.league_create_rb_takeall);
	}
	
	/**
	 * increments the wager and resets it
	 */
	private void incrementWager() {
		int wager = Integer.parseInt((String)wagerTV.getText());
		if (wager < Integer.MAX_VALUE)
			wager += WAGER_INCREMENT;
		wagerTV.setText(wager + "");
	}
	
	/**
	 * decrements the wager in the text view if valid, and resets it
	 */
	private void decrementWager() {
		int wager = Integer.parseInt((String)wagerTV.getText());
		if(wager > MIN_WAGER) {
			wager -= WAGER_INCREMENT;
			wagerTV.setText(wager + "");
		}
	}
	
	/**
	 * increments the days in the text view if valid, and resets it
	 */
	private void incrementDays() {
		int days = Integer.parseInt((String)daysTV.getText());
		if (days < MAX_DAYS) {
			days += DAYS_INCREMENT;
			daysTV.setText(days + "");
		}
	}
	
	/**
	 * decrements the days in the text view if valid, and resets it
	 */
	private void decrementDays() {
		int days = Integer.parseInt((String)daysTV.getText());
		if (days > DAYS_INCREMENT) {
			days -= DAYS_INCREMENT;
			daysTV.setText(days + "");
		}
	}
	
	/**
	 * opens up browser to faq page
	 */
	private void showFaqBrowser() {
 		//TODO change url to point to faq url
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fitsby.com/faq.html"));
 		startActivity(browserIntent);
	}
	
	/**
	 * Creates the game specified by the wager and days
	 */
	private void create() {
		int wager, duration, isPrivate;
		wager = Integer.parseInt((String) wagerTV.getText());
		duration = Integer.parseInt((String) daysTV.getText());
		isPrivate = createCheckBox.isChecked() ? 1 : 0;

		if (wager != 0) {
			ApplicationUser appData = (ApplicationUser)getApplicationContext();
			appData.setCreate();
			appData.setUserId(userID);
			appData.setDuration(duration);
			appData.setIsPrivate(isPrivate);
			appData.setWager(wager);
			Intent intent = new Intent(LeagueCreateActivity.this, CreditCardActivity.class);
			intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
			startActivity(intent);
		} else {
    		new CreateLeagueAsyncTask().execute(userID+"", duration+"",
    				isPrivate+"", wager+"", mUser.getFirstName());
		}
	
	}
	
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class CreateLeagueAsyncTask extends AsyncTask<String, Void, LeagueCreateResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LeagueCreateActivity.this, "",
                    "Creating your free league...");
		}
		
        protected LeagueCreateResponse doInBackground(String... params) {
        	LeagueCreateResponse response = LeagueCommunication.createLeague(Integer.parseInt(params[0]),
        			Integer.parseInt(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]),
        			"", "", "", "");
        	return response;
        }

        protected void onPostExecute(LeagueCreateResponse response) {
        	mProgressDialog.dismiss();

            	if (response.wasSuccessful()) {
            		Intent intent = new Intent(LeagueCreateActivity.this, LoggedinActivity.class);
            		intent.putExtra(CreditCardBundleKeys.KEY_LEAGUE_ID, response.getLeagueId());
            		startActivity(intent);

            	} else {
            		Toast toast = Toast.makeText(LeagueCreateActivity.this, "Sorry, but your league could not be created at this time.", Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
            		toast.show();
            	}
        }
    }
    
}
