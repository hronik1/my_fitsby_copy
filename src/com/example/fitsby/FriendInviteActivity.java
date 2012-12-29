package com.example.fitsby;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import responses.CreatorResponse;
import responses.PrivateLeagueResponse;
import responses.StatusResponse;
import responses.UsersGamesResponse;
import servercommunication.LeagueCommunication;
import servercommunication.UserCommunication;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.telephony.SmsManager;
import bundlekeys.LeagueDetailBundleKeys;

import com.example.fitsby.applicationsubclass.ApplicationUser;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

import dbtables.League;
import dbtables.User;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class FriendInviteActivity extends Activity {

	private final static String TAG = "FriendInviteActivity";
	private final static int PICK_CONTACT_REQUEST = 2;

	private Button homeButton;
	private Button inviteButton;
	private Button shareButton;
	private Button twitterLoginButton;
	private Button twitterShareButton;
	
	private ListView contactsListView;
	private ArrayList<String> contacts;
	private ArrayAdapter<String> contactsAdapter;
	
	private ProgressDialog mProgressDialog;
	private static int leagueId;
	private String creatorName;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	//twitter
	private SharedPreferences mSharedPreferences;
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    private static Twitter twitter;
    private static RequestToken requestToken;
	private String TWITTER_CONSUMER_KEY;
	private String TWITTER_CONSUMER_SECRET;
	
	private Uri uri;
	
	
	//facebook
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);
        
    	TWITTER_CONSUMER_KEY = getString(R.string.twitter_consumer_key);
    	TWITTER_CONSUMER_SECRET = getString(R.string.twitter_consumer_secret);
        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);
    	
        Log.i(TAG, "onCreate");
        
        mApplicationUser = (ApplicationUser)getApplicationContext();
        mUser = mApplicationUser.getUser();
        parseBundle(getIntent());
        uri = getIntent().getData();
        
