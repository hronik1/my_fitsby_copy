package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StatusResponse {
	private final static String TAG = "StatusResponse";
	
	private final static String RESPONSE_SUCCESS = "okay";
	private final static String RESPONSE_SUCCESS_GOOGLE_PLACES = "OK";
	private final static String RESPONSE_FAIL = "fail";
	
	private String status;
	private String error;
	
	public StatusResponse() {
		
	}
	
	public StatusResponse(String status) {
		this.status = status;
	}
	
	public boolean wasSuccessful() {
		if (status == null)
			return false;
		else
			return (status.equals(RESPONSE_SUCCESS) || status.equals(RESPONSE_SUCCESS_GOOGLE_PLACES));
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return this.error;
	}
	
	public static StatusResponse jsonToStatusResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			StatusResponse statusResponse = new StatusResponse(json.get("status").toString());
			try {
				statusResponse.setError(json.getString("error"));
			} catch (Exception e) {
				
			}	
			return statusResponse;
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
}

