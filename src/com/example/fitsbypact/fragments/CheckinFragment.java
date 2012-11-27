package com.example.fitsbypact.fragments;

import java.util.List;
import java.util.Vector;

import responses.AddPlaceResponse;
import responses.PlacesResponse;
import responses.StatusResponse;

import responses.ValidateGymResponse;
import servercommunication.CheckinCommunication;




import com.actionbarsherlock.app.SherlockFragment;
import com.example.fitsbypact.R;
import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbtables.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class CheckinFragment extends SherlockFragment{

	private final static String TAG = "CheckinFragment";
	
	private final static int MESSAGE_START_TIMER = 0;
	private final static int MESSAGE_UPDATE_TIMER = 1;
	private final static int MESSAGE_STOP_TIMER = 2;
	private final static int UPDATE_TIME_MILLIS = 1000; //one second
	private final static int DEFAULT_PLACES_RADIUS = 50; //200 meters

	private boolean checkedIn;
	private Activity parent;
	
	private TextView checkinLocationTV;
	private TextView minutesTV;
	private TextView secondsTV;
	
	private ImageView checkedInIv;
	private Button checkinButton;
	private Button checkoutButton;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	private static Handler mHandler;
	private int timeSeconds;
	private int timeMinutes;
	
	private String gym;
	private ProgressDialog mProgressDialog;

	private Vector<String> gyms;
	
	double longitude;
	double latitude;
	
	/**
	 * callback to add in the stats fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View viewer = (View) inflater.inflate(R.layout.activity_check_in, container, false);
	    Log.i(TAG, "onCreateView");
        
        initializeTextViews(viewer);
        initializeImageViews(viewer);
        initializeHandler();
        initializeTime();        
        initializeButtons(viewer);
        
       
        gyms = new Vector<String>();
        checkedIn = false;
        
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
	   
	/**
	 * 
	 */
	public void initializeTime() {
		timeSeconds = 0;
		timeMinutes = 0;
	}
	
	/**
	 * initializes handler
	 */
	public void initializeHandler() {
		mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message message) {
	            super.handleMessage(message);
	            switch (message.what) {
	            case MESSAGE_START_TIMER:
	                mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIMER, UPDATE_TIME_MILLIS);
	                break;

	            case MESSAGE_UPDATE_TIMER:
	                mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIMER,UPDATE_TIME_MILLIS);
	                //TODO hope jitter isn't a big problem
	                if (++timeSeconds == 60){
	                	timeSeconds = 0;
	                	timeMinutes++;
	                }
	                if (timeSeconds < 10)
	                	secondsTV.setText("0" + timeSeconds);
	                else 
	                	secondsTV.setText(timeSeconds + "");
	                if (timeMinutes < 10)
	                	minutesTV.setText("0" + timeMinutes);
	                else
	                	minutesTV.setText(timeMinutes + "");
	                break;
	                
	            case MESSAGE_STOP_TIMER:
	            	Log.d(TAG, "stopTimer");
	                mHandler.removeMessages(MESSAGE_UPDATE_TIMER);
	                timeSeconds = 0;
	                timeMinutes = 0;
	                secondsTV.setText("00");
	                minutesTV.setText("00");
	                break;

	            default:
	                break;
	            }
	        }
		};
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
		if (checkedIn) {
			Toast toast = Toast.makeText(parent.getApplicationContext(), "Hey buddy, don't go getting greedy ;) you're already checked in", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		LocationManager service = (LocationManager) parent.getSystemService(parent.LOCATION_SERVICE);
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
    	builder.setMessage("GPS location is required to make sure you are at a gym.\nWould you like to turn it on?")
    			.setCancelable(false)
    			.setPositiveButton("Yup!", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					try {
    					  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    					  startActivity(intent);
    					} catch (Exception e) {
    						//TODO make a more better error message
    						Toast toast = Toast.makeText(parent, "Sorry, but it seems we can't track your location at this moment", Toast.LENGTH_LONG);
    						toast.setGravity(Gravity.CENTER, 0, 0);
    						toast.show();
    					}
    				}
    			})
    			.setNegativeButton("I'll pass", new DialogInterface.OnClickListener() {
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
	  	input.setText("Gym name here");
    	builder.setView(input);
    	
    	builder.setMessage("If you are at your gym and it is not showing up please add it, but if you're lying beware... We will find you, and we will ban you")
    			.setCancelable(false)
    			.setPositiveButton("Added it", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					try {
    						new GooglePlacesAddAsyncTask().execute(getString(R.string.places_api_key), latitude+"",
    								longitude+"", DEFAULT_PLACES_RADIUS+"", "true", input.getText().toString());

    					} catch (Exception e) {
    						//TODO make a more better error message
    						Toast toast = Toast.makeText(parent, e.toString(), Toast.LENGTH_LONG);
    						toast.setGravity(Gravity.CENTER, 0, 0);
    						toast.show();
    					}
    				}
    			})
    			.setNegativeButton("Nope, not really at a gym", new DialogInterface.OnClickListener() {
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
		if (!checkedIn) {
			Toast toast = Toast.makeText(parent, "Sorry, but you can't check out because you never checked in", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		} 
		if (timeMinutes < 45) {

		  	AlertDialog.Builder builder = new AlertDialog.Builder(parent);
	    	builder.setMessage("Hey, you have to be here for 45 minutes for the checkin to count for your score, sure you want to?")
	    			.setCancelable(false)
	    			.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    	        		mHandler.sendEmptyMessage(MESSAGE_STOP_TIMER);
	    	        		checkinLocationTV.setText("You are not currently checked into a gym");
	    	        		checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.red_x_mark));
	    	        		checkedIn = false;
	    				}
	    			})
	    			.setNegativeButton("Oops!", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    				}
	    			}).show();
	    	return;

		}
		

		new CheckoutAsyncTask().execute(mUser.getID());
	}
	
	private class GooglePlacesAsyncTast extends AsyncTask<String, Void, PlacesResponse> {

		protected void onPreExecute() {
			gyms.clear();
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Finding nearby gyms...");
		}
		
        protected PlacesResponse doInBackground(String... params) {
        	PlacesResponse response = CheckinCommunication.getNearbyGyms(params[0],
        			params[1], params[2], params[3], params[4]);
        	return response;
        }

        protected void onPostExecute(PlacesResponse response) {
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Sorry, but we couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Sorry, but Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (response.getGyms().isEmpty()){
        		showAddGymDialog();
//        		new GooglePlacesSearchAsyncTask().execute(getString(R.string.places_api_key), latitude+"",
//				longitude+"", DEFAULT_PLACES_RADIUS+"", "true");
        	} else {
        		gyms.addAll(response.getGyms());
        		gym = response.getGyms().get(0);
				new CheckinAsyncTask().execute(mUser.getID());
        	}
        }
	}
	
	private class GooglePlacesSearchAsyncTask extends AsyncTask<String, Void, PlacesResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Finding nearby rec centers...");
		}
		
        protected PlacesResponse doInBackground(String... params) {
        	PlacesResponse response = CheckinCommunication.getNearbyRecCenter(params[0],
        			params[1], params[2], params[3], params[4]);
        	return response;
        }

        protected void onPostExecute(PlacesResponse response) {
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Sorry, but we couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, "Sorry, but Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (response.getGyms().isEmpty()){
        		Toast toast = Toast.makeText(parent, "Sorry, but there appears to be no gym or rec centers near you", Toast.LENGTH_LONG);
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
				new CheckinAsyncTask().execute(mUser.getID());
        	}
        }
	}
	
	
	private class GooglePlacesAddAsyncTask extends AsyncTask<String, Void, ValidateGymResponse> {

		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Adding your gym...");
		}
		

        protected ValidateGymResponse doInBackground(String... params) {
        	ValidateGymResponse response = CheckinCommunication.addGym(params[0],
        			params[1], params[2], params[3]);
        	return response;
        }

        protected void onPostExecute(ValidateGymResponse response) {
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(parent, "Sorry, but we couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent, response.getMessage(), Toast.LENGTH_LONG);

        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else {
        		Toast toast = Toast.makeText(parent, "Successfully added your gym... like a boss", Toast.LENGTH_LONG);
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
    private class CheckinAsyncTask extends AsyncTask<Integer, Void, StatusResponse> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Checking you in to your games...");
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = CheckinCommunication.checkin(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	if (response.wasSuccessful()) {
        		Toast toast = Toast.makeText(parent, "Check-in successful!", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        		mHandler.sendEmptyMessage(MESSAGE_START_TIMER);
				checkinLocationTV.setText("Checked in at " + gym);
				checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.green_check_mark));
				checkedIn = true;
        	} else {
        		String error = response.getError();
        		if (error == null)
        			error = "Sorry server could not be reached at the moment";
        		Toast toast = Toast.makeText(parent, error, Toast.LENGTH_LONG);
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
        		mHandler.sendEmptyMessage(MESSAGE_STOP_TIMER);
        		checkinLocationTV.setText("You are not currently checked into a gym");
        		checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.red_x_mark));
        		checkedIn = false;
        	} else {
        		Toast toast = Toast.makeText(parent, "Check-out failed!", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        	
        }
    }
}

