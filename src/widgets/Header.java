package widgets;

import com.example.fitsbypact.NewsfeedActivity;
import com.example.fitsbypact.SettingsActivity;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Header extends RelativeLayout{

	private TextView settingsTV;
	
	private Activity parentActivity;
	
	/**
	 * default Header constructor
	 * @param context
	 */
	public Header(Context context) {
		super(context);
	}
	
	/**
	 * 2 argument header constructor
	 * @param context
	 * @param attrs
	 */
	public Header(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.header, this);
		
		initializeTextViews();
	}
	
	/**
	 * sets the parentActivity
	 * @param parentActivity
	 */
	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}
	
	/**
	 * initialize the text views
	 */
	private void initializeTextViews() {
		settingsTV = (TextView)findViewById(R.id.header_settings);
		settingsTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToSettings();
			}
		});
	}
	
	/**
	 * go to the settings page
	 */
	private void goToSettings() {
		try {
			Intent intent = new Intent(this.getContext(), SettingsActivity.class);
			parentActivity.startActivity(intent);
		} catch (Exception e) {
    		Toast.makeText(parentActivity, e.toString(), Toast.LENGTH_LONG).show();
    	}
	}
	
	
}
