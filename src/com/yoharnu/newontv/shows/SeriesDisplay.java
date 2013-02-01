package com.yoharnu.newontv.shows;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.yoharnu.newontv.android.R;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class SeriesDisplay extends Activity {
	private Series series;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_series_display);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		series = new Series(savedInstanceState.getString("series"),
				Series.ID);
	}

	protected void onDestroy() {
		super.onDestroy();
		for (File f : new File(this.getCacheDir(), "images").listFiles()) {
			f.delete();
		}
		new File(this.getCacheDir(), "images").delete();
	}

	protected void onStart() {
		super.onStart();
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_series_display);
		final ImageView image = new ImageView(this);
		new Thread(new Runnable() {
			public void run() {
				final File imageFile = new File(
						SeriesDisplay.this.getCacheDir(), "images/"
								+ series.seriesid);
				try {
					if (!imageFile.exists()) {
						FileUtils.copyURLToFile(new URL(series.imageUrl),
								imageFile);
					}
					SeriesDisplay.this.runOnUiThread(new Runnable() {
						public void run() {
							image.setImageURI(Uri.fromFile(imageFile));
						}
					});
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		layout.addView(image);

		TextView title = new TextView(this);
		title.setText("Title: " + series.seriesName);
		layout.addView(title);

		TextView network = new TextView(this);
		network.setText("Network: " + series.network);
		layout.addView(network);

		TextView firstAired = new TextView(this);
		firstAired.setText("First Aired: " + series.firstAired);
		layout.addView(firstAired);

		TextView status = new TextView(this);
		status.setText("Status: " + series.status);
		layout.addView(status);

		TextView type = new TextView(this);
		type.setText("Type: " + series.classification);
		layout.addView(type);

		TextView summary = new TextView(this);
		summary.setText("Summary (courtesy of tvrage.com): " + series.summary);
		layout.addView(summary);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_series_display, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}

}
