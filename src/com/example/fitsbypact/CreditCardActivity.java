package com.example.fitsbypact;

import java.util.HashMap;
import java.util.Map;

import servercommunication.CreditCardCommunication;
import servercommunication.UserCommunication;

import bundlekeys.CreditCardBundleKeys;
import dbtables.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import com.example.fitsbypact.applicationsubclass.*;

public class CreditCardActivity extends Activity {

	private final static String TAG = "CreditCardActivity";
	
	private Button submitButton;
	private EditText numberET;
	private EditText expMonthET;
	private EditText expYearET;
	private EditText cvcET;
	private TextView wagerTV;
	
	private int wager;
	private boolean isValid;
	
	private User mUser;
	
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
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
    	
    	Bundle extras = intent.getExtras();
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
    	wagerTV.setText("" + wager);

    }
    
    /**
     * submit to database and/or charge customer
     */
    private void submit() {
    	//TODO submit credit card info
    	try {
    		Intent intent = new Intent(this, FriendInviteActivity.class);
    		sendCreditCard();
    		//TODO pass danny token
    		startActivity(intent);
    	} catch (Exception e) {
    		//remove in deployment
    		String stackTrace = android.util.Log.getStackTraceString(e);
    		Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	} 
    }
    
    /**
     * sends credit card info to server
     */
    private void sendCreditCard() {
    	String number = numberET.getText().toString();
    	String expYear = expYearET.getText().toString(); 
    	String cvc = cvcET.getText().toString();
    	String expMonth = expMonthET.getText().toString(); 
    	new SendCreditCardAsyncTask().execute(mUser.getID()+"", number, expMonth, expYear, cvc);
    	
    }
    
    /**
     * AsyncTask class to send info to server
     * @author brent
     *
     */
    private class SendCreditCardAsyncTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
        	String string = CreditCardCommunication.sendCreditCardInformation(params[0], params[1],
        			params[2], params[3], params[4]);
        	return string;
        }

        protected void onPostExecute(String string) {
        	Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        }
    }
    
}
