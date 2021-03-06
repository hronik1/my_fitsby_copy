package responses;

import gravatar.Gravatar;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import servercommunication.MyHttpClient;

import android.graphics.Bitmap;
import android.util.Log;

import dbtables.League;

public class PublicLeaguesResponse {
	private final static String TAG = "PublicLeaguesResponse";
	
	private StatusResponse mStatusResponse;
	private Vector<League> leagues;
	
	public PublicLeaguesResponse() {
		
	}
	
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
	public static PublicLeaguesResponse jsonToPublicLeagueResponse(JSONObject json) {
		try {
			HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
			String success = json.get("status").toString();
			Vector<League> leagues = new Vector<League>();
			JSONArray publicLeagues = json.getJSONArray("public_games");
			int length = publicLeagues.length();
			Bitmap bitmap;
			for (int i = 0; i < length; i++) {
				JSONObject jsonLeague = publicLeagues.getJSONObject(i);
				int id = Integer.parseInt(jsonLeague.getString("id"));
				int players = Integer.parseInt(jsonLeague.getString("players"));
				int duration = Integer.parseInt(jsonLeague.getString("duration"));
				int wager = Integer.parseInt(jsonLeague.getString("wager"));
				int stakes = Integer.parseInt(jsonLeague.getString("stakes"));
				String email;
				int goal;
				try {
					email = jsonLeague.getString("email");
				} catch(Exception e) {
					email = "";
				}
				try {
					goal = jsonLeague.getInt("goal_days");
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					goal = duration;
				}
				if (!imageMap.containsKey(email))  {
					Log.d(TAG, "getting image");
					String src = Gravatar.getGravatar(email);
					bitmap = MyHttpClient.getBitmapFromURL(src);
					imageMap.put(email, bitmap);
				} else {
					Log.d(TAG, "image exists");
					bitmap = imageMap.get(email);
				}
				leagues.add(new League(id, wager, players, duration, stakes, bitmap, goal));
			}
			return new PublicLeaguesResponse(success, leagues);
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return new PublicLeaguesResponse(e.toString(), null);
		}
	}
}
