package com.example.fitsbypact;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FriendInviteActivity extends Activity {

	private final static String TAG = "FriendInviteActivity";
	
	private Button homeButton;
	private Button inviteButton;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);
        
        Log.i(TAG, "onCreate");
        
        initializeButtons(); 
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
        
        Log.i(TAG, "onStart");
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
    	Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();
    	ContentResolver contentResolver = getContentResolver();
    	Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
    			null, null, null, null);
        if (contactsCursor.getCount() > 0) {
        	while (contactsCursor.moveToNext()) {
        		String id = contactsCursor.getString(
        				contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
        		String name = contactsCursor.getString(
        				contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        		if (Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
        			if (Integer.parseInt(contactsCursor.getString(
        					contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
        				Cursor phoneNumberCursor = contentResolver.query(
        						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
        						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
        								new String[]{id}, null);
        				while (phoneNumberCursor.moveToNext()) {
        					String phoneNumber = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));
        					Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_LONG).show();
        				} 
        				phoneNumberCursor.close();
        			}
        		}
        	}
        	Toast.makeText(getApplicationContext(), "end", Toast.LENGTH_LONG).show();
        }
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
    		Toast.makeText(getApplicationContext(), "sorry can not start activity", Toast.LENGTH_LONG).show();
    	}
    }
}
