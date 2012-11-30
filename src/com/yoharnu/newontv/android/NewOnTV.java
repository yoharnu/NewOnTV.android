package com.yoharnu.newontv.android;

import java.util.GregorianCalendar;

import com.yoharnu.newontv.android.shows.EditShowsList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewOnTV extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_on_tv);
		
		if(App.preferences.getInt("num_of_shows", 0) == 0){
			Button forceRefresh = (Button) findViewById(R.id.forceRefresh);
			forceRefresh.setVisibility(Button.GONE);
			TextView noShows = (TextView) findViewById(R.id.noShows);
			noShows.setText("You currently have no shows. Add shows to see what's new tonight.");
			noShows.setVisibility(TextView.VISIBLE);
			Button addShow = (Button) findViewById(R.id.addShowButton);
			addShow.setVisibility(Button.VISIBLE);
		}
		else{
			String dateUpdated = App.preferences.getString("dateUpdated", "00000000");
			GregorianCalendar cal = new GregorianCalendar();
			String today = Integer.toString(cal.get(GregorianCalendar.YEAR)) + Integer.toString(cal.get(GregorianCalendar.MONTH)+1) + Integer.toString(cal.get(GregorianCalendar.DATE));
			if(!today.equals(dateUpdated)){
				
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_on_tv, menu);
		return true;
	}
	
	public void onNoShowsClick(View view){
		startActivity(new Intent(this, EditShowsList.class));
	}

}
