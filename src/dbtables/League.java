package dbtables;

import android.graphics.Bitmap;

public class League {

	private int id;
	//private int creatorId;
	private int isPrivate;
	private int players;
	private int wager;
	private int duration;
	private int stakes;
	private String startDate;
	private String endDate;
	private int goal;
	private Bitmap bitmap;

	/* add time information */
	
	/**
	 * default empty league Constructor
	 */
	public League() {
		
	}
	
//	/**
//	 * 4 argument league constructor
//	 * @param creatorId
//	 * @param isPrivate
//	 * @param wager
//	 * @param duration
//	 */
//	public League(int creatorId, int isPrivate, int wager, int duration, int stakes) {
////		this.creatorId = creatorId;
////		this.isPrivate = isPrivate;
//		this.wager = wager;
//		this.duration = duration;
//	}
	
	/**
	 * 5 argument league constructor
	 * @param id
	 * @param creatorId
	 * @param isPrivate
	 * @param wager
	 * @param duration
	 */
	public League(int id, int wager, int players, int duration, int stakes) {
		this.id = id;
		this.wager = wager; 
		this.players = players;
		this.duration = duration;
		this.stakes = stakes;
		isPrivate = 0;
	}
	
	/**
	 * 6 argument league constructor
	 * @param id
	 * @param creatorId
	 * @param isPrivate
	 * @param wager
	 * @param duration
	 * @param bitmap
	 */
	public League(int id, int wager, int players, int duration, int stakes, Bitmap bitmap, int goal) {
		this.id = id;
		this.wager = wager; 
		this.players = players;
		this.duration = duration;
		this.stakes = stakes;
		isPrivate = 0;
		this.bitmap = bitmap;
		this.goal= goal;
	}
	
	/**
	 * returns id
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * sets id 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
//	/**
//	 * returns creatorId()
//	 * @return
//	 */
//	public int getCreatorId() {
//		return creatorId;
//	}
//	
//	/**
//	 * returns creatorId
//	 * @param creatorId
//	 */
//	public void setCreatorId(int creatorId) {
//		this.creatorId = creatorId;
//	}
//
//	/**
//	 * @return the isPrivate
//	 */
//	public int isPrivate() {
//		return isPrivate;
//	}
//
//	/**
//	 * @param isPrivate the isPrivate to set
//	 */
//	public void setPrivate(int isPrivate) {
//		this.isPrivate = isPrivate;
//	}

	/**
	 * @return the wager
	 */
	public int getWager() {
		return wager;
	}

	/**
	 * @param wager the wager to set
	 */
	public void setWager(int wager) {
		this.wager = wager;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getStakes() {
		return this.stakes;
	}

	public int getPlayers() {
		return this.players;
	}
	
	public int isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getGoal() {
		return goal;
	}
}
