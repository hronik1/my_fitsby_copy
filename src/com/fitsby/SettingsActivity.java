package com.fitsby;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.fitsby.fragments.MeFragment;

public class SettingsActivity extends KiipSherlockFragmentActivity {

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
