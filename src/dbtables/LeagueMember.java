package dbtables;

public class LeagueMember {

	private int id;
	private int leagueId;
	private int userId;
	
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
	
	
}
