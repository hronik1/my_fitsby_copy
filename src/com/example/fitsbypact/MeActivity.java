package com.example.fitsbypact;

import java.io.FileNotFoundException;
import java.io.InputStream;

import responses.StatsResponse;
import responses.StatusResponse;
import servercommunication.LeagueCommunication;
import servercommunication.UserCommunication;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbtables.Stats;
import dbtables.User;
import widgets.NavigationBar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import constants.FlurryConstants;

public class MeActivity extends Activity {

	private final static String TAG = "MeActivity";
	private final static int PICK_PHOTO_REQUEST = 1;
	
	private TextView nameTV;
	private TextView joinTV;
	private TextView earningsTV;
	private TextView checkinsTV;
	private TextView timeTV;
	
	private Button logoutButton;
	private Button changePictureButton;
	private Button submitButton;
	
	private EditText emailET;
	private EditText oldPasswordET;
	private EditText newPasswordET;
	private EditText newPasswordConfirmET;
	private ProgressDialog mProgressDialog;
	
	private ImageView profileIV;
	
	private NavigationBar navigation;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	/**
	 * called when activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        initializeNavigationBar();
        initializeTextViews();
        initializeButtons();
        initializeEditTexts();
        initializeImageView();
        
        new StatsAsyncTask().execute(mUser.getID());
    }

    /**
     * called when optionsmenu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_me, menu);
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
	    FlurryAgent.logEvent("Me Activity");
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == PICK_PHOTO_REQUEST) {

	        if (resultCode == RESULT_OK) {
	        	Uri selectedImage = data.getData();
	        	try {
					InputStream fileInputStream=this.getContentResolver().openInputStream(selectedImage);
					Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
					//TODO set profile picture to proper size
					//profileIV.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else if (resultCode == RESULT_CANCELED) {
	    		Toast toast = Toast.makeText(MeActivity.this, "Sorry, but you did not select a photo",
	    				Toast.LENGTH_LONG);
	    		toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
	        }
	        
	        //TODO actually change picture
	        
	    }
	}
	
	/**
	 * initialized NavigationBar for use
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.me_navigation_bar);
		navigation.setParentActivity(this);
		navigation.turnOffTV("me");
	}
	
	/**
	 * initialize the textviews
	 */
	public void initializeTextViews() {
		nameTV = (TextView)findViewById(R.id.me_textview_name);
		nameTV.setText(mUser.getFirstName() + " " + mUser.getLastName());
		
		joinTV = (TextView)findViewById(R.id.me_textview_join_date);
		//TODO add join date
		
		earningsTV = (TextView)findViewById(R.id.me_textview_total_money_earned_money);
		checkinsTV = (TextView)findViewById(R.id.me_textview_total_checkins);
		timeTV = (TextView)findViewById(R.id.me_textview_total_gym_time);
		//add earnings for user
	}
	
	/**
	 * initializes the editTexts
	 */
	private void initializeEditTexts() {
		emailET = (EditText)findViewById(R.id.me_settings_et_change_email);
		emailET.setText(mUser.getEmail());
		oldPasswordET = (EditText)findViewById(R.id.me_settings_et_old_password);
		newPasswordET = (EditText)findViewById(R.id.me_settings_et_new_password);
		newPasswordConfirmET = (EditText)findViewById(R.id.me_settings_et_new_password_confirm);
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons() {
		logoutButton = (Button)findViewById(R.id.me_settings_button_logout);
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});
		
		changePictureButton = (Button)findViewById(R.id.me_settings_button_change_picture);
		changePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changePicture();
			}
		});
		
		submitButton = (Button)findViewById(R.id.me_settings_button_submit); 
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		
	}
	
	/**
	 * initializes the imageview
	 */
	private void initializeImageView() {
		profileIV = (ImageView)findViewById(R.id.me_imageview);
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
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
	}
	
	/**
	 * submits user input info
	 */
	private void submit() {
		//TODO figure out what need to be submit and then submit it
		changeEmail();
	}
	
	/**
	 * call to change a users email
	 */
	private void changePassword() {
		//TODO implement 
		String oldPassword = oldPasswordET.getText().toString();
		String newPassword = newPasswordET.getText().toString();
		String newPasswordConfirm = newPasswordConfirmET.getText().toString();
		if (oldPassword.equals("") || newPassword.equals("") || newPasswordConfirm.equals("")) {
    		Toast toast = Toast.makeText(MeActivity.this, "Sorry, but all " +
    				"password related fields must be filled out in order to change your password.",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
		}
		
		//TODO add in asynccall to server
	}
	
	/**
	 * call to change a user's email
	 */
	private void changeEmail() {
		String email = emailET.getText().toString();
		if (email.equals("")) {
    		Toast toast = Toast.makeText(MeActivity.this, "Sorry, you must fill out your email," +
    				"so we can send you the change email form",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
		}
		
		new ChangeEmailAsyncTask().execute(email, mUser.getID()+"");
	}
	
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class StatsAsyncTask extends AsyncTask<Integer, Void, StatsResponse> {
        protected StatsResponse doInBackground(Integer... params) {
        	StatsResponse response = UserCommunication.getStats(params[0]);
        	return response;
        }

        protected void onPostExecute(StatsResponse response) {
        	if (response.wasSuccessful()) {
        		Stats stats = response.getStats();
        		earningsTV.setText(" $" + stats.getMoneyEarned());
        		checkinsTV.setText(" " + stats.getTotalCheckins());
        		timeTV.setText(" " + stats.getTotalTime() + " minutes");

        		joinTV.append(" " + stats.getJoinedMonth() + "/" + stats.getJoinedDay() + "/" + stats.getJoinedYear());

        		
        	} else {
        		Toast toast = Toast.makeText(MeActivity.this, "Sorry, but since there doesn't appear to be an internet connection at the moment, user statistics may be inaccurate.", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		earningsTV.setText(" $0");
        	}
        	
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class ChangeEmailAsyncTask extends AsyncTask<String, Void, StatusResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(MeActivity.this, "",
                    "Changing your email..");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = UserCommunication.changeEmail(params[0], params[1]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but there doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(getApplicationContext(), "Your email was changed", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }
}
