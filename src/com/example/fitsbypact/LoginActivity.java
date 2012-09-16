package com.example.fitsbypact;

import registration.RegisterClientSideValidation;
import servercommunication.ServerCommunication;
import dbhandlers.DatabaseHandler;
import dbtables.User;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";
	
	private Button buttonLogin;
	private EditText emailET;
	private EditText passwordET;
	
	private ServerCommunication comm;
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
    		Toast.makeText(LoginActivity.this, "Sorry no internet, please try again",
    				Toast.LENGTH_LONG).show();
    	}
    	
    	if (dbHandler.getUserTableHandler().isEmailPasswordComboValid(email, password)) {
    		Toast.makeText(LoginActivity.this, "User exists", Toast.LENGTH_LONG).show();
    	} else {
    		//TODO password salting maybe?
    		Toast.makeText(LoginActivity.this, "Incorrect password or username", Toast.LENGTH_LONG).show();
    	}
    	
    }
    
    /** 
     * helper function which initializes the EditTexts
     */
    private void initializeEditTexts() {
    	emailET = (EditText)findViewById(R.id.login_email_id);
    	passwordET = (EditText)findViewById(R.id.login_password_id);
    }
}
