package com.example.fitsbypact;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
	private long SplashDelay = 3000; //3 seconds
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    
        TimerTask task = new TimerTask()
    	{
    		
    		@Override
    		public void run() {
    			finish();
    			Intent mainIntent = new Intent().setClass(SplashActivity.this, LandingActivity.class);
    			startActivity(mainIntent);
    		}
    	};
    	
    	Timer timer = new Timer();
    	timer.schedule(task, SplashDelay);
    	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }
}