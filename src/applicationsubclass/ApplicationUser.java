package applicationsubclass;

import dbtables.User;
import android.app.Application;

public class ApplicationUser extends Application {
	private User mUser;
	
	/**
	 * 
	 * @return the user
	 */
	public User getUser() {
		return mUser;
	}
	
	/**
	 * sets user
	 * @param user
	 */
	public void setUser(User user) {
		mUser = user;
	}
}
