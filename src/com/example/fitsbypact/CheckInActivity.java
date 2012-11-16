package com.example.fitsbypact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import responses.AddPlaceResponse;
import responses.PlacesResponse;
import responses.StatusResponse;
import servercommunication.CheckinCommunication;
import servercommunication.UserCommunication;

import android.net.Uri;
import com.example.fitsbypact.applicationsubclass.ApplicationUser;

import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbtables.LeagueMember;
import dbtables.User;
import widgets.NavigationBar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.telephony.SmsManager;
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

public class CheckInActivity extends Activity {

	private final static int MESSAGE_START_TIMER = 0;
	private final static int MESSAGE_UPDATE_TIMER = 1;
	private final static int MESSAGE_STOP_TIMER = 2;
	private final static int UPDATE_TIME_MILLIS = 1000; //one second
	private final static int DEFAULT_PLACES_RADIUS = 50; //200 meters
	
	private final static String TAG = "CheckInActivity";
	
	private NavigationBar navigation;
	private int userID;
	private boolean checkedIn;
	
	private TextView checkinLocationTV;
	private TextView minutesTV;
	private TextView secondsTV;
	
	private ImageView checkedInIv;
	private Button checkinButton;
	private Button checkoutButton;
	
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private List<LeagueMember> mLeagueMemberList;
	
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
	 * called when Activity is created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Log.i(TAG, "onCreate");
        
        mApplicationUser = ((ApplicationUser)getApplicationContext());
        mUser = mApplicationUser.getUser();
        
        initializeTextViews();
        initializeImageViews();
        initializeHandler();
        initializeTime();        
        initializeNavigationBar();
        initializeButtons();
        
        
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mLeagueMemberList = mLeagueMemberTableHandler.getAllLeagueMembersByUserId(mUser.getID());
        
