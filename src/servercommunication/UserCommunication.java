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
	public String registerUser(String email, String password, String confirmPassword, String firstName, String lastName) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("confirm_password", confirmPassword));
		nameValuePairs.add(new BasicNameValuePair("first_name", firstName));
		nameValuePairs.add(new BasicNameValuePair("last_name", lastName));
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "register", nameValuePairs);
		//TODO do something with serverResonse
		return MyHttpClient.parseResponse(serverResponse);
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
		return MyHttpClient.parseResponse(serverResponse);

		//TODO do something with serverResonse
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}

}
