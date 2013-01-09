package servercommunication;

import java.io.ByteArrayOutputStream;
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

import com.fitsby.R;

import constants.SingletonContext;

import responses.CountdownResponse;
import responses.CreatorResponse;
import responses.LeagueCreateResponse;
import responses.PrivateLeagueResponse;
import responses.ProgressResponse;
import responses.PublicLeaguesResponse;
import responses.StakesResponse;
import responses.StatusResponse;
import responses.UsersGamesResponse;

import dbtables.League;
import loaders.PublicLeaguesCursorLoader;
import android.database.Cursor;
import cursors.MatrixCursor;
import android.graphics.Bitmap;
import android.util.Log;

public class LeagueCommunication {

	private final static String TAG = "LeagueCommunication";
	
	private static PublicLeaguesResponse getPublicLeaguesHelper(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
        try {
			nameValuePairs.add(new BasicNameValuePair("user_id", userId+""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "public_games", nameValuePairs);
			if (serverResponse.exception instanceof IOException) {
				PublicLeaguesResponse response = new PublicLeaguesResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return PublicLeaguesResponse.jsonToPublicLeagueResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new PublicLeaguesResponse(e.toString(), null);
		}
	}
	
	public static PrivateLeagueResponse getPrivateLeague(String id, String firstName, String userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {  
	        nameValuePairs.add(new BasicNameValuePair("game_id", id));
			nameValuePairs.add(new BasicNameValuePair("first_name_of_creator", firstName));
			nameValuePairs.add(new BasicNameValuePair("user_id", userId));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "get_private_game_info", nameValuePairs);
			if (serverResponse.exception instanceof IOException) {
				PrivateLeagueResponse response = new PrivateLeagueResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return PrivateLeagueResponse.jsonToPrivateLeagueResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new PrivateLeagueResponse(e.toString(), null);
		}
	}
	
	public static PrivateLeagueResponse getSingleGame(String id) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {  
	        nameValuePairs.add(new BasicNameValuePair("game_id", id));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "single_game_info", nameValuePairs);
			if (serverResponse.exception instanceof IOException) {
				PrivateLeagueResponse response = new PrivateLeagueResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return PrivateLeagueResponse.jsonToPrivateLeagueResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new PrivateLeagueResponse(e.toString(), null);
		}
	}
	
	public static Cursor getPublicLeagues(int userId) {
		MatrixCursor cursor = new MatrixCursor(PublicLeaguesCursorLoader.FROM_ARGS);
		PublicLeaguesResponse publicLeaguesResponse = getPublicLeaguesHelper(userId);
		if (publicLeaguesResponse == null || !publicLeaguesResponse.wasSuccessful())
			return null;
		Vector<League> leagues = publicLeaguesResponse.getLeagues();
		for(League league: leagues) {
			Bitmap bitmap = league.getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			int leagueId = league.getId();
			int numPlayers = league.getPlayers();
			int wager = league.getWager();
			cursor.addRow(new Object[] {byteArray, leagueId, numPlayers, wager, league.getDuration(), league.getStakes()});
		}
		
		//TODO parse json into cursor
		return cursor;
	}
	
	/**
	 * sends the request to create a league
	 * @param creatorId
	 * @return
	 */
	public static LeagueCreateResponse createLeague(int creatorId, int duration, boolean isPrivate, int wager, int structure, String cardNumber, String expYear, String expMonth, String cvc) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", creatorId);
			json.put("duration", duration);
			json.put("is_private", isPrivate);
			json.put("wager", wager);
			json.put("winning_structure", structure);
			//json.put("creator_first_name", creatorFirstName);
			json.put("credit_card_number", cardNumber);
			json.put("credit_card_exp_month", expMonth);
			json.put("credit_card_exp_year", expYear);
			json.put("credit_card_cvc", cvc);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "create_game", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				LeagueCreateResponse response = new LeagueCreateResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return LeagueCreateResponse.jsonToLeagueCreateResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new LeagueCreateResponse("fail", e.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new LeagueCreateResponse("fail", e.toString());
		}
	}
	
	/**
	 * sends the request to the server to join a game
	 * @param userId
	 * @param gameId
	 * @return
	 */
	public static StatusResponse joinLeague(int userId, int gameId, String cardNumber, String expYear, String expMonth, String cvc) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", userId);
			json.put("game_id", gameId);
			json.put("credit_card_number", cardNumber);
			json.put("credit_card_exp_month", expMonth);
			json.put("credit_card_exp_year", expYear);
			json.put("credit_card_cvc", cvc);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        //TODO add route
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "join_game", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		}
	}
	
	public static UsersGamesResponse getUsersLeagues(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
        try {
	        nameValuePairs.add(new BasicNameValuePair("user_id", userId +"")); 
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "games_user_is_in", nameValuePairs);
			if (serverResponse.exception instanceof IOException) {
				UsersGamesResponse response = new UsersGamesResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return UsersGamesResponse.jsonToGamesResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new UsersGamesResponse(e.toString(), null);
		}
	}
	
	public static CountdownResponse getCountdown(int gameId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
        try {
	        nameValuePairs.add(new BasicNameValuePair("game_id", gameId +"")); 
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "countdown", nameValuePairs);
			if (serverResponse.exception instanceof IOException) {
				CountdownResponse response = new CountdownResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return CountdownResponse.jsonToCountdownResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new CountdownResponse(e.toString(), null);
		}
	}
	
	public static CreatorResponse getCreator(String leagueId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
        try {
	        nameValuePairs.add(new BasicNameValuePair("game_id", leagueId)); 
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "get_first_name", nameValuePairs);
			if (serverResponse.exception instanceof IOException) {
				CreatorResponse response = new CreatorResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return CreatorResponse.jsonToCreatorResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new CreatorResponse(e.toString(), null);
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
			if (serverResponse.exception instanceof IOException) {
				StakesResponse response = new StakesResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
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
	
	/**
	 * gets the progress of a game
	 * @param gameId
	 * @return
	 */
	public static ProgressResponse getProgress(int gameId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("game_id", gameId + ""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "percentage_of_game", params);
			if (serverResponse.exception instanceof IOException) {
				ProgressResponse response = new ProgressResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return ProgressResponse.jsonToProgressResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new ProgressResponse(e.toString(), 0.0);
		}
	}
}
