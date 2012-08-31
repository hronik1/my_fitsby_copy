package registration;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.AsyncTask;

public class RegisterAsyncTask extends AsyncTask<String, Void, HttpResponse> {

	private Context ctx;
	private String email;
	private String password;
	
	/**
	 * 
	 * @param ctx
	 * @param email
	 * @param password
	 */
	public RegisterAsyncTask(Context ctx, String email, String password) {
		this.ctx = ctx;
		this.email = email;
		this.password = password;
	}
	
	@Override
	protected HttpResponse doInBackground(String... arg0) {
		// TODO create query to db
		
		return null;
	}

	@Override
	protected void onPostExecute(HttpResponse response) {
		//TODO parse response, update user accordingly
	}
	
}
