package com.example.fitsbypact;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	private Button logoutButton;
	private Button changePictureButton;
	private Button submitButton;
	private Button inviteButton;
	
	private EditText emailET;
	private EditText oldPasswordET;
	private EditText newPasswordET;
	private EditText newPasswordConfirmET;
	
	private final static String TAG = "SettingsActivity";
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeEditTexts();
    }

    /**
     * called when options menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
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
	 * initializes the editTexts
	 */
	private void initializeEditTexts() {
		emailET = (EditText)findViewById(R.id.settings_et_change_email);
		oldPasswordET = (EditText)findViewById(R.id.settings_et_old_password);
		newPasswordET = (EditText)findViewById(R.id.settings_et_new_password);
		newPasswordConfirmET = (EditText)findViewById(R.id.settings_et_new_password_confirm);
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons() {
		logoutButton = (Button)findViewById(R.id.settings_button_logout);
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});
		
		changePictureButton = (Button)findViewById(R.id.settings_button_change_picture);
		changePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changePicture();
			}
		});
		
		submitButton = (Button)findViewById(R.id.settings_button_submit); 
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		
		inviteButton = (Button)findViewById(R.id.settings_button_invite_friends);
		inviteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				invite();
			}
		});
	}
	
	/**
	 * logs the user out
	 */
	private void logout() {
		//TODO implement
	}
	
	/**
	 * change picture
	 */
	private void changePicture() {
		//TODO figure out how to do and then implement
	}
	
	/**
	 * submits user input info
	 */
	private void submit() {
		//TODO figure out what need to be submit and then submit it
	}
	
	/**
	 * goes to invite friends activity
	 */
	private void invite() {
		//TODO go to the invite friends activity
	}
}
