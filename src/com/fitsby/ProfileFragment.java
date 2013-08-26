package com.fitsby;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitsby.R;
import com.fitsby.applicationsubclass.ApplicationUser;

import dbtables.User;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class ProfileFragment extends Fragment {

	/**
	 * Tag used for logcat.
	 */
	private final String TAG = getClass().getName();
	
	/**
	 * Constant used to define a default user.
	 */
	private final int DEFAULT_USER_ID = -1;
	
	/**
	 * Displays the number of games won.
	 */
	private TextView mGamesWonCountTv;
	/**
	 * Displays the number of games played.
	 */
	private TextView mGamesPlayedCountTv;
	/**
	 * Displays the time spent in the gym.
	 */
	private TextView mTimeCountTv;
	/**
	 * Displays the number of workouts completed.
	 */
	private TextView mWorkoutsCountTv;
	/**
	 * Displays a link to add this person as a friend
	 */
	private TextView mAddFriendTv;
	/**
	 * Displays the person's name.
	 */
	private TextView mNameTv;
	/**
	 * Displays the person's email.
	 */
	private TextView mEmailTv;
	/**
	 * Displays link to upload image.
	 */
	private TextView mUploadImageTv;
	
	/**
	 * Displays the person's profile picture.
	 */
	private ImageView mProfilePicIv;
	
	/**
	 * The id of the person.
	 */
	private int mProfileId = DEFAULT_USER_ID;
	
	/**
	 * The logged in user.
	 */
	private User mUser;

	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewer = inflater.inflate(R.layout.fragment_profile, container, false);
		
		mUser = ((ApplicationUser)getActivity().getApplication()).getUser();
		
		initializeTextViews(viewer);
		initializeImageViews(viewer);
		
		if (DEFAULT_USER_ID == mProfileId) {
			Toast.makeText(getActivity(), "Sorry but something went wrong", Toast.LENGTH_SHORT).show();
		} else if (mUser.getID() == mProfileId) {
			initializeOwnProfile();
		} else {
			initializeOthersProfile();
		}
		
		return viewer;
	}

	/**
	 * Initializes the view as someone elses profile, disabling the upload
	 * picture link, and enabling the display friendship status.
	 */
	private void initializeOthersProfile() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initializes the view as your own profile, disabling the friendship
	 * status and enabling the ability to submit photos.
	 */
	private void initializeOwnProfile() {
		// TODO Auto-generated method stub
		mProfilePicIv.setImageBitmap(mUser.getBitmap());
		mEmailTv.setText(mUser.getEmail());
		mNameTv.setText(mUser.getFirstName() + " " + mUser.getLastName());
		mAddFriendTv.setVisibility(View.GONE);
	}
	
	/**
	 * Sets the id of the users who should be displayed.
	 * 
	 * @param id	id of the user to be displayed
	 */
	public void setProfileId(int id) {
		mProfileId = id;
	}
	
	/**
	 * Connects the TextViews from the view, and adds listeners.
	 * 
	 * @param viewer	view which containes the TextViews
	 */
	private void initializeTextViews(View viewer) {
		mGamesWonCountTv = (TextView) viewer.findViewById(R.id.profile_games_won_count_tv);
		mGamesPlayedCountTv = (TextView) viewer.findViewById(R.id.profile_games_played_count_tv);
		mTimeCountTv = (TextView) viewer.findViewById(R.id.profile_time_spent_count_tv);
		mWorkoutsCountTv = (TextView) viewer.findViewById(R.id.profile_num_workouts_count_tv);
		mAddFriendTv = (TextView) viewer.findViewById(R.id.profile_user_add_friend_tv);
		mNameTv = (TextView) viewer.findViewById(R.id.profile_user_name_tv);
		mEmailTv = (TextView) viewer.findViewById(R.id.profile_user_email_tv);
		mUploadImageTv = (TextView) viewer.findViewById(R.id.profile_user_pic_upload_tv);
		mUploadImageTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				uploadImage();
			}
		});
	}
	
	/**
	 * Connects the ImageViews from the view.
	 * 
	 * @param viewer	view which contains the ImageViews
	 */
	private void initializeImageViews(View viewer) {
		mProfilePicIv = (ImageView) viewer.findViewById(R.id.profile_user_pic_iv);
	}


	/**
	 * Uploads a profile picture for the user.
	 */
	private void uploadImage() {
		//TODO implement this
		Toast.makeText(getActivity(), "To be implemented", Toast.LENGTH_SHORT).show();
	}
}
