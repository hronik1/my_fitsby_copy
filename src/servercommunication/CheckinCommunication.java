package servercommunication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.fitsbypact.R;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import responses.AddPlaceResponse;
import responses.PlacesResponse;
import responses.PrivateLeagueResponse;
import responses.StatusResponse;
import responses.UserResponse;
import responses.ValidateGymResponse;

public class CheckinCommunication {

	private static final String TAG = "CheckinCommunication";
	
	public CheckinCommunication() {
		
	}
	
	/**
	 * sends the checkin request to the server
	 * @param id
	 * @return
	 */
	public static StatusResponse checkin(int id) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", id);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "check_in_request", stringEntity);
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		}
	}
	
	/**
	 * sends the checkout request to the server
	 * @param id
	 * @return
	 */
	public static StatusResponse checkout(int id) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", id);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "check_out_request", stringEntity);
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		}
	}
	
	public static PlacesResponse getNearbyGyms(String key, String latitude, String longitude,
			String radius, String sensorUsed) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {  
	        nameValuePairs.add(new BasicNameValuePair("key", key));
	        nameValuePairs.add(new BasicNameValuePair("location", latitude + "," + longitude));
	        nameValuePairs.add(new BasicNameValuePair("radius", radius ));
	        nameValuePairs.add(new BasicNameValuePair("sensor", sensorUsed));
	        nameValuePairs.add(new BasicNameValuePair("types", "gym"));
			ServerResponse serverResponse = myHttpClient.createGetRequest(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json", nameValuePairs);
			return PlacesResponse.jsonToPlacesResponse(MyHttpClient.parseResponse(serverResponse));
        } catch (Exception e) {
        	Log.d(TAG, e.toString());
        	return null;
        }
	}

	public static PlacesResponse getNearbyRecCenter(String key, String latitude, String longitude,
			String radius, String sensorUsed) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {  
	        nameValuePairs.add(new BasicNameValuePair("key", key));
	        nameValuePairs.add(new BasicNameValuePair("location", latitude + "," + longitude));
	        nameValuePairs.add(new BasicNameValuePair("radius", radius));
	        nameValuePairs.add(new BasicNameValuePair("sensor", sensorUsed));
	        nameValuePairs.add(new BasicNameValuePair("query", "recreation"));
			ServerResponse serverResponse = myHttpClient.createGetRequest(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json", nameValuePairs);
			return PlacesResponse.jsonToPlacesResponse(MyHttpClient.parseResponse(serverResponse));
        } catch (Exception e) {
        	Log.d(TAG, e.toString());
        	return null;
        }
	}
	
	/**
	 * attempt to add a gym to the google places api
	 * @param key
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param sensorUsed
	 * @param gymName
	 * @return
	 */
	public static ValidateGymResponse addGym(String userId, String latitude, String longitude,
			String gymName) {
		
		MyHttpClient myHttpClient = new MyHttpClient();
        try {  
        	JSONObject json = new JSONObject();
        	json.put("geo_lat", latitude);
        	json.put("geo_long", longitude);
    		json.put("gym_name", gymName);
    		json.put("user_id", userId);
    		Log.d(TAG, json.toString());
            StringEntity stringEntity = new StringEntity(json.toString()); 
			ServerResponse serverResponse = myHttpClient.createPostRequest(
					MyHttpClient.SERVER_URL + "validate_gym", stringEntity);
			return ValidateGymResponse.jsonToValidateGymResponse(MyHttpClient.parseResponse(serverResponse));
        } catch (Exception e) {
        	Log.d(TAG, e.toString());
        	return null;
        }
        
	}
}
