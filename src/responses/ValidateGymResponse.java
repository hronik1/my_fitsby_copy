package responses;

import org.json.JSONObject;

import android.util.Log;

public class ValidateGymResponse {

	private final static String TAG = "ValidateGymResponse";
	private StatusResponse mStatusResponse;
	private String message;
	
	public ValidateGymResponse() {
		
	}
	
	public ValidateGymResponse(String status, String message) {
		mStatusResponse = new StatusResponse(status);
		this.message = message;
	}
	
	public boolean wasSuccessful() { 
		return mStatusResponse.wasSuccessful();
	}
	
	public String getMessage() {
		return message;
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
	public static ValidateGymResponse jsonToValidateGymResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			String message = json.getString("string");
			return new ValidateGymResponse(status, message);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new ValidateGymResponse(e.toString(), null);
		}
	}
}
