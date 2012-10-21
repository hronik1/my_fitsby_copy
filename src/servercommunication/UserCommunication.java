package servercommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class UserCommunication {

	private final static String TAG = "UserCommunication";
	
	/**
	 * default UserCommunication constructor
	 */
	public UserCommunication() {
		
	}
	
	/**
	 * 
	 * @param email
	 * @param password
	 */
	public String registerUser(String email, String password, String confirmPassword) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("confirm_password", confirmPassword));
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "register", nameValuePairs);
		//TODO do something with serverResonse
		HttpResponse response = serverResponse.response;
		Exception exception = serverResponse.exception;
		if (exception != null)
			return exception.toString() + " exception in MyHttpClient";
		if (response == null)
			return "response null";
		if (response.getEntity() == null)
			return "entity null";
		if (response.getEntity().toString() == null)
			return "string null";
		return serverResponse.response.getEntity().toString();
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}
	
	/**
	 * 
	 * @param email
	 * @param password
	 */
	public String loginUser(String email, String password) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "login", nameValuePairs);
		HttpResponse response = serverResponse.response;
		Exception exception = serverResponse.exception;
		if (exception != null)
			return exception.toString();
		if (response == null)
			return "response null";
		if (response.getEntity() == null)
			return "entity null";
		if (response.getEntity().toString() == null)
			return "string null";
		return serverResponse.response.getEntity().toString();

		//TODO do something with serverResonse
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}

}
