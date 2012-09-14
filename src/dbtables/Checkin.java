package dbtables;

import java.sql.Timestamp;

public class Checkin {
	
	private int _id;
	private int leagueMemberId;
	private Timestamp checkinTime;
	
	/**
	 * default constructor
	 */
	public Checkin() {
		
	}
	
	/**
	 * 1 argument constructor
	 * @param leagueMemberId
	 */
	public Checkin(int leagueMemberId) {
		this.leagueMemberId = leagueMemberId;
	}
	
	/**
	 * 3 argument constructor
	 * @param id
	 * @param leagueMemberId
	 * @param checkinTime
	 */
	public Checkin(int id,  int leagueMemberId, Timestamp checkinTime) {
		this(leagueMemberId);
		this._id = id;
		this.checkinTime = checkinTime;
	}
	
	/**
	 * @return the _id
	 */
	public int get_id() {
		return _id;
	}
	/**
	 * @return the league_member_id
	 */
	public int getLeagueMemberId() {
		return leagueMemberId;
	}
	/**
	 * @return the checkinTime
	 */
	public Timestamp getCheckinTime() {
		return checkinTime;
	}
	
	
	
}
