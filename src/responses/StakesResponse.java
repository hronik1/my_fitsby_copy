package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StakesResponse {
	private static final String TAG = "StakesResponse";
	
	private StatusResponse mStatusResponse;
	private String stakes;
	
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

	public static StakesResponse jsonToStakesResponse(JSONObject json) {
		try {
			String status = json.getString("status");
			String stakes = json.getString("stakes");
			return new StakesResponse(status, stakes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new StakesResponse(e.toString(), null);
		}
	}
	
	
 }
