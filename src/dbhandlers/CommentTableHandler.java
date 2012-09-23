package dbhandlers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dbtables.Comment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommentTableHandler {

	private final static String TAG = "CommentTableHandler";
	public final static String TABLE = "comment";
	
	public final static String KEY_ID = "_id";
	public final static String KEY_MEMBER_FROM_ID = "from_id";
	public final static String KEY_LEAGUE_ID = "league_id";
	public final static String KEY_MESSAGE = "message";
	public final static String KEY_TIMESTAMP = "stamp";
	
	public final static String TYPE_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
	public final static String TYPE_MEMBER_FROM_ID = "INTEGER NOT NULL";
	public final static String TYPE_LEAGUE_ID = "INTEGER NOT NULL";
	public final static String TYPE_MESSAGE = "TEXT NOT NULL";
	public final static String TYPE_TIMESTAMP = "TIMESTAMP NOT NULL DEFAULT current_timestamp";
	public final static String FOREIGN_KEY_CONSTRAINT_LEAGUE =
			"FOREIGN KEY(" + KEY_LEAGUE_ID + ") REFERENCES "+ LeagueTableHandler.TABLE + "(" + LeagueTableHandler.KEY_ID + ")";
	public final static String FOREIGN_KEY_CONSTRAINT_FROM = "FOREIGN KEY(" + KEY_MEMBER_FROM_ID + 
			") REFERENCES "+ LeagueMemberTableHandler.TABLE + "(" + LeagueMemberTableHandler.KEY_ID + ")";


	public static final String CREATE_SQL = "CREATE TABLE " + TABLE + "("
			+ KEY_ID + " " + TYPE_ID + "," + KEY_MEMBER_FROM_ID + " " + TYPE_MEMBER_FROM_ID+ ","
			+ KEY_LEAGUE_ID + " " + TYPE_LEAGUE_ID + "," + KEY_MESSAGE + " " + TYPE_MESSAGE + "," 
			+ KEY_TIMESTAMP + " " + TYPE_TIMESTAMP + "," + FOREIGN_KEY_CONSTRAINT_FROM + ", " + FOREIGN_KEY_CONSTRAINT_FROM + ")";
	public static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE;
	
    private SQLiteDatabase db;
    
    /**
     * Constructor for LeagueTableHandler
     * @param db
     */
    public CommentTableHandler(SQLiteDatabase db) {
          this.db = db;
    }
    
	/**
	 * adds to the table
	 * @param comment
	 */
	public void addComment(Comment comment) {
		//TODO possibly more validation on use
	    ContentValues values = new ContentValues();
	    values.put(KEY_MEMBER_FROM_ID, comment.getMemberFromId());
	    values.put(KEY_LEAGUE_ID, comment.getLeagueId());
	    values.put(KEY_MESSAGE, comment.getMessage());
	    db.insert(TABLE, null, values);
	}
	
	/**
	 * returns comment with corresponding id
	 * @param id
	 * @return
	 */
	public Comment getComment(int id) {

		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_MEMBER_FROM_ID, KEY_LEAGUE_ID, KEY_MESSAGE, KEY_TIMESTAMP }, KEY_ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
		} else {
			return null;
		}

		Comment comment = new Comment(Integer.parseInt(cursor.getString(0)),
				Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
				cursor.getString(3), Timestamp.valueOf(cursor.getString(4)));

		cursor.close();
		return comment;
	}
	
	/**
	 * returns all comments withfromId
	 * @param memberFromId
	 * @return
	 */
	public List<Comment> getAllCommentByFromId(int memberFromId) {
		
		List<Comment> commentList = new ArrayList<Comment>();
		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_MEMBER_FROM_ID, KEY_LEAGUE_ID, KEY_MESSAGE, KEY_TIMESTAMP }, KEY_MEMBER_FROM_ID + "=?",
						new String[] { String.valueOf(memberFromId) }, null, null, null, KEY_TIMESTAMP + " DESC");

		if (cursor.moveToFirst()) {
			do {
				Comment comment = new Comment(Integer.parseInt(cursor.getString(0)),
						Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
						cursor.getString(3), Timestamp.valueOf(cursor.getString(4)));
				
				commentList.add(comment);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return commentList;
	}
	
	/**
	 * returns all comments for the various league, sorted in descending time
	 * @param leagueId
	 * @return
	 */
	public List<Comment> getAllCommentByLeagueId(int leagueId) {
		List<Comment> commentList = new ArrayList<Comment>();
		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_MEMBER_FROM_ID, KEY_LEAGUE_ID, KEY_MESSAGE, KEY_TIMESTAMP }, KEY_LEAGUE_ID + "=?",
						new String[] { String.valueOf(leagueId) }, null, null, null, KEY_TIMESTAMP + " DESC");

		if (cursor.moveToFirst()) {
			do {
				Comment comment = new Comment(Integer.parseInt(cursor.getString(0)),
						Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
						cursor.getString(3), Timestamp.valueOf(cursor.getString(4)));
				
				commentList.add(comment);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return commentList;
	}
	/**
	 * returns the number of comments
	 * @return
	 */
	public int getCommentCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        int count;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
        
        return count;
	}
	
	/**
	 * updates the Comment in the table
	 * @param comment
	 * @return
	 */
	public int updateComment(Comment comment) {
	    ContentValues values = new ContentValues();
	    values.put(KEY_MEMBER_FROM_ID, comment.getMemberFromId());
	    values.put(KEY_LEAGUE_ID, comment.getLeagueId());
	    values.put(KEY_MESSAGE, comment.getMessage());
	    values.put(KEY_TIMESTAMP,comment.getStamp().toString());

	    return db.update(TABLE, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(comment.get_id()) });
	}
	
	/**
	 * deletes the Comment comment from the table
	 * @param comment
	 */
	public void deleteComment(Comment comment) {
	    db.delete(TABLE, KEY_ID + "=?",
	            new String[] { String.valueOf(comment.get_id()) });
	}
}
