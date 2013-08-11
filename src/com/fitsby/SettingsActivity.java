package com.fitsby;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.fitsby.fragments.MeFragment;

/**
 * SettingsActivity is just a wrapper activity around the MeFragment.
 * 
 * @author brenthronk
 *
 */
public class SettingsActivity extends ActionBarActivity {

	/**
	 * Callback for creation of the Activity, initializes the view.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        MeFragment meFragment = new MeFragment();
        ft.add(R.id.settings_frame, meFragment);
        ft.commit();
    }

}
