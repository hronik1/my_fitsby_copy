package responses;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class PlacesResponse {
	private final static String TAG = "PlacesResponse";
	
	private StatusResponse mStatusResponse;
	private Vector<String> gyms;
	
	public PlacesResponse() {
		
	}
	
	public PlacesResponse(String status, Vector<String> gyms) {
		mStatusResponse = new StatusResponse(status);
		this.gyms = gyms;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public Vector<String> getGyms() {
		return gyms;
	}
	
	public void setError(String error) {
		if (mStatusResponse == null)
			mStatusResponse = new StatusResponse("fail");
		mStatusResponse.setError(error);
	}
	
	public String getError() {
		if (mStatusResponse != null)
			return mStatusResponse.getError();
		else 
			return "";
	}
	
	public static PlacesResponse jsonToPlacesResponse(JSONObject json) {
		Vector<String> gymsResponse = new Vector<String>();
		String status;
		if (json == null)
			status = "fail";
		else 
			status = "okay";
		try {
			JSONArray gymsJson = json.getJSONArray("results");
			int length = gymsJson.length();
			for (int i = 0; i < length; i++) {
				JSONObject gymJson = gymsJson.getJSONObject(i);
				gymsResponse.add(gymJson.getString("name"));
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return new PlacesResponse(status, gymsResponse);
	}
}
