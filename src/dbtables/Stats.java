package dbtables;

public class Stats {

	private String gamesPlayed;
	private String gamesWon;
	private String moneyEarned;
	private String joinedMonth;
	private String joinedDay;
	private String joinedYear;
	
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
}
