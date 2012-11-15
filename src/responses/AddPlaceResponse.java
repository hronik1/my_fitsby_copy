package responses;

import org.json.JSONObject;

import android.util.Log;

public class AddPlaceResponse {

	private final static String TAG = "AddPlaceResponse";
	
	private StatusResponse mStatusResponse;
	private String status;
	private String reference;
	private String id;
	
	/**
	 * @param status
	 * @param reference
	 * @param id
	 */
	public AddPlaceResponse(String status, String reference, String id) {
		super();
		mStatusResponse = new StatusResponse(status);
		this.status = status;
		this.reference = reference;
		this.id = id;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * returns whether this was a successful response
	 * @return
	 */
	public boolean wasSuccessful() {
		return mStatusResponse.wasSuccessful();
	}
	/**
	 * parses the json response and turns it into an AddPlaceResponse class
	 * @param json
	 * @return
	 */
	public static AddPlaceResponse jsonToAddPlaceResponse(JSONObject json) {
		
		try {
			Log.d(TAG, json.toString());
			String status = json.getString("status");
			if (status.equals("OK")) {
				String reference = json.getString("reference");
				String id = json.getString("id");
				return new AddPlaceResponse(status, reference, id);
			} else {
				return new AddPlaceResponse(status, null, null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
			return new AddPlaceResponse(e.toString(), e.toString(), e.toString());
		}
	}
}
