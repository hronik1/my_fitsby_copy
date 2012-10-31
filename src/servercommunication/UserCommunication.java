package servercommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class UserCommunication {

	private final static String TAG = "UserCommunication";
	
	/**
	 * default UserCommunication constructor
	 */
	public UserCommunication() {
		
	}
	
	/**
	 * sends registration of user to server
	 * @param email
	 * @param password
	 */
	public static String registerUser(String email, String password, String confirmPassword, String firstName, String lastName) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			json.put("email", email);
			json.put("password", password);
			json.put("password_confirmation", confirmPassword);
			json.put("first_name", firstName);
			json.put("last_name", lastName);
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("confirm_password", confirmPassword));
			params.add(new BasicNameValuePair("first_name", firstName));
			params.add(new BasicNameValuePair("last_name", lastName));
	        StringEntity stringEntity = new StringEntity(json.toString());  
			//nameValuePairs.add(new BasicNameValuePair("creator_id", creatorId + ""));
			//TODO add something to nameValuePairs
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "users.json", stringEntity);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (JSONException e) {
			return e.toString();
		} catch (UnsupportedEncodingException e) {
			return e.toString();
		}

	}
	
	/**
	 * send logging in of user to server
	 * @param email
	 * @param password
	 */
	public static String loginUser(String email, String password) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("password", password));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "login", params);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	/**
	 * gets the userStats
	 * @param userId
	 * @return
	 */
	public static String getStats(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("user_id", userId+""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "user_stats", params);
			return MyHttpClient.parseResponse(serverResponse);
		} catch (Exception e) {
			return e.toString();
		}
	}

}
