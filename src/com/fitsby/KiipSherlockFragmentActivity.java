package com.fitsby;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import me.kiip.sdk.Kiip;
import me.kiip.sdk.Poptart;
import me.kiip.sdk.Kiip.Callback;
import me.kiip.sdk.KiipFragmentCompat;
import android.os.Bundle;

public class KiipSherlockFragmentActivity extends SherlockFragmentActivity {

	  private final static String KIIP_TAG = "kiip_fragment_tag";

	    private KiipFragmentCompat mKiipFragment;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Create or re-use KiipFragment.
	        if (savedInstanceState != null) {
	            mKiipFragment = (KiipFragmentCompat) getSupportFragmentManager().findFragmentByTag(KIIP_TAG);
	        } else {
	            mKiipFragment = new KiipFragmentCompat();
	            getSupportFragmentManager().beginTransaction().add(mKiipFragment, KIIP_TAG).commit();
	        }
	    }

	    @Override
	    protected void onStart() {
	        super.onStart();
	        Kiip.getInstance().startSession(new Callback() {
	            @Override
	            public void onFailed(Kiip kiip, Exception exception) {
	                // handle failure
	            }

	            @Override
	            public void onFinished(Kiip kiip, Poptart poptart) {
	                onPoptart(poptart);
	            }
	        });
	    }

	    @Override
	    protected void onStop() {
	        super.onStop();
	        Kiip.getInstance().endSession(null);
	    }

	    public void onPoptart(Poptart poptart) {
	        mKiipFragment.showPoptart(poptart);
	    }
}
