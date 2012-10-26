package servercommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import loaders.NewsfeedCursorLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	/**
	 * helper function to get newsfeed
	 * @param leagueId
	 * @return
	 */
	private static String getCommentsHelper(int leagueId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("game_id", leagueId + ""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "leaderboard", params);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	/**
	 * sends add comment request to server
	 * @param leagueMemberId
	 * @param comment
	 * @return
	 */
	public static String addComment(int leagueMemberId, String comment) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("from_id", leagueMemberId);
			json.put("message", comment);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        //TODO add route
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "comment_new", stringEntity);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (JSONException e) {
			return e.toString();
		} catch (UnsupportedEncodingException e) {
			return e.toString();
		}
	}
	
	/**
	 * gets the newsfeed and turns into a cursor
	 * @param leagueId
	 * @return
	 */
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
