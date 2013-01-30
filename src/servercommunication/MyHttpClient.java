package servercommunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class MyHttpClient {
	
	private final static String TAG = "MyHttpClient";
//	public final static String SERVER_URL = "https://f-app.herokuapp.com/"; //production server
	public final static String SERVER_URL = "https://test-fitsby.herokuapp.com/"; //test server
	private final static int SOCKET_TIMEOUT_MILLIS = 20000;
	private final static int CONNECTION_TIMEOUT_MILLIS = 20000;
	private static DefaultHttpClient httpClient = new DefaultHttpClient();
	private static HttpParams httpParams = new BasicHttpParams(); 
	
	public ServerResponse createPostRequest(String urlString, StringEntity stringEntity) {

		if(httpClient == null) {
			httpClient = new DefaultHttpClient();			
		}
		if (httpParams == null) {
			httpParams = new BasicHttpParams();
		}
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT_MILLIS);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT_MILLIS);
		httpClient.setParams(httpParams);
		
		ServerResponse serverResponseObject = new ServerResponse();
		HttpResponse response = null;
		Exception exception = null;

		HttpPost httpPost = new HttpPost(urlString);
		httpPost.addHeader("Content-type", "application/json");
		
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
		if (httpParams == null) {
			httpParams = new BasicHttpParams();
		}
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT_MILLIS);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT_MILLIS);
		httpClient.setParams(httpParams);
		
		ServerResponse serverResponseObject = new ServerResponse();
		HttpResponse response = null;
		Exception exception = null;
	    String paramString = URLEncodedUtils.format(params, "utf-8");

		HttpGet httpGet = new HttpGet(urlString + "?" + paramString);
		httpGet.addHeader("Content-type", "application/json");
		        
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

	public static JSONObject parseResponse(ServerResponse serverResponse) {
		HttpResponse response = serverResponse.response;
		Exception exception = serverResponse.exception;
		if (response == null) {
			Log.d(TAG, "response null");
			return null;
		}
		if (exception != null) {
			Log.d(TAG, exception.toString());
			return null;
		}
		if (response.getEntity() == null) {
			Log.d(TAG, "entity null");
			return null;
		}
		
		return getJson(serverResponse.response.getEntity());
	}
	
	public static JSONObject getJson(HttpEntity entity) {
		try {
			InputStream instream = entity.getContent();
			String charset = getContentCharSet(entity);
			if (charset == null) { 
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			Reader reader = new InputStreamReader(instream, charset);
			StringBuilder buffer = new StringBuilder();
			 
			try {
				char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
			} finally {
				reader.close();
			}
			String string = buffer.toString();
			Log.d(TAG, string);
			//TODO make this line more elegant, if it works
			JSONObject json = new JSONObject(string);
			return json;
		} catch (IllegalStateException e) {
			Log.d(TAG, e.toString());
			return null;
		} catch (IOException e) {
			Log.d(TAG, e.toString());
			return null;
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return null;
		}
		
	}
	
	public static String getContentCharSet(final HttpEntity entity) throws ParseException {
		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
		String charset = null;
		if (entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();
			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");
				if (param != null) {
					charset = param.getValue();
				}
			}
		}
		return charset;
	}
	
	/**
	 * downloads a bitmap
	 * @param src
	 * @return
	 */
	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
}
