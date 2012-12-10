package com.yoharnu.newontv.android;

import java.io.File;
import java.util.concurrent.ExecutionException;

import com.yoharnu.newontv.android.shows.EditShowsList;
import com.yoharnu.newontv.android.shows.Episode;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewOnTV extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_on_tv);

		if (App.shows.size() == 0) {
			Button forceRefresh = (Button) findViewById(R.id.forceRefresh);
			forceRefresh.setVisibility(Button.GONE);
			TextView noShows = (TextView) findViewById(R.id.noShows);
			noShows.setText("You currently have no shows. Add shows to see what's new tonight.");
			noShows.setVisibility(TextView.VISIBLE);
			Button addShow = (Button) findViewById(R.id.addShowButton);
			addShow.setVisibility(Button.VISIBLE);
		} else {
			refresh();
		}

	}

	private void refresh() {
		for (int i = 0; i < App.shows.size(); i++) {
			new Episode(App.shows.get(i));
		}
		LinearLayout ll = (LinearLayout) this.findViewById(R.id.dynamicLayout);
		ll.removeAllViews();
		boolean noShows = true;

		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("8:00 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("8:00PM")
						|| App.shows.get(i).getTime().matches("20:00")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("8:30 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("8:30PM")
						|| App.shows.get(i).getTime().matches("20:30")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("9:00 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("9:00PM")
						|| App.shows.get(i).getTime().matches("21:00")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("9:30 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("9:30PM")
						|| App.shows.get(i).getTime().matches("21:30")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("10:00 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("10:00PM")
						|| App.shows.get(i).getTime().matches("22:00")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("10:30 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("10:30PM")
						|| App.shows.get(i).getTime().matches("22:30")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("11:00 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("11:00PM")
						|| App.shows.get(i).getTime().matches("23:00")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		{
			TextView time = new TextView(App.getContext());
			time.setVisibility(View.GONE);
			time.setText("11:30 PM");
			ll.addView(time);
			for (int i = 0; i < App.shows.size(); i++)
				if (App.shows.get(i).getTime().replaceAll(" ", "")
						.equalsIgnoreCase("11:30PM")
						|| App.shows.get(i).getTime().matches("23:30")) {
					for (int j = 0; j < App.shows.get(i).episodes.size(); j++) {
						time.setVisibility(View.VISIBLE);
						noShows = false;
						try {
							if (App.shows.get(i).episodes.get(j).task != null)
								App.shows.get(i).episodes.get(j).task.get();
							ll.addView(App.shows.get(i).episodes.get(j).print());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
		}
		if (noShows) {
			TextView nothing = new TextView(App.getContext());
			nothing.setText("There is nothing new on tonight.");
			ll.addView(nothing);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_on_tv, menu);
		return true;
	}

	public void onNoShowsClick(View view) {
		startActivity(new Intent(this, EditShowsList.class));
	}

	public void onOptionsItemSelect(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_remove:
			startActivity(new Intent(this, EditShowsList.class));
			break;
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			break;
		}
	}

	public void onForceRefresh(View view) {
		File dir = new File(getCacheDir(), "episodes");
		dir.delete();
		refresh();
	}

}
