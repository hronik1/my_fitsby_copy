package responses;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import dbtables.League;

public class PublicLeaguesResponse {
	private final static String TAG = "PublicLeaguesResponse";
	
	private StatusResponse mStatusResponse;
	private Vector<League> leagues;
	
	public PublicLeaguesResponse(String success, Vector<League> leagues) {
		mStatusResponse = new StatusResponse(success);
		this.leagues = leagues;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public Vector<League> getLeagues() {
		return leagues;
	}
	
	public static PublicLeaguesResponse jsonToPublicLeagueResponse(JSONObject json) {
		try {
			String success = json.get("status").toString();
			Vector<League> leagues = new Vector<League>();
			JSONArray publicLeagues = json.getJSONArray("public_games");
			int length = publicLeagues.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonLeague = publicLeagues.getJSONObject(i);
				int id = Integer.parseInt(jsonLeague.getString("id"));
				int players = Integer.parseInt(jsonLeague.getString("players"));
				int duration = Integer.parseInt(jsonLeague.getString("duration"));
				int wager = Integer.parseInt(jsonLeague.getString("wager"));
				int stakes = Integer.parseInt(jsonLeague.getString("stakes"));
				leagues.add(new League(id, wager, players, duration, stakes));
			}
			return new PublicLeaguesResponse(success, leagues);
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return new PublicLeaguesResponse(e.toString(), null);
		}
	}
}
