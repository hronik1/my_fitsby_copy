package com.fitsby;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fitsby.fragments.MeFragment;


import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class SettingsActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        MeFragment meFragment = new MeFragment();
        ft.add(R.id.settings_frame, meFragment);
        ft.commit();
       // ft.attach(meFragme)
    }

}
