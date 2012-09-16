package dbtables;

public class LeagueMember {

	private int id;
	private int leagueId;
	private int userId;
	
	private int checkins;
	private int checkouts;
	
	/**
	 * default LeagueMember constructor
	 */
	public LeagueMember() {
		
	}
	
	/**
	 * 2 argument LeagueMember constructor
	 * @param leagueId
	 * @param userId
	 */
	public LeagueMember(int leagueId, int userId) {
		this.leagueId = leagueId;
		this.userId = userId;
		checkins = 0;
		checkouts = 0;
	}
	
	/**
	 * 3 argument LeagueMember constructor
	 * @param id
	 * @param leagueId
	 * @param userId
	 */
	public LeagueMember(int id, int leagueId, int userId) {
		this(leagueId, userId);
		this.id = id;
	}
	
	/**
	 * 5 argument constructor
	 * @param id
	 * @param leagueId
	 * @param userId
	 * @param checkins
	 * @param checkouts
	 */
	public LeagueMember(int id, int leagueId, int userId, int checkins, int checkouts) {
		this(id, leagueId, userId);
		this.checkins = checkins;
		this.checkouts = checkouts;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the leagueId
	 */
	public int getLeagueId() {
		return leagueId;
	}
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param leagueId the leagueId to set
	 */
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the checkins
	 */
	public int getCheckins() {
		return checkins;
	}

	/**
	 * @param checkins the checkins to set
	 */
	public void setCheckins(int checkins) {
		this.checkins = checkins;
	}

	/**
	 * @return the checkouts
	 */
	public int getCheckouts() {
		return checkouts;
	}

	/**
	 * @param checkouts the checkouts to set
	 */
	public void setCheckouts(int checkouts) {
		this.checkouts = checkouts;
	}
	
}
