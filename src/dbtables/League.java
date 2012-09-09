package dbtables;

public class League {

	private int id;
	private int creatorId;
	private int isPrivate;
	private int wager;
	private int duration;
	/* add time information */
	
	/**
	 * default empty league Constructor
	 */
	public League() {
		
	}
	
	/**
	 * 4 argument league constructor
	 * @param creatorId
	 * @param isPrivate
	 * @param wager
	 * @param duration
	 */
	public League(int creatorId, int isPrivate, int wager, int duration) {
		this.creatorId = creatorId;
		this.isPrivate = isPrivate;
		this.wager = wager;
		this.duration = duration;
	}
	
	/**
	 * 5 argument league constructor
	 * @param id
	 * @param creatorId
	 * @param isPrivate
	 * @param wager
	 * @param duration
	 */
	public League(int id, int creatorId, int isPrivate, int wager, int duration) {
		this(creatorId, isPrivate, wager, duration);
		this.id = id;
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
	
	/**
	 * returns creatorId()
	 * @return
	 */
	public int getCreatorId() {
		return creatorId;
	}
	
	/**
	 * returns creatorId
	 * @param creatorId
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the isPrivate
	 */
	public int isPrivate() {
		return isPrivate;
	}

	/**
	 * @param isPrivate the isPrivate to set
	 */
	public void setPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}

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
}
