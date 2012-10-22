package servercommunication;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import android.util.Log;

public class MyHttpClient {
	
	private final static String TAG = "MyHttpClient";
	public final static String SERVER_URL = "http://f-app.herokuapp.com/";
	
	static HttpClient httpClient = new DefaultHttpClient();
	
	public ServerResponse createPostRequest(String urlString, ArrayList<NameValuePair> nameValuePairs) {

		if(httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		ServerResponse serverResponseObject = new ServerResponse();
		HttpResponse response = null;
		Exception exception = null;

		HttpPost httpPost = new HttpPost(urlString);
		httpPost.setHeader("Content-type", "application/json");
		        
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
		    Log.v("in HttpClient -> in createPostRequest(String urlString) -> in catch", "ClientProtocolException" + e);
		    response = null;
		    exception = e;
		} catch (IOException e) {
		     Log.v(TAG, "Post request: IOException" + e);
		     response = null;
		     exception = e;
		} catch (Exception e) {
			Log.v(TAG, "Post request: Exception" + e);
			response = null;
			exception = e;
		}
		        
		serverResponseObject.response = response;
		serverResponseObject.exception = exception;
		        
		return serverResponseObject;
	}
	
	public static String parseResponse(ServerResponse serverResponse) {
		HttpResponse response = serverResponse.response;
		Exception exception = serverResponse.exception;
		if (response == null)
			return "response null";
		if (response.getStatusLine() != null)
			return response.getStatusLine().getStatusCode() + "";
		if (exception != null)
			return exception.toString() + " exception in MyHttpClient";
		if (response.getEntity() == null)
			return "entity null";
		if (response.getEntity().toString() == null)
			return "string null";
		return serverResponse.response.getEntity().toString();
	}
}
