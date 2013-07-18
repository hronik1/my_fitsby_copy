package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FacebookSignupResponse {

	private final static String TAG = "FacebookSignupResponse";
	
	private StatusResponse mStatusResponse;
	private int userId = -1;
	
	public FacebookSignupResponse() {
		mStatusResponse = new StatusResponse();
	}
	
	public FacebookSignupResponse(String status, int id) {
		mStatusResponse = new StatusResponse(status);
		userId = id;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}

	public int getUserId() {
		return userId;
	}
	
	public static FacebookSignupResponse jsonToFacebookSignupResponse(JSONObject json) {
		if (json != null) {
			try {
				String status = json.getString("status");
				int id = json.getInt("user_id");
				return new FacebookSignupResponse(status, id);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				return new FacebookSignupResponse();
			}
		}
		else {
			Log.d(TAG, "json is null");
			return new FacebookSignupResponse();
		}
	}
}
