package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StatusResponse {
	private final static String TAG = "StatusResponse";
	
	private final static String RESPONSE_SUCCESS = "okay";
	private final static String RESPONSE_FAIL = "fail";
	
	private String status;
	
	public StatusResponse(String status) {
		this.status = status;
	}
	
	public boolean wasSuccessful() {
		return status.equals(RESPONSE_SUCCESS);
	}
	
	public static StatusResponse jsonToStatusResponse(JSONObject json) {
		try {
			return new StatusResponse(json.get("status").toString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
}

