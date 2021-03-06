package com.yoharnu.newontv.shows;

import com.yoharnu.newontv.App;
import com.yoharnu.newontv.Settings;
import com.yoharnu.newontv.android.R;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditShowsList extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_shows_list);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		EditText editable = (EditText) findViewById(R.id.addShowString);
		editable.setText("");

		update();
	}

	private void update() {
		App.sortByName();

		LinearLayout ll = (LinearLayout) findViewById(R.id.showsListLayout);
		ll.removeAllViews();

		TextView viewShows = new TextView(App.getContext());
		viewShows.setText(getString(R.string.viewShowsList));
		viewShows.setTextSize(20);
		ll.addView(viewShows);

		for (int i = 0; i < App.shows.size(); i++) {
			TextView temp = new TextView(App.getContext());
			final Series s=App.shows.get(i);
			temp.setText((i + 1) + ". " + s.getSeriesName());
			temp.setTextSize(20);
			temp.setClickable(true);
			temp.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(EditShowsList.this, SeriesDisplay.class);
					intent.putExtra("series", s.seriesid);
					startActivity(intent);
				}
			});
			ll.addView(temp);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_shows_list, menu);
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
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onAddShowSearchClick(View view) {
		String text = ((EditText) findViewById(R.id.addShowString)).getText()
				.toString();
		if (!text.equals("") && text != null) {
			SharedPreferences.Editor e = App.preferences.edit();
			e.putString("search", text);
			e.commit();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(findViewById(R.id.addShowString)
					.getWindowToken(), 0);

			startActivity(new Intent(App.getContext(), ChooseSeries.class));
		}
	}


}
