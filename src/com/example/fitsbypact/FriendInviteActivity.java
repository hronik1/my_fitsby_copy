package com.example.fitsbypact;

import java.net.URI;
import java.util.ArrayList;

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

import com.example.fitsbypact.applicationsubclass.ApplicationUser;
import com.facebook.FacebookActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

import dbtables.League;
import dbtables.User;

public class FriendInviteActivity extends FacebookActivity {

	private final static String TAG = "FriendInviteActivity";
	private final static int PICK_CONTACT_REQUEST = 2;
	private Button homeButton;
	private Button inviteButton;
	private LinearLayout facebookLL;
	private LinearLayout twitterLL;
	
	private ListView contactsListView;
	private ArrayList<String> contacts;
	private ArrayAdapter<String> contactsAdapter;
	
	private ProgressDialog mProgressDialog;
	private int leagueId;
	private String creatorName;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);
        
        Log.i(TAG, "onCreate");
        
        mApplicationUser = (ApplicationUser)getApplicationContext();
        mUser = mApplicationUser.getUser();
        initializeButtons(); 
        initializeLinearLayouts();
       // initializeListView();
        parseBundle(getIntent());
        try {
        	this.openSession();
        } catch (Exception e) {
        	Log.d(TAG, e.toString());
        }
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

        // Your existing onSaveInstanceState code

        // Save the Facebook session in the Bundle
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
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
        
        Log.i(TAG, "onResume");
    }
    
    /**
     * called when activity is paused
     */
    @Override
    public void onPause() {
        super.onPause();
       
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * called when activity is destroyed
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	Log.i(TAG, "onDestroy");
    	
    }
    
    /**
     * callback for recieving data from starting an activity for a result
     */
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PICK_CONTACT_REQUEST) {
    		if (resultCode == RESULT_OK) {
    			Uri contactUri = data.getData();
    			String[] projection = {Phone.NUMBER};
    			new PhoneNumberAsyncTask(contactUri).execute(projection);
    		}
        }
    }
    
    /**
     * callback for when state changes in facebook session
     */
    @Override
    protected void onSessionStateChange(SessionState state, Exception exception) {
    	switch(state) {


    	case CREATED_TOKEN_LOADED:
    		Toast.makeText(this, "created_token_loaded", Toast.LENGTH_LONG).show();
    		break;    

    	case OPENED_TOKEN_UPDATED:
    		Toast.makeText(this, "opened_token_updated", Toast.LENGTH_LONG).show();
    		break;

    	case CREATED:
    		Toast.makeText(this, "created", Toast.LENGTH_LONG).show();
    		break;

    	case OPENING:
    		Toast.makeText(this, "opning", Toast.LENGTH_LONG).show();
    		break;

    	case OPENED:
    		Toast.makeText(this, "opend", Toast.LENGTH_LONG).show();
    		break;

    	case CLOSED: 
    		Toast.makeText(this, "closed", Toast.LENGTH_LONG).show();
    		break;

    	default:
    		break;
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
    }
    
    private void initializeLinearLayouts() {
    	facebookLL = (LinearLayout)findViewById(R.id.friend_invite_facebook_ll);
    	facebookLL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Session session = Session.getActiveSession();
				if (session == null)
					Toast.makeText(FriendInviteActivity.this, "null", Toast.LENGTH_LONG).show();
				else if (session.isOpened())
					Toast.makeText(FriendInviteActivity.this, "opened", Toast.LENGTH_LONG).show();
				else if (session.isClosed())
					Toast.makeText(FriendInviteActivity.this, "closed", Toast.LENGTH_LONG).show();
				else 
					Toast.makeText(FriendInviteActivity.this, "none", Toast.LENGTH_LONG).show();
			}
    	});
    	
    	twitterLL = (LinearLayout)findViewById(R.id.friend_invite_twitter_ll);
    	twitterLL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
    		Toast.makeText(getApplicationContext(), "no bundle", Toast.LENGTH_LONG).show();
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
    		Toast.makeText(this, "Sorry but it appears that either your device does not have contacts," +
    				" or will not allow me to access them", Toast.LENGTH_LONG).show();
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
    		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but this activity could not be started", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
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
}
