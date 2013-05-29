package servercommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import responses.StatsResponse;
import responses.StatusResponse;
import responses.UserResponse;
import android.util.Log;

import com.fitsby.R;

import constants.SingletonContext;
import dbtables.User;

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
	public static UserResponse registerUser(String email, String password, String confirmPassword, String firstName, String lastName) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("email", email);
			json.put("password", password);
			json.put("password_confirmation", confirmPassword);
			json.put("first_name", firstName);
			json.put("last_name", lastName);
	        StringEntity stringEntity = new StringEntity(json.toString());  
	        
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "users.json", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				UserResponse response = new UserResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return jsonToUserResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return null;
		} catch (UnsupportedEncodingException e) {
			Log.d(TAG, e.toString());
			return null;
		}

	}
	
	/**
	 * send logging in of user to server
	 * @param email
	 * @param password
	 */
	public static UserResponse loginUser(String email, String password) {
		MyHttpClient myHttpClient = new MyHttpClient();
        try {
        	JSONObject json = new JSONObject();
			json.put("email", email);
			json.put("password", password);
			
			StringEntity stringEntity = new StringEntity(json.toString()); 
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "login_android", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				UserResponse response = new UserResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return jsonToUserResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return null;
		}
	}
	
	/**
	 * gets the userStats
	 * @param userId
	 * @return
	 */
	public static StatsResponse getStats(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("user_id", userId+""));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "user_stats", params);
			if (serverResponse.exception instanceof IOException) {
				StatsResponse response = new StatsResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatsResponse.jsonToStatsResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatsResponse(e.toString(), null);
		}
	}

	/**
	 * 
	 * @param email
	 * @return
	 */
	public static StatusResponse resetPassword(String email) {
		MyHttpClient myHttpClient = new MyHttpClient();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
        try {
			params.add(new BasicNameValuePair("email", email));
			ServerResponse serverResponse = myHttpClient.createGetRequest(MyHttpClient.SERVER_URL + "reset_password", params);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
	public static StatusResponse changeEmail(String email, String userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
        	json.put("new_email", email);
        	json.put("user_id", userId);
        	StringEntity stringEntity = new StringEntity(json.toString()); 
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "update_email", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
	public static UserResponse jsonToUserResponse(JSONObject json) {
		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			if ("okay".equals(status)) {
				String firstName = json.getString("first_name");
				String lastName = json.getString("last_name");
				String email = json.getString("email");
				int id = Integer.parseInt(json.get("id").toString());
				User user = new User(id, firstName, lastName, email);
				return new UserResponse(status, user, null);
			} else {
				String error = null;
				try {
					error = json.getString("error");
				} catch (Exception e){
					
				}
				return new UserResponse(json.get("status").toString(), null, error);
			}
		} catch (NumberFormatException e) {
			Log.d(TAG, e.toString());
			return new UserResponse(e.toString(), null, e.toString());
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return new UserResponse(e.toString(), null, e.toString());
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return new UserResponse(e.toString(), null, e.toString());
		}
	}
	

	public static StatusResponse notifySeverOfInvite(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
        	json.put("user_id", userId);
        	StringEntity stringEntity = new StringEntity(json.toString()); 
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "append_text_field", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}

	public static StatusResponse registerDevice(String deviceId, String userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
        	json.put("registration_id", deviceId);
        	json.put("user_id", userId);
        	StringEntity stringEntity = new StringEntity(json.toString()); 
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "push_registration", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
	public static StatusResponse enableNotifications(String userId, boolean enabled) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
        	json.put("user_id", userId);
        	StringEntity stringEntity = new StringEntity(json.toString()); 
        	ServerResponse serverResponse;
        	if (enabled)
        		serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "push_enable", stringEntity);
        	else 
        		serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "push_disable", stringEntity);
        	
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
	public static StatusResponse pushNotificationChange(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
		try {
        	json.put("user_id", userId);
        	StringEntity stringEntity = new StringEntity(json.toString()); 
        	ServerResponse serverResponse;
        	serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "push_position_change", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
        	return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
	
	public static StatusResponse deleteUser(int userId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
		try {
        	json.put("user_id", userId);
        	StringEntity stringEntity = new StringEntity(json.toString()); 
        	ServerResponse serverResponse;
        	serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "user_deletion", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
        	return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
}
