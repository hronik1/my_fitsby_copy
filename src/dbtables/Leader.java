package dbtables;

public class Leader {
	private String firstName;
	private String lastName;
	private int checkins;
	
	public Leader(String firstName, String lastName, int checkins) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.checkins = checkins;
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
}
