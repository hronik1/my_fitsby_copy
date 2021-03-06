package com.fitsby.fragments;

import java.util.Vector;

import me.kiip.sdk.Kiip;
import me.kiip.sdk.Poptart;
import responses.PlacesResponse;
import responses.StatusResponse;
import responses.ValidateGymResponse;
import servercommunication.CheckinCommunication;
import servercommunication.UserCommunication;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bundlekeys.LeagueDetailBundleKeys;

import com.fitsby.FirstTimeCheckinActivity;
import com.fitsby.LoggedinActivity;
import com.fitsby.MessengerService;
import com.fitsby.R;
import com.fitsby.ShareCheckinActivity;
import com.fitsby.applicationsubclass.ApplicationUser;

import dbtables.User;

public class CheckinFragment extends Fragment {

	private final static String TAG = "CheckinFragment";
	
	private final static long UPDATE_TIME_MILLIS = 1000; //one second
	private final static float UPDATE_MIN_DISTANCE = 0;
	private final static int DEFAULT_PLACES_RADIUS = 700; //200 meters
	private final static int MIN_CHECKIN_TIME = 30;
	private static Activity parent;
	
	private static TextView checkinLocationTV;
	private static TextView minutesTV;
	private static TextView secondsTV;
	
	private static ImageView checkedInIv;
	private Button checkinButton;
	private Button checkoutButton;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	private SharedPreferences mSharedPreferences;
	
	private static int timeSeconds;
	private static int timeMinutes;
	
	private static String gym;
	private ProgressDialog mProgressDialog;

	private Vector<String> gyms;
	
	private static LocationManager mLocationManager;
	private static LocationListener mLocationListener;
	double longitude;
	double latitude;
	
