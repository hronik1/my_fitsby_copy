package servercommunication;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.UserTableHandler;

public class GamesLeaderCommunication {
	
	private final static String TAG = "GamesLeaderCommunication";
	
	private static String getGamesLeaderHelper(int gameId) {
//		MyHttpClient myHttpClient = new MyHttpClient();
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		//TODO add something to nameValuePairs
//		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, nameValuePairs);
//		//TODO do something with serverResonse
//		HttpResponse response = serverResponse.response;
//        HttpEntity entity = response.getEntity();
//        try {
//			return EntityUtils.toString(entity);
//		} catch (ParseException e) {
//			Log.d(TAG, e.toString());
//			return null;
//		} catch (IOException e) {
//			Log.d(TAG, e.toString());
//			return null;
//		}
		return null;
	}
	
	public static Cursor getGamesLeader(int gameId) {
		MatrixCursor cursor = new MatrixCursor(new String[] { UserTableHandler.KEY_FIRST_NAME,
				UserTableHandler.KEY_LAST_NAME, LeagueMemberTableHandler.KEY_CHECKINS});
		if (getGamesLeaderHelper(gameId) == null)
			return cursor;
		//TODO parse JSON, and fill cursor
		
		return cursor;
		
	}
}
