package com.example.fitsbypact;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.fitsbypact.fragments.CheckinFragment;
import com.example.fitsbypact.fragments.GamesFragment;
import com.example.fitsbypact.fragments.MeFragment;
import com.example.fitsbypact.fragments.NewsfeedFragment;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

import tablisteners.TabManager;



import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

public class LoggedinActivity extends SherlockFragmentActivity {

	private static final String TAG = "LoggedinActivity";
	
	private SearchView searchView;

	private TabHost mTabHost;
	private TabManager mTabManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		
		setContentView(R.layout.activity_loggedin);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		mTabManager.addTab(mTabHost.newTabSpec(getString(R.string.navigation_textview_game_text))
				.setIndicator(getString(R.string.navigation_textview_game_text)),
				GamesFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec(getString(R.string.navigation_textview_newsfeed_text))
				.setIndicator(getString(R.string.navigation_textview_newsfeed_text)),
				NewsfeedFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec(getString(R.string.navigation_textview_checkin_text))
				.setIndicator(getString(R.string.navigation_textview_checkin_text)),
				CheckinFragment.class, null);
//		mTabManager.addTab(mTabHost.newTabSpec(getString(R.string.navigation_textview_me_text))
//				.setIndicator(getString(R.string.navigation_textview_me_text)),
//				MeFragment.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Settings")
        	.setIcon(R.drawable.settings_icon_unselected)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
  
    	return true;
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
		
		Log.i(TAG, "onSaveInstanceState");
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