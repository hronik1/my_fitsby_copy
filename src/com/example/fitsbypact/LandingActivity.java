package com.example.fitsbypact;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
    			goToRegisterPage();
    		}
    	});
    }
    
    /**
     * Changes to the LoginActivity
     */
    private void goToLoginPage() {
    	//TODO switch to login activity
    }
    
    /**
     * Changes to the Registration Activity
     */
    private void goToRegisterPage() {
    	try {
    		Intent intent = new Intent(this, RegisterActivity.class);
    		startActivity(intent);
    	}
    	// catches any exceptions and prints it to a toast message for debugging
    	// purposes
    	catch (Exception e) {
    		String stackTrace = android.util.Log.getStackTraceString(e);
    		Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    				Toast.LENGTH_LONG);
    		toast.show();
    	
    	} 
    }
}
