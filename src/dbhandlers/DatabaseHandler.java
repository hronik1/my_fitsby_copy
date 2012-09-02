package dbhandlers;

import java.util.ArrayList;
import java.util.List;

import dbtables.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final String TAG = "DatabaseHandler";
	
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "fitsby";
    private static final String TABLE_USERS = "users";

    //Users Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    
    /**
     * 
     * @param context
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FIRST_NAME + " TEXT NOT NULL,"
                + KEY_LAST_NAME + " TEXT NOT NULL," + KEY_EMAIL + " TEXT UNIQUE NOT NULL," 
                + KEY_PASSWORD + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
	}

	/**
	 * adds user to TABLE_USERS
	 * @param user
	 */
	public void addUser(User user) {
		//TODO possibly more validation on user
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    ContentValues values = new ContentValues();
	    values.put(KEY_FIRST_NAME, user.getFirstName());
	    values.put(KEY_LAST_NAME, user.getLastName());
	    values.put(KEY_EMAIL, user.getEmail());
	    values.put(KEY_PASSWORD, user.getPassword());
	 
	    db.insert(TABLE_USERS, null, values);
	    db.close();
	}
	 
	/**
	 * returns User with the corresponding id
	 * @param id
	 * @return
	 */
	public User getUser(int id) {
	      SQLiteDatabase db = this.getReadableDatabase();
	      
	       Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
	    		   KEY_FIRST_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD }, KEY_ID + "=?",
	    		   new String[] { String.valueOf(id) }, null, null, null, null);
	       if (cursor != null) {
	    	   cursor.moveToFirst();
	       } else {
	    	   db.close();
	    	   return null;
	       }
	       
	       User user = new User(Integer.parseInt(cursor.getString(0)),
	    		   cursor.getString(1), cursor.getString(2),
	    		   cursor.getString(3), cursor.getString(4));
	       
	       cursor.close();
	       db.close();
	       
	       return user;
	}
	 
	/**
	 * returns all users stored in TABLE_USERS
	 * @return
	 */
	public List<User> getAllUsers() {
	    List<User> usersList = new ArrayList<User>();
	    String selectQuery = "SELECT * FROM " + TABLE_USERS;
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    if (cursor.moveToFirst()) {
	        do {
	            User user = new User();
	            user.setID(Integer.parseInt(cursor.getString(0)));
	            user.setFirstName(cursor.getString(1));
	            user.setLastName(cursor.getString(2));
	            user.setEmail(cursor.getString(3));
	            user.setPassword(cursor.getString(4));
	            //TODO additional validation on cursor position
	            usersList.add(user);
	        } while (cursor.moveToNext());
	    }
	 
	    cursor.close();
	    db.close();
	    
	    return usersList;
	}
	 
	/**
	 * returns the number of Users stored in TABLE_USERS
	 * @return
	 */
	public int getUsersCount() {
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        int count;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
        db.close();
        
        return count;
	}
	
	/**
	 * updates the User user in TABLE_USERS
	 * @param user
	 * @return
	 */
	public int updateUser(User user) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    ContentValues values = new ContentValues();
	    values.put(KEY_FIRST_NAME, user.getFirstName());
	    values.put(KEY_LAST_NAME, user.getLastName());
	    values.put(KEY_EMAIL, user.getEmail());
	    values.put(KEY_PASSWORD, user.getPassword());

	    return db.update(TABLE_USERS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(user.getID()) });
	}
	 
	/**
	 * deletes the User user from TABLE_USERS
	 * @param user
	 */
	public void deleteUser(User user) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_USERS, KEY_ID + "=?",
	            new String[] { String.valueOf(user.getID()) });
	    db.close();
	}
	
	/**
	 * Checks if email is unique within table
	 * @param email
	 * @return
	 */
	public boolean isEmailUnique(String email) {
		SQLiteDatabase db = this.getWritableDatabase();
		/*String query = "SELECT * FROM " + TABLE_USERS +
					" WHERE " + KEY_EMAIL + " = " + email;*/
		String[] columns = new String[] {KEY_ID, KEY_FIRST_NAME, KEY_LAST_NAME,
					KEY_EMAIL, KEY_PASSWORD};
		Cursor cursor = db.query(TABLE_USERS, columns, KEY_EMAIL+"=?",
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
	
	public boolean isEmailPasswordComboValid(String email, String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_USERS, new String[] {KEY_PASSWORD}, KEY_EMAIL+"=?",
					new String[] {email}, null, null, null);
		db.close();
		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			return false;
		} else if (cursor.getString(0).equals(password)){
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}
}
