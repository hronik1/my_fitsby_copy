package com.example.fitsbypact;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";
	
	private Button buttonLogin;
	private Button buttonRegister;
	
	private EditText emailET;
	private EditText passwordET;
	
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /**
     * helper function which initializes the buttons
     */
    private void initializeButtons() {
    	buttonLogin = (Button)findViewById(R.id.login_button);
    	buttonLogin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			login();
    		}
    	});
    	
    	buttonRegister = (Button)findViewById(R.id.register_button);
    	buttonRegister.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			register();
    		}
    	});
    }	
    	
    /**
     * pulls information from editTexts and then logs in user
     */
    private void login() {
    		String password = passwordET.getText().toString();
    		String email = emailET.getText().toString();
    		//TODO actually login
    }
    
    /**
     * pulls information from editTexts and then registers user
     */
    private void register() {
    		String password = passwordET.getText().toString();
    		String email = emailET.getText().toString();
    		//TODO actually register user
    }
    
    /** 
     * helper function which initializes the EditTexts
     */
    private void initializeEditTexts() {
    	emailET = (EditText)findViewById(R.id.login_email_id);
    	passwordET = (EditText)findViewById(R.id.login_password_id);
    }

}
