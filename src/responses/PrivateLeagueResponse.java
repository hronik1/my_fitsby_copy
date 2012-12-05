package responses;

import gravatar.Gravatar;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import servercommunication.MyHttpClient;

import android.graphics.Bitmap;
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
			int id = Integer.parseInt(json.getString("game_id"));
			int players = Integer.parseInt(json.getString("players"));
			int duration = Integer.parseInt(json.getString("duration"));
			int wager = Integer.parseInt(json.getString("wager"));
			int stakes = Integer.parseInt(json.getString("stakes"));
			String isPrivate = json.getString("is_private");
			String startDate = json.getString("start_date");
			String email = json.getString("email");
			Bitmap bitmap = MyHttpClient.getBitmapFromURL(Gravatar.getGravatar(email));
			League league = new League(id, wager, players, duration, stakes, bitmap);
			try {
				String endDate = json.getString("end_date");
				league.setEndDate(endDate);
			} catch (Exception e) {
				
			}
			league.setStartDate(startDate);
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

