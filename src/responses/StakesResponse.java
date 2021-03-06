package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StakesResponse {
	private static final String TAG = "StakesResponse";
	
	private StatusResponse mStatusResponse;
	private String stakes;
	
	public StakesResponse() {
		
	}
	
	public StakesResponse(String status, String stakes) {
		mStatusResponse = new StatusResponse(status);
		this.stakes = stakes;
	}
	
	public String getStakes() {
		return stakes;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
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
	
	public static StakesResponse jsonToStakesResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			String stakes = json.getString("stakes");
			return new StakesResponse(status, stakes);
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return new StakesResponse(e.toString(), null);
		}
	}
	
	
 }
