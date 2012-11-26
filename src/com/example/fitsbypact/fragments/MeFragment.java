package com.example.fitsbypact.fragments;

import java.io.FileNotFoundException;
import java.io.InputStream;

import responses.StatsResponse;
import responses.StatusResponse;
import servercommunication.MyHttpClient;
import servercommunication.UserCommunication;
import widgets.NavigationBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.fitsbypact.LandingActivity;
import com.example.fitsbypact.R;

import com.example.fitsbypact.applicationsubclass.ApplicationUser;


import dbtables.Stats;
import dbtables.User;
import formatters.LastNameFormatter;
import gravatar.Gravatar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MeFragment extends SherlockFragment {


	private final static String TAG = "MeFragment";
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
	
	private Activity parent;
	
	/**
	 * callback to add in the stats fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View viewer = (View) inflater.inflate(R.layout.activity_me, container, false);
	    Log.i(TAG, "onCreateView");

	    initializeTextViews(viewer);
	    initializeButtons(viewer);
	    initializeEditTexts(viewer);
	    initializeImageView(viewer);

	    new StatsAsyncTask().execute(mUser.getID());
	    
	    return viewer;
	}
	
	/**
	 * callback for when this fragment is attached to a view
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");
		parent = activity;

		mApplicationUser = ((ApplicationUser)parent.getApplicationContext());
		mUser = mApplicationUser.getUser();
//        new StatsAsyncTask().execute(mUser.getID());
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == PICK_PHOTO_REQUEST) {

	        if (resultCode == parent.RESULT_OK) {
	        	Uri selectedImage = data.getData();
	        	try {
					InputStream fileInputStream = parent.getContentResolver().openInputStream(selectedImage);
					Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
					//TODO set profile picture to proper size
					//profileIV.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else if (resultCode == parent.RESULT_CANCELED) {
	    		Toast toast = Toast.makeText(parent, "Sorry, but you did not select a photo",
	    				Toast.LENGTH_LONG);
	    		toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
	        }
	        
	        //TODO actually change picture
	        
	    }
	}
	
	/**
	 * initialize the textviews
	 */
	public void initializeTextViews(View viewer) {
		nameTV = (TextView)viewer.findViewById(R.id.me_textview_name);
		nameTV.setText(mUser.getFirstName() + " " + LastNameFormatter.format(mUser.getLastName()));
		
		joinTV = (TextView)viewer.findViewById(R.id.me_textview_join_date);
		//TODO add join date
		
		earningsTV = (TextView)viewer.findViewById(R.id.me_textview_total_money_earned_money);
		checkinsTV = (TextView)viewer.findViewById(R.id.me_textview_total_checkins);
		timeTV = (TextView)viewer.findViewById(R.id.me_textview_total_gym_time);
		//add earnings for user
	}
	
	/**
	 * initializes the editTexts
	 */
	private void initializeEditTexts(View viewer) {
		emailET = (EditText)viewer.findViewById(R.id.me_settings_et_change_email);
		emailET.setText(mUser.getEmail());
		oldPasswordET = (EditText)viewer.findViewById(R.id.me_settings_et_old_password);
		newPasswordET = (EditText)viewer.findViewById(R.id.me_settings_et_new_password);
		newPasswordConfirmET = (EditText)viewer.findViewById(R.id.me_settings_et_new_password_confirm);
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons(View viewer) {
		logoutButton = (Button)viewer.findViewById(R.id.me_settings_button_logout);
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});
		
		changePictureButton = (Button)viewer.findViewById(R.id.me_settings_button_change_picture);
		changePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changePicture();
			}
		});
		
		submitButton = (Button)viewer.findViewById(R.id.me_settings_button_submit); 
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
	private void initializeImageView(View viewer) {
		profileIV = (ImageView)viewer.findViewById(R.id.me_imageview);
	}
	
	/**
	 * logs the user out
	 */
	private void logout() {
		Intent intent = new Intent(parent, LandingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mApplicationUser.setUser(null);
		startActivity(intent);
//		
//		parent.finish();
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
    		Toast toast = Toast.makeText(parent, "Sorry, but all " +
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
    		Toast toast = Toast.makeText(parent, "Sorry, you must fill out your email," +
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
        		checkinsTV.setText("" + stats.getTotalCheckins());
        		timeTV.setText(" " + stats.getTotalTime() + " minutes");
        		joinTV.append(" " + stats.getJoinedMonth() + "/" + stats.getJoinedDay() + "/" + stats.getJoinedYear());
        		new GravatarAsyncTask().execute(mUser.getEmail());
        		
        	} else {
        		Toast toast = Toast.makeText(parent, "Sorry, but since there doesn't appear to be an internet connection at the moment, user statistics may be inaccurate.", Toast.LENGTH_LONG);
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
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Changing your email..");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = UserCommunication.changeEmail(params[0], params[1]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(parent, "Sorry, but there doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, response.getStatus(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "Your email was changed", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class GravatarAsyncTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... params) {
        	String gravatarURL = Gravatar.getGravatar(params[0]);
        	return MyHttpClient.getBitmapFromURL(gravatarURL);
        }

        protected void onPostExecute(Bitmap response) {
        	if (response != null)
        		profileIV.setImageBitmap(response);
        }
    }
}
