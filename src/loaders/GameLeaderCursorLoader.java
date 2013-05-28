package loaders;

import servercommunication.GamesLeaderCommunication;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public class GameLeaderCursorLoader extends AsyncTaskLoader<Cursor> {

	private int mLeagueId;
	
	private Cursor mCursor;
	
	/**
	 * default GameLeaderCursorLoader class
	 * @param context
	 */
	public GameLeaderCursorLoader(Context context) {
		super(context);
	}

	/**
	 * 2 argument GameLeaderCursorLoader constructor
	 * @param context
	 * @param leagueId
	 */
	public GameLeaderCursorLoader(Context context, int leagueId) {
		this(context);
		mLeagueId = leagueId;
	}
	
	/**
	 * sets leagueid
	 * @param leagueId
	 */
	public void setLeagueId(int leagueId) {
		mLeagueId = leagueId;
	}

	/** asynctaskloader overriden methods **/
	
	@Override
	public Cursor loadInBackground() {
		return GamesLeaderCommunication.getGamesLeader(mLeagueId);
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
