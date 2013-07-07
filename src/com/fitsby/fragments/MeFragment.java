package com.fitsby.fragments;

import java.io.FileNotFoundException;
import java.io.InputStream;

import responses.StatsResponse;
import responses.StatusResponse;
import servercommunication.CheckinCommunication;
import servercommunication.MyHttpClient;
import servercommunication.UserCommunication;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fitsby.FirstTimeCheckinActivity;
import com.fitsby.LandingActivity;
import com.fitsby.LoginActivity;
import com.fitsby.MessengerService;
import com.fitsby.R;
import com.fitsby.TutorialActivity;
import com.fitsby.applicationsubclass.ApplicationUser;

import constants.S3Constants;
import constants.TutorialsConstants;


import dbtables.Stats;
import dbtables.User;
import formatters.LastNameFormatter;
import gravatar.Gravatar;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MeFragment extends Fragment {


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
	private TextView awsTV;
	
	private Button logoutButton;
	private Button submitButton;
	private Button tutorialButton;
	private Button checkinTutorialButton;
	private Button deleteButton;
	private LoginButton facebookAuthButton;
	
	private CheckBox enableNotificationsCB;
	private SharedPreferences mSharedPreferences;
	
	private EditText emailET;
	private ProgressDialog mProgressDialog;
	
	private ImageView profileIV;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	private Activity parent;

	private Messenger mService;
	private boolean isBound;
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(S3Constants.ACCESS_KEY_ID,
					S3Constants.SECRET_KEY));
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			Log.d(TAG, "service connected");
			
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
			Log.d(TAG, "service disconnected");
		}
	};
	/**
	 * callback to add in the stats fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View viewer = (View) inflater.inflate(R.layout.activity_me, container, false);
	    Log.i(TAG, "onCreateView");

        mSharedPreferences = parent.getSharedPreferences(
                "EnableNotifications", 0);
		
		mApplicationUser = ((ApplicationUser)parent.getApplicationContext());
		mUser = mApplicationUser.getUser();
		
	    initializeTextViews(viewer);
	    initializeButtons(viewer);
	    initializeEditTexts(viewer);
	    initializeImageView(viewer);
	    initializeCheckBox(viewer);
		
		doBindService();
		
	    new StatsAsyncTask().execute(mUser.getID());
		
	    return viewer;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	/**
	 * callback for when this fragment is attached to a view
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");
		parent = activity;

	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//keep around for potential future profile pic implementation
	    if (requestCode == PICK_PHOTO_REQUEST) {

	        if (resultCode == Activity.RESULT_OK) {
	        	Uri selectedImage = data.getData();
				new S3PutObjectTask().execute(selectedImage);
	        } else if (resultCode == Activity.RESULT_CANCELED) {
	    		Toast toast = Toast.makeText(parent, "You did not select a photo",
	    				Toast.LENGTH_LONG);
	    		toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
	        }
	        
	        //TODO actually change picture
	        
	    } else {
	    	uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	/**
	 * initialize the textviews
	 */
	public void initializeTextViews(View viewer) {
		nameTV = (TextView)viewer.findViewById(R.id.me_textview_name);
		nameTV.setText(mUser.getFirstName() + " " + LastNameFormatter.format(mUser.getLastName()));
		
		joinTV = (TextView)viewer.findViewById(R.id.me_textview_join_date);		
		earningsTV = (TextView)viewer.findViewById(R.id.me_textview_total_money_earned_money);
		checkinsTV = (TextView)viewer.findViewById(R.id.me_textview_total_checkins);
		timeTV = (TextView)viewer.findViewById(R.id.me_textview_total_gym_time);
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
		
		awsTV = (TextView)viewer.findViewById(R.id.me_settings_tv_aws_picture_link);
		awsTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changePicture();
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
		
		facebookAuthButton = (LoginButton) viewer.findViewById(R.id.me_settings_auth_button);
		facebookAuthButton.setFragment(this);
		if (mUser.isFbUser()) {
			facebookAuthButton.setVisibility(View.VISIBLE);
			logoutButton.setVisibility(View.GONE);
		}
		
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
				intent.putExtra(TutorialsConstants.FROM_ME, true);
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
		
		deleteButton = (Button)viewer.findViewById(R.id.me_settings_button_delete);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, FirstTimeCheckinActivity.class);
				showDeleteDialog();
			}
		});
	}
	
	/**
	 * initializes the imageview
	 */
	private void initializeImageView(View viewer) {
		profileIV = (ImageView)viewer.findViewById(R.id.me_imageview);
	}
	
	private void initializeCheckBox(View viewer) {
		enableNotificationsCB = (CheckBox)viewer.findViewById(R.id.notifications_enabled);
		enableNotificationsCB.setChecked(mSharedPreferences.getBoolean("enabled", true));
		enableNotificationsCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mSharedPreferences.edit().putBoolean("enabled", isChecked).commit();
				new EnableNotificationsAsyncTask().execute(isChecked);
			}
		});
	}
	
    private void doBindService() {
    	Log.d(TAG, "bindService");
        parent.bindService(new Intent(parent, 
                MessengerService.class), mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
        if (mService != null) {
        	try {
        		Message msg = Message.obtain(null,
        				MessengerService.MSG_REGISTER_CLIENT);
        		msg.replyTo = mMessenger;
        		mService.send(msg);
        	} catch (Exception e) {
        		Log.e(TAG, e.toString());
        	}
        }
          
    }

    private void doUnbindService() {
    	//keep around in case service needs unbinding from this context
        if (isBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MessengerService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            parent.unbindService(mConnection);
            isBound = false;
        }
    }
    
	/**
	 * logs the user out
	 */
	private void logout() {
        Message msg = Message.obtain(null,
                MessengerService.MSG_STOP_TIMER);
        msg.replyTo = mMessenger;
        try {

			mService.send(msg);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		Intent intent = new Intent(parent, LandingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mApplicationUser.setUser(null);
		startActivity(intent);
	}
	
	/**
	 * shows the alert dialog prompting user if the would like to delete their account
	 */
	private void showDeleteDialog() {
    	Log.i(TAG, "showDeleteDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	builder.setMessage("Are you sure you would like to delete your account?")
    			.setCancelable(false)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					new DeleteAccountAsyncTask().execute();
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
	}
	
	/**
	 * change picture
	 */
	private void changePicture() {
		//TODO keep around for potential profile pic implementation
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
	}
	
	/**
	 * opens up the gravatar url page
	 */
	private void openGravatarSite() {
 		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gravatar.com"));
 		startActivity(browserIntent);
	}
	
	/**
	 * submits user input info
	 */
	private void submit() {
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

    	alert.setMessage("A link to change your password will be sent to your email.");

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
    			.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					//new CheckoutAsyncTask().execute(mUser.getID());
    					logout();
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    /** facebook stuff **/
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	Log.i(TAG, "onSessionState");
        if (session != null && session.isClosed()) {
        	Log.d(TAG, "state is closed");
        	logout();
        } else {
        	Log.d(TAG, "bad state");
        	if (exception != null)
        		Log.d(TAG, exception.toString());
        	//TODO add logging
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class StatsAsyncTask extends AsyncTask<Integer, Void, StatsResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Gathering your information..", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						StatsAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatsResponse doInBackground(Integer... params) {

        	StatsResponse response = UserCommunication.getStats(params[0]);
        	return response;
        }

        protected void onPostExecute(StatsResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	
        	if (response.wasSuccessful()) {
        		Stats stats = response.getStats();
        		earningsTV.setText(" $" + stats.getMoneyEarned());
        		checkinsTV.setText("" + stats.getTotalCheckins());
        		timeTV.setText(" " + stats.getTotalTime() + " minutes");
        		joinTV.append(" " + stats.getJoinedMonth() + "/" + stats.getJoinedDay() + "/" + stats.getJoinedYear());
        		
        	} else {
        		Toast toast = Toast.makeText(parent, "User statistics may be inaccurate because there doesn't appear to be an internet connection", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		earningsTV.setText(" $0");
        	}
        	if (mUser.getBitmap() == null)
        		new GravatarAsyncTask().execute(mUser.getEmail());
        	else
        		profileIV.setImageBitmap(mUser.getBitmap());
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class ChangeEmailAsyncTask extends AsyncTask<String, Void, StatusResponse> {
		protected void onPreExecute() {
			try { 
				mProgressDialog = ProgressDialog.show(parent, "",
						"Updating changes..", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						ChangeEmailAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = UserCommunication.changeEmail(params[0], params[1]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response == null ) {
        		Toast toast = Toast.makeText(parent, "There doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, response.getStatus(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "Changes successfully updated", Toast.LENGTH_LONG);
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
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Gathering your gravatar..", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						GravatarAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected Bitmap doInBackground(String... params) {
        	String gravatarURL = Gravatar.getGravatar(params[0]);
        	return MyHttpClient.getBitmapFromURL(gravatarURL);
        }

        protected void onPostExecute(Bitmap response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
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
    	//keep around in case we need to be able to checkout from this context
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Checking you out of your games...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						CheckoutAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(Integer... params) {
//        	StatusResponse response = CheckinCommunication.checkout(params[0]);
//        	return response;
        	return null;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
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
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Sending you a link to change your password...", true, true,
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
        		Toast toast = Toast.makeText(parent, "Sorry, but there doesn't appear to be a connection to the internet at this moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "That email doesn't exist", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "A link to change your password has just been sent to you", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        }
    }

    private class EnableNotificationsAsyncTask extends AsyncTask<Boolean, Void, StatusResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Updating your notification status...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						EnableNotificationsAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(Boolean... params) {
        	StatusResponse response = UserCommunication.enableNotifications(mUser.getID()+"", params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
        	//TODO feeling toasty
        }
    }
    
    private class DeleteAccountAsyncTask extends AsyncTask<Void, Void, StatusResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Sending request to delete your account...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						DeleteAccountAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(Void... params) {
        	StatusResponse response = UserCommunication.deleteUser(mUser.getID());
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
				if (response.wasSuccessful()) {
	        		Toast toast = Toast.makeText(parent, "Your account will be deleted when your most last game has ended.", Toast.LENGTH_LONG);
	        		toast.setGravity(Gravity.CENTER, 0, 0);
	        		toast.show();
				}
			} catch (Exception e) { }
        	//TODO feeling toasty
        }
    }

	private class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("uploading pic");
			dialog.setCancelable(false);
			dialog.show();
		}

		protected S3TaskResult doInBackground(Uri... uris) {

			if (uris == null || uris.length != 1) {
				return null;
			}

			// The file location of the image selected.
			Uri selectedImage = uris[0];

			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();

			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			try {
				s3Client.createBucket(S3Constants.getPictureBucket());

				// Content type is determined by file extension.
				PutObjectRequest por = new PutObjectRequest(
						S3Constants.getPictureBucket(), S3Constants.PICTURE_NAME,
						new java.io.File(filePath));
				s3Client.putObject(por);
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			dialog.dismiss();

			if (result.getErrorMessage() != null) {
				Toast.makeText(getActivity(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
//				displayErrorAlert(
//						S3UploaderActivity.this
//								.getString(R.string.upload_failure_title),
//						result.getErrorMessage());
			} else {
				Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class S3TaskResult {
		String errorMessage = null;
		Uri uri = null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
	}
}
