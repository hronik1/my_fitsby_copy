package com.fitsby;

import responses.LeagueCreateResponse;
import servercommunication.LeagueCommunication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

public class LeagueCreateActivity extends KiipFragmentActivity {
	
	/**
	 * Tag used in logcat.
	 */
	private final static String TAG = "LeagueCreateActivity";
	
	/**
	 * Value to increment wager by.
	 */
	private final static int WAGER_INCREMENT = 1;
	/**
	 * The minimum wager.
	 */
	private final static int MIN_WAGER = 0;
	/**
	 * The initial wager amount.
	 */
	private final static int START_WAGER = 5;
	/**
	 * The amount to increment days by.
	 */
	private final static int DAYS_INCREMENT = 7;
	/**
	 * The maximum amount of days.
	 */
	private final static int MAX_DAYS = 28;
	/**
	 * The minimum number of goal days in a week.
	 */
	private final static int MIN_GOAL_PER_WEEK = 2;
	/**
	 * Number of days in a week
	 */
	private final static int DAYS_PER_WEEK = 7;
	
	/**
	 * Pressed to create game.
	 */
	private Button createButton;
	/**
	 * Checked to make game private.
	 */
	private CheckBox privateCheckBox;
	/**
	 * Pressed to increment wager.
	 */
	private Button wagerPlusButton;
	/**
	 * Pressed to decrement wager.
	 */
	private Button wagerMinusButton;
	/**
	 * Pressed to increment days.
	 */
	private Button daysPlusButton;
	/**
	 * Pressed to decrement days.
	 */
	private Button daysMinusButton;
	/**
	 * Pressed to increment goal.
	 */
	private Button goalsPlusButton;
	/**
	 * Pressed to decrement goal.
	 */
	private Button goalsMinusButton;
	/**
	 * Displays the wager.
	 */
	private TextView wagerTV;
	/**
	 * Displays the days.
	 */
	private TextView daysTV;
	/**
	 * Displays the goals.
	 */
	private TextView goalsTV;
	/**
	 * Pressed to open up faq.
	 */
	private Button faqButton;
	
	/**
	 * The id of the user.
	 */
	private int userID;
	
	/**
	 * Displayed to show progress in asynctasks.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * The session information for the user.
	 */
	private ApplicationUser mApplicationUser;
	
	/**
	 * Callback for the creation of the activity, intitializes views and
	 * obtains session information.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_create);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeCheckBoxes();  
        initializeTextViews();

        mApplicationUser = ((ApplicationUser)getApplicationContext());
        userID = mApplicationUser.getUser().getID();
        
    }

    /**
     * Callback for creation of the option menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_league_create, menu);
        Log.i(TAG, "onCreateOptionsMenu");
        
        return true;
    }

	/**
	 * Callback for the restarting of this activity.
	 */
	@Override
	public void onRestart() {
	    super.onRestart();
	
	    Log.i(TAG, "onRestart");
	}
	
	/**
	 * Callback for when the activity is restarted.
	 */
	@Override
	public void onStart() {
	    super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Game Create Activity");
	    Log.i(TAG, "onStart");
	}
	
	/**
	 * Callback for when the activity is stopped.
	 */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	/**
	 * Callback for when the activity is resumed.
	 */
	@Override
	public void onResume() {
	    super.onResume();
	    
	    Log.i(TAG, "onResume");
	}
	
	/**
	 * Callback for when the activity is paused.
	 */
	@Override
	public void onPause() {
	    super.onPause();
	
	    Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
	}
	
	/**
	 * Callback for when the activity is destroyed.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i(TAG, "onDestroy");
		
	}
    
	/**
	 * Connects checkboxes to layout.
	 */
	private void initializeCheckBoxes() {
		privateCheckBox = (CheckBox)findViewById(R.id.league_create_checkbox);
	}
	
	/**
	 * Connects textviews to layout.
	 */
	private void initializeTextViews() {
		wagerTV = (TextView)findViewById(R.id.league_create_wager);
		wagerTV.setText(START_WAGER + "");
		daysTV = (TextView)findViewById(R.id.league_create_days);
		goalsTV = (TextView)findViewById(R.id.league_create_goals);
	}
	
	/**
	 * Connects buttons to layout and adds listener.
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
		
		goalsPlusButton = (Button)findViewById(R.id.league_create_ib_goals_plus);
		goalsPlusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				incrementGoal();
			}
		});
		
		goalsMinusButton = (Button)findViewById(R.id.league_create_ib_goals_minus);
		goalsMinusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementGoal();
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
	 * Increments the wager and resets it.
	 */
	private void incrementWager() {
		int wager = Integer.parseInt((String)wagerTV.getText());
		if (wager < Integer.MAX_VALUE)
			wager += WAGER_INCREMENT;
		wagerTV.setText(wager + "");
	}
	
	/**
	 * Decrements the wager in the text view if valid, and resets it.
	 */
	private void decrementWager() {
		int wager = Integer.parseInt((String)wagerTV.getText());
		if(wager > MIN_WAGER) {
			wager -= WAGER_INCREMENT;
			wagerTV.setText(wager + "");
		}
	}
	
