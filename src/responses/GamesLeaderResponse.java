package responses;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import dbtables.Leader;
import dbtables.League;

public class GamesLeaderResponse {
	private final static String TAG = "GamesLeaderResponse";
	private StatusResponse mStatusResponse;
	private Vector<Leader> leaders;
	
	public GamesLeaderResponse(String status, Vector<Leader> leaders) {
		mStatusResponse = new StatusResponse(status);
		this.leaders = leaders;
	}
	
	public boolean wasSuccessful() {
		return  mStatusResponse.wasSuccessful();
	}
	
	public Vector<Leader> getLeaders() {
		return leaders;
	}
	
	public static GamesLeaderResponse jsonToGamesLeaderResponse(JSONObject json) {
		try {
			String success = json.get("status").toString();
			Vector<Leader> leaders = new Vector<Leader>();
			JSONArray jsonLeaders = json.getJSONArray("leaderboard");
			int length = jsonLeaders.length();
			for (int i = 0; i < length; i++) {
				JSONObject leader = jsonLeaders.getJSONObject(i);
				String firstName  = leader.getString("first_name");
				String lastName = leader.getString("last_name");
				int checkins = Integer.parseInt(leader.getString("successful_checks"));
				String id = (i+1) + "";
				leaders.add(new Leader(firstName, lastName, checkins, id));
			}
			return new GamesLeaderResponse(success, leaders);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new GamesLeaderResponse(e.toString(), null);
		}
	}
}
