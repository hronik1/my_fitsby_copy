package com.fitsby.applicationsubclass;

import me.kiip.sdk.Kiip;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import constants.SingletonContext;
import dbtables.User;

public class ApplicationUser extends Application {
	
	/**
	 * Used as key to store obtain shared preference for user info.
	 */
	private static final String PREF_KEY_USER = "prefKeyUser";
	/**
	 * Key for id within preference.
	 */
	private static final String PREF_KEY_ID = "prefKeyID";
	/**
	 * Key for first name in preference.
	 */
	private static final String PREF_KEY_FIRST_NAME = "prefKeyFirstName";
	/**
	 * Key for last name within preference.
	 */
	private static final String PREF_KEY_LAST_NAME = "prefKeyLastName";
	/**
	 * Key for email within preference.
	 */
	private static final String PREF_KEY_EMAIL = "prefKeyEmail";
	/**
	 * Kiip app key.
	 */
	private static final String MY_APP_KEY = "38c7edc90b99cfbd0880160ce03a9af2";
	/**
	 * Kiip app secret.
	 */
	private static final String MY_APP_SECRET = "733d6f4d3738825840d81d7ed0854740";
	/**
	 * Kiip moment id
	 */
	public static final String MY_MOMENT_ID = "checkin_moment";
	/**
	 * Default id, if present in preferences no user is authenticated.
	 */
	public static final int DEFAULT_ID = -1;
	
	private User mUser;
	private SharedPreferences mSharedPreferences;
	
	/**
	 * Callback for creation of the application, initializes kiip information,
	 * as well as shared prefs.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		mSharedPreferences = getSharedPreferences(PREF_KEY_USER, MODE_PRIVATE);
		SingletonContext.initializeContext(this);
	    Kiip kiip = Kiip.init(this, MY_APP_KEY, MY_APP_SECRET);
	    Kiip.setInstance(kiip);
	}
	
	/**
	 * Reads user data from sharedpreferences, if existent.
	 * @return the user, or null if none authenticated
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
	 * Sets user, writing to shared preferences as well.
	 * @param user
	 */
	public void setUser(User user) {
		mUser = user;
		if (user != null) {
			Editor e = mSharedPreferences.edit();
			e.putInt(PREF_KEY_ID, user.getID());
			e.putString(PREF_KEY_FIRST_NAME, user.getFirstName());
			e.putString(PREF_KEY_LAST_NAME, user.getLastName());
			e.putString(PREF_KEY_EMAIL, user.getEmail());
			e.commit();
		} else {
			Editor e = mSharedPreferences.edit();
			e.putInt(PREF_KEY_ID, DEFAULT_ID);
			e.putString(PREF_KEY_FIRST_NAME, "");
			e.putString(PREF_KEY_LAST_NAME, "");
			e.putString(PREF_KEY_EMAIL, "");
			e.commit();
		}
	}
	
	//TODO cleanup all of this mess below, so not even bothering to comment
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
	private int goal;
	
	
	
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
	
	public int getGoal() {
		return this.goal;
	}
	
	public void setGoal(int goal) {
		this.goal = goal;
	}
}
