package dbhandlers;

import java.util.ArrayList;
import java.util.List;

import dbtables.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserTableHandler {

	private static final String TAG = "UserTableHandler";
	public static final String TABLE = "user";
	
    public static final String KEY_ID = "_id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    
    public static final String TYPE_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String TYPE_FIRST_NAME = "TEXT NOT NULL";
    public static final String TYPE_LAST_NAME = "TEXT NOT NULL";
    public static final String TYPE_EMAIL = "TEXT UNIQUE NOT NULL";
    public static final String TYPE_PASSWORD = "TEXT NOT NULL";
    
    public static final String CREATE_SQL = "CREATE TABLE " + TABLE + "("
            + KEY_ID + " " + TYPE_ID + "," + KEY_FIRST_NAME + " " + TYPE_FIRST_NAME + ","
            + KEY_LAST_NAME + " " + TYPE_LAST_NAME + "," + KEY_EMAIL + " " + TYPE_EMAIL + "," 
            + KEY_PASSWORD + " " + TYPE_PASSWORD + ")";
    public static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE;
    
    private SQLiteDatabase db;

    /**
     * Constructor class for UserTableHandler Class
     * @param ctx
     * @throws SQLException
     */
    public UserTableHandler(SQLiteDatabase db) {
        this.db = db;
    }
  //TODO fix up comments  
	/**
	 * adds user to TABLE_USERS
	 * @param user
	 */
//	public void addUser(User user) {
//		//TODO possibly more validation on use
//	    
//	    ContentValues values = new ContentValues();
//	    values.put(KEY_FIRST_NAME, user.getFirstName());
//	    values.put(KEY_LAST_NAME, user.getLastName());
//	    values.put(KEY_EMAIL, user.getEmail());
//	    values.put(KEY_PASSWORD, user.getPassword());
//	 
//	    db.insert(TABLE, null, values);
//	}
	
	/**
	 * returns User with the corresponding id
	 * @param id
	 * @return
	 */
//	public User getUser(int id) {
//
//		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
//				KEY_FIRST_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD }, KEY_ID + "=?",
//						new String[] { String.valueOf(id) }, null, null, null, null);
//		if (cursor != null && cursor.getCount() != 0) {
//			cursor.moveToFirst();
//		} else {
//			return null;
//		}
//
//		User user = new User(Integer.parseInt(cursor.getString(0)),
//				cursor.getString(1), cursor.getString(2),
//				cursor.getString(3), cursor.getString(4));
//
//		cursor.close();
//		return user;
//	}
	
	/**
	 * returns User with the corresponding id
	 * @param id
	 * @return
	 */
//	public User getUser(String email) {
//		if (email == null)
//			return null;
//		
//		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
//				KEY_FIRST_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD }, KEY_EMAIL + "=?",
//						new String[] { email }, null, null, null, null);
//		if (cursor != null && cursor.getCount() != 0)
//			cursor.moveToFirst();
//		else
//			return null;
//
//		User user = new User(Integer.parseInt(cursor.getString(0)),
//				cursor.getString(1), cursor.getString(2),
//				cursor.getString(3), cursor.getString(4));
//
//		cursor.close();
//		return user;
//	}
	
	/**
	 * returns all users stored in TABLE_USERS
	 * @return
	 */
//	public List<User> getAllUsers() {
//	    List<User> usersList = new ArrayList<User>();
//	    String selectQuery = "SELECT * FROM " + TABLE;
//	    Cursor cursor = db.rawQuery(selectQuery, null);
//	 
//	    if (cursor.moveToFirst()) {
//	        do {
//	            User user = new User();
//	            user.setID(Integer.parseInt(cursor.getString(0)));
//	            user.setFirstName(cursor.getString(1));
//	            user.setLastName(cursor.getString(2));
//	            user.setEmail(cursor.getString(3));
//	            user.setPassword(cursor.getString(4));
//
//	            usersList.add(user);
//	        } while (cursor.moveToNext());
//	    }
//	 
//	    cursor.close();
//	    
//	    return usersList;
//	}
//	
	/**
	 * returns the number of Users stored in TABLE_USERS
	 * @return
	 */
	public int getUsersCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        int count;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
        
        return count;
	}
	
	/**
	 * updates the User user in TABLE_USERS
	 * @param user
	 * @return
	 */
//	public int updateUser(User user) {
//	    ContentValues values = new ContentValues();
//	    values.put(KEY_FIRST_NAME, user.getFirstName());
//	    values.put(KEY_LAST_NAME, user.getLastName());
//	    values.put(KEY_EMAIL, user.getEmail());
//	    values.put(KEY_PASSWORD, user.getPassword());
//
//	    return db.update(TABLE, values, KEY_ID + " = ?",
//	            new String[] { String.valueOf(user.getID()) });
//	}
	
	/**
	 * deletes the User user from TABLE_USERS
	 * @param user
	 */
	public void deleteUser(User user) {
	    db.delete(TABLE, KEY_ID + "=?",
	            new String[] { String.valueOf(user.getID()) });
	}
	
	/**
	 * Checks if email is unique within table
	 * @param email
	 * @return
	 */
	public boolean isEmailUnique(String email) {
		String[] columns = new String[] {KEY_ID, KEY_FIRST_NAME, KEY_LAST_NAME,
					KEY_EMAIL, KEY_PASSWORD};
		Cursor cursor = db.query(TABLE, columns, KEY_EMAIL+"=?",
					new String[] {email}, null, null, null);
		Log.d(TAG, ((Integer)cursor.getCount()).toString());
		if (cursor.getCount() == 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}
	
	/**
	 * checks to ensure that email and password belong to the same people
	 * @param email
	 * @param password
	 * @return
	 */
	public boolean isEmailPasswordComboValid(String email, String password) {
		Cursor cursor = db.query(TABLE, new String[] {KEY_PASSWORD}, KEY_EMAIL+"=?",
					new String[] {email}, null, null, null);
		
		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			return false;
		} else {
			cursor.moveToFirst();
			String returnedPassword = cursor.getString(0);
			cursor.close();
			if (returnedPassword.equals(password))
				return true;
			else
				return false;
		}
	}
}
