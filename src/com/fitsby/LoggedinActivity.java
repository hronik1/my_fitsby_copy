package com.fitsby;


import gravatar.Gravatar;
import servercommunication.MyHttpClient;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuItem;
import com.fitsby.applicationsubclass.ApplicationUser;
import com.fitsby.fragments.CheckinFragment;
import com.fitsby.fragments.GamesFragment;
import com.fitsby.fragments.NewsfeedFragment;
import com.flurry.android.FlurryAgent;
import com.viewpagerindicator.TabPageIndicator;

import constants.FlurryConstants;
import dbtables.User;

/**
 * LoggedinActivity class is the activity which displays the main content of a
 * logged in user, namely, their games, newsfeed and ability to check in.
 * 
 * @author brenthronk
 *
 */
public class LoggedinActivity extends Activity {
	
	/**
	 * Names of the 3 tabs.
	 */
	private static final String[] CONTENT = new String[] { "GAMES", "NEWSFEED", "CHECK IN"};
	/**
	 * Position of the check in tab.
	 */
	public static final int CHECK_IN_POSITION = 2;
	/**
	 * Position of the newsfeed tab.
	 */
	public static final int NEWSFEED_POSITION = 1;
	/**
	 * Key used in bundles to restore appropriate tab.
	 */
	public static final String POSITION_KEY = "positionKey";
	/**
	 * Tag used for logcat messages.
	 */
	private static final String TAG = "LoggedinActivity";
	
	/**
	 * Item in actionbar which will contain users photo.
	 */
	private MenuItem settingsMenuItem;
	/**
	 * The currently logged in user.
	 */
	private User mUser;

//	/**
//	 * Callback for the creation of the activity, initializes views.
//	 */
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mUser = ((ApplicationUser)getApplicationContext()).getUser();
//		if (mUser == null) {
//			// if user gets alert when logged out send them to the landing activity
//			Intent intent = new Intent(this, LandingActivity.class);
//			startActivity(intent);
//		}
//		Log.i(TAG, "onCreate");
//		
//		setContentView(R.layout.activity_loggedin);
//		
//        FragmentPagerAdapter adapter = new LoggedinFragmentAdapter(getSupportFragmentManager());
//
//        ViewPager pager = (ViewPager)findViewById(R.id.loggedin_pager);
//        pager.setOffscreenPageLimit(CONTENT.length);
//        pager.setAdapter(adapter);
//        int position = getIntent().getIntExtra(POSITION_KEY, 0);
//        Log.i(TAG, position + "");
//        
//        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.loggedin_indicator);
//        indicator.setViewPager(pager);
//        pager.setCurrentItem(position);
//	}
//	
//	/**
//	 * Callback for creation of the options menu, adds the users gravatar
//	 * to the actionbar.
//	 */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        settingsMenuItem = menu.add("Settings");
//        if (mUser.getBitmap() == null) {
//        	Log.d(TAG, "bitmap null");
//        	new GravatarAsyncTask().execute(mUser.getEmail());
//        }
//        else {
//        	Log.d(TAG, "bitmap not null");
//    		settingsMenuItem.setIcon(new BitmapDrawable(mUser.getBitmap()))
//    		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        }
//        return true;
//    }
//
//    /**
//     * Callback for selecting an item in the option menu, takes the user to
//     * the settings activity.
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//    	Intent intent = new Intent(this, SettingsActivity.class);
//    	startActivity(intent);
//  
//    	return true;
//    }
//    
//    /**
//     * Callback for saving the state of the activity when placed in the
//     * background.
//     */
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		
//		Log.i(TAG, "onSaveInstanceState");
//	}
//	
//    /**
//     * Callback for when activity is restarted.
//     */
//    @Override
//    public void onRestart() {
//        super.onRestart();
//
//        Log.i(TAG, "onRestart");
//    }
//
//    /**
//     * Callback for when activity is starting, starts the flurry serssion.
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//	    FlurryAgent.onStartSession(this, FlurryConstants.key);
//	    FlurryAgent.onPageView();
//	    FlurryAgent.logEvent("LoggedinActivity");
//        Log.i(TAG, "onStart");
//    }
//    
//    /**
//     * Callback for the stopping of the activity, stops the flurry session.
//     */
//	@Override
//	protected void onStop()
//	{
//		super.onStop();		
//		FlurryAgent.onEndSession(this);
//	}
//    
//    /**
//     * Callback for when activity resumes.
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        
//        Log.i(TAG, "onResume");
//    }
//    
//    /**
//     * Callback for when activity is paused.
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//       
//        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
//    }
//    
//    /**
//     * Callback for when activity is destroyed.
//     */
//    @Override
//    public void onDestroy() {
//    	super.onDestroy();
//    	
//    	Log.i(TAG, "onDestroy");
//    	
//    }
//    
//    /**
//     * GravatarAsyncTask fetches the users gravatar on a background thread.
//     * 
//     * @author brenthronk
//     *
//     */
//    private class GravatarAsyncTask extends AsyncTask<String, Void, Bitmap> {
//        protected Bitmap doInBackground(String... params) {
//        	String gravatarURL = Gravatar.getGravatar(params[0], 80);
//        	return MyHttpClient.getBitmapFromURL(gravatarURL);
//        }
//
//        protected void onPostExecute(Bitmap response) {
//        	if (response != null) {
//        		settingsMenuItem.setIcon(new BitmapDrawable(response))
//        		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        		mUser.setBitmap(response);
//        	}
//        }
//    }
//
//    /**
//     * LoggedinFragmentAdapter is responsible for mapping the tabs to their
//     * respective fragment.
//     *
//     * @author brenthronk
//     *
//     */
//    class LoggedinFragmentAdapter extends FragmentPagerAdapter {
//        public LoggedinFragmentAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//        	if (position == 0) 
//        		return new GamesFragment();
//        	else if (position == 1) 
//        		return new NewsfeedFragment();
//        	else 
//        		return new CheckinFragment();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return CONTENT[position % CONTENT.length].toUpperCase();
//        }
//
//        @Override
//        public int getCount() {
//          return CONTENT.length;
//        }
//    }

}