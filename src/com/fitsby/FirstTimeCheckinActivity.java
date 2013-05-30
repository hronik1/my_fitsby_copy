package com.fitsby;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * FirstTimeCheckinActivity takes the user through a tutorial, showing them
 * how the checkin process works.
 * 
 * @author brenthronk
 *
 */
public class FirstTimeCheckinActivity extends KiipFragmentActivity {

	/** 
	 * This is responsible for switching adapting the viewpager for the
	 * various tutorial pages.
	 */
    TutorialPagerAdapter mTutorialPagerAdapter;
    
    /**
     * This is where the various pages will be displayed and swiped through.
     */
    ViewPager mViewPager;
    
    /**
     * These are the resources for the corresponding tutorial pages.
     */
    private static int[] pageDrawableResources = new int[] {R.drawable.checkin_onboard1, R.drawable.checkin_onboard2,
    		R.drawable.checkin_onboard3};
    
    /**
     * Callback for the creation of the activity, initializes the view.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_time_checkin);
		
		mTutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.first_checkin_pager);
        mViewPager.setAdapter(mTutorialPagerAdapter);
	}

	/**
	 * Callback for when the options menu is created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first_time_checkin, menu);
		return true;
	}

	/**
	 * TutorialPagerAdapter is an adapter class which is responsible for
	 * for giving a viewpager the appropriate view, as well as activating
	 * the done button on the last page.
	 * 
	 * @author brenthronk
	 *
	 */
    private static class TutorialPagerAdapter extends FragmentStatePagerAdapter {

        public TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.ARG_OBJECT, FirstTimeCheckinActivity.pageDrawableResources[i]); 
            if (i == FirstTimeCheckinActivity.pageDrawableResources.length-1)
            	args.putBoolean(DemoObjectFragment.ARG_LAST, true);
            else
            	args.putBoolean(DemoObjectFragment.ARG_LAST, false);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
        	return FirstTimeCheckinActivity.pageDrawableResources.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + (position+1) + "/" + pageDrawableResources.length;
        }
        
        
        public static class DemoObjectFragment extends Fragment {

            public static final String ARG_OBJECT = "object";
            public static final String ARG_LAST = "last";

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_tutorial_object, container, false);
                Bundle args = getArguments();
                ((ImageView) rootView.findViewById(R.id.fragment_tutorial_object_iv)).setImageResource(args.getInt(ARG_OBJECT));
                
                Button button = (Button) rootView.findViewById(R.id.fragment_tutorial_object_button);
                if (args.getBoolean(ARG_LAST)) {
                	button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							getActivity().finish();
						}
                	});
                } else {
                	button.setVisibility(View.GONE);
                	button.setClickable(false);
                }
                	
                return rootView;
            }

        	@Override
        	public void onAttach(Activity activity) {
        		super.onAttach(activity);

        	}
        }
    }
}
