package loaders;

import java.util.List;

import servercommunication.NewsfeedCommunication;

import dbhandlers.CommentTableHandler;
import dbhandlers.DatabaseHandler;
import dbhandlers.LeagueMemberTableHandler;
import dbhandlers.LeagueTableHandler;
import dbhandlers.UserTableHandler;
import dbtables.Comment;
import dbtables.League;
import dbtables.LeagueMember;
import dbtables.User;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;


public class NewsfeedCursorLoader extends AsyncTaskLoader<Cursor> {
	
	private DatabaseHandler mDBHandler;
	private LeagueTableHandler mLeagueTableHandler;
	private LeagueMemberTableHandler mLeagueMemberTableHandler;
	private UserTableHandler mUserTableHandler;
	private CommentTableHandler mCommentTableHandler;
	
	public final static String[] FROM_ARGS = { UserTableHandler.KEY_FIRST_NAME,
			UserTableHandler.KEY_LAST_NAME, CommentTableHandler.KEY_TIMESTAMP,
			CommentTableHandler.KEY_MESSAGE };
	
	private Cursor mCursor;
	private int mLeagueId;
	/**
	 * constructor
	 * @param context
	 */
	public NewsfeedCursorLoader(Context context, int leagueId) {
		super(context);
		mDBHandler = DatabaseHandler.getInstance(context);
		mLeagueTableHandler = mDBHandler.getLeagueTableHandler();
		mLeagueMemberTableHandler = mDBHandler.getLeagueMemberTableHandler();
		mCommentTableHandler = mDBHandler.getCommentTableHandler();
		mLeagueId = leagueId;
	}

/** start overridden AsyncTaskLoader methods **/
	
	@Override
	public Cursor loadInBackground() {
		return NewsfeedCommunication.getNewsfeed(mLeagueId);
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
