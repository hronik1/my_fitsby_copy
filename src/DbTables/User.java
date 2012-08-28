package DbTables;

public class User {
	
	private int _id;
	private String email;
	private String password;
	
	/**
	 * checks existence of user in DB
	 * @param email
	 * @param password
	 * @return true if user exists, false otherwise
	 */
	public boolean isUserValid(String email, String password) {
		return false;
	}
	
	/**
	 * registers a user
	 * @param email
	 * @param password
	 * @return true on successful registration, false otherwise
	 */
	public boolean registerUser(String email, String password) {
		return false;
	}
}

