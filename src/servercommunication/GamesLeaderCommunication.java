package servercommunication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fitsby.R;

import constants.SingletonContext;

import responses.GamesLeaderResponse;
import responses.StatusResponse;

import android.database.Cursor;
import cursors.MatrixCursor;
import android.graphics.Bitmap;
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
		if (serverResponse.exception instanceof IOException) {
			GamesLeaderResponse response = new GamesLeaderResponse();
			response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
			return response;
		}
		return GamesLeaderResponse.jsonToGamesLeaderResponse(MyHttpClient.parseResponse(serverResponse));
		
	}
	
	public static Cursor getGamesLeader(int gameId) {
		MatrixCursor cursor = new MatrixCursor(new String[] { UserTableHandler.KEY_FIRST_NAME,
				UserTableHandler.KEY_LAST_NAME, LeagueMemberTableHandler.KEY_CHECKINS, "_id", Leader.KEY_BITMAP, Leader.KEY_RANK});
		
		GamesLeaderResponse gamesLeaderResponse = getGamesLeaderHelper(gameId);
		if (gamesLeaderResponse == null || !gamesLeaderResponse.wasSuccessful())
			return null;
		Vector<Leader> leaders = gamesLeaderResponse.getLeaders();
		for (Leader leader: leaders) {
			Bitmap bitmap = leader.getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			cursor.addRow(new Object[] { leader.getFirstName(), LastNameFormatter.format(leader.getLastName()),
					leader.getCheckins(), leader.getId(), byteArray, leader.getId()});
		}
		//TODO parse JSON, and fill cursor
		
		return cursor;
		
	}
}
