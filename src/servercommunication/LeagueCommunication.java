package servercommunication;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, nameValuePairs);
		//TODO do something with serverResonse
        HttpResponse response = serverResponse.response;
        HttpEntity entity = response.getEntity();
        try {
			return EntityUtils.toString(entity);
		} catch (ParseException e) {
			Log.d(TAG, e.toString());
			return null;
		} catch (IOException e) {
			Log.d(TAG, e.toString());
			return null;
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
	
	public static void createLeague(int creatorId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("creator_id", creatorId + ""));
		//TODO add something to nameValuePairs
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, nameValuePairs);
	}
}
