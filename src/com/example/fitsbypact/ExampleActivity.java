package com.example.fitsbypact;

import servercommunication.ServerCommunication;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import com.flurry.android.FlurryAgent;


public class ExampleActivity extends Activity {

	private final static String TAG = "ExampleActivity";
	
	/**
	 * Called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example);
        
        Log.i(TAG, "onCreate");
    }
    
    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example, menu);
        
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

}
