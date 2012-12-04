package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ProgressResponse {

	private static final String TAG = "StakesResponse";
	
	private StatusResponse mStatusResponse;
	private double progress;
	
	public ProgressResponse(String status, double progress) {
		mStatusResponse = new StatusResponse(status);
		this.progress = progress;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}

	public static ProgressResponse jsonToProgressResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			double progress = json.getDouble("percentage");
			return new ProgressResponse(status, progress);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new ProgressResponse(e.toString(), 0.0);
		}
	}
}
