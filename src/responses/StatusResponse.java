package responses;

public class StatusResponse {
	private final static String RESPONSE_SUCCESS = "okay";
	private final static String RESPONSE_FAIL = "fail";
	
	private String status;
	
	public StatusResponse(String status) {
		this.status = status;
	}
	
	public boolean wasSuccessful() {
		return status.equals(RESPONSE_SUCCESS);
	}
	
}

