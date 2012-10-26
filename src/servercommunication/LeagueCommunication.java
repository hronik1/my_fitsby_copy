package servercommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import dbtables.League;
import loaders.PublicLeaguesCursorLoader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class LeagueCommunication {

	private final static String TAG = "LeagueCommunication";
	
	private static String getPublicLeaguesHelper() {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		JSONObject json = new JSONObject();
        try {
	        StringEntity stringEntity = new StringEntity(json.toString());  
			//nameValuePairs.add(new BasicNameValuePair("creator_id", creatorId + ""));
			//TODO add something to nameValuePairs
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, stringEntity);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (UnsupportedEncodingException e) {
			return e.toString();
		}
	}
	
	public static Cursor getPublicLeagues() {
		MatrixCursor cursor = new MatrixCursor(PublicLeaguesCursorLoader.FROM_ARGS);
		String jsonResponse = getPublicLeaguesHelper();
		if (jsonResponse == null)
			return cursor;
//		for(League league: publicLeaguesList) {
//			int leagueId = league.getId();
//			int numPlayers = mLeagueMemberTableHandler.getLeagueMembersCountByLeagueId(leagueId);
//			int wager = league.getWager();
//			cursor.addRow(new Object[] {leagueId, numPlayers, wager, league.getDuration(), numPlayers*wager});
//		}
		
		//TODO parse json into cursor
		return cursor;
	}
	
	/**
	 * sends the request to create a league
	 * @param creatorId
	 * @return
	 */
	public static String createLeague(int creatorId, int duration, boolean isPrivate, int wager) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("creator_id", creatorId);
			json.put("duration", duration);
			json.put("is_private", isPrivate);
			json.put("wager", wager);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, stringEntity);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (JSONException e) {
			return e.toString();
		} catch (UnsupportedEncodingException e) {
			return e.toString();
		}
	}
	
	/**
	 * sends the request to the server to join a game
	 * @param userId
	 * @param gameId
	 * @return
	 */
	public static String joinLeague(int userId, int gameId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", userId);
			json.put("message", gameId);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        //TODO add route
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, stringEntity);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (JSONException e) {
			return e.toString();
		} catch (UnsupportedEncodingException e) {
			return e.toString();
		}
	}
}
