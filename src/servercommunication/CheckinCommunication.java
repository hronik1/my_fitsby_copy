package servercommunication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import responses.StatusResponse;
import responses.UserResponse;

public class CheckinCommunication {

	private static final String TAG = "CheckinCommunication";
	
	public CheckinCommunication() {
		
	}
	
	/**
	 * sends the checkin request to the server
	 * @param id
	 * @return
	 */
	public static StatusResponse checkin(int id) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", id);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "check_in_request", stringEntity);
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
	 * sends the checkout request to the server
	 * @param id
	 * @return
	 */
	public static StatusResponse checkout(int id) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", id);
	        StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "check_out_request", stringEntity);
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return new StatusResponse("fail");
		}
	}
	


}