	private Messenger mService;
	private boolean isBound;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
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
        	Log.d(TAG, "service disconnected");
            mService = null;

        }
    };
	
	/**
	 * callback to add in the stats fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View viewer = (View) inflater.inflate(R.layout.activity_check_in, container, false);
	    Log.i(TAG, "onCreateView");
        
        initializeTextViews(viewer);
        initializeImageViews(viewer);
        initializeTime();        
        initializeButtons(viewer);
        
       
        gyms = new Vector<String>();
        Intent intent = new Intent(parent, MessengerService.class);
        parent.startService(intent);
        
        mSharedPreferences = parent.getSharedPreferences(
                "FirstCheckinPrefs", 0);
        return viewer;
	}
	
	/**
	 * callback for when this fragment is attached to a view
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = activity;

		mApplicationUser = ((ApplicationUser)parent.getApplicationContext());
		mUser = mApplicationUser.getUser();
		Intent intent = new Intent(parent, MessengerService.class);
		parent.startService(intent);

	}
	   
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		doBindService();
	}
	
	/**
	 * 
	 */
	public void initializeTime() {
		timeSeconds = 0;
		timeMinutes = 0;
	}
	
	
	
	/**
	 * initializes the ImageViews
	 */
	private void initializeImageViews(View viewer) {
		checkedInIv = (ImageView)viewer.findViewById(R.id.check_in_iv);
		
	}
	
	/**
	 * initializes the TextViews
	 */
	private void initializeTextViews(View viewer) {
		checkinLocationTV = (TextView)viewer.findViewById(R.id.verified_gym);
		secondsTV = (TextView)viewer.findViewById(R.id.seconds);
		minutesTV = (TextView)viewer.findViewById(R.id.minutes);
	}

	
	/**
	 * initializes buttons on this activity
	 */
	public void initializeButtons(View viewer) {
		checkinButton = (Button)viewer.findViewById(R.id.check_in_button_check_in);
		checkinButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkin();
			}
		});
		
		checkoutButton = (Button)viewer.findViewById(R.id.check_in_button_check_out);
		checkoutButton.setOnClickListener(new OnClickListener () {
			public void onClick(View v) {

				checkout();
			}
		});
	}
	
	/**
	 * checks in user
	 */
	public void checkin() {
		if (!mSharedPreferences.getBoolean("first_time", false)) {
			Log.d(TAG, "first time");
			mSharedPreferences.edit().putBoolean("first_time", true).commit();
			Intent intent = new Intent(parent, FirstTimeCheckinActivity.class);
			startActivity(intent);
			return;
		} else {
			Log.d(TAG, "not first time");
		}
		if (timeSeconds > 0 || timeMinutes > 0) {
			Toast toast = Toast.makeText(parent.getApplicationContext(), "You're already checked in!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		mLocationManager = (LocationManager) parent.getSystemService(Activity.LOCATION_SERVICE);
		boolean gpsEnabled = mLocationManager
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!gpsEnabled) {
			showAlertDialog(); 
		} else {
			mLocationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
				}

				public void onStatusChanged(String provider, int status, Bundle extras) {}

				public void onProviderEnabled(String provider) {}

				public void onProviderDisabled(String provider) {}
			};
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					UPDATE_TIME_MILLIS, UPDATE_MIN_DISTANCE, mLocationListener);
			Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				Log.i(TAG, "location not null");
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				new GooglePlacesAsyncTast().execute(getString(R.string.places_api_key), latitude+"",
						longitude+"", DEFAULT_PLACES_RADIUS+"", "true");
			} else {
				Log.i(TAG, "location null");
				new GetLocationAsyncTask(true).execute();
			}
		}
	}
	
	/**
	 * show the alertdialog for confirmation that user should checkout
	 */
	public void showCheckoutDialog() {
		Log.d(TAG, "showCheckoutDialog");
		
	  	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	builder.setMessage("Are you sure you want to check out?")
    			.setCancelable(false)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					new CheckoutAsyncTask().execute(mUser.getID()+"", latitude+"", longitude+"");
    				}
    			})
    			.setNegativeButton("No", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
	}
	
    /**
     * shows AlertDialog
     */
    public void showAlertDialog() {
    	Log.i(TAG, "onCreateDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	builder.setMessage("Turning on GPS is required to verify that you are at a gym. Would you like to turn GPS on?")
    			.setCancelable(false)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					try {
    					  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    					  startActivity(intent);
    					} catch (Exception e) {
    						//TODO make a more better error message
    						Toast toast = Toast.makeText(parent, "Unable to track your location at this moment", Toast.LENGTH_LONG);
    						toast.setGravity(Gravity.CENTER, 0, 0);
    						toast.show();
    					}
    				}
    			})
    			.setNegativeButton("No", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    public void showAddGymDialog() {
    	Log.i(TAG, "onCreateAddGymDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	
	  	final EditText input = new EditText(parent);
	  	input.setHint("Enter gym name here");
	  	input.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Log.d(TAG + ":afterTextChanged", s.toString());
				String string = s.toString();
				if (string.length() > 0 && Character.isLowerCase(s.charAt(0))) {
					string = Character.toUpperCase(s.charAt(0)) + "";
					s.replace(0, 1, string, 0, 1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				Log.d(TAG + ":beforeTextChanged", s.toString() + "Start:" + start
						+ " Count:" + count + " After:" + after);
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.d(TAG + ":onTextChanged", s.toString() + "Start:" + start
						+ " Count:" + count + " Before:" + before);
			}
    		
    	});
    	builder.setView(input);
    	
    	builder.setMessage("We couldn't find any gyms nearby you, or you have requested to add your gym. Please enter your gym name so we can verify that it exists. (Your check-in will count for now)")
    			.setCancelable(false)
    			.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					String text = input.getText().toString();
    					if (text != null && !text.trim().equals("")) {
    						try {
    							new GooglePlacesAddAsyncTask().execute(mUser.getID()+"", latitude+"",
    									longitude+"", input.getText().toString());

    						} catch (Exception e) {
    							//TODO make a more better error message
    							Toast toast = Toast.makeText(parent, e.toString(), Toast.LENGTH_LONG);
    							toast.setGravity(Gravity.CENTER, 0, 0);
    							toast.show();
    						}
    					} else {
							Toast toast = Toast.makeText(parent, "Gym name can't be empty", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
    					}
    				}
    			})
    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
    }
    
    private void showSelectGymDialog(final Object[] gymNames) {
    	Log.i(TAG, "showSelectGymDialog");
    	
    	ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(parent,android.R.layout.simple_list_item_1, gymNames);
    	ListView listView = new ListView(parent);
    	listView.setAdapter(adapter);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setView(listView);
    	builder.setTitle("Please select your gym, or add your own.")
		.setCancelable(false)
		.setPositiveButton("Add New Gym", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showAddGymDialog();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
    	final AlertDialog dialog = builder.create();
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> adapterView, View view, int position,
    				long id) {
    			gym = gymNames[position].toString();
    			new CheckinAsyncTask().execute(mUser.getID()+"", latitude+"", longitude+"");
    			dialog.dismiss();
    		}
    	});
    	dialog.show();
    }
    
	/**
	 * checks out user
	 */
	public void checkout() {
		//TODO redo checkout
		if (timeSeconds == 0 && timeMinutes == 0) {
			Toast toast = Toast.makeText(parent, "You can't check out because you never checked in!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		} 
		if (timeMinutes < MIN_CHECKIN_TIME) {

		  	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
	    	builder.setMessage("You have to be at the gym for at least " + MIN_CHECKIN_TIME + " minutes to have a successful check-out. Are you sure you want to stop early?")
	    			.setCancelable(false)
	    			.setPositiveButton("Stop Early", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					Message msg = Message.obtain(null,
	    							MessengerService.MSG_STOP_TIMER);
	    					msg.replyTo = mMessenger;
	    					try {
	    						mService.send(msg);
	    					} catch (RemoteException e) {
	    						Log.e(TAG, e.toString());
	    					}
	    					checkinLocationTV.setText("Currently not checked in at a gym");
	    					checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.red_x_mark));
	    	        		if (mLocationManager != null && mLocationListener != null) {
	    	        			mLocationManager.removeUpdates(mLocationListener);
	    	        			//TODO look into why this is commented out
//	    	        			new NotificationAsyncTask().execute(mUser.getID());
	    	        		}
	    				}
	    			})
	    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    				}
	    			}).show();
	    	return;

		} else {
			
			if (mLocationManager == null)
				mLocationManager = (LocationManager) parent.getSystemService(Activity.LOCATION_SERVICE);
			boolean gpsEnabled = mLocationManager
			  .isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!gpsEnabled) {
				showAlertDialog(); 
			} else {
				if (mLocationListener == null) {
					mLocationListener = new LocationListener() {
						public void onLocationChanged(Location location) {
						}

						public void onStatusChanged(String provider, int status, Bundle extras) {}

						public void onProviderEnabled(String provider) {}

						public void onProviderDisabled(String provider) {}
					};
				}
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						UPDATE_TIME_MILLIS, UPDATE_MIN_DISTANCE, mLocationListener);
				Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					Log.i(TAG, "location null");
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					showCheckoutDialog();
				} else {
					Log.i(TAG, "location not null");
					new GetLocationAsyncTask(false).execute();
				}

			}
		}
		
	}
	
	private class GooglePlacesAsyncTast extends AsyncTask<String, Void, PlacesResponse> {

		protected void onPreExecute() {
			gyms.clear();
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Finding nearby gyms...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						GooglePlacesAsyncTast.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected PlacesResponse doInBackground(String... params) {
        	PlacesResponse response = CheckinCommunication.getNearbyGyms(params[0],
        			params[1], params[2], params[3], params[4]);
        	return response;
        }

        protected void onPostExecute(PlacesResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (response.getGyms().isEmpty()){
        		showAddGymDialog();
        	} else {

        		showSelectGymDialog(response.getGyms().toArray());
        	}
        }
	}
	
	private class GooglePlacesAddAsyncTask extends AsyncTask<String, Void, ValidateGymResponse> {
		String latitude, longitude;
		
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Adding your gym...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						GooglePlacesAddAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}

        protected ValidateGymResponse doInBackground(String... params) {
        	ValidateGymResponse response = CheckinCommunication.addGym(params[0],
        			params[1], params[2], params[3]);
        	gym = params[3];
        	latitude = params[1];
        	longitude = params[2];
        	return response;
        }

        protected void onPostExecute(ValidateGymResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	}  else if (response.getError() != null && !response.getError().equals("")) {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, response.getMessage(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "Verification successfully sent", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		new CheckinAsyncTask().execute(mUser.getID()+"", latitude, longitude);
        	}
        }
	}
	
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class CheckinAsyncTask extends AsyncTask<String, Void, StatusResponse> {
    	
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Checking you in to your games...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						CheckinAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(String... params) {
        	//TODO give gym name to Danny
        	StatusResponse response = CheckinCommunication.checkin(Integer.parseInt(params[0]), gym, params[1], params[2]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
        	if (response.wasSuccessful()) {
        		Toast toast = Toast.makeText(parent, "Check-in successful!", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
                Message msg = Message.obtain(null,
                        MessengerService.MSG_START_TIMER);
                msg.replyTo = mMessenger;
                try {
					mService.send(msg);
				} catch (RemoteException e) {
					Log.e(TAG, e.toString());
				}
                msg = Message.obtain(null,
                        MessengerService.MSG_SET_GYM);
                Bundle data = new Bundle();
                data.putString(MessengerService.GYM_NAME_KEY, gym);
                msg.setData(data);
                try {
					mService.send(msg);
				} catch (RemoteException e) {
					Log.e(TAG, e.toString());
				}
				checkinLocationTV.setText("Checked in at " + gym);
				checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.green_check_mark));
        	} else {
        		String error = response.getError();
        		if (error == null || error.equals(""))
        			error = getString(R.string.timeout_message);
        		Toast toast = Toast.makeText(parent, error, Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        	
        }
    }
    
    private class NotificationAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
		protected void onPreExecute() {
			try {
				mProgressDialog = ProgressDialog.show(parent, "",
						"Checking you out of your games...", true, true,
						new OnCancelListener() {
					public void onCancel(DialogInterface pd) {
						NotificationAsyncTask.this.cancel(true);
					}
				});
			} catch (Exception e) { }
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = UserCommunication.pushNotificationChange(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
			Intent intent = new Intent(parent, ShareCheckinActivity.class);
			intent.putExtra(LeagueDetailBundleKeys.KEY_GYM_NAME, gym);
			startActivity(intent);
        }
    }
    
    private class GetLocationAsyncTask extends AsyncTask<Void, Void, Location> {
		
    	private final boolean onCheckin;
    	public GetLocationAsyncTask(boolean onCheckin) {
    		super();
    		this.onCheckin = onCheckin;
    	}
    	
    	protected void onPreExecute() {
    		Log.i(TAG, "GetLocationAsyncTask onPreExecute");
    		try {
    			mProgressDialog = ProgressDialog.show(parent, "",
    					"Getting your location", true, true,
    					new OnCancelListener() {
    				public void onCancel(DialogInterface pd) {
    					GetLocationAsyncTask.this.cancel(true);
    				}
    			});
    		} catch (Exception e) { }
		}
		
        protected Location doInBackground(Void... params) {
        	Location location = null;
        	while ((location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        			== null) {
        		//TODO think of a cleaner solution
        	}
        	return location;
        }

        protected void onPostExecute(Location response) {
        	Log.i(TAG, "GetLocationAsyncTask onPostExecute");
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) { }
			
			latitude = response.getLatitude();
			longitude = response.getLongitude();
			if (onCheckin) {
				new GooglePlacesAsyncTast().execute(getString(R.string.places_api_key), latitude+"",
						longitude+"", DEFAULT_PLACES_RADIUS+"", "true");
			} else {
				showCheckoutDialog();
			}
        }
    }
    
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class CheckoutAsyncTask extends AsyncTask<String, Void, StatusResponse> {
    	
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
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = CheckinCommunication.checkout(Integer.parseInt(params[0]), params[1], params[2], gym);
        	return response;
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
        		checkinLocationTV.setText("You are currently not checked in at a gym");
        		checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.red_x_mark));
        		//TODO potentially add kiip back in
//        		Kiip.getInstance().saveMoment(ApplicationUser.MY_MOMENT_ID, new Kiip.Callback() {
//        			@Override
//        			public void onFinished(Kiip kiip, Poptart reward) {
//        				parent.onPoptart(reward);
//        			}
//
//        			@Override
//        			public void onFailed(Kiip kiip, Exception exception) {
//        				// handle failure
//        			}
//        		});


        		if (mLocationManager != null && mLocationListener != null) {
        			mLocationManager.removeUpdates(mLocationListener);
        			new NotificationAsyncTask().execute(mUser.getID());
        		}
        	} else {
        		Toast toast = Toast.makeText(parent, response.getError(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        	
        }
    }
    
    /**
     * Handler of incoming messages from service.
     */
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_SET_VALUE:
                	timeSeconds = msg.arg2;
                	timeMinutes = msg.arg1;
	                if (timeSeconds < 10)
	                	secondsTV.setText("0" + timeSeconds);
	                else 
	                	secondsTV.setText(timeSeconds + "");
	                if (timeMinutes < 10)
	                	minutesTV.setText("0" + timeMinutes);
	                else
	                	minutesTV.setText(timeMinutes + "");
	            break;
                case MessengerService.MSG_SET_GYM:
                	Log.d(TAG, "set gym");
                	Bundle data = msg.getData();
                	gym = data.getString(MessengerService.GYM_NAME_KEY);
                	Log.d(TAG, "set gym name = " + gym);
                	if (gym != null) {
        				checkinLocationTV.setText("Checked in at " + gym);
        				checkedInIv.setImageDrawable(parent.getResources().getDrawable(R.drawable.green_check_mark));
                	}
                break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    
    void doBindService() {
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
        	} catch (RemoteException e) {
        		Log.e(TAG, e.toString());
        	}
        }
          
    }

    void doUnbindService() {
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

}

