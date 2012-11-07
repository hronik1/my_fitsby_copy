package dbtables;

public class Stats {

	private String gamesPlayed;
	private String gamesWon;
	private String moneyEarned;
	
	public Stats(String gamesPlayed, String gamesWon, String moneyEarned) {
		this.gamesPlayed = gamesPlayed;
		this.gamesWon = gamesWon;
		this.moneyEarned = moneyEarned;
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
}
