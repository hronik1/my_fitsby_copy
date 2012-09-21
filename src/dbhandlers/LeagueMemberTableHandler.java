package dbhandlers;

import java.util.ArrayList;
import java.util.List;

import dbtables.League;
import dbtables.LeagueMember;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LeagueMemberTableHandler {

	private final static String TAG = "LeagueMemberTableHandler";
	public final static String TABLE = "leaguemember";

	public final static String KEY_ID = "_id";
	public final static String KEY_LEAGUE_ID = "league_id";
	public final static String KEY_USER_ID = "user_id";
	public final static String KEY_CHECKINS = "checkins";
	public final static String KEY_CHECKOUTS = "checkouts";
	
	public final static String TYPE_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
	public final static String TYPE_LEAGUE_ID = "INTEGER NOT NULL";
	public final static String TYPE_USER_ID = "INTEGER NOT NULL";
	public final static String TYPE_CHECKINS = "INTEGER NOT NULL";
	public final static String TYPE_CHECKOUTS = "INTEGER NOT NULL";
	public final static String FOREIGN_KEY_CONSTRAINT_LEAGUE =
				"FOREIGN KEY(" + KEY_LEAGUE_ID + ") REFERENCES "+ LeagueTableHandler.TABLE + "(" + LeagueTableHandler.KEY_ID + ")";
	public static final String FOREIGN_KEY_CONSTRAINT_USER = 
				"FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES "+ UserTableHandler.TABLE + "(" + UserTableHandler.KEY_ID + ")";
	

	public static final String CREATE_SQL = "CREATE TABLE " + TABLE + "("
			+ KEY_ID + " " + TYPE_ID + "," + KEY_LEAGUE_ID + " " + TYPE_LEAGUE_ID + ","
			+ KEY_USER_ID + " " + TYPE_USER_ID + "," 
			+ KEY_CHECKINS + " " + TYPE_CHECKINS + "," + KEY_CHECKOUTS + " " + TYPE_CHECKOUTS + ","
			+ FOREIGN_KEY_CONSTRAINT_LEAGUE + "," + FOREIGN_KEY_CONSTRAINT_USER + ")";
	public static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE;
	
	private SQLiteDatabase db;
	
	/**
	 * constructor for LeagueMemberTableHandler
	 * @param ctx
	 * @param db
	 */
	public LeagueMemberTableHandler(SQLiteDatabase db) {
		this.db = db;
	}
	
	/**
	 * adds leagueMember to the table
	 * @param leagueMember
	 */
	public void addLeagueMember(LeagueMember leagueMember) {
		//TODO possibly more validation on use
	    
	    ContentValues values = new ContentValues();
	    values.put(KEY_LEAGUE_ID, leagueMember.getLeagueId());
	    values.put(KEY_USER_ID, leagueMember.getUserId());
	    values.put(KEY_CHECKINS, leagueMember.getCheckins());
	    values.put(KEY_CHECKOUTS, leagueMember.getCheckouts());
	    db.insert(TABLE, null, values);
	}
	
	/**
	 * returns LeagueMember with corresponding id
	 * @param id
	 * @return
	 */
	public LeagueMember getLeagueMemberByID(int id) {
		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_LEAGUE_ID, KEY_USER_ID, KEY_CHECKINS, KEY_CHECKOUTS }, KEY_ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
		} else {
			return null;
		}

		LeagueMember leagueMember= new LeagueMember(Integer.parseInt(cursor.getString(0)),
				Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
				Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));

		cursor.close();
		return leagueMember;
	}
	
	/**
	 * queries the database for the leaguemember with the corresponding userId
	 * @param userId
	 * @return
	 */
	public LeagueMember getLeagueMemberByUserID(int userId) {
		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_LEAGUE_ID, KEY_USER_ID }, KEY_USER_ID + "=?",
						new String[] { String.valueOf(userId) }, null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
		} else {
			return null;
		}

		LeagueMember leagueMember= new LeagueMember(Integer.parseInt(cursor.getString(0)),
				Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)));

		cursor.close();
		return leagueMember;
	}
	
	/**
	 * returns all LeagueMembers with the corresponding LeagueId
	 * @param leagueId
	 * @return
	 */
	public List<LeagueMember> getAllLeagueMembersByLeagueId(int leagueId, String sortBy) {
		List<LeagueMember> leagueMembersList = new ArrayList<LeagueMember>();
		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
					KEY_LEAGUE_ID, KEY_USER_ID, KEY_CHECKINS, KEY_CHECKOUTS }, KEY_LEAGUE_ID + "=?",
					new String[] { String.valueOf(leagueId) }, null, null, null, sortBy);
		
	    if (cursor.moveToFirst()) {
	        do {
	            LeagueMember leagueMember = new LeagueMember();
	            leagueMember.setId(Integer.parseInt(cursor.getString(0)));
	            leagueMember.setLeagueId(Integer.parseInt(cursor.getString(1)));
	            leagueMember.setUserId(Integer.parseInt(cursor.getString(2)));
	            leagueMember.setCheckins(Integer.parseInt(cursor.getString(3)));
	            leagueMember.setCheckouts(Integer.parseInt(cursor.getString(4)));

	            //TODO additional validation on cursor position
	            leagueMembersList.add(leagueMember);
	        } while (cursor.moveToNext());
	    }
	 
	    cursor.close();
	    
	    return leagueMembersList;
	}
	
	/**
	 * returns all leagueMembers stored
	 * @return
	 */
	public List<LeagueMember> getAllLeagueMembers() {
	    List<LeagueMember> leagueMembersList = new ArrayList<LeagueMember>();
	    String selectQuery = "SELECT * FROM " + TABLE;
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    if (cursor.moveToFirst()) {
	        do {
	            LeagueMember leagueMember = new LeagueMember();
	            leagueMember.setId(Integer.parseInt(cursor.getString(0)));
	            leagueMember.setLeagueId(Integer.parseInt(cursor.getString(1)));
	            leagueMember.setUserId(Integer.parseInt(cursor.getString(2)));
	            leagueMember.setCheckins(Integer.parseInt(cursor.getString(3)));
	            leagueMember.setCheckouts(Integer.parseInt(cursor.getString(4)));
	            
	            //TODO additional validation on cursor position
	            leagueMembersList.add(leagueMember);
	        } while (cursor.moveToNext());
	    }
	 
	    cursor.close();
	    
	    return leagueMembersList;
	}
	
	/**
	 * returns the number of leagueMembers
	 * @return
	 */
	public int getLeagueMembersCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        int count;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
        
        return count;
	}
	
	/**
	 * returns the the number of user in the league specified by leagueId
	 * @param leagueId
	 * @return
	 */
	public int getLeagueMembersCountByLeagueId(int leagueId) {
        String countQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_LEAGUE_ID + " = " + "leagueId";
        int count;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
        
        return count;
	}
	
	/**
	 * updates the LeagueMember leagueMember in the table
	 * @param leagueMember
	 * @return
	 */
	public int updateLeagueMember(LeagueMember leagueMember) {
	    ContentValues values = new ContentValues();
	    values.put(KEY_LEAGUE_ID, leagueMember.getLeagueId());
	    values.put(KEY_USER_ID, leagueMember.getUserId());
	    values.put(KEY_CHECKINS, leagueMember.getCheckins());
	    values.put(KEY_CHECKOUTS, leagueMember.getCheckouts());
	    
	    return db.update(TABLE, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(leagueMember.getId()) });
	}
	
	/**
	 * deletes the League league from the table
	 * @param league
	 */
	public void deleteLeagueMember(LeagueMember leagueMember) {
	    db.delete(TABLE, KEY_ID + "=?",
	            new String[] { String.valueOf(leagueMember.getId()) });
	}
}
