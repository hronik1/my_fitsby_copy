package servercommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import responses.GamesLeaderResponse;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.Leader;
import formatters.LastNameFormatter;

public class GamesLeaderCommunication {
	
	private final static String TAG = "GamesLeaderCommunication";
	
	private static GamesLeaderResponse getGamesLeaderHelper(int gameId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", gameId+""));
		ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "leaderboard", nameValuePairs);
		return GamesLeaderResponse.jsonToGamesLeaderResponse(MyHttpClient.parseResponse(serverResponse));
		
	}
	
	public static Cursor getGamesLeader(int gameId) {
		MatrixCursor cursor = new MatrixCursor(new String[] { UserTableHandler.KEY_FIRST_NAME,
				UserTableHandler.KEY_LAST_NAME, LeagueMemberTableHandler.KEY_CHECKINS, "_id"});
		
		GamesLeaderResponse gamesLeaderResponse = getGamesLeaderHelper(gameId);
		if (gamesLeaderResponse == null || !gamesLeaderResponse.wasSuccessful())
			return cursor;
		Vector<Leader> leaders = gamesLeaderResponse.getLeaders();
		for (Leader leader: leaders) {
			cursor.addRow(new Object[] { leader.getFirstName(), LastNameFormatter.format(leader.getLastName()),
					leader.getCheckins(), leader.getId()});
		}
		//TODO parse JSON, and fill cursor
		
		return cursor;
		
	}
}
