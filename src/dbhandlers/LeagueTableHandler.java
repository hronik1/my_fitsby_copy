package dbhandlers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import dbhandlers.AbstractTableHandler.DatabaseHelper;
import dbtables.League;
import dbtables.User;

public class LeagueTableHandler {

	private final static String TAG = "LeagueTableHandler";
	public final static String TABLE = "league";
	
	public final static String KEY_ID = "_id";
	public final static String KEY_CREATOR_ID = "creator_id";
	public final static String KEY_IS_PRIVATE = "is_private";
	public final static String KEY_DURATION = "duration";
	public final static String KEY_WAGER = "wager";
	
	public static final String TYPE_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
	public static final String TYPE_CREATOR_ID = "INTEGER NOT NULL";
	public static final String TYPE_IS_PRIVATE = "INTEGER NOT NULL";
	public static final String TYPE_DURATION = "INTEGER NOT NULL";
	public static final String TYPE_WAGER = "INTEGER NOT NULL";
	public static final String FOREIGN_KEY_CONSTRAINT_CREATOR = "FOREIGN KEY(" + KEY_CREATOR_ID + 
				") REFERENCES " +  UserTableHandler.TABLE + "(" + UserTableHandler.KEY_ID + ")";
	
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE + "("
				+ KEY_ID + " " + TYPE_ID + "," + KEY_CREATOR_ID + " " + TYPE_CREATOR_ID + ","
				+ KEY_IS_PRIVATE + " " + TYPE_IS_PRIVATE + "," + KEY_DURATION + " " + TYPE_DURATION + "," 
				+ KEY_WAGER + " " + TYPE_WAGER + "," + FOREIGN_KEY_CONSTRAINT_CREATOR + ")";
	public static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE;
	
    private SQLiteDatabase db;
    
    /**
     * Constructor for LeagueTableHandler
     * @param db
     */
    public LeagueTableHandler(SQLiteDatabase db) {
          this.db = db;
    }
    
	/**
	 * adds league to the table
	 * @param league
	 */
//	public void addLeague(League league) {
//		//TODO possibly more validation on use
//	    
//	    ContentValues values = new ContentValues();
//	    values.put(KEY_CREATOR_ID, league.getCreatorId());
//	    values.put(KEY_IS_PRIVATE, league.isPrivate());
//	    values.put(KEY_DURATION, league.getDuration());
//	    values.put(KEY_WAGER, league.getWager());
//	 
//	    db.insert(TABLE, null, values);
//	}
	
	/**
	 * returns League with corresponding id
	 * @param id
	 * @return
	 */
	public League getLeague(int id) {

		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_CREATOR_ID, KEY_IS_PRIVATE, KEY_WAGER, KEY_DURATION }, KEY_ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
		} else {
			return null;
		}

		League league = new League(Integer.parseInt(cursor.getString(0)),
				Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
				Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));

		cursor.close();
		return league;
	}
	
	/**
	 * returns League with corresponding id
	 * @param id
	 * @return
	 */
	public League getLeagueByCreatorID(int id) {

		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_CREATOR_ID, KEY_IS_PRIVATE, KEY_WAGER, KEY_DURATION }, KEY_CREATOR_ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
		} else {
			return null;
		}

		League league= new League(Integer.parseInt(cursor.getString(0)),
				Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
				Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));

		cursor.close();
		return league;
	}
	
	/**
	 * returns all leagues store in leagues table
	 * @return
	 */
//	public List<League> getAllLeagues() {
//	    List<League> leaguesList = new ArrayList<League>();
//	    String selectQuery = "SELECT * FROM " + TABLE;
//	    Cursor cursor = db.rawQuery(selectQuery, null);
//	 
//	    if (cursor.moveToFirst()) {
//	        do {
//	            League league = new League();
//	            league.setId(Integer.parseInt(cursor.getString(0)));
//	            league.setCreatorId(Integer.parseInt(cursor.getString(1)));
//	            league.setPrivate(Integer.parseInt(cursor.getString(2)));
//	            league.setDuration(Integer.parseInt(cursor.getString(3)));
//	            league.setWager(Integer.parseInt(cursor.getString(4)));
//
//	            leaguesList.add(league);
//	        } while (cursor.moveToNext());
//	    }
//	 
//	    cursor.close();
//	    
//	    return leaguesList;
//	}
	
	/**
	 * returns all public leagues stored in leagues table
	 * @return
	 */
//	public List<League> getAllPublicLeagues() {
//	    List<League> leaguesList = new ArrayList<League>();
//		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
//					KEY_CREATOR_ID, KEY_IS_PRIVATE, KEY_DURATION, KEY_WAGER }, KEY_IS_PRIVATE + "=?",
//					new String[] { String.valueOf(0) }, null, null, null, null);
//	 
//	    if (cursor.moveToFirst()) {
//	        do {
//	            League league = new League();
//	            league.setId(Integer.parseInt(cursor.getString(0)));
//	            league.setCreatorId(Integer.parseInt(cursor.getString(1)));
//	            league.setPrivate(Integer.parseInt(cursor.getString(2)));
//	            league.setDuration(Integer.parseInt(cursor.getString(3)));
//	            league.setWager(Integer.parseInt(cursor.getString(4)));
//
//	            leaguesList.add(league);
//	        } while (cursor.moveToNext());
//	    }
//	 
//	    cursor.close();
//	    
//	    return leaguesList;
//	}
	
	/**
	 * returns the number of leagues
	 * @return
	 */
	public int getLeagueCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        int count;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
        
        return count;
	}
	
	/**
	 * updates the League league in the table
	 * @param league
	 * @return
	 */
//	public int updateLeague(League league) {
//	    ContentValues values = new ContentValues();
//	    values.put(KEY_CREATOR_ID, league.getCreatorId());
//	    values.put(KEY_IS_PRIVATE, league.isPrivate());
//	    values.put(KEY_DURATION, league.getDuration());
//	    values.put(KEY_WAGER, league.getWager());
//
//	    return db.update(TABLE, values, KEY_ID + " = ?",
//	            new String[] { String.valueOf(league.getId()) });
//	}
	
	/**
	 * deletes the League league from the table
	 * @param league
	 */
	public void deleteLeague(League league) {
	    db.delete(TABLE, KEY_ID + "=?",
	            new String[] { String.valueOf(league.getId()) });
	}
}
