package com.yoharnu.newontv.android.shows;

import java.io.File;
import java.util.LinkedList;

import com.yoharnu.newontv.android.App;
import com.yoharnu.newontv.android.R;
import com.yoharnu.newontv.android.Settings;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;

public class DeleteShow extends Activity implements OnItemSelectedListener {
	Series selected = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_show);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Spinner spinner = (Spinner) findViewById(R.id.spins);
		LinkedList<String> showNames = new LinkedList<String>();
		for (int i = 0; i < App.shows.size(); i++) {
			showNames.add(App.shows.get(i).getSeriesName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				App.getContext(), android.R.layout.simple_dropdown_item_1line,
				showNames);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_delete_show, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_add_remove:
			startActivity(new Intent(this, EditShowsList.class));
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		String name = (String) parent.getItemAtPosition(pos);
		for (int i = 0; i < App.shows.size(); i++) {
			if (name.equals(App.shows.get(i).getSeriesName())) {
				selected = App.shows.get(i);
				break;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public void delete(View view) {
		new File(this.getCacheDir(), "/series/" + selected.getSeriesId())
				.delete();
		App.shows.remove(selected);
		App.preferences.edit().remove("db-shows-rev").commit();
		App.save();
		App.setChanged(true);
		this.finish();
	}

	public void cancel(View view) {
		this.finish();
	}

}
