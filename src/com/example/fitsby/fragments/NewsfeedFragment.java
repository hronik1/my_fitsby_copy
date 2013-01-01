package com.example.fitsby.fragments;

import gravatar.Gravatar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import responses.StatusResponse;
import responses.UsersGamesResponse;
import servercommunication.LeagueCommunication;
import servercommunication.MyHttpClient;
import servercommunication.NewsfeedCommunication;

import loaders.NewsfeedCursorLoader;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.fitsby.applicationsubclass.ApplicationUser;
import com.example.fitsby.R;


import dbtables.Comment;
import dbtables.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class NewsfeedFragment extends SherlockFragment {

	private static final String TAG = "NewsfeedActivity";

	private Spinner gamesSpinner;
	private ListView newsfeedLV;
	private EditText commentET;
	private Button submitButton;
	private ImageView mImageView;
	private ArrayAdapter<String> spinnerDataAdapter;
	private List<String> spinnerData;
	
	private Cursor newsfeedCursor;
	private SimpleCursorAdapter mAdapter;
	private int[] toArgs = { R.id.list_item_newsfeed_first_name, 
			R.id.list_item_newsfeed_last_name, R.id.list_item_newsfeed_timestamp,
			R.id.list_item_newsfeed_message, R.id.list_item_id, R.id.list_item_newsfeed_imageview, R.id.list_item_bold, R.id.checkin_icon };
	
	private ApplicationUser mApplicationUser;
	
	private User user;
	private int spinnerPosition;
	
	private ProgressDialog mProgressDialog;
	
	private Activity parent;
	
	/**
	 * callback to add in the stats fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View viewer = (View) inflater.inflate(R.layout.activity_newsfeed, container, false);
	    Log.i(TAG, "onCreateView");

        initializeButtons(viewer);
        initializeListView(viewer);
        initializeEditText(viewer);
        initializeSpinner(viewer);
        initializeImageView(viewer);

        new SpinnerDataAsyncTask().execute();
        
	    return viewer;
	}
	
	/**
	 * callback for when this fragment is attached to a view
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");
		parent = activity;

		mApplicationUser = ((ApplicationUser)parent.getApplicationContext());
		user = mApplicationUser.getUser();
    
//		new SpinnerDataAsyncTask().execute();
	}
	
	/**
	 * initialize the EditText
	 */
	private void initializeEditText(View viewer) {
		commentET = (EditText)viewer.findViewById(R.id.newsfeed_et_comment);
	}
	
	/**
	 * initializes the buttons
	 */
	private void initializeButtons(View viewer) {
		submitButton = (Button)viewer.findViewById(R.id.newsfeed_button_submit);
		submitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				submit();
			}
		});
	}
	
	/**
	 * method which gets the data from the edit text and submits that
	 */
	private void submit() {
		if(spinnerData == null || spinnerData.size() == 0) {
			//TODO maybe do something more robust
			Toast toast = Toast.makeText(parent.getApplicationContext(), "There are no games", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		else {
			String gameId = UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition))+"";
			String comment = commentET.getText().toString();
			if (!comment.equals("")) {
				new AddCommentAsyncTask().execute(user.getID()+"", gameId, comment);
			} else {
				Toast toast = Toast.makeText(parent.getApplicationContext(), "Comment can't be blank", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
		
	}
	
	/**
	 * initialize the listview
	 */
	private void initializeListView(View viewer) {
		newsfeedLV = (ListView)viewer.findViewById(R.id.newsfeed_list_view);
		newsfeedLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// TODO add appropriate click functionality here
			}
		});
    	mAdapter = new SimpleCursorAdapter(parent, R.layout.list_item_newsfeed, newsfeedCursor,
    			NewsfeedCursorLoader.FROM_ARGS, toArgs, 0);
    	mAdapter.setViewBinder(new MyViewBinder());
    	newsfeedLV.setAdapter(mAdapter);
	}
	
	/**
	 * initialize the spinner
	 */
	private void initializeSpinner(View viewer) {
		gamesSpinner = (Spinner)viewer.findViewById(R.id.newsfeed_spinner);
		spinnerData =  new ArrayList<String>();

		spinnerDataAdapter = new ArrayAdapter<String>(parent,
				android.R.layout.simple_spinner_item, spinnerData);
		spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gamesSpinner.setAdapter(spinnerDataAdapter);
		gamesSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parentView, View view,
					int position, long id) {
				//TODO show game states of element clicked on
				//TODO change comments showing to be those of this league
				spinnerPosition = position;
				new CursorDataAsyncTask().execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				/** do nothing **/
			}
			
		});
	}
	
	private void initializeImageView(View viewer) {
		mImageView = (ImageView)viewer.findViewById(R.id.imageView1);
		
	}
	   /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class AddCommentAsyncTask extends AsyncTask<String, Void, StatusResponse> {
		protected void onPreExecute() {
			commentET.setText("");
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Submitting your comment...");
		}
		
        protected StatusResponse doInBackground(String... params) {
        	StatusResponse response = NewsfeedCommunication.addComment(params[0], params[1], params[2], Calendar.getInstance().getTime().toString());
        	return response;
        }

        protected void onPostExecute(StatusResponse response) {
        	mProgressDialog.dismiss();
        	if (response.wasSuccessful()) {
        		
        		//Toast toast = Toast.makeText(parent.getApplicationContext(), "Successfully posted your comment", Toast.LENGTH_LONG);
        		//toast.setGravity(Gravity.CENTER, 0, 0);
        		//toast.show();        		
        	} else {
        		Toast toast = Toast.makeText(parent.getApplicationContext(), "Comment successfully posted", Toast.LENGTH_LONG); //changed from 'comment fail' as the toast
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	}
        	new CursorDataAsyncTask().execute();
        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class SpinnerDataAsyncTask extends AsyncTask<String, Void, UsersGamesResponse> {
		
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Getting your games...");
		}
		
        protected UsersGamesResponse doInBackground(String... params) {
        	UsersGamesResponse response = LeagueCommunication.getUsersLeagues(user.getID());
        	return response;
        }

        protected void onPostExecute(UsersGamesResponse response) {
        	mProgressDialog.dismiss();
        	
        	if (response == null ) {
        		Toast toast = Toast.makeText(parent.getApplicationContext(), "There doesn't appear to be an internet connection at the moment", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else if (!response.wasSuccessful()){
        		Toast toast = Toast.makeText(parent.getApplicationContext(), "Error grabbing the data for your game", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        	} else {
        		//TODO switch to next page
        		spinnerData.addAll(response.getGames());
        		spinnerDataAdapter.notifyDataSetChanged();
        		//new CursorDataAsyncTask().execute();
        		new GravatarAsyncTask().execute(user.getEmail());
        	}
        }
    }
    
    /**
     * AsyncTask to find users games
     * @author brent
     *
     */
    private class CursorDataAsyncTask extends AsyncTask<String, Void, Cursor> {
    	
		protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(parent, "",
                    "Filling out your newsfeed...");
		}
		
        protected Cursor doInBackground(String... params) {
        	Cursor cursor = NewsfeedCommunication.getNewsfeed(UsersGamesResponse.StripGameIdFromSpinner(spinnerData.get(spinnerPosition)));
        	return cursor;
        }

		protected void onPostExecute(Cursor cursor) {
        	mProgressDialog.dismiss();
        	mAdapter.swapCursor(cursor);
        	mAdapter.notifyDataSetChanged();

        }
    }
    

    /**
     * AsyncTask to Register user
     * @author brent
     *
     */
    private class GravatarAsyncTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... params) {
        	String gravatarURL = Gravatar.getGravatar(params[0]);
        	return MyHttpClient.getBitmapFromURL(gravatarURL);
        }

        protected void onPostExecute(Bitmap response) {
        	if (response != null)
        		mImageView.setImageBitmap(response);
        }
    }
    
    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewId = view.getId();
            if(viewId == R.id.list_item_newsfeed_imageview) {
            	ImageView profilePic = (ImageView) view;
            	byte[] bytes = cursor.getBlob(columnIndex);
            	profilePic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            } else if (viewId == R.id.checkin_icon) {
            	String checkin = cursor.getString(columnIndex);
            	if (checkin.equals("true")) {
            		view.setVisibility(View.VISIBLE);
            		Log.d(TAG, "setting cup visible: " + checkin);
            	} else {
            		view.setVisibility(View.INVISIBLE);
            		Log.d(TAG, "setting cup invisible: " + checkin);
            	}
            } else {
            	TextView name = (TextView) view;
            	name.setText(cursor.getString(columnIndex));
//            	if (viewId == R.id.list_item_newsfeed_message && cursor.getString(cursor.getColumnIndex(Comment.KEY_BOLD)).equals("true"))
//            		name.setTypeface(Typeface.DEFAULT_BOLD);
            }
            
            return true;
        }
    }
}
