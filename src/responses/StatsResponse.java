package responses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import dbtables.Stats;


public class StatsResponse {
	private final static String TAG = "StatsResponse";
			
	private StatusResponse mStatusResponse;
	private Stats mStats;
	
	public StatsResponse(String status, Stats stats) {
		mStatusResponse = new StatusResponse(status);
		this.mStats = stats;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public Stats getStats() {
		return mStats;
	}
	
	public static StatsResponse jsonToStatsResponse(JSONObject json) {
		try {
			String status = json.getString("status");
			String gamesWon = json.getString("games_won");
			String gamesPlayed = json.getString("games_played");
			String moneyEarned = json.getString("money_earned");
			Stats stats = new Stats(gamesPlayed, gamesWon, moneyEarned);
			return new StatsResponse(status, stats);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new StatsResponse("fail", null);
		}
	}
}
