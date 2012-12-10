package com.yoharnu.newontv.android.shows;

import com.yoharnu.newontv.android.App;
import com.yoharnu.newontv.android.R;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
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
		super.onStart();

		this.update();
	}

	private void update() {
		App.sort();

		LinearLayout ll = (LinearLayout) findViewById(R.id.showsListLayout);
		ll.removeAllViews();

		TextView viewShows = new TextView(App.getContext());
		viewShows.setText(getString(R.string.viewShowsList));
		ll.addView(viewShows);

		for (int i = 0; i < App.shows.size(); i++) {
			TextView temp = new TextView(App.getContext());
			temp.setText((i + 1) + ". " + App.shows.get(i).getSeriesName());
			ll.addView(temp);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_shows_list, menu);
		return true;
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

	public void onRefreshClick(View view) {
		this.update();
	}

	public void onDeleteShowClick(View view) {
		startActivity(new Intent(App.getContext(), DeleteShow.class));
	}
}
