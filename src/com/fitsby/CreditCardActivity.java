package com.fitsby;

import responses.LeagueCreateResponse;
import responses.StatusResponse;
import servercommunication.LeagueCommunication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

/**
 * CreditCardActivity is the class that the application user will interact
 * with when submitting their credit card information.
 * 
 * @author brenthronk
 *
 */
public class CreditCardActivity extends KiipFragmentActivity {

	/**
	 * This is the tag used for logcat messages.
	 */
	private final static String TAG = "CreditCardActivity";
	
	/**
	 * Button to submit credit card information.
	 */
	private Button submitButton;
	
	/**
	 * EditText to enter card number.
	 */
	private EditText numberET;
	
	/**
	 * EditText to enter expiration month.
	 */
	private EditText expMonthET;
	
	/**
	 * EditText to enter expiration year.
	 */
	private EditText expYearET;
	
	/**
	 * EditText to enter security digits.
	 */
	private EditText cvcET;
	
	/**
	 * TextView to display wager.
	 */
	private TextView wagerTV;
	
	/**
	 * Bundle to store data passed in from Activity that started this.
	 */
	private Bundle extras;
	
	/**
	 * The amount that will be displayed and submitted for payment.
	 */
	private int wager;
	
	/**
	 * Spinner used to display progress while submitting information for
	 * verification.
	 */
	private ProgressDialog mProgressDialog;
	
	/**
	 * Callback for when activity is first created, initializes the interface
	 * and parses passed in information.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        
        Log.i(TAG, "onCreate");
        
        parseBundle(getIntent());
        
        initializeButtons();
        initializeEditTexts();
        initializeTextViews();
    }

    /**
     * Callback for when menu is first created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_credit_card, menu);
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
     * Callback for when activity is started, starts a flurry session.
     */
    @Override
    public void onStart() {
        super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Enter Credit Card Info");
        Log.i(TAG, "onStart");
    }
    
    /**
     * Callback for when the activity is stopped, stops the flurry session.
     */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
    /**
     * Callback for when activity resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        
        Log.i(TAG, "onResume");
    }
    
    /**
     * Callback for when activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
       
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * Callback for when activity is destroyed.
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	Log.i(TAG, "onDestroy");
    	
    }

    /**
     * Parses incoming wager information from the activity which started this
     * activity, will store state in wager.
     * 
     * @param intent the bundle to be parsed
     */
    private void parseBundle(Intent intent) {
    	if(intent == null) {
    		return;
    	}
    	
    	extras = intent.getExtras();
    	if(extras == null) {
    		return;
    	}
    	
    	wager = extras.getInt(CreditCardBundleKeys.KEY_WAGER);
    }
    
