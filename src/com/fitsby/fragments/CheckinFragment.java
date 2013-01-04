package com.fitsby.fragments;

import java.util.List;
import java.util.Vector;

import responses.AddPlaceResponse;
import responses.PlacesResponse;
import responses.StatusResponse;
import responses.ValidateGymResponse;
import servercommunication.CheckinCommunication;
import servercommunication.UserCommunication;




import bundlekeys.LeagueDetailBundleKeys;

import com.actionbarsherlock.app.SherlockFragment;
import com.fitsby.FirstTimeCheckinActivity;
import com.fitsby.LoggedinActivity;
import com.fitsby.MessengerService;
import com.fitsby.R;
import com.fitsby.ShareCheckinActivity;
import com.fitsby.applicationsubclass.ApplicationUser;

import constants.RememberMeConstants;

import dbtables.User;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CheckinFragment extends SherlockFragment {

	private final static String TAG = "CheckinFragment";
	
	private final static int UPDATE_TIME_MILLIS = 1000; //one second
	private final static int DEFAULT_PLACES_RADIUS = 500; //200 meters
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
//        doBindService();
        
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
		
		LocationManager service = (LocationManager) parent.getSystemService(LoggedinActivity.LOCATION_SERVICE);
		boolean gpsEnabled = service
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!gpsEnabled) {
			showAlertDialog(); 
		} else {
			LocationListener listener = new LocationListener() {
				public void onLocationChanged(Location location) {
				}

				public void onStatusChanged(String provider, int status, Bundle extras) {}

				public void onProviderEnabled(String provider) {}

				public void onProviderDisabled(String provider) {}
			};
			List<String> matchingProviders = service.getAllProviders();
			Location bestResult = service.getLastKnownLocation(matchingProviders.get(0));
			float bestAccuracy = Float.MAX_VALUE;
			long bestTime = Long.MIN_VALUE;
			long minTime = Long.MAX_VALUE;
			for (String provider: matchingProviders) {
			  Location location = service.getLastKnownLocation(provider);
			  if (location != null) {
			    float accuracy = location.getAccuracy();
			    long time = location.getTime();
			        
			    if ((time > minTime && accuracy < bestAccuracy)) {
			      bestResult = location;
			      bestAccuracy = accuracy;
			      bestTime = time;
			    }
			    else if (time < minTime && 
			             bestAccuracy == Float.MAX_VALUE && time > bestTime){
			      bestResult = location;
			      bestTime = time;
			    }
			  }
			}
			service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener); 
			latitude = bestResult.getLatitude();
			longitude = bestResult.getLongitude();
			new GooglePlacesAsyncTast().execute(getString(R.string.places_api_key), latitude+"",
					longitude+"", DEFAULT_PLACES_RADIUS+"", "true");

		}
	}
	
    /**
     * shows AlertDialog
     */
    public void showAlertDialog() {
    	Log.i(TAG, "onCreateDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	builder.setMessage("Turning on your phone's GPS is required to make sure you are at a gym.\nWould you like to turn it on?")
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

        
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(parent,
        		android.R.layout.simple_list_item_1, gymNames);
        ListView listView = new ListView(parent);
        listView.setAdapter(adapter);
 
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	builder.setView(listView);
        
    	builder.setMessage("Please select your gym, or add your own if your gym is not listed")
		.setCancelable(false)
		.setPositiveButton("Add my own gym", new DialogInterface.OnClickListener() {
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
    
    public void showPublishGymDialog() {
    	Log.i(TAG, "showPublisGymDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    	
    	builder.setMessage("Congratulations! You had a successful workout! Share the news with your friends?")
    			.setCancelable(false)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					Intent intent = new Intent(parent, ShareCheckinActivity.class);
    					intent.putExtra(LeagueDetailBundleKeys.KEY_GYM_NAME, gym);
    					startActivity(intent);
    				}
    			})
    			.setNegativeButton("No", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			}).show();
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
	    	builder.setMessage("You have to be here for at least " + MIN_CHECKIN_TIME + " minutes to have a successful check-out. Are you sure you want to stop early?")
	    			.setCancelable(false)
	    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					Message msg = Message.obtain(null,
	    							MessengerService.MSG_STOP_TIMER);
	    					msg.replyTo = mMessenger;
	    					try {
	    						mService.send(msg);
	    					} catch (RemoteException e) {
	    						Log.e(TAG, e.toString());
	    					}
	    					checkinLocationTV.setText("You are currently not checked in at a verified gym");
	    					checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.red_x_mark));
	    				}
	    			})
	    			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    				}
	    			}).show();
	    	return;

		} else {
			LocationManager service = (LocationManager) parent.getSystemService(LoggedinActivity.LOCATION_SERVICE);
			boolean gpsEnabled = service
			  .isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!gpsEnabled) {
				showAlertDialog(); 
			} else {
				LocationListener listener = new LocationListener() {
					public void onLocationChanged(Location location) {
					}

					public void onStatusChanged(String provider, int status, Bundle extras) {}

					public void onProviderEnabled(String provider) {}

					public void onProviderDisabled(String provider) {}
				};
				List<String> matchingProviders = service.getAllProviders();
				Location bestResult = service.getLastKnownLocation(matchingProviders.get(0));
				float bestAccuracy = Float.MAX_VALUE;
				long bestTime = Long.MIN_VALUE;
				long minTime = Long.MAX_VALUE;
				for (String provider: matchingProviders) {
				  Location location = service.getLastKnownLocation(provider);
				  if (location != null) {
				    float accuracy = location.getAccuracy();
				    long time = location.getTime();
				        
				    if ((time > minTime && accuracy < bestAccuracy)) {
				      bestResult = location;
				      bestAccuracy = accuracy;
				      bestTime = time;
				    }
				    else if (time < minTime && 
				             bestAccuracy == Float.MAX_VALUE && time > bestTime){
				      bestResult = location;
				      bestTime = time;
				    }
				  }
				}
				service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener); 
				latitude = bestResult.getLatitude();
				longitude = bestResult.getLongitude();
				
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
		}
		

		
	}
	
	private class GooglePlacesAsyncTast extends AsyncTask<String, Void, PlacesResponse> {

		String latitude, longitude;
		protected void onPreExecute() {
			gyms.clear();
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Finding nearby gyms...");
		}
		
        protected PlacesResponse doInBackground(String... params) {
        	PlacesResponse response = CheckinCommunication.getNearbyGyms(params[0],
        			params[1], params[2], params[3], params[4]);
        	latitude = params[1];
        	longitude = params[2];
        	return response;
        }

        protected void onPostExecute(PlacesResponse response) {
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (response.getGyms().isEmpty()){
        		showAddGymDialog();
//        		new GooglePlacesSearchAsyncTask().execute(getString(R.string.places_api_key), latitude+"",
//				longitude+"", DEFAULT_PLACES_RADIUS+"", "true");
        	} else {
//        		gyms.addAll(response.getGyms());
//        		gym = response.getGyms().get(0);
//				new CheckinAsyncTask().execute(mUser.getID()+"", latitude, longitude);
        		showSelectGymDialog(response.getGyms().toArray());
        	}
        }
	}
	
	private class GooglePlacesSearchAsyncTask extends AsyncTask<String, Void, PlacesResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Finding nearby gyms and rec centers...");
		}
		
        protected PlacesResponse doInBackground(String... params) {
        	PlacesResponse response = CheckinCommunication.getNearbyRecCenter(params[0],
        			params[1], params[2], params[3], params[4]);
        	return response;
        }

        protected void onPostExecute(PlacesResponse response) {
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (response.getGyms().isEmpty()){
        		Toast toast = Toast.makeText(parent, "There doesn't appear to be a gym or rec center near you", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else {
        		gym = response.getGyms().get(0);
        		for (String temp: response.getGyms()) {
            		Toast toast = Toast.makeText(parent, temp, Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        		}
        		gyms.addAll(response.getGyms());
				//new CheckinAsyncTask().execute(mUser.getID()+"");
        	}
        }
	}
	
	private class GooglePlacesAddAsyncTask extends AsyncTask<String, Void, ValidateGymResponse> {
		String latitude, longitude;
		
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Adding your gym...");
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
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Couldn't find an internet connection", Toast.LENGTH_LONG); 
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
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Checking you in to your games...");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	//TODO give gym name to Danny
        	StatusResponse response = CheckinCommunication.checkin(Integer.parseInt(params[0]), gym, params[1], params[2]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
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
        			error = "Server could not be reached at the moment";
        		Toast toast = Toast.makeText(parent, error, Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        	
        }
    }
    
    private class NotificationAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Checking you out of your games...");
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = UserCommunication.pushNotificationChange(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	showPublishGymDialog();
        }
    }
    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class CheckoutAsyncTask extends AsyncTask<String, Void, StatusResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Checking you out of your games...");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = CheckinCommunication.checkout(Integer.parseInt(params[0]), params[1], params[2]);
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
        		checkinLocationTV.setText("You are currently not checked in at a gym");
        		checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.red_x_mark));
        		new NotificationAsyncTask().execute(mUser.getID());
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

