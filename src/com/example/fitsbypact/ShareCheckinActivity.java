package com.example.fitsbypact;


import bundlekeys.LeagueDetailBundleKeys;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

import dbtables.User;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShareCheckinActivity extends Activity {

	private final static String TAG = "ShareCheckinActivity";
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	private Button mShareButton;
	private String mGymName;
	
	//facebook
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_checkin);
		
        Log.i(TAG, "onCreate");
        
        mApplicationUser = (ApplicationUser)getApplicationContext();
        mUser = mApplicationUser.getUser();
        
        parseBundle(getIntent());
        initializeButtons(); 
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    /**
     * called when activity is starting
     */
    @Override
    public void onStart() {
        super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Invite Friends");
        Log.i(TAG, "onStart");
    }
    
    /**
     * called when activity is paused
     */
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * called when activity is destroyed
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	uiHelper.onDestroy();
    	Log.i(TAG, "onDestroy");
    	
    }
    
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
    /**
     * called when activity resumes
     */
    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
               (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
        Log.i(TAG, "onResume");
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_share_checkin, menu);
		return true;
	}
	
    /**
     * callback for recieving data from starting an activity for a result
     */
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	uiHelper.onActivityResult(requestCode, resultCode, data);

    }
  
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
          
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
           
        }
    }

    /**
     * initializes the buttons
     */
    private void initializeButtons() {
    	mShareButton = (Button)findViewById(R.id.checkin_share_facebook_shareButton);
    	mShareButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			publishStory();
    		}
    	});
    }
    
    /**
     * parses the incoming bundle
     * @param intent
     */
    private void parseBundle(Intent intent) {
    	Bundle extras = intent.getExtras();
    	if (extras == null) {
    		Toast.makeText(getApplicationContext(), "no bundle", Toast.LENGTH_LONG).show();
    	}
    		
    	mGymName = extras.getString(LeagueDetailBundleKeys.KEY_GYM_NAME);
    }
    
    /**
     * opens up dialog for users to share their checkin on facebook
     */
    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session == null || session.isClosed()) {
        	Toast.makeText(this, "Sorry, but you must sign in", Toast.LENGTH_LONG).show();
        }
        else {

            Bundle postParams = new Bundle();
            postParams.putString("name", "Fitsby");
            postParams.putString("caption", "An app that motivates you to go to the gym.");
            postParams.putString("description", mUser.getFirstName() + " just checked in at" + mGymName);
            postParams.putString("link", "http://fitsby.com");
            postParams.putString("picture", "http://fitsby.com/images/Fitsby_Logo.png");

            WebDialog feedDialog = (
                    new WebDialog.FeedDialogBuilder(this,
                        Session.getActiveSession(),
                        postParams))
                    .setOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Bundle values,
                            FacebookException error) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(ShareCheckinActivity.this,
                                    "Posted story, id: "+postId,
                                Toast.LENGTH_SHORT).show();
                            }
                        }

                    })
                    .build();
                feedDialog.show();
        }

    }
}
