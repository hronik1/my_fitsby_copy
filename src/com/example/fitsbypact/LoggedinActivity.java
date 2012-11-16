package com.example.fitsbypact;


import com.example.fitsbypact.fragments.CheckinFragment;
import com.example.fitsbypact.fragments.GamesFragment;
import com.example.fitsbypact.fragments.MeFragment;
import com.example.fitsbypact.fragments.NewsfeedFragment;

import tablisteners.TabListener;



import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.view.Menu;
import android.widget.SearchView;

public class LoggedinActivity extends Activity {

	private SearchView searchView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        Tab tab = actionBar.newTab()
                .setText(R.string.navigation_textview_game_text)
                .setTabListener(new TabListener<GamesFragment>(
                        this, getResources().getString(R.string.navigation_textview_game_text), GamesFragment.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
                .setText(R.string.navigation_textview_newsfeed_text)
                .setTabListener(new TabListener<NewsfeedFragment>(
                        this, getResources().getString(R.string.navigation_textview_newsfeed_text), NewsfeedFragment.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
                .setText(R.string.navigation_textview_checkin_text)
                .setTabListener(new TabListener<CheckinFragment>(
                        this, getResources().getString(R.string.navigation_textview_checkin_text), 
                        CheckinFragment.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
                .setText(R.string.navigation_textview_me_text)
                .setTabListener(new TabListener<MeFragment>(
                        this, getResources().getString(R.string.navigation_textview_me_text), 
                        MeFragment.class));
        actionBar.addTab(tab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_loggedin, menu);
        //searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        return true;
    }
}