package registration;

public class RegisterClientSideValidation {

	private static final int minLength = 6;
	
	/**
	 * brief client-side validation
	 * @param email
	 * @param password
	 * @return empty string if valid, error otherwise
	 */
	public static String validate(String email, String password) {
		String ret = "";
		
		if (email == null || email.length() < minLength) {
			ret += "email too short ";
			//TODO additional email verification
		}
		
		if (password == null || password.length() < minLength) {
			ret += "password too short";
		}
		
		return ret;
	}
}
