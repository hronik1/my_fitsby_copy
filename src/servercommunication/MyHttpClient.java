package servercommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import android.util.Log;

public class MyHttpClient {
	
	private final static String TAG = "MyHttpClient";
	public final static String SERVER_URL = "http://f-app.herokuapp.com/";
	
	static HttpClient httpClient = new DefaultHttpClient();
	
	public ServerResponse createPostRequest(String urlString, StringEntity stringEntity) {

		if(httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		ServerResponse serverResponseObject = new ServerResponse();
		HttpResponse response = null;
		Exception exception = null;

		HttpPost httpPost = new HttpPost(urlString);
		        
		try {
			httpPost.setEntity(stringEntity);
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
	
	public ServerResponse createGetRequest(String urlString, List<NameValuePair> params) {
		if(httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		ServerResponse serverResponseObject = new ServerResponse();
		HttpResponse response = null;
		Exception exception = null;
	    String paramString = URLEncodedUtils.format(params, "utf-8");

		HttpGet httpGet = new HttpGet(urlString + "?" + paramString);
		httpGet.setHeader("Content-type", "application/json");
		        
		try {
		    response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
		    Log.v("in HttpClient -> in createGet -> in catch", "ClientProtocolException" + e);
		    response = null;
		    exception = e;
		} catch (IOException e) {
		     Log.v(TAG, "Get request: IOException" + e);
		     response = null;
		     exception = e;
		} catch (Exception e) {
			Log.v(TAG, "Get request: Exception" + e);
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
//		if (response.getStatusLine() != null)
//			return response.getStatusLine().getStatusCode() + "";
		if (exception != null)
			return exception.toString() + " exception in MyHttpClient";
		if (response.getEntity() == null)
			return "entity null";
		if (response.getEntity().toString() == null)
			return "string null";
		return serverResponse.response.getEntity().toString();
	}
}
