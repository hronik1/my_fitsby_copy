package com.example.fitsbypact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;

public class CheckInActivity extends Activity {

	private final static int MESSAGE_START_TIMER = 0;
	private final static int MESSAGE_UPDATE_TIMER = 1;
	private final static int MESSAGE_STOP_TIMER = 2;
	private final static int UPDATE_TIME_MILLIS = 1000; //one second
	private final static int DEFAULT_PLACES_RADIUS = 100;
	
	private final static String TAG = "CheckInActivity";
	
	private NavigationBar navigation;
	private int userID;
	
	private Button checkinButton;
	private Button checkoutButton;
	
	private DatabaseHandler mdbHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private List<LeagueMember> mLeagueMemberList;
	
	private ApplicationUser mApplicationUser;
	private User mUser;
	
	private Handler mHandler;
	private int timeSeconds;
	private int timeMinutes;

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
        
        initializeHandler();
        initializeTime();        
        initializeNavigationBar();
        initializeButtons();
        
        mdbHandler = DatabaseHandler.getInstance(getApplicationContext());
        mLeagueMemberTableHandler = mdbHandler.getLeagueMemberTableHandler();
        mLeagueMemberList = mLeagueMemberTableHandler.getAllLeagueMembersByUserId(mUser.getID());
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
	    FlurryAgent.onStartSession(this, "SPXCFGBJFSSSYQM6YD2X");
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
	                //TODO update TextView
	                break;
	                
	            case MESSAGE_STOP_TIMER:
	                mHandler.removeMessages(MESSAGE_UPDATE_TIMER);
	                break;

	            default:
	                break;
	            }
	        }
		};
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
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean gpsEnabled = service
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!gpsEnabled) {
			showAlertDialog(); 
		} else {
			LocationListener listener = new LocationListener() {
				public void onLocationChanged(Location location) {}

				public void onStatusChanged(String provider, int status, Bundle extras) {}

				public void onProviderEnabled(String provider) {}

				public void onProviderDisabled(String provider) {}
			};
			
			service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener); 
			Location location = service.getLastKnownLocation(LOCATION_SERVICE);
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			String uri = buildPlacesUri(latitude, longitude, DEFAULT_PLACES_RADIUS, true);
			JSONObject jsonObject = jsonFromStringUri(uri);
			boolean success = checkinSuccesful(jsonObject);
			if (success) {
				mHandler.sendEmptyMessage(MESSAGE_START_TIMER);
				Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
				//TODO increase checkins
			} else {
				Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
			}
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
    	builder.setMessage("GPS location is required to make sure you are at a gym(not like you would lie)./nWould you like to turn it on?")
    			.setCancelable(false)
    			.setPositiveButton("Yup!", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					try {
    					  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    					  startActivity(intent);
    					} catch (Exception e) {
    						//TODO make a more better error message
    						Toast.makeText(CheckInActivity.this, "Sorry could not change your GPS at the moment", Toast.LENGTH_LONG).show();
    					}
    				}
    			})
    			.setNegativeButton("I'll pass", new DialogInterface.OnClickListener() {
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
		mHandler.sendEmptyMessage(MESSAGE_STOP_TIMER);
		//TODO increase checkouts by 1
//		for(LeagueMember member: mLeagueMemberList) {
//			if(member.getCheckins() == member.getCheckouts() + 1) {
//				member.setCheckouts(member.getCheckins());
//				mLeagueMemberTableHandler.updateLeagueMember(member);
//			}
//		}
		
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
			HttpPost httpPost = new HttpPost(uriString);

			HttpResponse httpResponse = httpClient.execute(httpPost);
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
}
	

