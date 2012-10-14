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

import android.util.Log;

public class MyHttpClient {
	
	private final static String TAG = "MyHttpClient";
	static HttpClient httpClient = new DefaultHttpClient();
	
	public ServerResponse createPostRequest(String urlString, ArrayList<NameValuePair> nameValuePairs) {

		if(httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		ServerResponse serverResponseObject = new ServerResponse();
		HttpResponse response = null;
		Exception exception = null;

		HttpPost httpPost = new HttpPost(urlString);
		//httpPost.setHeader("Content-type", "application/json"); might need this
		        
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
}
