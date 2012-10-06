package servercommunication;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

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
	public void registerUser(String email, String password) {
		HttpPost registrationRequest = new HttpPost();
		//TODO set uri of request
		registrationRequest.setHeader("Content-type", "application/json");
		HttpClient httpClient = new DefaultHttpClient();
		
	}
	
	/**
	 * 
	 * @param email
	 * @param password
	 */
	public void loginUser(String email, String password) {
		HttpPost loginRequest = new HttpPost();
		//TODO set uri of request
		loginRequest.setHeader("Content-type", "application/json");
	}
	
}
