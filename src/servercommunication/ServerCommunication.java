package servercommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;


public class ServerCommunication {

	private Context ctx;
	
	/**
	 * ServerCommunication which stores the context of the app
	 * @param ctx
	 */
	public ServerCommunication(Context ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * queries the given URL in string format
	 * returns the response in a string
	 * @param stringURL: url to request information from
	 * @return empty string on error, or result on success
	 */
	public static String readServerData(String stringURL) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				stringURL);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(ServerCommunication.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}
	
	/**
	 * checks the status of the internet connectivity
	 * @return true if connected to the internet, false otherwise
	 */
	public boolean isInternetConnected() {
	    ConnectivityManager connec = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    boolean isMobileNetworkConnected = connec.getNetworkInfo (ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
	    boolean isWiFiNetworkConnected = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
	    return (isMobileNetworkConnected || isWiFiNetworkConnected);
	}
}
