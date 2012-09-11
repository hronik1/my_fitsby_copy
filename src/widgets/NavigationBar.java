package widgets;

import java.util.HashMap;


import com.example.fitsbypact.GamesActivity;
import com.example.fitsbypact.LeagueLandingActivity;
import com.example.fitsbypact.R;

import dbtables.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationBar extends LinearLayout{

	private TextView gamesTV;
	private TextView newsfeedTV;
	private TextView checkinTV;
	private TextView meTV;
	
	private HashMap<String, TextView> tvMap;
	
	private Activity parentActivity;
	private int userID;
	
	public NavigationBar(Context context) {
		super(context);
	}
	
	public NavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		setOrientation(HORIZONTAL);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.navigation, this);
		initializeTabs();
		initializeMap();
	}
	
	public void initializeTabs() {
		gamesTV = (TextView)findViewById(R.id.navigation_textview_game);
		gamesTV.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToGames();
			}
		});

		newsfeedTV = (TextView)findViewById(R.id.navigation_textview_newsfeed);
		newsfeedTV.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToNewsfeed();
			}
		});
		
		checkinTV = (TextView)findViewById(R.id.navigation_textview_checkin);
		checkinTV.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToCheckin();
			}
		});
		
		meTV = (TextView)findViewById(R.id.navigation_textview_me);
		meTV.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToSettings();
			}
		});
	}

	public void initializeMap() {
		tvMap = new HashMap<String, TextView>();
		tvMap.put("games", gamesTV);
		tvMap.put("newsfeed", newsfeedTV);
		tvMap.put("checkin", checkinTV);
		tvMap.put("me", meTV);
	}
	
	public void goToGames() {
		//TODO implement
    	try {
    		Intent intent = new Intent(this.getContext(), GamesActivity.class);
    		intent.putExtra(User.ID_KEY, userID);
    		parentActivity.startActivity(intent);
    	} catch (Exception e) {
    		
    	}
	}
	
	public void goToNewsfeed() {
		//TODO implement
	}
	
	public void goToCheckin() {
		//TODO implement
	}
	
	public void goToSettings() {
		//TODO implement
	}

	/**
	 * method which allows outside methods to turn off a given element in the navigation bar
	 * @param key
	 */
	public void turnOffTV(String key) {
		if (key == null)
			return;
		TextView tv = tvMap.get(key);
		if (tv == null) {
			return;
		} else {
			tv.setClickable(false);
			tv.setFocusable(false);
			//TODO alter backgroundcolor
		}
			
	}

	/**
	 * 
	 * @param parentActivity
	 */
	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}
	
	/**
	 * 
	 * @param userID
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
}
