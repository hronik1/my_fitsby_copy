package com.fitsby;

import registration.RegisterClientSideValidation;
import responses.UserResponse;
import servercommunication.ServerCommunication;
import servercommunication.UserCommunication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fitsby.applicationsubclass.ApplicationUser;
import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;

import constants.FlurryConstants;

/**
 * RegisterActivity is the class that users interact with to register with
 * Fitsby.
 * 
 * @author brenthronk
 *
 */
public class RegisterActivity extends ActionBarActivity {

	/**
	 * Tag used in logat messages.
	 */
	private final static String TAG = "MainActivity";
	
	/**
	 * Button pressed to register.
	 */
	private Button buttonRegister;
	
	/**
	 * EditText to input users first name.
	 */
	private EditText firstNameET;
	/**
	 * EditText to input users last name.
	 */
	private EditText lastNameET;
	/**
	 * EditText to input users  email address.
	 */
	private EditText emailET;
	/**
	 * EditText to input desired password.
	 */
	private EditText passwordET;
	/**
	 * EditText to input confirmation of desired password.
	 */
	private EditText confirmPasswordET;
	/**
	 * Clicked to open up terms of service.
	 */
	private TextView serviceTV;
	/**
	 * Clicked to open up privacy policy.
	 */
	private TextView privacyTV;
	
	/**
	 * Validates internet connectivity.
	 */
	private ServerCommunication comm;
	/**
	 * Stores session information.
	 */
	private ApplicationUser mApplicationUser;
	
	/**
	 * Displays ongoing background activity.
	 */
	private ProgressDialog mProgressDialog;
	
	/**
	 * Called when activity is created, initializes views.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeEditTexts();
        initializeTextViews();
        
        comm = new ServerCommunication(this);
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        
    }
    
    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register, menu);
        return true;
    }
    
    /**
     * Called when activity is restarted.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        Log.i(TAG, "onRestart");
    }

    /**
     * Called when activity is starting, starts flurry session.
     */
    @Override
    public void onStart() {
        super.onStart();
	    FlurryAgent.onStartSession(this, FlurryConstants.key);
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Register Activity");
        Log.i(TAG, "onStart");
    }
    
    /**
     * Called when activity is stopping, stops flurry session.
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
        
        Log.i(TAG, "onResume");
    }
    
    /**
     * Called when activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
       
        Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : " Not Finishing"));
    }
    
    /**
     * Called when activity is destroyed.
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	Log.i(TAG, "onDestroy");
    	
    }
    
    /**
     * Connects the buttons the the layout and adds listeners.
     */
    private void initializeButtons() {
    	buttonRegister = (Button)findViewById(R.id.register_button_register);
    	buttonRegister.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			register();
    		}
    	});
    }	
    
    /**
     * Pulls information from editTexts and then registers user.
     */
    private void register() {  
    	final String firstName = firstNameET.getText().toString();
    	final String lastName = lastNameET.getText().toString();
    	final String password  = passwordET.getText().toString();
    	final String confirmPassword = confirmPasswordET.getText().toString();
    	final String email = emailET.getText().toString();

    	String validity = RegisterClientSideValidation.validateName(firstName, lastName);
    	validity += RegisterClientSideValidation.validateEmail(email);
    	validity += RegisterClientSideValidation.validatePassword(password, confirmPassword);
    	
    	if (validity != "") {
    		Toast toast = Toast.makeText(RegisterActivity.this, validity, Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		return;
    	}

    	if (!comm.isInternetConnected()) {
    		Toast toast = Toast.makeText(RegisterActivity.this, "Low or no internet connectiviy. Please try again later",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		return;
    	}
    
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Please confirm that " + email +
    			" is your email.")
    			 .setCancelable(false)
    			.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					new RegisterAsyncTask().execute(email, password, confirmPassword, firstName, lastName);
    				}
    			})
    			.setNegativeButton("No", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    	
    }
    
    /** 
     * Initializes the EditTexts.
     */
    private void initializeEditTexts() {
    	firstNameET = (EditText)findViewById(R.id.register_first_name_id);
    	lastNameET = (EditText)findViewById(R.id.register_last_name_id);
    	emailET = (EditText)findViewById(R.id.register_email_id);
    	passwordET = (EditText)findViewById(R.id.register_password_id);
    	confirmPasswordET = (EditText)findViewById(R.id.register_confirm_password_id);
    }

    /**
     * Connects the TextViews to the layouts and adds listeners.
     */
    private void initializeTextViews() {
    	serviceTV = (TextView)findViewById(R.id.register_tos_tv);
    	serviceTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fitsby.com/terms.html"));
		 		startActivity(browserIntent);
			}
    	});
    	
    	privacyTV = (TextView)findViewById(R.id.register_privacy_tv);
    	privacyTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fitsby.com/privacy.html"));
		 		startActivity(browserIntent);
			}
    	});
    }

    /**
     * RegisterAsyncTask registers the user on a background thread.
     * 
     * @author brenthronk
     *
     */
    private class RegisterAsyncTask extends AsyncTask<String, Void, UserResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(RegisterActivity.this, "",
						"Registering you...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						RegisterAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected UserResponse doInBackground(String... params) {
        	UserResponse response = UserCommunication.registerUser(params[0], params[1], params[2], params[3], params[4]);
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
        	}  else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(RegisterActivity.this, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, that email is either invalid or already taken. It is also possible that the internet quality may be inadequate.", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		//TODO switch to next page
        		mApplicationUser.setUser(response.getUser());
        		Log.v(TAG, "successful registration");
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
				Intent intent = new Intent(RegisterActivity.this, LeagueJoinActivity.class);
				startActivity(intent);
        	}
        }
    }
   
    
    private class RegisterGCMAsyncTask extends AsyncTask<String, Void, Void> {
    	//TODO check if this needs to be called
		@Override
		protected Void doInBackground(String... params) {
			UserCommunication.registerDevice(params[0], (mApplicationUser.getUser().getID()+""));
			return null;

		}
		
		protected void onPostExecute(Void response) {
			Intent intent = new Intent(RegisterActivity.this, LeagueJoinActivity.class);
			startActivity(intent);
		}
    }
    
}