	/**
	 * Increments the days in the text view if valid, and resets it.
	 */
	private void incrementDays() {
		int days = Integer.parseInt((String)daysTV.getText());
		int goal = Integer.parseInt((String)goalsTV.getText());
		if (days < MAX_DAYS) {
			days += DAYS_INCREMENT;
			daysTV.setText(days + "");
		}
		if (goal < minGoalDays(days))
			goalsTV.setText(minGoalDays(days) + "");
	}
	
	/**
	 * Decrements the days in the text view if valid, and resets it.
	 */
	private void decrementDays() {
		int days = Integer.parseInt((String)daysTV.getText());
		int goal = Integer.parseInt((String)goalsTV.getText());
		if (days > DAYS_INCREMENT) {
			days -= DAYS_INCREMENT;
			daysTV.setText(days + "");
		}
		if (goal > days)
			goalsTV.setText(days + "");
	}
	
	/**
	 * Increment the goal days if valid.
	 */
	private void incrementGoal() {
		int days = Integer.parseInt((String)daysTV.getText());
		int goal = Integer.parseInt((String)goalsTV.getText());
		if (days > goal) {
			goal++;
			goalsTV.setText(goal + "");
		}
	}
	
	/**
	 * Decrement the goal day if valid.
	 */
	private void decrementGoal() {
		int days = Integer.parseInt((String)daysTV.getText());
		int goal = Integer.parseInt((String)goalsTV.getText());
		if (goal > minGoalDays(days)) {
			goal--;
			goalsTV.setText(goal + "");
		}
	}
	
	/**
	 * Calculates the minimum goal for the desired number of days.
	 * 
	 * @param days	the desired number of days
	 * @return		the minimum length that the goal can be
	 */
	private int minGoalDays(int days) {
		return (days/DAYS_PER_WEEK*MIN_GOAL_PER_WEEK);
	}
	
	/**
	 * Opens up browser to faq page.
	 */
	private void showFaqBrowser() {
 		//TODO change url to point to faq url
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fitsby.com/faq.html"));
 		startActivity(browserIntent);
	}
	
	/**
	 * Creates the game specified by the wager and days.
	 */
	private void create() {
		int wager, duration, isPrivate, goal;
		wager = Integer.parseInt((String) wagerTV.getText());
		duration = Integer.parseInt((String) daysTV.getText());
		goal = Integer.parseInt((String) goalsTV.getText());
		isPrivate = privateCheckBox.isChecked() ? 1 : 0;

		if (wager != 0) {
			ApplicationUser appData = (ApplicationUser)getApplicationContext();
			appData.setCreate();
			appData.setUserId(userID);
			appData.setDuration(duration);
			appData.setIsPrivate(isPrivate);
			appData.setWager(wager);
			appData.setGoal(goal);
			Intent intent = new Intent(LeagueCreateActivity.this, CreditCardActivity.class);
			intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
			startActivity(intent);
		} else {
			showConfirmation();
		}
	
	}
	
    /**
     * Opens up a dialog, allowing user to confirm that they actually want to
     * create the game.
     */
    private void showConfirmation() {
	  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setMessage("Are you sure you want to create this game?")
    			.setCancelable(false)
    			.setPositiveButton("Create", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {

    		    		new CreateLeagueAsyncTask().execute(userID+"", daysTV.getText().toString(),
    		    				(privateCheckBox.isChecked() ? "1" : "0"), wagerTV.getText().toString(), goalsTV.getText().toString());
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /**
     * AsyncTask to create league, handles creation on a background thread.
     * 
     * @author brent
     *
     */
    private class CreateLeagueAsyncTask extends AsyncTask<String, Void, LeagueCreateResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(LeagueCreateActivity.this, "",
						"Creating your game...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						CreateLeagueAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected LeagueCreateResponse doInBackground(String... params) {
        	LeagueCreateResponse response = LeagueCommunication.createLeague(Integer.parseInt(params[0]),
        			Integer.parseInt(params[1]), "1".equals(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]),
        			"", "", "", "");
        	return response;
        }

        protected void onPostExecute(LeagueCreateResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }

            	if (response.wasSuccessful()) {
            		Log.d(TAG, "create response: league id" + response.getLeagueId());
            		Intent intent = new Intent(LeagueCreateActivity.this, FriendInviteActivity.class);
            		intent.putExtra(LeagueDetailBundleKeys.KEY_LEAGUE_ID, Integer.parseInt(response.getLeagueId()));
            		startActivity(intent);

            	} else if (response.getError() != null && !response.getError().equals("")) {
            		Toast toast = Toast.makeText(LeagueCreateActivity.this, response.getError(), Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
            	} else {
            		Toast toast = Toast.makeText(LeagueCreateActivity.this, "Your game could not be created at this time.", Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
            		toast.show();
            	}
        }
    }
    
}
