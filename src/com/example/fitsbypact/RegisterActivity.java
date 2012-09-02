package com.example.fitsbypact;


import dbhandlers.DatabaseHandler;
import dbtables.User;
import registration.RegisterClientSideValidation;
import servercommunication.ServerCommunication;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private final static String TAG = "MainActivity";
	
	private Button buttonLogin;
	private Button buttonRegister;
	
	private EditText firstNameET;
	private EditText lastNameET;
	private EditText emailET;
	private EditText passwordET;
	
	private ServerCommunication comm;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
        
        initializeButtons();
        initializeEditTexts();
        
        comm = new ServerCommunication(this);
    }
    
    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
    	buttonRegister = (Button)findViewById(R.id.register_button);
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
    	String email = "";
    	DatabaseHandler dbHandler = new DatabaseHandler(this);
    	
    	if (firstNameET != null && firstNameET.getText() != null)
    		firstName = firstNameET.getText().toString();
    	if (lastNameET != null && lastNameET.getText() != null)
        	lastName = lastNameET.getText().toString();
    	if (passwordET != null && passwordET.getText() != null)
    		password = passwordET.getText().toString();
    	if (emailET != null && emailET.getText() != null)
    		email = emailET.getText().toString();

    	String validity = RegisterClientSideValidation.validateName(firstName, lastName);
    	validity += RegisterClientSideValidation.validateEmail(email);
    	validity += RegisterClientSideValidation.validatePassword(password);
    	
    	if (validity != "") {
    		Toast.makeText(RegisterActivity.this, validity, Toast.LENGTH_LONG).show();
    		return;
    	}

    	if (!comm.isInternetConnected()) {
    		Toast.makeText(RegisterActivity.this, "Sorry no internet, please try again",
    				Toast.LENGTH_LONG).show();
    	}
    	
    	if (!dbHandler.isEmailUnique(email)) {
    		Toast.makeText(RegisterActivity.this, "Sorry email already exists", Toast.LENGTH_LONG).show();
    	} else {
    		//TODO password salting maybe?
    		User user = new User(firstName, lastName, email, password);
    		dbHandler.addUser(user);
    	}
    	
    }
    
    /** 
     * helper function which initializes the EditTexts
     */
    private void initializeEditTexts() {
    	firstNameET = (EditText)findViewById(R.id.register_first_name_id);
    	lastNameET = (EditText)findViewById(R.id.register_last_name_id);
    	emailET = (EditText)findViewById(R.id.register_email_id);
    	passwordET = (EditText)findViewById(R.id.register_password_id);
    }

}
