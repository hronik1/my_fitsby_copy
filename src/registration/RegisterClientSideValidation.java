package registration;

public class RegisterClientSideValidation {

	private static final int minLength = 6;
	


	/**
	 * ensures firstName and lastName are not empty
	 * @param firstName
	 * @param lastName
	 * @return an empty string if success, an error message
	 */
	public static String validateName(String firstName, String lastName) {
		String ret = "";
		
		if (firstName == null || firstName.length() == 0) {
			ret += "First name is required.\n";
			
		}
		if (lastName == null || lastName.length() == 0) {
			ret += "Last name is required.\n";
		}
		
		return ret;
	}
	
	/**
	 * validates email
	 * @param email
	 * @return empty string if valid, error message otherwise
	 */
	public static String validateEmail(String email) {
		String ret = "";
		
		if (email == null || email.length() < minLength) {
			ret += "Email must be least 6 characters.\n";
			//TODO additional email verification
		}
		
		return ret;
	}
	
	/**
	 * validates password
	 * @param password
	 * @return empty string if fine, error message otherwise
	 */
	public static String validatePassword(String password) {
		String ret = "";
		
		if (password == null || password.length() < minLength) {
			ret += "Password must be at least 6 characters.\n";
		}
		//TODO additional password verification
		
		return ret;
	}
}
