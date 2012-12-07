package dbtables;

import android.graphics.Bitmap;

public class Leader {
	public static final String KEY_BITMAP = "bitmap";
	public static final String KEY_RANK = "rank";
	private String firstName;
	private String lastName;
	private int checkins;
	private String id;
	private Bitmap bitmap;
	
	public Leader(String firstName, String lastName, int checkins, String id, Bitmap bitmap) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.checkins = checkins;
		this.id = id;
		this.bitmap = bitmap;
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
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
}
