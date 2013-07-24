package dbtables;

import android.graphics.Bitmap;

public class User {
	
	private int _id;
	private String _firstName;
	private String _lastName;
	private String _email;
	private Bitmap mBitmap;
	private boolean isFbUser;

	public final static String ID_KEY = "userID";
	/**
	 * default constructor, creates empty User
	 */
	public User() {
		
	}
	
	/**
	 * 4 argument User constructor
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 */
	public User(String firstName, String lastName, String email) {
		this._firstName = firstName;
		this._lastName = lastName;
		this._email = email;
	}
	
	/**
	 * 5 argument User constructor
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 */
	public User(int id, String firstName, String lastName, String email) {
		this(firstName, lastName, email);
		this._id = id;
	}
	
	/**
	 * @return the _id
	 */
	public int getID() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void setID(int id) {
		this._id = id;
	}

	/**
	 * @return the _firstName
	 */
	public String getFirstName() {
		return _firstName;
	}

	/**
	 * @param _firstName the _firstName to set
	 */
	public void setFirstName(String firstName) {
		this._firstName = firstName;
	}

	/**
	 * @return the _lastName
	 */
	public String getLastName() {
		return _lastName;
	}

	/**
	 * @param _lastName the _lastName to set
	 */
	public void setLastName(String lastName) {
		this._lastName = lastName;
	}

	/**
	 * @return the _email
	 */
	public String getEmail() {
		return _email;
	}

	/**
	 * @param _email the _email to set
	 */
	public void setEmail(String email) {
		this._email = email;
	}

	/**
	 * @return the mBitmap
	 */
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	/**
	 * 
	 * @param bitmap the bitmap to set
	 */
	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
	}
	
	/**
	 * 
	 * @return	true if the user is from facebook, false otherwise
	 */
	public boolean isFbUser() {
		return isFbUser;
	}

	/**
	 * 
	 * @param isFbUser	boolean to set as isFbUser
	 */
	public void setFbUser(boolean isFbUser) {
		this.isFbUser = isFbUser;
	}
}

