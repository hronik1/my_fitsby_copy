package com.example.fitsby.fragments;

import java.io.FileNotFoundException;
import java.io.InputStream;

import responses.StatsResponse;
import responses.StatusResponse;
import servercommunication.CheckinCommunication;
import servercommunication.MyHttpClient;
import servercommunication.UserCommunication;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.fitsby.FirstTimeCheckinActivity;
import com.example.fitsby.LandingActivity;
import com.example.fitsby.LoginActivity;
import com.example.fitsby.MessengerService;
import com.example.fitsby.TutorialActivity;
import com.example.fitsby.applicationsubclass.ApplicationUser;
import com.example.fitsby.R;


import dbtables.Stats;
import dbtables.User;
import formatters.LastNameFormatter;
import gravatar.Gravatar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
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
	private TextView gravatarLinkTV;
	private TextView faqTV;
	private TextView termsTV;
	private TextView policyTV;
	private TextView facebookTV;
	private TextView resetPasswordTV;
	private TextView twitterTV;
	
	private Button logoutButton;
	private Button submitButton;
	private Button tutorialButton;
	private Button checkinTutorialButton;
	
	private EditText emailET;
	private ProgressDialog mProgressDialog;
	
	private ImageView profileIV;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	private Activity parent;
	
	private Messenger mService;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            mService = new Messenger(service);

            try {
                Message msg = Message.obtain(null,
                        MessengerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            	Log.e(TAG, e.toString());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;

        }
    };
    
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
	    		Toast toast = Toast.makeText(parent, "You did not select a photo",
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
		gravatarLinkTV = (TextView)viewer.findViewById(R.id.me_settings_tv_change_picture_link);
		gravatarLinkTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openGravatarSite();
			}
		});
		
		faqTV = (TextView)viewer.findViewById(R.id.me_faq);
		faqTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fitsby.com/faq.html"));
		 		startActivity(browserIntent);
			}
		});
		
		termsTV  = (TextView)viewer.findViewById(R.id.me_terms_service);
		termsTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fitsby.com/terms.html"));
		 		startActivity(browserIntent);
			}
		});
		
		policyTV = (TextView)viewer.findViewById(R.id.me_privacy_policy);
		policyTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fitsby.com/privacy.html"));
		 		startActivity(browserIntent);
			}
		});
		
		facebookTV = (TextView)viewer.findViewById(R.id.me_facebook);
		facebookTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/FitsbyApp"));
		 		startActivity(browserIntent);
			}
		});
		
		twitterTV = (TextView)viewer.findViewById(R.id.me_twitter);
		twitterTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/fitsby"));
		 		startActivity(browserIntent);
			}
		});
		
		resetPasswordTV = (TextView)viewer.findViewById(R.id.me_reset_password);
		resetPasswordTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertResetPassword();
			}
		});
		
	}
	
	/**
	 * initializes the editTexts
	 */
	private void initializeEditTexts(View viewer) {
		emailET = (EditText)viewer.findViewById(R.id.me_settings_et_change_email);
		emailET.setText(mUser.getEmail());
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons(View viewer) {
		logoutButton = (Button)viewer.findViewById(R.id.me_settings_button_logout);
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoutAlertDialog();
			}
		});
		
