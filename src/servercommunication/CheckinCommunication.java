package servercommunication;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

public class CheckinCommunication {

	public CheckinCommunication() {
		
	}
	
	public void Checkin(int id) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		ServerResponse serverResponse = myHttpClient.createPostRequest(getString(R.string.server_url), nameValuePairs);
		//TODO do something with serverResonse
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}
}
