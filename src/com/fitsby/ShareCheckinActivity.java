package com.fitsby;


import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bundlekeys.LeagueDetailBundleKeys;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

/**
 * ShareCheckinActivity is an activity where users can share their checkin
 * to various social networks.
 * 
 * @author brenthronk
 *
 */
public class ShareCheckinActivity extends ActionBarActivity {

	/**
	 * Tag used for logcat messages.
	 */
	private final static String TAG = "ShareCheckinActivity";
	/**
	 * Key for the gym name when placed in preferences.
	 */
	private final static String GYM_KEY = "gymKey";
	/**
	 * Pressed to share content.
	 */
	private Button mShareButton;
	/**
	 * The name of the gym where the user is currently checked in to.
	 */
	private static String mGymName;
	/**
	 * Displays ongoing background progress.
	 */
	private ProgressDialog mProgressDialog;
	
	//twitter
	/**
	 * Lightweight storeage for twitter information.
	 */
	private SharedPreferences mSharedPreferences;
	/**
	 * Key to get twitter shared preference.
	 */
    static String PREFERENCE_NAME = "twitter_oauth";
    /**
     * Key for twitter oauth token in preference.
     */
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    /**
     * Key for twitter ouath secret in preference.
     */
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    /**
     * Key for boolean of whether user is logged in or not.
     */
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    /**
     * Used as query paramater to twitter to get ouath token.
     */
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    /**
     * Callback url for obtaining a twitter oauth token.
     */
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsamplecheckin";
    /**
     * Helper class for authenticating to twitter.
     */
    private static Twitter twitter;
    /**
     * Token for twitter request.
     */
    private static RequestToken requestToken;
    /**
     * Key for twitter.
     */
	private String TWITTER_CONSUMER_KEY;
	/**
	 * Secret string for twitter.
	 */
	private String TWITTER_CONSUMER_SECRET;
	/**
	 * Pressed to login to twitter.
	 */
	private Button twitterLoginButton;
	/**
	 * Pressed to share to twitter.
	 */
	private Button twitterShareButton;
	
	/**
	 * Reference to passed in uri.
	 */
	private Uri uri;
	
	//facebook
	/**
	 * Helper class for facebook lifecycle.
	 */
	private UiLifecycleHelper uiHelper;
	/**
	 * Callback wrapper for various aspects of the facebook lifecycle.
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	/**
	 * Callback for creation of the activity, initializes view and social
	 * network handling.
	 */
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
        
        parseBundle(getIntent());

        uri = getIntent().getData();
        
        initializeButtons(); 
        
        new ParseTwitterLoginResponseAsyncTask().execute();
	}
	
	/**
	 * Callback to save state of app so that it can be restored later, saves
	 * state for facebook.
	 */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    /**
     * Called when activity is starting, starts flurry session.
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
     * Callback for pausing of the activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * Called when activity is destroyed.
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	uiHelper.onDestroy();
    	Log.i(TAG, "onDestroy");
    	
    }
    
    /**
     * Callback for stopping of the activity.
     */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
    /**
     * Called when activity resumes.
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

    /**
     * Called when options menu is created.
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_share_checkin, menu);
		return true;
	}
	
    /**
     * Callback for receiving data from starting an activity for a result/
     */
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode != RESULT_CANCELED) {
    		uiHelper.onActivityResult(requestCode, resultCode, data);
    	}

    }
    
    /**
     * Callback for changing of the Session state.
     * 
     * @param session		facebook session info
     * @param state			new state of the session
     * @param exception		reference to exception that occured during change
     * 						of state, if any
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
          
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
           
        }
    }

    /**
     * Connects buttons to layout, adds listeners.
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
     * Confirms that user is logged in to twitter, if so opens up dialog for
     * user to input story to publish, otherwise a prompt explaining that they
     * must first log in.
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
    
    /**
     * Opens up a dialog for user to post twitter story.
     */
    private void showTwitterDialog() {
    	Log.i(TAG, "showTwitterDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
	  	final EditText input = new EditText(this);
	  	input.setHint("Enter message here:");	  	
	  	input.setText("Just had an awesome workout at " + mGymName + "! One step closer to my goal! via @Fitsby #gymmotivation");
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
     * UpdateTwitterAsyncTask posts a story to twitter on a background thread.
     * 
     * @author brenthronk
     *
     */
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
     * Pares the incoming bundle from previous activity.
     * 
     * @param intent	contains bundle to be parsed
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
     * Opens up dialog for users to share their check-in on Facebook.
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
            postParams.putString("name", "I just had an awesome workout at " + mGymName + " using Fitsby!");
            postParams.putString("caption", "Want to stay motivated to work out with me?");
            postParams.putString("description", "I'm one step closer to my goal! Come challenge me to a game of gym check-ins.");
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
     * Logins in user if not, logsout if logged in.
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
     * Shows AlertDialog allowing user to confirm that they want to log out.
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
     */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }
    
    private class LoginTwitterAsyncTask extends AsyncTask<Integer, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(ShareCheckinActivity.this, "",
                    "Connecting to Twitter...", true, true,
                    new OnCancelListener() {
            			public void onCancel(DialogInterface pd) {
            				LoginTwitterAsyncTask.this.cancel(true);
            			}
            		});
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
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
			ShareCheckinActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(requestToken.getAuthenticationURL())));
		}
    }
    
    /**
     * ParseTwitterLoginResponseAsyncTask parses the twitter login callback on
     * a background thread.
     * 
     * @author brenthronk
     *
     */
    private class ParseTwitterLoginResponseAsyncTask extends AsyncTask<Integer, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(ShareCheckinActivity.this, "",
                    "Checking if you are logged in to any social networks...", true, true,
                    new OnCancelListener() {
            			public void onCancel(DialogInterface pd) {
            				ParseTwitterLoginResponseAsyncTask.this.cancel(true);
            			}
            		});
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
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
		}
    }
}
