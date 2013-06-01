package com.fitsby;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import me.kiip.sdk.Kiip;
import me.kiip.sdk.Poptart;
import me.kiip.sdk.Kiip.Callback;
import me.kiip.sdk.KiipFragmentCompat;
import android.os.Bundle;

/**
 * KiipSherlockFragmentActivity is a class that classes that need to use
 * kiip and actionbarsherlock functionality will subclass.
 * 
 * @author brenthronk
 *
 */
public class KiipSherlockFragmentActivity extends SherlockFragmentActivity {

	/**
	 * Key for adding/finding the kiip fragment.
	 */
	private final static String KIIP_TAG = "kiip_fragment_tag";

	/**
	 * Reference to the kiip fragment.
	 */
	private KiipFragmentCompat mKiipFragment;

	/**
	 * Callback for creation of the activity, obtains reference to kiip
	 * fragment.
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
	 * Callback for starting of the activity, starts kiip session.
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
	 * Callback for stopping of the activity, stops kiip session.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Kiip.getInstance().endSession(null);
	}

	public void onPoptart(Poptart poptart) {
		mKiipFragment.showPoptart(poptart);
	}
}
