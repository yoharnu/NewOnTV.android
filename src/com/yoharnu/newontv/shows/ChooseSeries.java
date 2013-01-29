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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ChooseSeries extends Activity {
	Series s;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_series);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (new File(App.getContext().getCacheDir().getAbsolutePath(), "search")
				.listFiles() != null) {
			for (File f : new File(App.getContext().getCacheDir()
					.getAbsolutePath(), "search").listFiles()) {
				f.delete();
			}
			new File(App.getContext().getCacheDir().getAbsolutePath(), "search")
					.delete();
		}
	}

	protected void onResume() {
		super.onResume();
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
					new AlertDialog.Builder(ChooseSeries.this).setMessage("");
				}

				File temp = new File(s.seriesFile);
				Series.parseSearch(temp);
				temp.delete();
				if (App.preferences.getBoolean("only_show_running", true)) {
					LinkedList<Series> tempOptions = new LinkedList<Series>();
					for (int i = 0; i < Series.options.size(); i++) {
						tempOptions.add(Series.options.get(i));
					}
					Series.options.clear();
					for (Series s : tempOptions) {
						if (s.getStatus() == null) {
							System.out.println(s.getSeriesName());
						}
						else if (!s.getStatus().equals("Canceled/Ended")
								&& !s.getStatus().equals("Pilot Rejected")
								&& !s.getStatus().equals("Never Aired")) {
							Series.options.add(s);
						}
					}
				}

				LinkedList<String> optionsNames = new LinkedList<String>();
				for (int i = 0; i < Series.options.size(); i++) {
					optionsNames.add(Series.options.get(i).getSeriesName());
				}
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						App.getContext(),
						android.R.layout.simple_dropdown_item_1line,
						optionsNames);
				runOnUiThread(new Runnable() {
					public void run() {
						Spinner spinner = (Spinner) findViewById(R.id.spins);
						spinner.setAdapter(adapter);
						spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int pos, long id) {
								String name = (String) parent
										.getItemAtPosition(pos);
								Series series = null;
								for (int i = 0; i < Series.options.size(); i++) {
									if (name.equals(Series.options.get(i)
											.getSeriesName())) {
										series = Series.options.get(i);
										break;
									}
								}
								TextView overview = (TextView) findViewById(R.id.spins_overview);
								overview.setText("First Aired: "
										+ series.getFirstAired()
										+ "\nSummary courtesy of tvrage.com: "
										+ series.getSummary());
								overview.setSingleLine(false);

								overview.setVisibility(View.VISIBLE);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
					}
				});
				pd.dismiss();
			}
		}).start();

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

	public void add(View view) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setTitle("Loading");
		pd.setMessage("Downloading. Please wait.");
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				Spinner spinner = (Spinner) findViewById(R.id.spins);
				TextView t = (TextView) spinner.getSelectedView();
				String text = t.getText().toString();
				for (int i = 0; i < Series.options.size(); i++) {
					if (text.equals(Series.options.get(i).getSeriesName())) {
						App.add(Series.options.get(i).getSeriesId());
					}
				}
				App.preferences.edit().remove("db-shows-rev").commit();
				App.save();
				App.setChanged(true);
				pd.dismiss();
				ChooseSeries.this.finish();
			}
		}).start();
	}

	public void cancel(View view) {
		this.finish();
	}

}
