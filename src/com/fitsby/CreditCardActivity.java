package com.fitsby;

import java.util.HashMap;
import java.util.Map;

import responses.LeagueCreateResponse;
import responses.StatusResponse;
import responses.UsersGamesResponse;
import servercommunication.CreditCardCommunication;
import servercommunication.LeagueCommunication;
import servercommunication.UserCommunication;
import android.text.Spanned;
import bundlekeys.CreditCardBundleKeys;
import bundlekeys.LeagueDetailBundleKeys;
import dbtables.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
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

import com.fitsby.applicationsubclass.*;
import com.flurry.android.FlurryAgent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

import constants.FlurryConstants;

public class CreditCardActivity extends KiipFragmentActivity {

	private final static String TAG = "CreditCardActivity";
	
	private Button submitButton;
	private EditText numberET;
	private EditText expMonthET;
	private EditText expYearET;
	private EditText cvcET;
	private TextView wagerTV;
	private Bundle extras;
	
	private int wager;
	private int leagueId;
	private boolean isValid;
	
	private User mUser;
	
	private ProgressDialog mProgressDialog;
	
	/**
	 * called when activity first created
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
        
        mUser = ((ApplicationUser)getApplicationContext()).getUser();
    }

    /**
     * called when menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_credit_card, menu);
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
	    FlurryAgent.logEvent("Enter Credit Card Info");
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

    private void parseBundle(Intent intent) {
    	if(intent == null) {
    		isValid = false;
    		return;
    	}
    	
    	extras = intent.getExtras();
    	if(extras == null) {
    		isValid = false;
    		return;
    	}
    	
    	wager = extras.getInt(CreditCardBundleKeys.KEY_WAGER);

    	isValid = true;
    }
    
    /**
     * registers all the edittexts to their corresponding fields in the layout
     */
    private void initializeEditTexts() {
    	numberET = (EditText)findViewById(R.id.credit_card_et_card_number);
//    	InputFilter filter = new InputFilter() {
//    	    @Override
//    	    public CharSequence filter(CharSequence source, int start, int end,
//    	            Spanned dest, int dstart, int dend) {
//
//    	    	StringBuilder filteredStringBuilder = new StringBuilder();
//    	    	for (int i = 0; i < end && i < 19; i++) { 
//    	    		char currentChar = source.charAt(i);
//    	    		if (!Character.isSpaceChar(currentChar)) {    
//    	    			filteredStringBuilder.append(currentChar);
//    	    		}  
//					if (filteredStringBuilder.length() == 4 || filteredStringBuilder.length() == 9 || filteredStringBuilder.length() == 14)
//						filteredStringBuilder.append(" ");
//    	    	}
//    	    	return filteredStringBuilder.toString();
//    	       
//    	    }
//    	};
//    	numberET.setFilters(new InputFilter[]{filter});
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
     * registers buttons from layout and adds listeners to them
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
     * initializes the textviews
     */
    private void initializeTextViews() {
    	wagerTV = (TextView)findViewById(R.id.credit_card_tv_wager);
    	wagerTV.setText("$" + wager);

    }
    
    /**
     * 
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
     * submit to database and/or charge customer
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
     * 
     */
    private void showConfirmation() {
	  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setMessage("Please confirm that you want to start this game")
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
     * sends credit card info to server
     */
    private void sendCreditCard() {
    	ApplicationUser appData = (ApplicationUser)getApplicationContext();
    	if (appData.getCreate()) {
    		new CreateLeagueAsyncTask().execute(appData.getUserId()+"", appData.getDuration()+"",
    				appData.getIsPrivateString(), appData.getWager()+"", appData.getStructure()+"");
    	} else if (appData.getJoin()) {
    		new JoinLeagueAsyncTask().execute(appData.getUserId(), appData.getLeagueId());
    	}
    	
    }
    
    private String parseCreditCard(String unparsed) {
    	return (unparsed.substring(0,4) + unparsed.substring(5,9) + 
    			unparsed.substring(10,14) + unparsed.substring(15,19));
    }
    /**
     * AsyncTask class to send info to server
     * @author brent
     *
     */
    private class SendCreditCardAsyncTask extends AsyncTask<String, Void, StatusResponse> {
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = CreditCardCommunication.sendCreditCardInformation(params[0], params[1],
        			params[2], params[3], params[4]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	if (response.wasSuccessful()) {
        		Intent intent = new Intent(CreditCardActivity.this, FriendInviteActivity.class);
        		startActivity(intent);
        	} else {
        		Toast toast = Toast.makeText(CreditCardActivity.this, "Your card was declined. Are you sure you filled in all the information correctly?", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
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
