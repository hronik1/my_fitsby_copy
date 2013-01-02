package com.fitsby;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.crittercism.app.Crittercism;
import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;

import constants.FlurryConstants;
import constants.TutorialsConstants;

public class LandingActivity extends Activity {

	private final static String TAG = "LandingActivity";
	
	private Button buttonLogin;
	private Button buttonStart;
	
	/**
	 * Called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_landing);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_landing, menu);
        
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
	    FlurryAgent.logEvent("Landing Activity");
        Crittercism.init(getApplicationContext(), "506f841701ed850f8f000003");
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
     * initializes the login and start buttons 
     */
    private void initializeButtons() {
    	buttonLogin = (Button)findViewById(R.id.landing_button_login);
    	buttonLogin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			goToLoginPage();
    		}
    	});
    	
    	buttonStart = (Button)findViewById(R.id.landing_button_start);
    	buttonStart.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			goToHelpPage();
    		}
    	});
    }
    
    /**
     * Changes to the LoginActivity
     */
    private void goToLoginPage() {
    	try {
    		Intent intent = new Intent(this, LoginActivity.class);
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
     * Changes to the Registration Activity
     */
    private void goToHelpPage() {
    	try {
    		Intent intent = new Intent(this, TutorialActivity.class);
    		intent.putExtra(TutorialsConstants.FROM_ME, false);
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
}
