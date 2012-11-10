package dbtables;

public class Leader {
	private String firstName;
	private String lastName;
	private int checkins;
	private String id;
	
	public Leader(String firstName, String lastName, int checkins, String id) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.checkins = checkins;
		this.id = id;
	}		
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public int getCheckins() {
		return checkins;
	}
	
	public String getId() {
		return id;
	}
}
