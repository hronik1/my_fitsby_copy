package com.fitsby;

import com.fitsby.fragments.CheckinFragment;
import com.fitsby.fragments.GamesFragment;
import com.fitsby.fragments.MeFragment;
import com.fitsby.fragments.NewsfeedFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class NewLoggedinActivity extends ActionBarActivity {

	/**
	 * Tag used for logcat messages.
	 */
	private final String TAG = getClass().getName();
	
	/**
	 * Compatability action bar.
	 */
	private ActionBar mActionBar;
	
	/**
	 * Drawer toggle responsible for handling state changes of the Navigation Drawer.
	 */
	private ActionBarDrawerToggle mActionBarDrawerToggle;
	
	/**
	 * Container for the Navigation Drawer.
	 */
	private DrawerLayout mDrawerLayout;
	
	/**
	 * Contains the selection items of the Navigation Drawer.
	 */
	private ListView mDrawerListView;
	
	/**
	 * Possible fragments to be selected from via the NavigationDrawer.
	 */
	private Fragment[] mFragments = {new GamesFragment(), new NewsfeedFragment(), new CheckinFragment(), new MeFragment()};
	
	/**
	 * Strings to be displayed in the NavigationDrawer ListView.
	 */
	private String[] mDrawerItems = {"games", "newsfeed", "checkin", "settings"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_loggedin);
		
		initializeNavigationDrawer();
		
		if (null == savedInstanceState)
			setFragment(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_loggedin, menu);
		return true;
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    
	/**
	 * Inflates the NavigationDrawer and adds listener.
	 */
	private void initializeNavigationDrawer() {
		mActionBar = getSupportActionBar();
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.new_loggedin_drawer_layout);
		
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	Log.d(TAG, "onDrawerClosed");
            	//TODO maybe uncomment above statement
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	Log.d(TAG, "onDrawerOpened");
            	//TODO maybe comment out above comment
            }
        };
        
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
		mDrawerListView = (ListView) findViewById(R.id.new_loggedin_left_drawer);
		mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mDrawerItems));
		
		mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
	}

	/**
	 * Sets the fragment to the one selected in the navigation drawer.
	 * 
	 * @param position	the position of the fragment to be set
	 */
	private void setFragment(int position) {
		if (position >= 0 && position < mFragments.length) {
		    FragmentManager fragmentManager = getSupportFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.new_loggedin_content_frame, mFragments[position])
		                   .commit();
		}
	}
	
	/**
	 * DrawerItemClickListener provides navigation functionality when the navigation drawer is clicked.
	 * 
	 * @author brenthronk
	 *
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			setFragment(position);
		}
		
	}
}
