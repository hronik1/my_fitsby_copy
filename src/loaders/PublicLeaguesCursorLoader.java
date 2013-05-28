package loaders;

import servercommunication.LeagueCommunication;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import dbhandlers.LeagueTableHandler;

public class PublicLeaguesCursorLoader extends AsyncTaskLoader<Cursor> {

	private final static String TAG = "PublicLeagueCursorLoader";
	
	public final static String KEY_BITMAP = "bitmap";
	public final static String KEY_NUM_PLAYERS = "players";
	public final static String KEY_GOAL = "goal";
	
	public final static String[] FROM_ARGS = { KEY_BITMAP, LeagueTableHandler.KEY_ID, KEY_NUM_PLAYERS,
			LeagueTableHandler.KEY_WAGER, KEY_GOAL, LeagueTableHandler.KEY_DURATION };
	private Cursor mCursor;
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private int userId;
	
	/**
	 * default GameLeaderCursorLoader class
	 * @param context
	 */
	public PublicLeaguesCursorLoader(Context context, int userId) {
		super(context);
		mContext = context;
		this.userId = userId;
	}

	/** start overridden AsyncTaskLoader methods **/
	
	@Override
	public Cursor loadInBackground() {
		Log.d(TAG, "userID = " + userId);
		return LeagueCommunication.getPublicLeagues(userId);
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
        	try {
        		mProgressDialog = ProgressDialog.show(mContext, "",
        				"Games are loading...", true, true);
        	} catch (Exception e) { }
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
    public ProgressDialog getProgressDialog() {
    	return mProgressDialog;
    }
}
