package responses;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import dbtables.League;

public class PrivateLeagueResponse {
	
	private static final String TAG = "PrivateLeagueResponse";
	
	private StatusResponse mStatusResponse;
	private League mLeague;
	
	public PrivateLeagueResponse(String status, League league) {
		mStatusResponse = new StatusResponse(status);
		mLeague = league;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public League getLeague() {
		return mLeague;
	}
	
	public static PrivateLeagueResponse jsonToPrivateLeagueResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			String success = json.get("status").toString();
			int id = Integer.parseInt(json.getString("id"));
			int players = Integer.parseInt(json.getString("players"));
			int duration = Integer.parseInt(json.getString("duration"));
			int wager = Integer.parseInt(json.getString("wager"));
			int stakes = Integer.parseInt(json.getString("stakes"));
			String isPrivate = json.getString("isPrivate");
			
			League league = new League(id, wager, players, duration, stakes);
			if (isPrivate.equals("true"))
				league.setPrivate(1);

			return new PrivateLeagueResponse(success, league);
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return new PrivateLeagueResponse(e.toString(), null);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new PrivateLeagueResponse(e.toString(), null);
		}
	}
}

