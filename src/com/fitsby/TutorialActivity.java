package com.fitsby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import constants.TutorialsConstants;

public class TutorialActivity extends ActionBarActivity {

	/**
	 * Adapter from drawable resources to proper pages.
	 */
    TutorialPagerAdapter mTutorialPagerAdapter;
    /**
     * Allows swiping of the pages.
     */
    ViewPager mViewPager;
    
    /**
     * True if mefragment started this activity, false otherwise.
     */
    private static boolean fromMe;
    
    /**
     * Drawables corresponding to the pages.
     */
    private static int[] pageDrawableResources = new int[] {R.drawable.registration_onboard1, R.drawable.registration_onboard2,
    		R.drawable.registration_onboard3, R.drawable.registration_onboard4, R.drawable.registration_onboard5};

    /**
     * Callback for creation of the activity, initializes the views.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		
		mTutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTutorialPagerAdapter);
        
        parseBundle(getIntent());
	}

	/**
	 * Callback for creation of the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tutorial, menu);
		return true;
	}
	
	/**
	 * Parses the bundle from previous activity.
	 * 
	 * @param intent	contains bundle to be parsed.
	 */
	private void parseBundle(Intent intent) {
		fromMe = intent.getBooleanExtra(TutorialsConstants.FROM_ME, false);
	}
	
	/**
	 * TutorialPagerAdapter is responsible for mapping the drawables to the
	 * appropriate fragment.
	 * 
	 * @author brenthronk
	 *
	 */
    public static class TutorialPagerAdapter extends FragmentStatePagerAdapter {

        public TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.ARG_OBJECT, TutorialActivity.pageDrawableResources[i]); 
            if (i == TutorialActivity.pageDrawableResources.length-1)
            	args.putBoolean(DemoObjectFragment.ARG_LAST, true);
            else
            	args.putBoolean(DemoObjectFragment.ARG_LAST, false);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
        	return TutorialActivity.pageDrawableResources.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + (position+1) + "/" + pageDrawableResources.length;
        }
        
        
        public static class DemoObjectFragment extends Fragment {

            public static final String ARG_OBJECT = "object";
            public static final String ARG_LAST = "last";
            
            private Activity parent;

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
							if (!fromMe) {
								Intent intent = new Intent(parent, RegisterActivity.class);
								startActivity(intent);
							} else {
								getActivity().finish();
							}
						}
                	});
                } else {
                	button.setVisibility(View.GONE);
                	button.setClickable(false);
                }
                	
                return rootView;
            }
            
        	/**
        	 * callback for when this fragment is attached to a view
        	 */
        	@Override
        	public void onAttach(Activity activity) {
        		super.onAttach(activity);
        		parent = activity;

        	}
        }
    }

}
