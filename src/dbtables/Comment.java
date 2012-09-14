package dbtables;

import java.sql.Timestamp;

public class Comment {


	private int _id;
	private int memberFromId;
	private int memberToId;
	private String message;
	private Timestamp stamp;
	
	/**
	 * default Comment constructor
	 */
	public Comment() {
		
	}
	
	/**
	 * 3 argument Comment constructor
	 * @param memberFromId
	 * @param memberToId
	 * @param message
	 */
	public Comment(int memberFromId, int memberToId, String message) {
		this.memberFromId = memberFromId;
		this.memberToId = memberToId;
		this.message = message;
	}
	
	/**
	 * 5 argument Comment constructor
	 * @param id
	 * @param memberFromId
	 * @param memberToId
	 * @param message
	 * @param stamp
	 */
	public Comment(int id, int memberFromId, int memberToId, String message, Timestamp stamp) {
		this(memberFromId, memberToId, message);
		this._id = id;
		this.stamp = stamp;
	}
	
	/**
	 * @return the _id
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * @return the memberFromId
	 */
	public int getMemberFromId() {
		return memberFromId;
	}

	/**
	 * @return the memberToId
	 */
	public int getMemberToId() {
		return memberToId;
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
}
