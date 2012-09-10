package widgets;

import java.util.HashMap;

import android.R;
import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationBar extends LinearLayout{

	private TextView gamesTV;
	private TextView newsfeedTV;
	private TextView checkinTV;
	private TextView settingsTV;
	
	private HashMap<String, TextView> tvMap;
	
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
		
		settingsTV = (TextView)findViewById(R.id.navigation_textview_settings);
		settingsTV.setOnClickListener(new OnClickListener() {
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
		tvMap.put("settings", settingsTV);
	}
	
	public void goToGames() {
		//TODO implement
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

}
