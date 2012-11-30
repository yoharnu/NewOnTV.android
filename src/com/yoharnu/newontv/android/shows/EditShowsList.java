package com.yoharnu.newontv.android.shows;

import com.yoharnu.newontv.android.App;
import com.yoharnu.newontv.android.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class EditShowsList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_shows_list);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_shows_list, menu);
		return true;
	}
	
	public void onAddShowSearchClick(View view){
		String text = ((EditText) findViewById(R.id.addShowString)).getText().toString();
		Series s = new Series(text, Series.NAME);
	}

}
