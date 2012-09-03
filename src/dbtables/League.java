package dbtables;

public class League {

	private int id;
	private int creatorId;
	/* add time information */
	
	/**
	 * returns id
	 * @return
	 */
	int getId() {
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

}
