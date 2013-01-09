package responses;
import dbtables.User;

public class UserResponse {
	private User user;
	private StatusResponse mStatusResponse;
	private String status;
	
	public UserResponse() {
		
	}
	
	public UserResponse(String status, User user, String error) {
		mStatusResponse = new StatusResponse(status);
		this.status = status;
		this.user = user;
		mStatusResponse.setError(error);
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
	
	public void setError(String error) {
		if (mStatusResponse == null)
			mStatusResponse = new StatusResponse("fail");
		mStatusResponse.setError(error);
	}
	
	public String getError() {
		if (mStatusResponse != null)
			return mStatusResponse.getError();
		else 
			return "";
	}
}
