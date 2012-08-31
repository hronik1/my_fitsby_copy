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

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fitsby";
    private static final String TABLE_USERS = "users";

    //Users Table Columns names
    private static final String KEY_ID = "id";
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
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT," + KEY_EMAIL + " TEXT," 
                + KEY_PASSWORD + " TEXT" + ")";
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
	    String selectQuery = "SELECT  * FROM " + TABLE_USERS;
	 
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
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();
        
        return cursor.getCount();
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
	    db.delete(TABLE_USERS, KEY_ID + " = ?",
	            new String[] { String.valueOf(user.getID()) });
	    db.close();
	}
}