    /**
     * Inflates all the edittexts to their corresponding fields in the layout.
     */
    private void initializeEditTexts() {
    	numberET = (EditText)findViewById(R.id.credit_card_et_card_number);
    	numberET.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Log.d(TAG, s.toString());
				char[] digits = s.toString().toCharArray();
				if (!isCreditCardFormatValid(digits)) {
					String strippedString = "";
					
					for (char digit: digits) {
						if (digit != ' ')
							strippedString += digit;
					}
					char[] strippedDigits = strippedString.toCharArray();
					String validString = "";
					for (int i = 0, j = 0; i < strippedDigits.length; i++, j++) {
						if (j == 4 || j == 9 || j == 14) {
							validString += ' ';
							j++;
						}
						validString += strippedDigits[i];
					}
					s.clear();
					s.append(validString);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	expMonthET = (EditText)findViewById(R.id.credit_card_et_expire_month);
    	expYearET = (EditText)findViewById(R.id.credit_card_et_expire_year);
    	cvcET = (EditText)findViewById(R.id.credit_card_et_card_cvc);
    }
    
    /**
     * Registers buttons from layout and adds listeners to them.
     */
    private void initializeButtons() {
    	submitButton =(Button)findViewById(R.id.credit_card_button_submit);
    	submitButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			submit();
    		}
    	});
    }
    
    /**
     * Initialize the textview to display appropriate wager.
     */
    private void initializeTextViews() {
    	wagerTV = (TextView)findViewById(R.id.credit_card_tv_wager);
    	wagerTV.setText("$" + wager);

    }
    
    /**
     * This is a helper method, that tests for the validity of a credit card
     * format.
     * 
     * @param digits  	a character array containing the number, contains 
     * 				  	spaces
     * @return			true if valid format, false otherwise
     */
    private boolean isCreditCardFormatValid(char[] digits) {
    	for (int i = 0; i < digits.length; i++ ) {
    		if ((i == 4 || i == 9 || i == 14) && digits[i] != ' ')
    			return false;
    		else if ((i != 4 && i != 9 && i != 14) && digits[i] == ' ')
    			return false;
    	}
    	return true;
    }
    
    /**
     * Validates credit card information and provides appropriate alerts to
     * users based on outcome.
     */
    private void submit() {

    	String number = numberET.getText().toString();
    	String expMonth = expMonthET.getText().toString();
    	String expYear = expYearET.getText().toString();
    	String cvc = cvcET.getText().toString();
    	if (number.length() != 19) {
    		Toast toast = Toast.makeText(this, "Your card number is too short", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
    	} 
    		
    	if (number.equals("") || expMonth.equals("") || expYear.equals("") || cvc.equals("")) {
    		Toast toast = Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
    	} else
    		showConfirmation();
    }
    
    /**
     * Opens a dialog for the user, confirming their want to start the game.
     */
    private void showConfirmation() {
	  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setMessage("Please confirm that you want to start this game.")
    			.setCancelable(false)
    			.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					sendCreditCard();
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /**
     * Sends credit card information to the server to either join or create a
     * league, as appropriate.
     */
    private void sendCreditCard() {
    	ApplicationUser appData = (ApplicationUser)getApplicationContext();
    	if (appData.getCreate()) {
    		new CreateLeagueAsyncTask().execute(appData.getUserId()+"", appData.getDuration()+"",
    				appData.getIsPrivateString(), appData.getWager()+"", appData.getGoal()+"");
    	} else if (appData.getJoin()) {
    		new JoinLeagueAsyncTask().execute(appData.getUserId(), appData.getLeagueId());
    	}
    	
    }
    
    /**
     * Strips out the inner white space, from an valid format credit card.
     * 
     * @param unparsed	string containing a valid format credit, where the
     * 					space will be removed from
     * @return			string containing the numbers without white space
     */
    private String parseCreditCard(String unparsed) {
    	return (unparsed.substring(0,4) + unparsed.substring(5,9) + 
    			unparsed.substring(10,14) + unparsed.substring(15,19));
    }
    
    /**
     * CreateLeagueAsyncTask sends, on a background thread, all of the
     * information necessary to create a league.
     * 
     * @author brenthronk
     *
     */
    private class CreateLeagueAsyncTask extends AsyncTask<String, Void, LeagueCreateResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(CreditCardActivity.this, "",
						"Submitting your card information for verification...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						CreateLeagueAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		

        protected LeagueCreateResponse doInBackground(String... params) {
        	String number = parseCreditCard(numberET.getText().toString());
        	LeagueCreateResponse response = LeagueCommunication.createLeague(Integer.parseInt(params[0]),
        			Integer.parseInt(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]),
        			number, expYearET.getText().toString(), expMonthET.getText().toString(),
        			cvcET.getText().toString());
        	return response;
        }

        protected void onPostExecute(LeagueCreateResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }

            	if (response.wasSuccessful()) {
            		Intent intent = new Intent(CreditCardActivity.this, FriendInviteActivity.class);
            		intent.putExtra(LeagueDetailBundleKeys.KEY_LEAGUE_ID, Integer.parseInt(response.getLeagueId()));
            		startActivity(intent);

            	} else if (response.getError() != null && !response.getError().equals("")) {
            		Toast toast = Toast.makeText(CreditCardActivity.this, response.getError(), Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
            	} else {
            		Toast toast = Toast.makeText(CreditCardActivity.this, "Your card was declined. Are you sure you filled in all the information correctly?", Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
            		toast.show();
            	}
        }
    }
    
    /**
     * JoinLeagueAsyncTask sends, on a background thread, the information
     * needed to join a league.
     * 
     * @author brenthronk
     *
     */
    private class JoinLeagueAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(CreditCardActivity.this, "",
						"Submiting your card info for verification...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						JoinLeagueAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = LeagueCommunication.joinLeague(params[0], params[1],
        			parseCreditCard(numberET.getText().toString()), expYearET.getText().toString(), expMonthET.getText().toString(),
        			cvcET.getText().toString());
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response.wasSuccessful()) {
        		try {
        			ApplicationUser appData = (ApplicationUser)getApplicationContext();
            		Intent intent = new Intent(CreditCardActivity.this, FriendInviteActivity.class);
            		intent.putExtra(LeagueDetailBundleKeys.KEY_LEAGUE_ID, appData.getLeagueId());
            		startActivity(intent);
        		} catch(Exception e) {
        		}
        	} else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(CreditCardActivity.this, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else {
        		Toast toast = Toast.makeText(CreditCardActivity.this, "Your card was declined. Are you sure you filled in all the information correctly?", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        }
    }
}
