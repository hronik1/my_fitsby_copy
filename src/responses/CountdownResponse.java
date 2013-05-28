package responses;

import org.json.JSONObject;

import android.util.Log;

public class CountdownResponse {

	private static final String TAG = "CountdownResponse";
	
	private StatusResponse mStatusResponse;
	private String daysLeft;
	
	public CountdownResponse() {
		
	}
	
	public CountdownResponse(String status, String daysLeft) {
		mStatusResponse = new StatusResponse(status);
		this.daysLeft = daysLeft;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public String getDaysLeft() {
		return daysLeft;
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
	
	public static CountdownResponse jsonToCountdownResponse(JSONObject json) {
		Log.d(TAG, json.toString());
		try {
			String status = json.getString("status");
			String daysRemaining = json.getString("string");
			return new CountdownResponse(status, daysRemaining);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new CountdownResponse(e.toString(), null);
		}
	}
}
