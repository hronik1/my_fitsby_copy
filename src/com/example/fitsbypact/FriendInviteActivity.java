package com.example.fitsbypact;

import java.net.URI;
import java.util.ArrayList;

import responses.CreatorResponse;
import responses.PrivateLeagueResponse;
import responses.UsersGamesResponse;
import servercommunication.LeagueCommunication;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.telephony.SmsManager;
import bundlekeys.LeagueDetailBundleKeys;

import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

import dbtables.League;
import dbtables.User;

public class FriendInviteActivity extends Activity {

	private final static String TAG = "FriendInviteActivity";
	private final static int PICK_CONTACT_REQUEST = 2;
	private Button homeButton;
	private Button inviteButton;
	
	private ListView contactsListView;
	private ArrayList<String> contacts;
	private ArrayAdapter<String> contactsAdapter;
	
	private ProgressDialog mProgressDialog;
	private String leagueId;
	private String creatorName;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);
        
        Log.i(TAG, "onCreate");
        
        initializeButtons(); 
        initializeListView();
        parseBundle(getIntent());
        
        new CreatorAsyncTask().execute(leagueId);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PICK_CONTACT_REQUEST) {
    		if (resultCode == RESULT_OK) {
    			Uri contactUri = data.getData();
    			String[] projection = {Phone.NUMBER};
    			new PhoneNumberAsyncTask(contactUri).execute(projection);
    		}
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
		    	builder.setMessage("Please confirm that you want to send an invite to " + contacts.get(position) +
		    			" is your email.")
		    			.setCancelable(false)
		    			.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog, int id) {
		    				    SmsManager smsManager = SmsManager.getDefault();
		    				    smsManager.sendTextMessage(contacts.get(position), null, "Hey, I just downloaded this free app called Fitsby and I want to compete with you on gym check-ins!" +
		    				    		"Join my game! Here's the game info: Game Creator's First Name = "  + creatorName + ", Game ID = " + leagueId, null, null);
		    				}
		    			})
		    			.setNegativeButton("Oops!", new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog, int id) {
		    					dialog.cancel();
		    				}
		    			}).show();
			}
			
		});
		
    }
    
    private void parseBundle(Intent intent) {
    	Bundle extras = intent.getExtras();
    	
    	leagueId = extras.getString(LeagueDetailBundleKeys.KEY_LEAGUE_ID);
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
    	

    	Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
    	pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
    	startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);

    }
    /**
     * go to your home page
     */
    private void home() {
    	try {
    		Intent intent = new Intent(this, GamesActivity.class);
    		startActivity(intent);
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

        @SuppressLint("NewApi")
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
        	CreatorResponse response = LeagueCommunication.getCreator(leagueId);
        	return response;
        }

        @SuppressLint("NewApi")
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

        @SuppressLint("NewApi")
		protected void onPostExecute(final String number) {
        	mProgressDialog.dismiss();
        	
		  	AlertDialog.Builder builder = new AlertDialog.Builder(FriendInviteActivity.this);
	    	builder.setMessage("Please confirm that you want to send an invite to " + number +
	    			" is your email.")
	    			.setCancelable(false)
	    			.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    				    SmsManager smsManager = SmsManager.getDefault();
	    				    smsManager.sendTextMessage(number, null, "Hey, I just downloaded this free app called Fitsby and I want to compete with you on gym check-ins!" +
	    				    		"Join my game! Here's the game info: Game Creator's First Name = "  + creatorName + ", Game ID = " + leagueId, null, null);
	    				}
	    			})
	    			.setNegativeButton("Oops!", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    				}
	    			}).show();
        		

        }
    }
}
