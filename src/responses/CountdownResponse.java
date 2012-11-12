package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CountdownResponse {

	private static final String TAG = "CountdownResponse";
	
	private StatusResponse mStatusResponse;
	private String daysLeft;
	
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
	
	public static CountdownResponse jsonToCountdownResponse(JSONObject json) {
		Log.d(TAG, json.toString());
		try {
			String status = json.getString("status");
			String daysRemaining = json.getString("days_remaining");
			return new CountdownResponse(status, daysRemaining);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new CountdownResponse(e.toString(), null);
		}
	}
}
