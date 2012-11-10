package responses;

import java.sql.Timestamp;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import dbtables.Comment;


public class CommentsResponse {
	private final static String TAG = "CommentsResponse";
	private StatusResponse mStatusResponse;
	private Vector<Comment> comments;
	
	public CommentsResponse(String status, Vector<Comment> comments) {
		mStatusResponse = new StatusResponse(status);
		this.comments = comments;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public Vector<Comment> getComments() {
		return comments;
	}
	
	public static CommentsResponse jsonToCommentsResponse(JSONObject json) {
		try {
			String success = json.get("status").toString();
			Vector<Comment> comments = new Vector<Comment>();
			JSONArray jsonComments = json.getJSONArray("all_comments");
			int length = jsonComments.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonComment = jsonComments.getJSONObject(i);
				String firstName = jsonComment.getString("first_name");
				String lastName = jsonComment.getString("last_name");
				int id = Integer.parseInt(jsonComment.getString("user_id"));
				String message = jsonComment.getString("message");
				String stamp = jsonComment.getString("stamp");
				String _id = jsonComment.getString("_id");
				comments.add(new Comment(id, firstName, lastName, message, stamp, _id));
			}
			return new CommentsResponse(success, comments);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.toString());
			return new CommentsResponse(e.toString(), null);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new CommentsResponse(e.toString(), null);
		}


	}
}
