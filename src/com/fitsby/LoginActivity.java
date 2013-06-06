package com.fitsby;


import responses.StatusResponse;
import responses.UserResponse;
import servercommunication.ServerCommunication;
import servercommunication.UserCommunication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;

import constants.FlurryConstants;
import constants.RememberMeConstants;

public class LoginActivity extends KiipFragmentActivity {

	/**
	 * Tag used for logcat messages.
	 */
	private static final String TAG = "LoginActivity";
	
	/**
	 * Pressed to login.
	 */
	private Button buttonLogin;
	/**
	 * Where users enter their email.
	 */
	private EditText emailET;
	/**
	 * Where users enter their password.
	 */
	private EditText passwordET;
	/**
	 * Clicked when users forget their password.
	 */
	private TextView forgotPasswordTV;
	/**
	 * Checked if users want their credentials to be remembered.
	 */
	private CheckBox rememberMeCB;
	
	/**
	 * Where the users credentials will be remembered, if they so choose.
	 */
	private SharedPreferences mSharedPreferences;
	/**
	 * The stored email, if exists.
	 */
	private String preferencesEmail;
	/**
	 * The stored password, if exists.
	 */
	private String preferencesPassword;
	
	/**
	 * Checks internet connectivity.
	 */
	private ServerCommunication comm;
	/**
	 * Where session information is stored.
	 */
	private ApplicationUser mApplicationUser;
	
	/**
	 * Used to display ongoing background activity.
	 */
	private ProgressDialog mProgressDialog;
	
	/**
	 * Callback for when activity is created, initializes views.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "onCreate");
        
        comm = new ServerCommunication(this);
        initializeEditTexts();
        initializeButtons();
        initializeTextViews();
        initializeCheckBox();
        initializeSharedPreferences();

        mApplicationUser = ((ApplicationUser)getApplicationContext());
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        Log.i(TAG, "onCreateOptionsMenu");
        
        return true;
    }
    
    /**
     * Callback for when activity is restarted.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        Log.i(TAG, "onRestart");
    }

    /**
     * Callback for when activity is started, starts the flurry session.
     */
    @Override
    public void onStart() {
        super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Log In Activity");
        Log.i(TAG, "onStart");
    }
    
    /**
     * Callback for the stopping of the activity, stops flurry session.
     */
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
    
