package responses;

import org.json.JSONObject;

import android.util.Log;

public class LeagueCreateResponse {
	
	private final static String TAG = "LeagueCreateResponse";
	
	private StatusResponse mStatusResponse;
	private String leagueId;
	
	public LeagueCreateResponse() {
		
	}
	
	public LeagueCreateResponse(String status, String leagueId) {
		mStatusResponse = new StatusResponse(status);
		this.leagueId = leagueId;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public String getLeagueId() {
		return leagueId;
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
	public static LeagueCreateResponse jsonToLeagueCreateResponse(JSONObject json) {

		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			String gameId = json.getString("game_id");
			return new LeagueCreateResponse(status, gameId);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new LeagueCreateResponse(e.toString(), null);
		}
	}
}