//        if (!isTwitterLoggedInAlready()) {
//            
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
//                    Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    // Check log for login errors
//                	Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("Twitter Login Error", "> " + e.getMessage());
//                }
//            }
//        }
        
        initializeButtons(); 
        
        new CreatorAsyncTask().execute(leagueId+"");
    }

    /**
     * callled when menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend_invite, menu);
        Log.i(TAG, "onCreateOptionsMenu");
        return true;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    
    /**
     * called when activity is restarted
     */
    @Override
    public void onRestart() {
        super.onRestart();

        Log.i(TAG, "onRestart");
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
    
    /**
     * callback for recieving data from starting an activity for a result
     */
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (requestCode == PICK_CONTACT_REQUEST) {
    		if (resultCode == RESULT_OK) {
    			Uri contactUri = data.getData();
    			String[] projection = {Phone.NUMBER};
    			new PhoneNumberAsyncTask(contactUri).execute(projection);
    		}
        } else {
        	uiHelper.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
          
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
           
        }
    }
    
    /**
     * registers the buttons from the layout and adds listeners to them
     */
    private void initializeButtons() {
    	inviteButton = (Button)findViewById(R.id.friend_invite_button_invite);
    	inviteButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			invite();
    		}
    	});
    	
    	homeButton = (Button)findViewById(R.id.friend_invite_button_home);
    	homeButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			home();
    		}
    	});
    	
    	shareButton = (Button)findViewById(R.id.facebook_share_button);
    	shareButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			publishStory();
    		}
    	});
    	
    	twitterLoginButton = (Button)findViewById(R.id.invite_twitter_login_button);
    	twitterLoginButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			authenticateTwitter();
    		}
    	});
    	if (isTwitterLoggedInAlready())
    		twitterLoginButton.setText("twitter logout");
    	else
    		twitterLoginButton.setText("twitter login");
    	
    	twitterShareButton = (Button)findViewById(R.id.invite_twitter_share_button);
    	twitterShareButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			publishToTwitter();
    		}
    	});
    }

    private void initializeListView() {
		contacts =  new ArrayList<String>();

		contactsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, contacts);
		contactsListView.setAdapter(contactsAdapter);
		
		contactsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, final int position,
					long id) {
			  	AlertDialog.Builder builder = new AlertDialog.Builder(FriendInviteActivity.this);
		    	
			  	final EditText input = new EditText(FriendInviteActivity.this);
			  	input.setText("I want to challenge you on gym check-ins using Fitsby! " +
			  			"Download the free app at fitsby.com, then join my game (Game Host is "  + creatorName + " & Game ID is " + leagueId + ")");
		    	builder.setView(input);
		    	
		    	builder.setMessage("Please confirm that you want to send an invite to " + contacts.get(position))
		    			.setCancelable(false)
		    			.setPositiveButton("Send", new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog, int id) {
		    				    SmsManager smsManager = SmsManager.getDefault();
		    				    smsManager.sendTextMessage(contacts.get(position), null, input.getText().toString(), null, null);
		    				}
		    			})
		    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog, int id) {
		    					dialog.cancel();
		    				}
		    			}).show();
			}
			
		});
		
    }
    
    private void parseBundle(Intent intent) {
    	Bundle extras = intent.getExtras();
    	if (extras == null) {
    		return;
    	}
    		
    	leagueId = extras.getInt(LeagueDetailBundleKeys.KEY_LEAGUE_ID);
    }
    /**
     * invite your friends selected from the content provider
     */
    private void invite() {
    	//TODO implement inviting functionality
    	queryContacts();
    }
    
    /**
     * querys the contacts of the given user
     */
    private void queryContacts() {
    	//TODO turn this shitty looking throw away code into something beautiful
    	
//    	ContentResolver contentResolver = getContentResolver();
//    	Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
//    			null, null, null, null);
//        if (contactsCursor.getCount() > 0) {
//        	while (contactsCursor.moveToNext()) {
//        		String id = contactsCursor.getString(
//        				contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
//        		String name = contactsCursor.getString(
//        				contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//        		if (Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//        			if (Integer.parseInt(contactsCursor.getString(
//        					contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//        				Cursor phoneNumberCursor = contentResolver.query(
//        						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
//        						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
//        								new String[]{id}, null);
//        				while (phoneNumberCursor.moveToNext()) {
//        					String phoneNumber = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));
//        					contacts.add(phoneNumber);
//        					contactsAdapter.notifyDataSetChanged();
//        				} 
//        				phoneNumberCursor.close();
//        			}
//        		}
//        	}
//        }
    	
    	try {
    		Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
    		pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
    		startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    	} catch (Exception e) {
    		Toast.makeText(this, "It appears that either your device does not have contacts," +
    				" or will not allow us to access them", Toast.LENGTH_LONG).show();
    	}

    }
    /**
     * go to your home page
     */
    private void home() {
    	try {
    		Intent intent = new Intent(this, LoggedinActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
//    		this.finish();
    	} catch(Exception e) {
    		//TODO handle not starting intent better
    		Toast toast = Toast.makeText(getApplicationContext(), "This activity could not be started", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	}
    }
    
    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session == null || !session.isOpened()) {
        	Toast.makeText(this, "You must sign in", Toast.LENGTH_LONG).show();
        }
        else {

            Bundle postParams = new Bundle();
            postParams.putString("name", "Fitsby");
            postParams.putString("caption", "An app that motivates you to go to the gym.");
            postParams.putString("description", "Challenge your friends on gym check-ins and win their money when the don't go to the gym.");
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
                                Toast.makeText(FriendInviteActivity.this,
                                    "Posted story, id: "+postId,
                                Toast.LENGTH_SHORT).show();
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
    	    // Clear the shared preferences
    	    Editor e = mSharedPreferences.edit();
    	    e.remove(PREF_KEY_OAUTH_TOKEN);
    	    e.remove(PREF_KEY_OAUTH_SECRET);
    	    e.remove(PREF_KEY_TWITTER_LOGIN);
    	    e.commit();
    	    
    	    twitterLoginButton.setText("twitter login");
    	}
    	
    }
    
    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }
    
    /**
     * 
     */
    private void publishToTwitter() {
    	if (isTwitterLoggedInAlready()) {
    		showTwitterDialog();
    	} else {
    		Toast.makeText(this, "Sorry, must log in to twitter first", Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void showTwitterDialog() {
    	Log.i(TAG, "showTwitterDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
	  	final EditText input = new EditText(this);
	  	input.setHint("status");
    	builder.setView(input);
    	
    	builder.setMessage("Enter status and post to twitter.")
    			.setCancelable(false)
    			.setPositiveButton("Post", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					String text = input.getText().toString();
    					if (text != null && !text.trim().equals("")) {
    						try {
    							new UpdateTwitterAsyncTask().execute(text);
    						} catch (Exception e) {
    							//TODO make a more better error message
    							Toast toast = Toast.makeText(FriendInviteActivity.this, e.toString(), Toast.LENGTH_LONG);
    							toast.setGravity(Gravity.CENTER, 0, 0);
    							toast.show();
    						}
    					} else {
							Toast toast = Toast.makeText(FriendInviteActivity.this, "Sorry, but your status can't be empty", Toast.LENGTH_LONG);
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
            mProgressDialog = ProgressDialog.show(FriendInviteActivity.this, "",
                    "Updating to twitter...");

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
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class ContactsAsyncTask extends AsyncTask<String, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(FriendInviteActivity.this, "",
                    "Gathering your contacts...");
		}
		
        protected Void doInBackground(String... params) {
        	queryContacts();
			return null;
        }

		protected void onPostExecute() {
        	mProgressDialog.dismiss();
        	
        }
    }
    
    private class CreatorAsyncTask extends AsyncTask<String, Void, CreatorResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(FriendInviteActivity.this, "",
                    "Gathering game data...");
		}
		
        protected CreatorResponse doInBackground(String... params) {
        	CreatorResponse response = LeagueCommunication.getCreator(leagueId+"");
        	return response;
        }

		protected void onPostExecute(CreatorResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response.wasSuccessful()) {
            	creatorName = response.getCreatorFirstName();
        	}
        		
        	new ParseTwitterLoginResponseAsyncTask().execute();
        }
    }
    
    private class PhoneNumberAsyncTask extends AsyncTask<String, Void, String> {
    	
    	private Uri uri;
    	
    	public PhoneNumberAsyncTask(Uri uri) {
    		this.uri = uri;
    	}
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(FriendInviteActivity.this, "",
                    "Gathering contact info...");
		}
		
        protected String doInBackground(String... params) {
			Cursor cursor = getContentResolver()
					.query(uri, params, null, null, null);
			cursor.moveToFirst();
			int column = cursor.getColumnIndex(Phone.NUMBER);
			String number = cursor.getString(column);
        	return number;
        }

		protected void onPostExecute(final String number) {
        	mProgressDialog.dismiss();
        	
		  	AlertDialog.Builder builder = new AlertDialog.Builder(FriendInviteActivity.this);
		  	
		  	final EditText input = new EditText(FriendInviteActivity.this);
		  	input.setText("I want to challenge you on gym check-ins using Fitsby! " +
		  			"Download the free app at fitsby.com, then join my game (Game Host is "  + creatorName + " & Game ID is " + leagueId + ")");
	    	builder.setView(input);
	    	
	    	builder.setMessage("Edit personal message to your friend")
	    			.setCancelable(false)
	    			.setPositiveButton("Send", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    				    SmsManager smsManager = SmsManager.getDefault();
	    				    ArrayList<String> texts = smsManager.divideMessage(input.getText().toString());
	    				    smsManager.sendMultipartTextMessage(number, null, texts, null, null);
	    				    new NotifyAsyncTask().execute(mUser.getID());
	    				}
	    			})
	    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    				}
	    			}).show();
        		

        }
    }
    
    private class NotifyAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {

		@Override
		protected StatusResponse doInBackground(Integer... params) {
			return UserCommunication.notifySeverOfInvite(params[0]);
		}
    	
    }
    
    private class LoginTwitterAsyncTask extends AsyncTask<Integer, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(FriendInviteActivity.this, "",
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
			FriendInviteActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(requestToken.getAuthenticationURL())));
		}
    }
    
    private class ParseTwitterLoginResponseAsyncTask extends AsyncTask<Integer, Void, Void> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(FriendInviteActivity.this, "",
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
                        twitterLoginButton.setText("twitter logout");
                    } catch (Exception e) {
                        // Check log for login errors
                        Log.e("Twitter Login Error", "> " + e.getMessage());
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
