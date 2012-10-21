package com.example.fitsbypact;


import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbhandlers.DatabaseHandler;
import dbhandlers.UserTableHandler;
import dbtables.User;
import registration.RegisterClientSideValidation;
import servercommunication.ServerCommunication;
import servercommunication.UserCommunication;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;

public class RegisterActivity extends Activity {

	private final static String TAG = "MainActivity";
	
	private Button buttonRegister;
	
	private EditText firstNameET;
	private EditText lastNameET;
	private EditText emailET;
	private EditText passwordET;
	private EditText confirmPasswordET;
	
	private ServerCommunication comm;
	private ApplicationUser mApplicationUser;
	private DatabaseHandler mdbHandler;
	private UserTableHandler mUserTableHandler;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeEditTexts();
        
        comm = new ServerCommunication(this);
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
        getMenuInflater().inflate(R.menu.activity_register, menu);
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
	    FlurryAgent.onPageView();
	    FlurryAgent.logEvent("Register Activity");
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
     * shows AlertDialog
     */
    public void showAlertDialog() {
    	Log.i(TAG, "onCreateDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Please confirm that " + emailET.getText().toString() +
    			" is your email.")
    			.setCancelable(false)
    			.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					String firstName = firstNameET.getText().toString();
    					String lastName = lastNameET.getText().toString();
    					String email = emailET.getText().toString();
    					String password = passwordET.getText().toString();
    					//TODO password salting maybe?
    					User user = new User(firstName, lastName, email, password);
    					mdbHandler.getUserTableHandler().addUser(user);
    					user = mUserTableHandler.getUser(email);
    					if (user != null) {
    						mApplicationUser.setUser(user);
    						try {
    							Intent intent = new Intent(RegisterActivity.this, LeagueLandingActivity.class);
    							startActivity(intent);
    						} catch (Exception e) {
    							//remove in deployment
    							String stackTrace = android.util.Log.getStackTraceString(e);
    							Toast toast = Toast.makeText(getApplicationContext(), stackTrace,
    									Toast.LENGTH_LONG);
    							toast.setGravity(Gravity.CENTER, 0, 0);
    							toast.show();
    						} 
    					} else {
    						Toast toast = Toast.makeText(RegisterActivity.this, "sorry error occured", Toast.LENGTH_LONG);
    						toast.setGravity(Gravity.CENTER, 0, 0);
    						toast.show();
    					}
    				}
    			})
    			.setNegativeButton("Oops!", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    
    }
    
    /**
     * helper function which initializes the buttons
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
     * pulls information from editTexts and then registers user
     */
    private void register() {  
    	String firstName = "";
    	String lastName = "";
    	String password = "";
    	String confirmPassword = "";
    	String email = "";
    	
    	if (firstNameET != null && firstNameET.getText() != null)
    		firstName = firstNameET.getText().toString();
    	if (lastNameET != null && lastNameET.getText() != null)
        	lastName = lastNameET.getText().toString();
    	if (passwordET != null && passwordET.getText() != null)
    		password = passwordET.getText().toString();
    	if (confirmPasswordET != null && confirmPasswordET.getText() != null)
    		confirmPassword = confirmPasswordET.getText().toString();
    	if (emailET != null && emailET.getText() != null)
    		email = emailET.getText().toString();

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
    		Toast toast = Toast.makeText(RegisterActivity.this, "Sorry, no internet. Please try again",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		return;
    	}
    
    	new RegisterAsyncTask().execute(email, password, confirmPassword);
//    	if (!mUserTableHandler.isEmailUnique(email)) {
//    		Toast toast = Toast.makeText(RegisterActivity.this, "That email already exists.", Toast.LENGTH_LONG);
//    		toast.setGravity(Gravity.CENTER, 0, 0);
//    		toast.show();
//    	} else {
//    		Log.i(TAG, "showing dialog");
//    		showAlertDialog();
//    	}
    	
    }
    
    /** 
     * helper function which initializes the EditTexts
     */
    private void initializeEditTexts() {
    	firstNameET = (EditText)findViewById(R.id.register_first_name_id);
    	lastNameET = (EditText)findViewById(R.id.register_last_name_id);
    	emailET = (EditText)findViewById(R.id.register_email_id);
    	passwordET = (EditText)findViewById(R.id.register_password_id);
    	confirmPasswordET = (EditText)findViewById(R.id.register_confirm_password_id);
    }

    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class RegisterAsyncTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
        	String string = new UserCommunication().registerUser(params[0], params[1], params[2]);
        	return string;
        }

        protected void onPostExecute(String string) {
        	Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        }
    }
}
