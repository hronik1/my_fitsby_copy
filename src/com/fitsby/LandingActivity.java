package com.fitsby;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;
import constants.TutorialsConstants;

/**
 * LandingActivity is the page that users will be taken to when they first
 * open the application.
 * 
 * @author brenthronk
 *
 */
public class LandingActivity extends KiipFragmentActivity {

	/**
	 * Tag used in logcat.
	 */
	private final static String TAG = "LandingActivity";
	
	/**
	 * Button to open login page.
	 */
	private Button buttonLogin;
	/**
	 * Button to open start page.
	 */
	private Button buttonStart;
	
	/**
	 * Callback for when activity is created. If user is logged in will
	 * redirect to logged in page, otherwise initializes view.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (((ApplicationUser)getApplicationContext()).getUser().getID() != ApplicationUser.DEFAULT_ID) {
        	Intent intent = new Intent(this, NewLoggedinActivity.class);
        	startActivity(intent);
        	this.finish();
        }
        	
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
     * Callback for restarting the activity.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        Log.i(TAG, "onRestart");
    }

    /**
     * Callback for when the activity is started.
     * Starts flurry session.
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
    
    /**
     * Callback for when the activity is stopped. Ends the flurry session.
     */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
    
    /**
     * Callback for when the activity resumes.
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
     * Callback for when activity is destroyed.
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	Log.i(TAG, "onDestroy");
    	
    }

    /**
     * Connects buttons and adds listeners.
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
     * Changes to the Login Activity.
     */
    private void goToLoginPage() {
    	try {
    		Intent intent = new Intent(this, LoginActivity.class);
    		startActivity(intent);
    	} catch (Exception e) {
    		//TODO possibly change or remove
    		String stackTrace = android.util.Log.getStackTraceString(e);
    		Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	} 
    }
    
    /**
     * Changes to the Registration Activity.
     */
    private void goToHelpPage() {
    	try {
    		Intent intent = new Intent(this, TutorialActivity.class);
    		intent.putExtra(TutorialsConstants.FROM_ME, false);
    		startActivity(intent);
    	} catch (Exception e) {
    		//TODO possibly change or remove
    		String stackTrace = android.util.Log.getStackTraceString(e);
    		Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	} 
    }
}
