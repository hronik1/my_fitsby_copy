package com.example.fitsbypact;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import bundlekeys.CreditCardBundleKeys;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import dbtables.LeagueMember;
import dbtables.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class LeagueCreateActivity extends Activity {
	
	private final static String TAG = "LeagueCreateActivity";
	
	private final static int WAGER_INCREMENT = 5;
	private final static int DAYS_INCREMENT = 7;
	private final static int MAX_DAYS = 28;
	
	private Button createButton;
	private CheckBox createCheckBox;
	private ImageButton wagerPlusIB;
	private ImageButton wagerMinusIB;
	private ImageButton daysPlusIB;
	private ImageButton daysMinusIB;
	private TextView wagerTV;
	private TextView daysTV;
	
	private int userID;
	
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
        initializeCheckBoxes();  
        initializeImageButtons();
        initializeTextViews();
        
        dbHandler = DatabaseHandler.getInstance(getApplicationContext());
        leagueTableHandler = dbHandler.getLeagueTableHandler();
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
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
	 * initializes the checkbox
	 */
	private void initializeCheckBoxes() {
		createCheckBox = (CheckBox)findViewById(R.id.league_create_checkbox);
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
	}
	
	/**
	 * initializes all of the imageButtons
	 */
	private void initializeImageButtons() {
		wagerPlusIB = (ImageButton)findViewById(R.id.league_create_ib_wager_plus);
		wagerPlusIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				incrementWager();
			}
		});
		
		wagerMinusIB = (ImageButton)findViewById(R.id.league_create_ib_wager_minus);
		wagerMinusIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementWager();
			}	
		});
		
		daysPlusIB = (ImageButton)findViewById(R.id.league_create_ib_days_plus);
		daysPlusIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				incrementDays();
			}
		});
		
		daysMinusIB = (ImageButton)findViewById(R.id.league_create_ib_days_minus);
		daysMinusIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementDays();
			}
		});
	}
	
	/**
	 * initializes the text views
	 */
	private void initializeTextViews() {
		wagerTV = (TextView)findViewById(R.id.league_create_wager);
		daysTV = (TextView)findViewById(R.id.league_create_days);
	}
	
	/**
	 * increments the wager and resets it
	 */
	private void incrementWager() {
		int wager = Integer.parseInt((String)wagerTV.getText());
		wager += WAGER_INCREMENT;
		wagerTV.setText(wager + "");
	}
	
	/**
	 * decrements the wager in the text view if valid, and resets it
	 */
	private void decrementWager() {
		int wager = Integer.parseInt((String)wagerTV.getText());
		if(wager > 0) {
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
	 * Creates the game specified by the wager and days
	 */
	private void create() {
		int wager, duration, isPrivate;
		wager = Integer.parseInt((String) wagerTV.getText());
		duration = Integer.parseInt((String) daysTV.getText());
		isPrivate = createCheckBox.isChecked() ? 1 : 0;
		League league = new League(userID, isPrivate, wager, duration);
		leagueTableHandler.addLeague(league);
		
		league = leagueTableHandler.getLeagueByCreatorID(userID);
		LeagueMember member = new LeagueMember(league.getId(), userID);
		leagueMemberTableHandler.addLeagueMember(member);
	
		try {
			Intent intent = new Intent(this, CreditCardActivity.class);
			intent.putExtra(CreditCardBundleKeys.KEY_WAGER, wager);
			startActivity(intent);
		} catch(Exception e) {
			//TODO handle failure more robustly
			Toast.makeText(getApplicationContext(), "could not start credit card activity", Toast.LENGTH_LONG).show();
		}
	}
}
