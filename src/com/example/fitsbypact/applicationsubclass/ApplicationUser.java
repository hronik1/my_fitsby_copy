package com.example.fitsbypact.applicationsubclass;

import dbtables.User;
import android.app.Application;

public class ApplicationUser extends Application {
	private User mUser;
	
	/**
	 * 
	 * @return the user
	 */
	public User getUser() {
		return mUser;
	}
	
	/**
	 * sets user
	 * @param user
	 */
	public void setUser(User user) {
		mUser = user;
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
}
