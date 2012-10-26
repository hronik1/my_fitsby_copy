package widgets;

import java.util.HashMap;


import com.example.fitsbypact.CheckInActivity;
import com.example.fitsbypact.GamesActivity;
import com.example.fitsbypact.LeagueLandingActivity;
import com.example.fitsbypact.MeActivity;
import com.example.fitsbypact.NewsfeedActivity;
import com.example.fitsbypact.R;

import dbtables.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

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
				goToMe();
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
		//TODO handle failure more robustly
    	try {
    		Intent intent = new Intent(parentActivity, GamesActivity.class);
    		parentActivity.startActivity(intent);
    	} catch (Exception e) {
    		Toast.makeText(parentActivity, e.toString(), Toast.LENGTH_LONG).show();
    	}
	}
	
	public void goToNewsfeed() {
		//TODO handle failure more robustly
		try {
			Intent intent = new Intent(parentActivity, NewsfeedActivity.class);
			parentActivity.startActivity(intent);
		} catch (Exception e) {
    		Toast.makeText(parentActivity, e.toString(), Toast.LENGTH_LONG).show();
    	}
	}
	
	public void goToCheckin() {
		//TODO handler failure more robustly
		try {
			Intent intent = new Intent(parentActivity, CheckInActivity.class);
			parentActivity.startActivity(intent);
		} catch (Exception e) {
    		Toast.makeText(parentActivity, e.toString(), Toast.LENGTH_LONG).show();
    	}
	}
	
	public void goToMe() {
		//TODO handle failure more robustly
		try {
			Intent intent = new Intent(parentActivity, MeActivity.class);
			parentActivity.startActivity(intent);
		} catch (Exception e) {
    		Toast.makeText(parentActivity, e.toString(), Toast.LENGTH_LONG).show();
    	}
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
			tv.setEnabled(false);
			tv.setTextColor(Color.RED);
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
}
