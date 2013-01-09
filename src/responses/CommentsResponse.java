package responses;

import gravatar.Gravatar;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import servercommunication.MyHttpClient;

import android.graphics.Bitmap;
import android.util.Log;

import dbtables.Comment;


public class CommentsResponse {
	private final static String ANNOUNCEMENT_FIRST_NAME = "ANNOUNCEMENT";
	private final static String TAG = "CommentsResponse";
	private StatusResponse mStatusResponse;
	private Vector<Comment> comments;
	
	public CommentsResponse() {
		
	}
	
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
	
	public void setError(String error) {
		if (mStatusResponse == null)
			mStatusResponse = new StatusResponse("fail");
		mStatusResponse.setError(error);
	}
	
	public String getError() {
		if (mStatusResponse != null)
			return mStatusResponse.getError();
		else 
			return "";
	}
	
	public static CommentsResponse jsonToCommentsResponse(JSONObject json) {
		try {
			String success = json.get("status").toString();
			Vector<Comment> comments = new Vector<Comment>();
			JSONArray jsonComments = json.getJSONArray("all_comments");
			int length = jsonComments.length();
			HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
			Bitmap bitmap;
			for (int i = 0; i < length; i++) {
				JSONObject jsonComment = jsonComments.getJSONObject(i);
				String firstName = jsonComment.getString("first_name");
				String lastName = jsonComment.getString("last_name");
				if (firstName.equals(ANNOUNCEMENT_FIRST_NAME))
					lastName = "";
				int id = Integer.parseInt(jsonComment.getString("user_id"));
				String message = jsonComment.getString("message");
				String stamp = jsonComment.getString("stamp");
				String _id = jsonComment.getString("_id");
				String email = jsonComment.getString("email");
				String bold = jsonComment.getBoolean("bold") + "";
				String checkin = jsonComment.getBoolean("checkin") + "";
				//TODO change to actually parse email
				if (!imageMap.containsKey(email))  {
					Log.d(TAG, "getting image");
					String src = Gravatar.getGravatar(email);
					bitmap = MyHttpClient.getBitmapFromURL(src);
					imageMap.put(email, bitmap);
				} else {
					Log.d(TAG, "image exists");
					bitmap = imageMap.get(email);
				}
				comments.add(new Comment(id, firstName, lastName, message, stamp, _id, bitmap, bold, checkin));
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
