package loaders;

import java.util.List;

import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbtables.League;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

public class PublicLeaguesCursorLoader extends AsyncTaskLoader<Cursor> {

	private DatabaseHandler mDBHandler;
	private LeagueTableHandler mLeagueTableHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	
	public final static String KEY_NUM_PLAYERS = "players";
	public final static String KEY_POT = "pot";
	
	public final static String[] FROM_ARGS = { LeagueTableHandler.KEY_ID, KEY_NUM_PLAYERS,
			LeagueTableHandler.KEY_WAGER, LeagueTableHandler.KEY_DURATION, KEY_POT };
	private Cursor mCursor;
	
	/**
	 * default GameLeaderCursorLoader class
	 * @param context
	 */
	public PublicLeaguesCursorLoader(Context context) {
		super(context);
		mDBHandler = DatabaseHandler.getInstance(context);
		mLeagueTableHandler = mDBHandler.getLeagueTableHandler();
		mLeagueMemberTableHandler = mDBHandler.getLeagueMemberTableHandler();
	}

	/** start overridden AsyncTaskLoader methods **/
	
	@Override
	public Cursor loadInBackground() {
		List<League> publicLeaguesList = mLeagueTableHandler.getAllPublicLeagues();
		MatrixCursor cursor = new MatrixCursor(FROM_ARGS);
		for(League league: publicLeaguesList) {
			int leagueId = league.getId();
			int numPlayers = mLeagueMemberTableHandler.getLeagueMembersCountByLeagueId(leagueId);
			int wager = league.getWager();
			cursor.addRow(new Object[] {leagueId, numPlayers, wager, league.getDuration(), numPlayers*wager});
		}
		
		return cursor;
	}
		
    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        
        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }
    
    /**end overriden asynctaskloader methods **/
}
