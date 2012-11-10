package servercommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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

import responses.CommentsResponse;
import responses.StatusResponse;

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
	private static CommentsResponse getCommentsHelper(int leagueId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("game_id", leagueId + ""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "game_comments", params);
			return CommentsResponse.jsonToCommentsResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new CommentsResponse(e.toString(), null);
		}
	}
	
	/**
	 * sends add comment request to server
	 * @param leagueMemberId
	 * @param comment
	 * @return
	 */
	public static StatusResponse addComment(String userId, String gameId, String comment, String stamp) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", userId);
			json.put("game_id", gameId);
			json.put("message", comment);
			json.put("stamp", stamp);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        //TODO add route
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "post_comment", stringEntity);
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
	 * gets the newsfeed and turns into a cursor
	 * @param leagueId
	 * @return
	 */
	public static Cursor getNewsfeed(int leagueId) {
		CommentsResponse commentsResponse = getCommentsHelper(leagueId);
		
		MatrixCursor cursor = new MatrixCursor(NewsfeedCursorLoader.FROM_ARGS);
		if (commentsResponse == null || !commentsResponse.wasSuccessful()) {
			Log.d(TAG, "unsuccess");
			return cursor;
		}
		Vector<Comment> comments = commentsResponse.getComments();
		Log.d(TAG, comments.size()+"");
		for(Comment comment: comments) {
			cursor.addRow(new Object[] { comment.getFirstName(), comment.getLastName(),
					comment.getStamp(), comment.getMessage(), comment.getId() });
		}
		return cursor;
	}
	
}
