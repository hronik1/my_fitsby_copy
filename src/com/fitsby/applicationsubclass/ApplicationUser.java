package com.fitsby.applicationsubclass;

import constants.RememberMeConstants;
import constants.SingletonContext;
import dbtables.User;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ApplicationUser extends Application {
	
	private static final String PREF_KEY_USER = "prefKeyUser";
	private static final String PREF_KEY_ID = "prefKeyID";
	private static final String PREF_KEY_FIRST_NAME = "prefKeyFirstName";
	private static final String PREF_KEY_LAST_NAME = "prefKeyLastName";
	private static final String PREF_KEY_EMAIL = "prefKeyEmail";
	
	private User mUser;
	private SharedPreferences mSharedPreferences;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSharedPreferences = getSharedPreferences(PREF_KEY_USER,MODE_PRIVATE);
		SingletonContext.initializeContext(this);
		Log.d(this.toString(), "createdSingletonContext");
	}
	
	/**
	 * reads user data from sharedpreferences, if non existent
	 * @return the user
	 */
	public User getUser() {
		if (mUser == null) {
			int id = mSharedPreferences.getInt(PREF_KEY_ID, -1);
			String firstName = mSharedPreferences.getString(PREF_KEY_FIRST_NAME, "");
			String lastName = mSharedPreferences.getString(PREF_KEY_LAST_NAME, "");
			String email = mSharedPreferences.getString(PREF_KEY_EMAIL, "");
			mUser = new User(id, firstName, lastName, email);
		}
		return mUser;
	}
	
	/**
	 * sets user, writing to shared preferences as well
	 * @param user
	 */
	public void setUser(User user) {
		mUser = user;
		Editor e = mSharedPreferences.edit();
		e.putInt(PREF_KEY_ID, user.getID());
		e.putString(PREF_KEY_FIRST_NAME, user.getFirstName());
		e.putString(PREF_KEY_LAST_NAME, user.getLastName());
		e.putString(PREF_KEY_EMAIL, user.getEmail());
		e.commit();
	}
	
	private boolean isJoin;
	private boolean isCreate;
	
	//in create
	private int wager;
	private int duration;
	private int isPrivate;
	private String firstName;
	//in both
	private int userId;
	//in join
	private int leagueId;
	private int structure;
	
	
	
	public void setJoin() {
		isJoin = true;
		isCreate = false;
	}
	
	public void setCreate() {
		isCreate = true;
		isJoin = false;
	}
	
	public boolean getJoin() {
		return isJoin;
	}
	
	public boolean getCreate() {
		return isCreate;
	}

	public int getWager() {
		return wager;
	}

	public void setWager(int wager) {
		this.wager = wager;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getIsPrivate() {
		return isPrivate;
	}
	
	public String getIsPrivateString() {
		if (isPrivate == 0)
			return "false";
		else
			return "true";
	}

	public void setIsPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	
	public void setStructure(int structure) {
		this.structure = structure;
	}
	
	public int getStructure() {
		return structure;
	}
}
