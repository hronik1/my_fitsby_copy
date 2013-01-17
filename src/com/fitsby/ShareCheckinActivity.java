package com.fitsby;


import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import bundlekeys.LeagueDetailBundleKeys;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.fitsby.FriendInviteActivity.UpdateTwitterAsyncTask;
import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

import dbtables.User;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShareCheckinActivity extends KiipFragmentActivity {

	private final static String TAG = "ShareCheckinActivity";
	private final static String GYM_KEY = "gymKey";
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	private Button mShareButton;
	private static String mGymName;
	private ProgressDialog mProgressDialog;
	
	//twitter
	private SharedPreferences mSharedPreferences;
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsamplecheckin";
    private static Twitter twitter;
    private static RequestToken requestToken;
	private String TWITTER_CONSUMER_KEY;
	private String TWITTER_CONSUMER_SECRET;
	private Button twitterLoginButton;
	private Button twitterShareButton;
	
	private Uri uri;
	
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
        
    	TWITTER_CONSUMER_KEY = getString(R.string.twitter_consumer_key);
    	TWITTER_CONSUMER_SECRET = getString(R.string.twitter_consumer_secret);
        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);
        
        mApplicationUser = (ApplicationUser)getApplicationContext();
        mUser = mApplicationUser.getUser();
        
        parseBundle(getIntent());

        uri = getIntent().getData();
