package servercommunication;

import java.io.IOException;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import responses.StatusResponse;
import android.util.Log;

import com.fitsby.R;

import constants.SingletonContext;

public class CreditCardCommunication {

	private final static String TAG = "CreditCardCommunication";
	
	public CreditCardCommunication() {
		
	}
	
	/**
	 * sends the credit card information for a given user
	 * @param userId
	 * @param cardNumber
	 * @param expMonth
	 * @param expYear
	 * @param cvc
	 * @return
	 */
	public static StatusResponse sendCreditCardInformation(String userId, String cardNumber, String expMonth, String expYear, String cvc) {
		MyHttpClient myHttpClient = new MyHttpClient();
		JSONObject json = new JSONObject();
        try {
			json.put("user_id", userId+"");
			json.put("credit_card_number", cardNumber+"");
			json.put("credit_card_exp_month", expMonth+"");
			json.put("credit_card_exp_year", expYear+"");
			json.put("credit_card_cvc", cvc+"");
			StringEntity stringEntity = new StringEntity(json.toString());  
			ServerResponse serverResponse = myHttpClient.createPostRequest(MyHttpClient.SERVER_URL + "get_and_save_stripe_info", stringEntity);
			if (serverResponse.exception instanceof IOException) {
				StatusResponse response = new StatusResponse();
				response.setError(SingletonContext.getInstance().getContext().getString(R.string.timeout_message));
				return response;
			}
			return StatusResponse.jsonToStatusResponse(MyHttpClient.parseResponse(serverResponse));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return new StatusResponse(e.toString());
		}
	}
}
