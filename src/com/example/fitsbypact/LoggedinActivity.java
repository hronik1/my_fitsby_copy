package com.example.fitsbypact;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.fitsbypact.fragments.CheckinFragment;
import com.example.fitsbypact.fragments.GamesFragment;
import com.example.fitsbypact.fragments.MeFragment;
import com.example.fitsbypact.fragments.NewsfeedFragment;

import tablisteners.TabManager;



import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TabHost;

public class LoggedinActivity extends SherlockFragmentActivity {

	private SearchView searchView;

	private TabHost mTabHost;
	private TabManager mTabManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);

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
		mTabManager.addTab(mTabHost.newTabSpec(getString(R.string.navigation_textview_me_text))
				.setIndicator(getString(R.string.navigation_textview_me_text)),
				MeFragment.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}
}