//        if (!isTwitterLoggedInAlready()) {
//            Uri uri = getIntent().getData();
//            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
//                // oAuth verifier
//                String verifier = uri
//                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
//     
//                try {
//                    // Get the access token
//                    AccessToken accessToken = twitter.getOAuthAccessToken(
//                            requestToken, verifier);
//     
//                    // Shared Preferences
//                    Editor e = mSharedPreferences.edit();
//     
//                    // After getting access token, access token secret
//                    // store them in application preferences
//                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
//                    e.putString(PREF_KEY_OAUTH_SECRET,
//                            accessToken.getTokenSecret());
//                    // Store login status - true
//                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
//                    e.commit(); // save changes
//
//                } catch (Exception e) {
//                    // Check log for login errors
//                    Log.e("Twitter Login Error", "> " + e.getMessage());
//                }
//            }
//        }

        
        initializeButtons(); 
        
        new ParseTwitterLoginResponseAsyncTask().execute();
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
	    FlurryAgent.logEvent("Share successful workout");
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
     * callback for receiving data from starting an activity for a result
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
    	
    	twitterLoginButton = (Button)findViewById(R.id.checkin_share_twitter_login_button);
    	twitterLoginButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			authenticateTwitter();
    		}
    	});
    	if (isTwitterLoggedInAlready())
    		twitterLoginButton.setText("Log Out");
    	else
    		twitterLoginButton.setText("Log In");
    	
    	twitterShareButton = (Button)findViewById(R.id.checkin_share_twitter_share_button);
    	twitterShareButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			publishToTwitter();
    		}
    	});
    }
    
    /**
     * 
     */
    private void publishToTwitter() {
    	if (isTwitterLoggedInAlready()) {
    		showTwitterDialog();
    	} else {
    		Toast toast = Toast.makeText(this, "You must first log in to Twitter to share", Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	}
    }
    
    private void showTwitterDialog() {
    	Log.i(TAG, "showTwitterDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
	  	final EditText input = new EditText(this);
	  	input.setHint("Enter a message here");	  	
	  	input.setText("I just had an awesome workout at " + mGymName + "! @Fitsby's motivating me to hit the gym! http://fitsby.com #gymmotivation");
    	builder.setView(input);
    	
    	builder.setMessage("Share on Twitter:")
    			.setCancelable(false)
    			.setPositiveButton("Post", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					String text = input.getText().toString();
    					if (text != null && !text.trim().equals("")) {
    						try {
    							new UpdateTwitterAsyncTask().execute(text);
    						} catch (Exception e) {
    							//TODO make a more better error message
    							Toast toast = Toast.makeText(ShareCheckinActivity.this, e.toString(), Toast.LENGTH_LONG);
    							toast.setGravity(Gravity.CENTER, 0, 0);
    							toast.show();
    						}
    					} else {
							Toast toast = Toast.makeText(ShareCheckinActivity.this, "Your status can't be empty", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
    					}
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /**
     * Function to update status
     * */
    class UpdateTwitterAsyncTask extends AsyncTask<String, String, String> {
     
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(ShareCheckinActivity.this, "",
                    "Updating to Twitter...");

        }
     
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
     
                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
     
                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
     
                // Update status
                twitter4j.Status response = twitter.updateStatus(status);
     
                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }
     
        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            mProgressDialog.dismiss();
        }
     
    }
    
    /**
     * parses the incoming bundle
     * @param intent
     */
    private void parseBundle(Intent intent) {
    	Bundle extras = intent.getExtras();
    	if (extras == null) {
    		Toast.makeText(this, "extras null", Toast.LENGTH_SHORT).show();
    		mGymName = mSharedPreferences.getString(GYM_KEY, "");
    		return;
    	}	
    	mGymName = extras.getString(LeagueDetailBundleKeys.KEY_GYM_NAME);
    	if (mGymName == null) {
    		mGymName = mSharedPreferences.getString(GYM_KEY, "");
    	}
    	else {
            Editor e = mSharedPreferences.edit();
            // After getting access token, access token secret
            // store them in application preferences
            e.putString(GYM_KEY, mGymName);
            e.commit();
    	}
    }
    
    /**
     * opens up dialog for users to share their check-in on Facebook
     */
    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session == null || !session.isOpened()) {
        	Toast toast = Toast.makeText(this, "You must first log in to Facebook to share", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
        }
        else {

            Bundle postParams = new Bundle();
            postParams.putString("name", "Fitsby");
            postParams.putString("caption", "Stay motivated to hit the gym.");
            postParams.putString("description", "I just had an awesome workout at " + mGymName + "! Fitsby is motivating me to hit the gym! Download the free app at fitsby.com.");
            postParams.putString("link", "http://fitsby.com");
            postParams.putString("picture", "http://fitsby.com/images/icon_logo.png");

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
                        	if (error == null && values != null) {
                        		final String postId = values.getString("post_id");
                        		if (postId != null) {
                        			Toast toast = Toast.makeText(ShareCheckinActivity.this, "Successfully posted to Facebook", Toast.LENGTH_SHORT);
                        			toast.setGravity(Gravity.CENTER, 0, 0);
                        			toast.show();
                        		}
                        	}
                        }

                    })
                    .build();
                feedDialog.show();
        }

    }
    
    /**
     * logins in user if not, logsout if logged in
     */
    private void authenticateTwitter() {
    	if (!isTwitterLoggedInAlready()) {
    		ConfigurationBuilder builder = new ConfigurationBuilder();
    		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
    		builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);    	
    		Configuration configuration = builder.build();

    		TwitterFactory factory = new TwitterFactory(configuration);
    		twitter = factory.getInstance();

    		new LoginTwitterAsyncTask().execute();
    	} else {
    		showTwitterLogoutAlertDialog();
    	}
    	
    }
    
    /**
     * shows AlertDialog
     */
    private void showTwitterLogoutAlertDialog() {
    	Log.i(TAG, "showTwitterLogoutAlertDialog");

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure that you would like to log out of Twitter?")
    			.setCancelable(false)
    			.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    		    	    // Clear the shared preferences
    		    	    Editor e = mSharedPreferences.edit();
    		    	    e.remove(PREF_KEY_OAUTH_TOKEN);
    		    	    e.remove(PREF_KEY_OAUTH_SECRET);
    		    	    e.remove(PREF_KEY_TWITTER_LOGIN);
    		    	    e.commit();
    		    	    
    		    	    twitterLoginButton.setText("Log In");
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }
    
    private class LoginTwitterAsyncTask extends AsyncTask<Integer, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(ShareCheckinActivity.this, "",
                    "Connecting to Twitter...");
		}
		
		@Override
		protected Void doInBackground(Integer... params) {
	   		try {
    			requestToken = twitter
    					.getOAuthRequestToken(TWITTER_CALLBACK_URL);
    		} catch (TwitterException e) {
    			e.printStackTrace();
    		}
			return null;
		}
		
		protected void onPostExecute(Void input) {
			mProgressDialog.dismiss();
			ShareCheckinActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(requestToken.getAuthenticationURL())));
		}
    }
    
    private class ParseTwitterLoginResponseAsyncTask extends AsyncTask<Integer, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(ShareCheckinActivity.this, "",
                    "Checking if you are logged in to any social networks...");
		}
		
    	protected Void doInBackground(Integer... params) {
            if (!isTwitterLoggedInAlready()) {
                
                if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                    // oAuth verifier
                    String verifier = uri
                            .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
         
                    try {
                        // Get the access token
                        AccessToken accessToken = twitter.getOAuthAccessToken(
                                requestToken, verifier);
         
                        // Shared Preferences
                        Editor e = mSharedPreferences.edit();
         
                        // After getting access token, access token secret
                        // store them in application preferences
                        e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                        e.putString(PREF_KEY_OAUTH_SECRET,
                                accessToken.getTokenSecret());
                        // Store login status - true
                        e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                        e.commit(); // save changes
                        twitterLoginButton.setText("Log Out");
                    } catch (Exception e) {
                        // Check log for login errors
                        Log.e("Twitter Log In Error", "> " + e.getMessage());
                    }
                }
            }
            return null;
    	}
    	
		protected void onPostExecute(Void input) {
            mProgressDialog.dismiss();
		}
    }
}
