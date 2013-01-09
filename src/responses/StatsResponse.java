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
	
	public StatsResponse() {
		
	}
	
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
	public static StatsResponse jsonToStatsResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			String gamesWon = json.getString("games_won");
			String gamesPlayed = json.getString("games_played");
			String moneyEarned = json.getString("money_earned");
			String joinedMonth = json.getString("joined_month");
			String joinedDay = json.getString("joined_day");
			String joinedYear = json.getString("joined_year");
			//TODO parse checkins and total gym time
			String totalCheckins = json.getString("successful_checks");
			String totalTime = json.getString("total_minutes_at_gym");
			Stats stats = new Stats(gamesPlayed, gamesWon, moneyEarned, joinedMonth, joinedDay, joinedYear);
			stats.setTotalCheckins(totalCheckins);
			stats.setTotalTime(totalTime);
			return new StatsResponse(status, stats);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new StatsResponse("fail", null);
		}
	}
}
