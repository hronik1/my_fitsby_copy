package com.fitsby;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.qualcommlabs.usercontext.Callback;
import com.qualcommlabs.usercontext.ContextCoreConnector;
import com.qualcommlabs.usercontext.ContextCoreConnectorFactory;
import com.qualcommlabs.usercontext.ContextCoreStatus;
import com.qualcommlabs.usercontext.ContextPlaceConnector;
import com.qualcommlabs.usercontext.ContextPlaceConnectorFactory;
import com.qualcommlabs.usercontext.PlaceEventListener;
import com.qualcommlabs.usercontext.protocol.GeoFenceCircle;
import com.qualcommlabs.usercontext.protocol.Place;
import com.qualcommlabs.usercontext.protocol.PlaceEvent;

public class AddPlacesActivity extends KiipFragmentActivity
	implements OnMapLongClickListener, PlaceEventListener {

	private final String TAG = getClass().getName();
	private final static int DEFAULT_GEOFENCE_RADIUS = 100;
	
	private GoogleMap mMap;
	
	private ContextCoreConnector contextCoreConnector;
	private ContextPlaceConnector contextPlaceConnector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_places);
		
		Log.i(TAG, "onCreate");
		
		initializeContextCoreConnector();
		initializeContextPlaceConnector();
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		contextCoreConnector.setCurrentActivity(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		contextCoreConnector.setCurrentActivity(null);
	}
	
	private void initializeContextCoreConnector() {
		contextCoreConnector = ContextCoreConnectorFactory.get(this);
		
		if (contextCoreConnector.isPermissionEnabled()) { 
			Log.d(TAG, "permission already enabled");

		}
		else {
			Log.d(TAG, "permission not enabled");
			ContextCoreStatus status = contextCoreConnector.getStatus();
			contextCoreConnector.enable(this, new Callback<Void>() { 
				@Override
				public void success(Void responseObject) {
				// do something when successfully enabled
					Log.d(TAG, "successfully enabled");
				}
				@Override
				public void failure(int statusCode, String errorMessage) {
					// failed with statusCode
					Log.d(TAG, "failed enabled");
				}
			});
		}
	}
	
	private void initializeContextPlaceConnector() {
		contextPlaceConnector = ContextPlaceConnectorFactory.get(this);
		contextPlaceConnector.addPlaceEventListener(this);
		if (contextPlaceConnector.isPermissionEnabled()) {
			Log.d(TAG, "place connector enabled");
		} else {
			Log.d(TAG, "place connector not enabled");
		}
	}
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
    	//TODO perform additional setup if need be
    	mMap.setOnMapLongClickListener(this);
    }
	
    private void showAddGymDialog(final LatLng point) {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setMessage("Add Gym?");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setHint("Gym Name");
    	alert.setView(input);

    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			String gymName = input.getText().toString();
    			if (!"".equals(gymName)) {
    				addGym(point, gymName);
    			}
    			else {
            		Toast toast = Toast.makeText(getApplicationContext(), "Name cannot be empty.", Toast.LENGTH_LONG);
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
    
    private void addGym(LatLng point, String gymName) {
    	if (contextPlaceConnector.isPermissionEnabled()) {
    		GeoFenceCircle circle = new GeoFenceCircle();
    		circle.setLatitude(point.latitude);
    		circle.setLongitude(point.longitude);
    		circle.setRadius(DEFAULT_GEOFENCE_RADIUS);
    		
    		Place place = new Place();
    		place.setPlaceName(gymName);
    		place.setGeoFence(circle);
    		
    		contextPlaceConnector.createPlace(place, new Callback<Place>() { 
    			
    			@Override
    			public void success(Place place) {
    				// do somethind with place
    				Log.d(TAG, "successfully added place");
    			}
    			
    			@Override
    			public void failure(int statusCode, String errorMessage) {
    				// failed with statusCode
    				Log.d(TAG, "failed at adding place");
    			}
    		});
    		
    	} else {
    		Toast.makeText(this, "Please enable gimbal to add gym", Toast.LENGTH_LONG).show();
    	}
    }
    
	@Override
	public void onMapLongClick(LatLng point) {
		showAddGymDialog(point);
	}

	@Override
	public void placeEvent(PlaceEvent placeEvent) {
		String eventType = placeEvent.getEventType();
		if (PlaceEvent.PLACE_EVENT_TYPE_AT.equals(eventType)) {
			Log.d(TAG, "entered " + placeEvent.getName());
			//TODO publish notification
		} else if (PlaceEvent.PLACE_EVENT_TYPE_LEFT.equals(eventType)) {
			Log.d(TAG, "leaving " + placeEvent.getName());
			//TODO publish notification
		}
	} 
    
}
