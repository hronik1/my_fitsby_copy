package com.fitsby;


import registration.RegisterClientSideValidation;
import responses.StatusResponse;
import responses.UserResponse;
import servercommunication.ServerCommunication;
import servercommunication.UserCommunication;
import dbhandlers.DatabaseHandler;
import dbhandlers.UserTableHandler;
import dbtables.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";
	
	private Button buttonLogin;
	private EditText emailET;
	private EditText passwordET;
	private TextView forgotPasswordTV;
	private CheckBox rememberMeCB;
	
	private SharedPreferences mSharedPreferences;
	private String preferencesEmail;
	private String preferencesPassword;
	
	private ServerCommunication comm;
	private DatabaseHandler mdbHandler;
	private UserTableHandler mUserTableHandler;
	private ApplicationUser mApplicationUser;
	
	private ProgressDialog mProgressDialog;
	
	/**
	 * Called when activity is created
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
        
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mUserTableHandler = mdbHandler.getUserTableHandler();
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
	    FlurryAgent.logEvent("Log In Activity");
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
     * helper function which initializes the buttons
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
     * pulls information from editTexts and registers user
     */
    private void login() {  

    	String password = "";
    	String email = "";
    	DatabaseHandler dbHandler = DatabaseHandler.getInstance(this);

    	if (passwordET != null && passwordET.getText() != null)
    		password = passwordET.getText().toString();
    	if (emailET != null && emailET.getText() != null)
    		email = emailET.getText().toString();

    	if (!comm.isInternetConnected()) {
    		Toast toast = Toast.makeText(LoginActivity.this, "You are not connected to the internet. Please try again later.",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		return;
    	}
    	
    	 new LoginAsyncTask().execute(email, password);
//    	String string = new UserCommunication().loginUser(email, password);
//    	Toast.makeText(this, string, Toast.LENGTH_LONG).show();
//    	if (dbHandler.getUserTableHandler().isEmailPasswordComboValid(email, password)) {
//    		User user = mUserTableHandler.getUser(email);
//    		mApplicationUser.setUser(user);
//    		try {
//    			Intent intent = new Intent(this, GamesActivity.class);
//    			startActivity(intent);
//    		} catch (Exception e) {
//    			//TODO something more robust possibly
//    			Toast toast = Toast.makeText(LoginActivity.this, "We cannot log you in at the moment.", Toast.LENGTH_LONG);
//    			toast.setGravity(Gravity.CENTER, 0, 0);
//    			toast.show();
//    		}
//    	} else {
//    		//TODO password salting maybe?
//    		Toast toast = Toast.makeText(LoginActivity.this, "Incorrect Email or Password.", Toast.LENGTH_LONG);
//    		toast.setGravity(Gravity.CENTER, 0, 0);
//    		toast.show();
//    	}
    	
    }
    
    /** 
     * helper function which initializes the EditTexts
     */
    private void initializeEditTexts() {
    	emailET = (EditText)findViewById(R.id.login_email_id);
    	passwordET = (EditText)findViewById(R.id.login_password_id);
    }
    
    /**
     * initializes the textview
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
     * initializes the checkbox
     */
    private void initializeCheckBox() {
    	rememberMeCB = (CheckBox)findViewById(R.id.login_remember_me_checkbox);
    }
    
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
     * shows dialog to reset their password
     */
    private void showAlertInput() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setMessage("Enter your email to receive a link to set a new password");

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
     * helper function to update password
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
     * AsyncTask class to login the user
     * @author brent
     *
     */
    private class LoginAsyncTask extends AsyncTask<String, Void, UserResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "",
                    "Logging you in...");
		}
		
        protected UserResponse doInBackground(String... params) {
        	UserResponse response = UserCommunication.loginUser(params[0], params[1]);
        	return response;
        }

        protected void onPostExecute(UserResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "There doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (response.getError() != null && !response.getError().equals("")) {
         		Toast toast = Toast.makeText(LoginActivity.this, response.getError(), Toast.LENGTH_LONG);
         		toast.setGravity(Gravity.CENTER, 0, 0);
     			toast.show();
         	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Either your email or password was invalid. It's also possible the internet quality may be inadequate.", Toast.LENGTH_LONG);
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
     * AsyncTask class to login the user
     * @author brent
     *
     */
    private class PasswordResetAsyncTask extends AsyncTask<String, Void, StatusResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "",
                    "Sending you a link to reset your password...");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = UserCommunication.resetPassword(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "There doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
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
        		Toast toast = Toast.makeText(getApplicationContext(), "A link to change your password has just been sent to you", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }
}
