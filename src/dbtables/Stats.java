package dbtables;

public class Stats {

	private String gamesPlayed;
	private String gamesWon;
	private String moneyEarned;
	private String joinedMonth;
	private String joinedDay;
	private String joinedYear;
	private String totalCheckins;
	private String totalTime;
	
	public Stats(String gamesPlayed, String gamesWon, String moneyEarned, String joinedMonth, String joinedDay, String joinedYear) {
		this.gamesPlayed = gamesPlayed;
		this.gamesWon = gamesWon;
		this.moneyEarned = moneyEarned;
		this.joinedMonth = joinedMonth;
		this.joinedDay = joinedDay;
		this.joinedYear = joinedYear;
	}
	
	public String getGamesPlayed() {
		return gamesPlayed;
	}
	
	public String getGamesWon() {
		return gamesWon;
	}
	
	public String getMoneyEarned() {
		return moneyEarned;
	}

	public String getJoinedMonth() {
		return joinedMonth;
	}

	public String getJoinedDay() {
		return joinedDay;
	}

	public String getJoinedYear() {
		return joinedYear;
	}

	/**
	 * @return the totalCheckins
	 */
	public String getTotalCheckins() {
		return totalCheckins;
	}

	/**
	 * @param totalCheckins the totalCheckins to set
	 */
	public void setTotalCheckins(String totalCheckins) {
		this.totalCheckins = totalCheckins;
	}

	/**
	 * @return the totalTime
	 */
	public String getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime the totalTime to set
	 */
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
}
