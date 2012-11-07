package responses;
import dbtables.User;

public class UserResponse {
	private User user;
	private StatusResponse mStatusResponse;
	private String status;
	private String error;
	
	public UserResponse(String status, User user, String error) {
		mStatusResponse = new StatusResponse(status);
		this.status = status;
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getError() {
		return error;
	}
}
