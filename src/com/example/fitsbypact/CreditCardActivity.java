package com.example.fitsbypact;

import bundlekeys.CreditCardBundleKeys;
import dbtables.User;
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
    		startActivity(intent);
    	} catch (Exception e) {
    		//remove in deployment
    		String stackTrace = android.util.Log.getStackTraceString(e);
    		Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    				Toast.LENGTH_LONG);
    		toast.show();
    	} 
    }
}
