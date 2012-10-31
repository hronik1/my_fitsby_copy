package responses;

public class UserResponse {
	private int id;
	private StatusResponse mStatusResponse;
	
	public UserResponse(String status, int id) {
		mStatusResponse = new StatusResponse(status);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
}
