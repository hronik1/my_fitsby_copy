package responses;
import dbtables.User;

public class UserResponse {
	private User user;
	private StatusResponse mStatusResponse;
	
	public UserResponse(String status, User user) {
		mStatusResponse = new StatusResponse(status);
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	
}
