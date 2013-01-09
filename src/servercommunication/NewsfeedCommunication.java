package servercommunication;

import java.io.ByteArrayOutputStream;
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

import com.fitsby.R;

import constants.SingletonContext;

import responses.CommentsResponse;
import responses.StatusResponse;

import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.Comment;
import dbtables.LeagueMember;
import dbtables.User;
import formatters.LastNameFormatter;

import android.database.Cursor;
import cursors.MatrixCursor;
import android.graphics.Bitmap;
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
			if (serverResponse.exception instanceof IOException) {
				CommentsResponse response = new CommentsResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
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
        	Log.d(TAG, "addComment: user_id " + userId + " game_id " + gameId + " message " + comment);
			json.put("user_id", userId);
			json.put("game_id", gameId);
			json.put("message", comment);
			json.put("stamp", stamp);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        //TODO add route
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "post_comment", stringEntity);
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
			return null;
		}
		Vector<Comment> comments = commentsResponse.getComments();
		Log.d(TAG, comments.size()+"");
		for(Comment comment: comments) {
			Bitmap bitmap = comment.getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			cursor.addRow(new Object[] { comment.getFirstName(), LastNameFormatter.format(comment.getLastName()),
					comment.getStamp(), comment.getMessage(), comment.getId(), byteArray, comment.getBold(), comment.getCheckin() });
		}
		return cursor;
	}
	
}
