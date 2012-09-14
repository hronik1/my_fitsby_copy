package dbtables;

import java.sql.Timestamp;

public class Checkout {
	
	private int _id;
	private int memberId;
	private int checkinId;
	private Timestamp checkoutTime;
	
	/**
	 * @return the _id
	 */
	public int get_id() {
		return _id;
	}
	/**
	 * @return the memberId
	 */
	public int getMemberId() {
		return memberId;
	}
	/**
	 * @return the checkinId
	 */
	public int getCheckinId() {
		return checkinId;
	}
	/**
	 * @return the checkoutTime
	 */
	public Timestamp getCheckoutTime() {
		return checkoutTime;
	}
}
