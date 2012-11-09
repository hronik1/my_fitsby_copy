package servercommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import responses.PublicLeaguesResponse;
import responses.StakesResponse;
import responses.StatusResponse;

import dbtables.League;
import loaders.PublicLeaguesCursorLoader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class LeagueCommunication {

	private final static String TAG = "LeagueCommunication";
	
	private static PublicLeaguesResponse getPublicLeaguesHelper() {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		JSONObject json = new JSONObject();
        try {
	        StringEntity stringEntity = new StringEntity(json.toString());  
			
			//TODO add something to nameValuePairs
			//ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "public_games", stringEntity);
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "public_games", nameValuePairs);
			return PublicLeaguesResponse.jsonToPublicLeagueResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new PublicLeaguesResponse(e.toString(), null);
		}
	}
	
	public static Cursor getPublicLeagues() {
		MatrixCursor cursor = new MatrixCursor(PublicLeaguesCursorLoader.FROM_ARGS);
		PublicLeaguesResponse publicLeaguesResponse = getPublicLeaguesHelper();
		if (publicLeaguesResponse == null || !publicLeaguesResponse.wasSuccessful())
			return cursor;
		Vector<League> leagues = publicLeaguesResponse.getLeagues();
		for(League league: leagues) {
			int leagueId = league.getId();
			int numPlayers = league.getPlayers();
			int wager = league.getWager();
			cursor.addRow(new Object[] {leagueId, numPlayers, wager, league.getDuration(), league.getStakes()});
		}
		
		//TODO parse json into cursor
		return cursor;
	}
	
	/**
	 * sends the request to create a league
	 * @param creatorId
	 * @return
	 */
	public static StatusResponse createLeague(int creatorId, int duration, boolean isPrivate, int wager, String creatorFirstName) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("creator_id", creatorId);
			json.put("duration", duration);
			json.put("is_private", isPrivate);
			json.put("wager", wager);
			json.put("creator_first_name", creatorFirstName);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "games", stringEntity);
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		}
	}
	
	/**
	 * sends the request to the server to join a game
	 * @param userId
	 * @param gameId
	 * @return
	 */
	public static StatusResponse joinLeague(int userId, int gameId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", userId);
			json.put("game_id", gameId);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        //TODO add route
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "join_game", stringEntity);
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		}
	}
	
	/**
	 * method to get pot size
	 * @param gameId
	 * @return
	 */
	public static StakesResponse getPotSize(int gameId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("game_id", gameId + ""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "stakes", params);
			return StakesResponse.jsonToStakesResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StakesResponse(e.toString(), null);
		}
	}
	
	
//	/**
//	 * method to get Number of Players for a league
//	 * @param gameId
//	 * @return
//	 */
//	public static String getNumberPlayers(int gameId) {
//		MyHttpClient myHttpClient = new MyHttpClient();
//		List<NameValuePair> params = new LinkedList<NameValuePair>();
//        try {
//			params.add(new BasicNameValuePair("league_id", gameId + ""));
//			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "number_of_players", params);
//			return MyHttpClient.parseResponse(serverResponse);
//		} catch (Exception e) {
//			return e.toString();
//		}
//	}
}
