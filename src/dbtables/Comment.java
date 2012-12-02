package dbtables;

import java.sql.Timestamp;

import android.graphics.Bitmap;

public class Comment {

	public final static String KEY_BITMAP = "bitmap";
	private int memberFromId;
	private String firstName;
	private String lastName;
	private String message;
	private String stamp;
	private String id;
	private Bitmap bitmap;
	
	/**
	 * default Comment constructor
	 */
	public Comment() {
		
	}

	
	/**
	 * 5 argument Comment constructor
	 * @param id
	 * @param memberFromId
	 * @param memberToId
	 * @param message
	 * @param stamp
	 */
	public Comment(int memberFromId, String firstName, String lastName, String message, String stamp, String id, Bitmap bitmap) {
		this.memberFromId = memberFromId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.message = message;
		this.stamp = stamp;
		this.id = id;
		this.bitmap = bitmap;
	}

	/**
	 * @return the memberFromId
	 */
	public int getMemberFromId() {
		return memberFromId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the stamp
	 */
	public String getStamp() {
		return stamp;
	}	
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getId() {
		return id;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
}