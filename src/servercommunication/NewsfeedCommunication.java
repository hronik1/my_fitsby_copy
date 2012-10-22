package servercommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loaders.NewsfeedCursorLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.Comment;
import dbtables.LeagueMember;
import dbtables.User;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class NewsfeedCommunication {

	private static final String TAG = "NewsfeedCommunication";
	
	private static String getCommentsHelper(int leagueId) {
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
	
	public void addComment(int leagueMemberId, String comment) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("from_id", leagueMemberId + ""));
		nameValuePairs.add(new BasicNameValuePair("message", comment));
		//TODO add something to nameValuePairs
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, nameValuePairs);
		//TODO do something with serverResonse
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}
	
	public static Cursor getNewsfeed(int leagueId) {
		String jsonResponse = getCommentsHelper(leagueId);
		MatrixCursor cursor = new MatrixCursor(NewsfeedCursorLoader.FROM_ARGS);
		if (jsonResponse == null)
			return cursor;
//		for(Comment comment: commentList) {
//			LeagueMember member = mLeagueMemberTableHandler.getLeagueMemberByID(comment.getMemberFromId());
//			User user = mUserTableHandler.getUser(member.getUserId());
//			cursor.addRow(new Object[] { user.getFirstName(), user.getLastName(),
//					comment.getStamp(), comment.getMessage() });
//		}
		
		//TODO parse json into cursor
		return cursor;
	}
	
}