        gyms = new Vector<String>();
        checkedIn = false;
    }

    /**
     * called when options menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_check_in, menu);
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
	    FlurryAgent.logEvent("Check In");
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
	private void initializeImageViews() {
		checkedInIv = (ImageView)findViewById(R.id.check_in_iv);
		
	}
	
	/**
	 * initializes the TextViews
	 */
	private void initializeTextViews() {
		checkinLocationTV = (TextView)findViewById(R.id.verified_gym);
		secondsTV = (TextView)findViewById(R.id.seconds);
		minutesTV = (TextView)findViewById(R.id.minutes);
	}
	
	/**
	 * initialized NavigationBar for use
	 */
	public void initializeNavigationBar() {
		navigation = (NavigationBar)findViewById(R.id.check_in_navigation_bar);
		navigation.setParentActivity(this);
		navigation.turnOffTV("checkin");
	}
	
	/**
	 * initializes buttons on this activity
	 */
	public void initializeButtons() {
		checkinButton = (Button)findViewById(R.id.check_in_button_check_in);
		checkinButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkin();
			}
		});
		
		checkoutButton = (Button)findViewById(R.id.check_in_button_check_out);
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
			Toast toast = Toast.makeText(getApplicationContext(), "Hey buddy, don't go getting greedy ;) you're already checked in", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean gpsEnabled = service
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!gpsEnabled) {
			showAlertDialog(); 
		} else {
			LocationListener listener = new LocationListener() {
				public void onLocationChanged(Location location) {
					//Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT).show();
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
//			Toast.makeText(getApplicationContext(), bestResult.toString(), Toast.LENGTH_LONG).show();
			service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener); 
//			Location location = service.getLastKnownLocation(LOCATION_SERVICE);
			latitude = bestResult.getLatitude();
			longitude = bestResult.getLongitude();
//			String uri = buildPlacesUri(latitude, longitude, DEFAULT_PLACES_RADIUS, true);
			new GooglePlacesAsyncTast().execute(getString(R.string.places_api_key), latitude+"",
					longitude+"", DEFAULT_PLACES_RADIUS+"", "true");
//			JSONObject jsonObject = jsonFromStringUri(uri);
//			boolean success = checkinSuccesful(jsonObject);
//			if (success) {
//				mHandler.sendEmptyMessage(MESSAGE_START_TIMER);
//				Toast toast = Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//				String gym = parseGym(jsonObject);
//				new CheckinAsyncTask().execute(mUser.getID());
//				checkinLocationTV.setText("Checked in at " + gym);
//				checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.green_check_mark));
//				
//				//TODO increase checkins
//			} else {
//				Toast toast = Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//			}
			
//			for(LeagueMember member: mLeagueMemberList) {
//				if(member.getCheckins() == member.getCheckouts()) {
//					member.setCheckins(member.getCheckins() + 1);
//					mLeagueMemberTableHandler.updateLeagueMember(member);
//				}
//			}
		}
	}
	
    /**
     * shows AlertDialog
     */
    public void showAlertDialog() {
    	Log.i(TAG, "onCreateDialog");

    	//TODO clean this up
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("GPS location is required to make sure you are at a gym.\nWould you like to turn it on?")
    			.setCancelable(false)
    			.setPositiveButton("Yup!", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					try {
    					  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    					  startActivity(intent);
    					} catch (Exception e) {
    						//TODO make a more better error message
    						Toast toast = Toast.makeText(CheckInActivity.this, "Sorry, but it seems we can't track your location at this moment", Toast.LENGTH_LONG);
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
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
	  	final EditText input = new EditText(CheckInActivity.this);
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
    						Toast toast = Toast.makeText(CheckInActivity.this, e.toString(), Toast.LENGTH_LONG);
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
			Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but you can't check out because you never checked in", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		} 
		if (timeMinutes < 45) {

		  	AlertDialog.Builder builder = new AlertDialog.Builder(CheckInActivity.this);
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
	
	/**
	 * Builds a properly formatted to send the places api
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param sensorUsed
	 * @return
	 */
	private String buildPlacesUri(double latitude, double longitude, int radius, boolean sensorUsed) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("https").authority("google.com").path("/maps/api/place/search/json")
		    .appendQueryParameter("key", getString(R.string.places_api_key))
		    .appendQueryParameter("location", latitude + "," + longitude)
		    .appendQueryParameter("radius", radius + "")
		    .appendQueryParameter("sensor", Boolean.toString(sensorUsed))
		    .appendQueryParameter("types", "gym");
		Uri uri = builder.build();
		String uriString = uri.toString();
		Log.d(TAG, uriString);
		return uriString;
	}
	
	/**
	 * 
	 * @param uriString
	 * @return
	 */
	private JSONObject jsonFromStringUri(String uriString) {
		InputStream inputStream = null;
		String json = "";
		JSONObject jsonObject = null;

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uriString);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();          

		} catch (UnsupportedEncodingException e) {
			//TODO, handle gracefully
		} catch (ClientProtocolException e) {
			//TODO, handle gracefully
		} catch (IOException e) {
			//TODO, handle gracefully
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "n");
			}
			inputStream.close();
			json = stringBuilder.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jsonObject;

	}
	
	private boolean checkinSuccesful(JSONObject jsonObject) {
		String status = "";
		try {
			status = jsonObject.getString("status");
			Log.d(TAG, "status");
			
		} catch (JSONException e) {
			Log.d(TAG, "json parsing failed");
			return false;
		}
		
		if ("OK".equals(status))
			return true;
		else
			return false;
	}
	
	private String parseGym(JSONObject jsonObject) {
		JSONArray jsonArray;
		try {
			jsonArray = jsonObject.getJSONArray("results");
			return jsonArray.getJSONObject(0).getString("name");
		} catch (JSONException e) {
			//TODO gracefully handle error checkin
			return "";
		}
	}
	
	private class GooglePlacesAsyncTast extends AsyncTask<String, Void, PlacesResponse> {

		protected void onPreExecute() {
			gyms.clear();
            mProgressDialog = ProgressDialog.show(CheckInActivity.this, "",
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
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but we couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
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
            mProgressDialog = ProgressDialog.show(CheckInActivity.this, "",
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
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but we couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but Google Places API appears to be down at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else if (response.getGyms().isEmpty()){
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but there appears to be no gym or rec centers near you", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else {
        		gym = response.getGyms().get(0);
        		for (String temp: response.getGyms()) {
            		Toast toast = Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        		}
        		gyms.addAll(response.getGyms());
				new CheckinAsyncTask().execute(mUser.getID());
        	}
        }
	}
	
	private class GooglePlacesAddAsyncTask extends AsyncTask<String, Void, AddPlaceResponse> {
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(CheckInActivity.this, "",
                    "Adding your gym...");
		}
		
        protected AddPlaceResponse doInBackground(String... params) {
        	AddPlaceResponse response = CheckinCommunication.addGym(params[0],
        			params[1], params[2], params[3], params[4], params[5]);
        	return response;
        }

        protected void onPostExecute(AddPlaceResponse response) {
        	mProgressDialog.dismiss();
        	if (response == null) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, but we couldn't find an internet connection", Toast.LENGTH_LONG); 
    			toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	} else {
        		Toast toast = Toast.makeText(getApplicationContext(), "Successfully added your gym... like a boss", Toast.LENGTH_LONG);
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
            mProgressDialog = ProgressDialog.show(CheckInActivity.this, "",
                    "Checking you in to your games...");
		}
		
        protected StatusResponse doInBackground(Integer... params) {
        	StatusResponse response = CheckinCommunication.checkin(params[0]);
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	if (response.wasSuccessful()) {
        		Toast toast = Toast.makeText(getApplicationContext(), "Check-in successful!", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        		mHandler.sendEmptyMessage(MESSAGE_START_TIMER);
				checkinLocationTV.setText("Checked in at " + gym);
				checkedInIv.setImageDrawable(getResources().getDrawable(R.drawable.green_check_mark));
				checkedIn = true;
        	} else {
        		Toast toast = Toast.makeText(getApplicationContext(), "Check-in failed!", Toast.LENGTH_LONG);
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
            mProgressDialog = ProgressDialog.show(CheckInActivity.this, "",
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
        		Toast toast = Toast.makeText(getApplicationContext(), "Check-out failed!", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
    			toast.show();
        	}
        	
        }
    }
}
	

