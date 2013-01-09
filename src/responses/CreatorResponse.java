package responses;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CreatorResponse {
	
	private final static String TAG = "CreatorResponse";
	
	private StatusResponse mStatusResponse;
	private String creatorFirstName;
	
	public CreatorResponse() {
		
	}
	
	public CreatorResponse(String status, String creatorFirstName) {
		mStatusResponse = new StatusResponse(status);
		this.creatorFirstName = creatorFirstName;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public String getCreatorFirstName() {
		return creatorFirstName;
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
	
	public static CreatorResponse jsonToCreatorResponse(JSONObject json) {
		Log.d(TAG, json.toString());
		try {
			String status = json.getString("status");
			String firstName = json.getString("creator_first_name");
			return new CreatorResponse(status, firstName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new CreatorResponse(e.toString(), null);
		}
	}
}

