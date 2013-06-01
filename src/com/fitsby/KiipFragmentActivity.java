package com.fitsby;

import me.kiip.sdk.Kiip;
import me.kiip.sdk.Poptart;
import me.kiip.sdk.Kiip.Callback;
import me.kiip.sdk.KiipFragmentCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * KiipFragmentActivity is a base class which all future activities will
 * subclass, directly or indirectly. It exists to abstract away the details
 * of implementing kiip in all activities.
 * 
 * @author brenthronk
 *
 */
public class KiipFragmentActivity extends FragmentActivity {

	/**
	 * Key for adding/finding the kiip fragment.
	 */
    private final static String KIIP_TAG = "kiip_fragment_tag";

    /**
     * Reference to the kiip fragment.
     */
    private KiipFragmentCompat mKiipFragment;

    /**
     * Callback for the creation of the fragment, obtain reference to
     * kiipfragment.
     * 
     */
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

    /**
     * Callback for starting of the activity, starts the kiip session.
     */
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

    /**
     * Callback for the stopping of the activity, ends the kiip session.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Kiip.getInstance().endSession(null);
    }

    /**
     * Shows the poptart.
     * 
     * @param poptart	the poptart to be displayed
     */
    public void onPoptart(Poptart poptart) {
        mKiipFragment.showPoptart(poptart);
    }

}
