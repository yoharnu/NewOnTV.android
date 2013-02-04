package com.yoharnu.newontv.shows;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;

import com.yoharnu.newontv.App;
import com.yoharnu.newontv.Settings;
import com.yoharnu.newontv.android.R;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ChooseSeries extends Activity {
	private Series s;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_series);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		// }

		// protected void onStart() {
		// super.onStart();
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Loading...");
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				s = new Series(App.preferences.getString("search", ""),
						Series.NAME);
				try {
					FileUtils.copyURLToFile(new URL(s.seriesUrl), new File(
							s.seriesFile));
				} catch (MalformedURLException e) {
					pd.dismiss();
					e.printStackTrace();
				} catch (IOException e) {
					pd.dismiss();
					e.printStackTrace();
				}

				File temp = new File(s.seriesFile);
				Series.parseSearch(temp);
				temp.delete();
				if (App.preferences.getBoolean("only_show_running", false)) {
					LinkedList<Series> tempOptions = new LinkedList<Series>();
					for (int i = 0; i < Series.options.size(); i++) {
						tempOptions.add(Series.options.get(i));
					}
					Series.options.clear();
					for (Series s : tempOptions) {
						if (s.getStatus() == null) {
							System.out.println(s.getSeriesName());
						}
						if (!s.getStatus().equals("Canceled/Ended")
								&& !s.getStatus().equals("Pilot Rejected")
								&& !s.getStatus().equals("Never Aired")) {
							Series.options.add(s);
						}
					}
				}

				final LinearLayout layout = (LinearLayout) ChooseSeries.this
						.findViewById(R.id.layout_choose_series);
				for (final Series series : Series.options) {
					final TextView name = new TextView(ChooseSeries.this);
					name.setText(series.getSeriesName());
					name.setTextSize(20);
					name.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Intent intent = new Intent(ChooseSeries.this,
									SeriesDisplay.class);
							intent.putExtra("series", series.seriesid);
							startActivity(intent);
						}
					});
					ChooseSeries.this.runOnUiThread(new Runnable() {
						public void run() {
							layout.addView(name);
						}
					});
				}
				pd.dismiss();
			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (File f : new File(this.getCacheDir(), "search").listFiles()) {
			f.delete();
		}
		new File(this.getCacheDir(), "search").delete();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_choose_series, menu);
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

}
