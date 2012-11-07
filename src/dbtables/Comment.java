package dbtables;

import java.sql.Timestamp;

public class Comment {



	private int memberFromId;
	private String firstName;
	private String lastName;
	private String message;
	private Timestamp stamp;
	
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
	public Comment(int memberFromId, String firstName, String lastName, String message, Timestamp stamp) {
		this.memberFromId = memberFromId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.message = message;
		this.stamp = stamp;
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
	public Timestamp getStamp() {
		return stamp;
	}	
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
}