package servercommunication;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

public class CommentCommunication {

	public CommentCommunication() {
		
	}
	
	public void getLeagueComments(int leagueId) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, nameValuePairs);
		//TODO do something with serverResonse
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}
	
	public void addComment(int leagueId, String comment) {
		MyHttpClient myHttpClient = new MyHttpClient();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO add something to nameValuePairs
		ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL, nameValuePairs);
		//TODO do something with serverResonse
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        return EntityUtils.toString(entity);
	}
	
	
}