    /**
     * Callback for when activity resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        
        Log.i(TAG, "onResume");
    }
    
    /**
     * Callback for when activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
       
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * Callback for when activity is destroyed.
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	Log.i(TAG, "onDestroy");
    	
    }
    
    /**
     * Connects buttons to the layout and adds listeners.
     */
    private void initializeButtons() {
    	buttonLogin = (Button)findViewById(R.id.login_button_login);
    	buttonLogin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			login();
    		}
    	});
    }	
    
    /**
     * Pulls information from editTexts and passes information off to
     * asynctask to login.
     */
    private void login() {  

    	String password = "";
    	String email = "";

    	if (passwordET != null && passwordET.getText() != null)
    		password = passwordET.getText().toString();
    	if (emailET != null && emailET.getText() != null)
    		email = emailET.getText().toString();

    	if (!comm.isInternetConnected()) {
    		Toast toast = Toast.makeText(LoginActivity.this, "Limited or no internet connectivity. Please try again later",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		return;
    	}
    	
    	 new LoginAsyncTask().execute(email, password);
    	
    }
    
    /** 
     * Connects the editTexts to the layout.
     */
    private void initializeEditTexts() {
    	emailET = (EditText)findViewById(R.id.login_email_id);
    	passwordET = (EditText)findViewById(R.id.login_password_id);
    }
    
    /**
     * Connects the textViews to the layout.
     */
    private void initializeTextViews() {
    	forgotPasswordTV = (TextView)findViewById(R.id.login_forgot_password);
    	forgotPasswordTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertInput();
			}
    	});
    }
    
    /**
     * Connects the checkBox to the layout.
     */
    private void initializeCheckBox() {
    	rememberMeCB = (CheckBox)findViewById(R.id.login_remember_me_checkbox);
    }
    
    /**
     * Initializes the sharedpreferences, and gathers the stored email and
     * password if they exist.
     */
    private void initializeSharedPreferences() {
    	mSharedPreferences = getSharedPreferences(RememberMeConstants.PREFS_NAME,MODE_PRIVATE); 
    	preferencesEmail = mSharedPreferences.getString(RememberMeConstants.PREF_EMAIL, null);
    	preferencesPassword = mSharedPreferences.getString(RememberMeConstants.PREF_PASSWORD, null);
    	
    	if (preferencesEmail != null) {
    		rememberMeCB.setChecked(true);
    		emailET.setText(preferencesEmail);
    		passwordET.setText(preferencesPassword);
    	} else {
    		rememberMeCB.setChecked(true);
    	}
    }
    
    /**
     * Shows dialog for user to reset password.
     */
    private void showAlertInput() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setMessage("Enter your email to receive a link to set a new password.");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	alert.setView(input);

    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			String value = input.getText().toString();
    			if (!"".equals(value)) {
    				new PasswordResetAsyncTask().execute(value);
    			}
    			else {
            		Toast toast = Toast.makeText(getApplicationContext(), "Your email cannot be empty.", Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
            		toast.show();
    			}	
    		}  
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    		  dialog.cancel();
    	  }
    	});

    	alert.show();
    }
    
    /**
     * Helper function to update password in sharedpreference.
     */
    private void updateRememberMe() {
    	String email = emailET.getText().toString();
    	String password = passwordET.getText().toString();
    	if (rememberMeCB.isChecked()) {
    		//remember new user
            mSharedPreferences.edit()
            .putString(RememberMeConstants.PREF_EMAIL, email)
            .putString(RememberMeConstants.PREF_PASSWORD, password)
            .commit();
    	} else if (!rememberMeCB.isChecked() && email.equals(preferencesEmail)) {
    		//forget old user
            mSharedPreferences.edit()
            .putString(RememberMeConstants.PREF_EMAIL, null)
            .putString(RememberMeConstants.PREF_PASSWORD, null)
            .commit();
    	}
    }

    /**
     * LoginAsyncTask logins the user on a background thread.
     * 
     * @author brenthronk
     *
     */
    private class LoginAsyncTask extends AsyncTask<String, Void, UserResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(LoginActivity.this, "",
						"Logging you in...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						LoginAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected UserResponse doInBackground(String... params) {
        	UserResponse response = UserCommunication.loginUser(params[0], params[1]);
        	return response;
        }

        protected void onPostExecute(UserResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Limited or no internet connectivity", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (response.getError() != null && !response.getError().equals("")) {
         		Toast toast = Toast.makeText(LoginActivity.this, response.getError(), Toast.LENGTH_LONG);
         		toast.setGravity(Gravity.CENTER, 0, 0);
     			toast.show();
         	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Either your email or password was invalid.", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		//TODO switch to next page
        		updateRememberMe();
        		mApplicationUser.setUser(response.getUser());
                try {
                	GCMRegistrar.checkDevice(getApplicationContext());
                	GCMRegistrar.checkManifest(getApplicationContext());
                	final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
                	if (regId.equals("")) {
                		GCMRegistrar.register(getApplicationContext(), getString(R.string.gcm_sender_id));
                		Log.d(TAG, "Just now registered");
                	} else {
                		Log.d(TAG, "Already registered");
                	}
                } catch (Exception e) {
                	Log.e(TAG, e.toString());
                }
    			Intent intent = new Intent(LoginActivity.this, LoggedinActivity.class);
    			startActivity(intent);
        	}
        }
    }
    
    /**
     * PasswordResetAsyncTask sends information to the server to reset the
     * users forgotten password.
     * 
     * @author brenthronk
     *
     */
    private class PasswordResetAsyncTask extends AsyncTask<String, Void, StatusResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(LoginActivity.this, "",
						"Sending you a link to set a new password...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						PasswordResetAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = UserCommunication.resetPassword(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Low or no internet connectivity", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} 
        	 else if (response.getError() != null && !response.getError().equals("")) {
         		Toast toast = Toast.makeText(LoginActivity.this, response.getError(), Toast.LENGTH_LONG);
         		toast.setGravity(Gravity.CENTER, 0, 0);
     			toast.show();
         	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "That email does not exist", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(getApplicationContext(), "A link to set a new password has just been sent to you", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }
}
