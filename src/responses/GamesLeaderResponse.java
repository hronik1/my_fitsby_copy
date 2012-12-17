package responses;

import gravatar.Gravatar;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import servercommunication.MyHttpClient;

import android.util.Log;
import android.graphics.Bitmap;

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
		HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
		try {
			Log.d(TAG, json.toString() + "");
			String success = json.getString("status");
			Vector<Leader> leaders = new Vector<Leader>();
			JSONArray jsonLeaders = json.getJSONArray("leaderboard");
			int length = jsonLeaders.length();
			Bitmap bitmap;
			for (int i = 0; i < length; i++) {
				JSONObject leader = jsonLeaders.getJSONObject(i);
				String firstName  = leader.getString("first_name");
				String lastName = leader.getString("last_name");
				int checkins = Integer.parseInt(leader.getString("successful_checks"));
				String id = (i+1) + "";
				String email = leader.getString("email");
				//TODO change to actually parse email
				if (!imageMap.containsKey(email))  {
					Log.d(TAG, "getting image");
					String src = Gravatar.getGravatar(email);
					bitmap = MyHttpClient.getBitmapFromURL(src);
					imageMap.put(email, bitmap);
				} else {
					Log.d(TAG, "image exists");
					bitmap = imageMap.get(email);
				}
				
				leaders.add(new Leader(firstName, lastName, checkins, id, bitmap));
			}
			return new GamesLeaderResponse(success, leaders);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new GamesLeaderResponse(e.toString(), null);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new GamesLeaderResponse(e.toString(), null);
		}
	}
}
