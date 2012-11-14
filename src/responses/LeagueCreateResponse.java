package responses;

import org.json.JSONObject;

import android.util.Log;

public class LeagueCreateResponse {
	
	private final static String TAG = "LeagueCreateResponse";
	
	private StatusResponse mStatusResponse;
	private String leagueId;
	
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
	
	public static LeagueCreateResponse jsonToLeagueCreateResponse(JSONObject json) {

		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			String gameId = json.getString("game_id");
			return new LeagueCreateResponse(status, gameId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new LeagueCreateResponse(e.toString(), null);
		}
	}
}
