package responses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class UsersGamesResponse {
	private final static String TAG = "UsersGameResponse";
	
	private StatusResponse mStatusResponse;
	private List<String> games;
	
	public UsersGamesResponse(String status, List<String> games) {
		mStatusResponse = new StatusResponse(status);
		this.games = games;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public List<String> getGames() {
		return games;
	}
	
	public static UsersGamesResponse jsonToGamesResponse(JSONObject json) {
		try {
			String status = json.getString("status");
			List<String> games = new ArrayList<String>();
			if (status.equals("okay")) {
				JSONArray jsonArray = json.getJSONArray("games_user_is_in");
				int length = jsonArray.length();
				for (int i = 0; i < length; i++) {
					games.add(jsonArray.get(i).toString());
				}
			}
			return new UsersGamesResponse(status, games);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new UsersGamesResponse(e.toString(), null);
		}
	}
}
