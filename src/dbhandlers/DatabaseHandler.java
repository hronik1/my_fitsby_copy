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

	//TODO migrate to table handler
	private static final String TAG = "DatabaseHandler";
	
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "fitsby";
    
    private SQLiteDatabase writableDB;
	private UserTableHandler userTableHandler;
    private LeagueTableHandler leagueTableHandler;
    private LeagueMemberTableHandler leagueMemberTableHandler;

	/**
     * 
     * @param context
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
        writableDB = this.getWritableDatabase();
        userTableHandler = new UserTableHandler(writableDB);
        leagueTableHandler = new LeagueTableHandler(writableDB);
        leagueMemberTableHandler = new LeagueMemberTableHandler(writableDB);
    }
    
    /**
     * called when this database is first created, or upon upgrading
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTableHandler.CREATE_SQL);
        db.execSQL(LeagueTableHandler.CREATE_SQL);
        db.execSQL(LeagueMemberTableHandler.CREATE_SQL);
	}

	/**
	 * called when db is upgraded to new version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(LeagueMemberTableHandler.DROP_SQL);
		db.execSQL(LeagueTableHandler.DROP_SQL);
		db.execSQL(UserTableHandler.DROP_SQL);
        onCreate(db);
	}

    /**
	 * @return the userTableHandler
	 */
	public UserTableHandler getUserTableHandler() {
		return userTableHandler;
	}

	/**
	 * @return the leagueTableHandler
	 */
	public LeagueTableHandler getLeagueTableHandler() {
		return leagueTableHandler;
	}

	/**
	 * @return the leagueMemberTableHandler
	 */
	public LeagueMemberTableHandler getLeagueMemberTableHandler() {
		return leagueMemberTableHandler;
	}
	
}
