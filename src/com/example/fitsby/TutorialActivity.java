package com.example.fitsby;

import com.example.fitsby.applicationsubclass.ApplicationUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialActivity extends FragmentActivity {

    TutorialPagerAdapter mTutorialPagerAdapter;
    ViewPager mViewPager;
    
    private static int[] pageDrawableResources = new int[] {R.drawable.registration_onboard1, R.drawable.registration_onboard2,
    		R.drawable.registration_onboard3, R.drawable.registration_onboard4};
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		
		mTutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTutorialPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tutorial, menu);
		return true;
	}
	
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
		               		Intent intent = new Intent(parent, RegisterActivity.class);
		               		startActivity(intent);
						}
                	});
                } else {
                	button.setVisibility(View.INVISIBLE);
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