//		//changePictureButton = (Button)viewer.findViewById(R.id.me_settings_button_change_picture);
//		changePictureButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				changePicture();
//			}
//		});
		
		submitButton = (Button)viewer.findViewById(R.id.me_settings_button_submit); 
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		
		tutorialButton = (Button)viewer.findViewById(R.id.me_settings_tutorial_button);
		tutorialButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, TutorialActivity.class);
				startActivity(intent);
			}
		});
		
		checkinTutorialButton = (Button)viewer.findViewById(R.id.me_settings_checkin_tutorial_button);
		checkinTutorialButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, FirstTimeCheckinActivity.class);
				startActivity(intent);
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
	 * opens up the gravatar url page
	 */
	private void openGravatarSite() {
 		//TODO change url to point to faq url
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gravatar.com"));
 		startActivity(browserIntent);
	}
	
	/**
	 * submits user input info
	 */
	private void submit() {
		//TODO figure out what need to be submit and then submit it
		changeEmail();
	}
	
	/**
	 * call to change a user's email
	 */
	private void changeEmail() {
		String email = emailET.getText().toString();
		if (email.equals("")) {
    		Toast toast = Toast.makeText(parent, "You must fill out your email," +
    				"so we can send you the change email form",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
		}
		if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			new ChangeEmailAsyncTask().execute(email, mUser.getID()+"");
		} else {
    		Toast toast = Toast.makeText(parent, "That doesn't appear to be a valid email",
    				Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
		}
	}
	
    /**
     * shows dialog to reset their password
     */
    private void showAlertResetPassword() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(parent);

    	alert.setMessage("Reset password link will be sent to your email.");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(parent);
    	input.setText(mUser.getEmail());
    	alert.setView(input);

    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			String value = input.getText().toString();
    			if (!"".equals(value)) {
    				new PasswordResetAsyncTask().execute(value);
    			}
    			else {
            		Toast toast = Toast.makeText(parent, "Sorry, but your email cannot be empty.", Toast.LENGTH_LONG);
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
     * shows AlertDialog
     */
    public void showLogoutAlertDialog() {
    	Log.i(TAG, "onCreateDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	builder.setMessage("If you are currently checked in at a gym, you must check out to have it count. Are you sure you want to log out right now?")
    			.setCancelable(false)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					//new CheckoutAsyncTask().execute(mUser.getID());
    					logout();
    				}
    			})
    			.setNegativeButton("No", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class StatsAsyncTask extends AsyncTask<Integer, Void, StatsResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Gathering your information..");
		}
		
        protected StatsResponse doInBackground(Integer... params) {

        	StatsResponse response = UserCommunication.getStats(params[0]);
        	return response;
        }

        protected void onPostExecute(StatsResponse response) {
        	mProgressDialog.dismiss();
        	if (response.wasSuccessful()) {
        		Stats stats = response.getStats();
        		earningsTV.setText(" $" + stats.getMoneyEarned());
        		checkinsTV.setText("" + stats.getTotalCheckins());
        		timeTV.setText(" " + stats.getTotalTime() + " minutes");
        		joinTV.append(" " + stats.getJoinedMonth() + "/" + stats.getJoinedDay() + "/" + stats.getJoinedYear());
        		new GravatarAsyncTask().execute(mUser.getEmail());
        		
        	} else {
        		Toast toast = Toast.makeText(parent, "User statistics may be inaccurate because there doesn't appear to be an internet connection", Toast.LENGTH_LONG);
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
        		Toast toast = Toast.makeText(parent, "There doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, response.getStatus(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "Your email was successfully changed", Toast.LENGTH_LONG);
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
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Gathering your gravatar..");
		}
		
        protected Bitmap doInBackground(String... params) {
        	String gravatarURL = Gravatar.getGravatar(params[0]);
        	return MyHttpClient.getBitmapFromURL(gravatarURL);
        }

        protected void onPostExecute(Bitmap response) {
        	mProgressDialog.dismiss();
        	if (response != null)
        		profileIV.setImageBitmap(response);
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class CheckoutAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Checking you out of your games...");
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = CheckinCommunication.checkout(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	if (response.wasSuccessful()) {
                Message msg = Message.obtain(null,
                        MessengerService.MSG_STOP_TIMER);
                msg.replyTo = mMessenger;
                try {
					mService.send(msg);
				} catch (RemoteException e) {
					Log.e(TAG, e.toString());
				}
        	}
			logout();
        }
    }
    
    /**
     * Handler of incoming messages from service.
     */
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
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
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Sending you a link to reset your password...");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = UserCommunication.resetPassword(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(parent, "Sorry, but there doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "That email does not exist", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "A link to change your password has just been sent to you", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }
}
