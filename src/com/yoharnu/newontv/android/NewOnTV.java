package com.yoharnu.newontv.android;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;

import com.yoharnu.newontv.android.shows.EditShowsList;
import com.yoharnu.newontv.android.shows.Episode;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewOnTV extends Activity {
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_on_tv);
		// setContentView(R.layout.loading_screen);
	}

	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Loading...");
		pd.setIndeterminate(true);
		pd.show();
		new Thread(new Runnable() {
			public void run() {

				final Button date = (Button) findViewById(R.id.changeDate);
				runOnUiThread(new Runnable() {
					public void run() {
						String today = "";
						if (App.today.get(GregorianCalendar.MONTH) < 9)
							today += "0";
						today += Integer.toString(App.today
								.get(GregorianCalendar.MONTH) + 1) + "-";

						if (App.today.get(GregorianCalendar.DATE) < 10)
							today += "0";
						today += Integer.toString(App.today
								.get(GregorianCalendar.DATE)) + "-";
						today += Integer.toString(App.today
								.get(GregorianCalendar.YEAR));
						date.setText(today);
					}
				});
				for (int i = 0; i < App.shows.size(); i++) {
					new Episode(App.shows.get(i));
				}
				final LinearLayout ll = (LinearLayout) findViewById(R.id.dynamicLayout);
				ll.post(new Runnable() {
					public void run() {
						ll.removeAllViews();
					}
				});
				boolean noShows = true;

				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("8:00 PM");

					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("8:00PM")
								|| App.shows.get(i).getTime().matches("20:00")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("8:30 PM");
					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("8:30PM")
								|| App.shows.get(i).getTime().matches("20:30")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("9:00 PM");
					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("9:00PM")
								|| App.shows.get(i).getTime().matches("21:00")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("9:30 PM");
					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("9:30PM")
								|| App.shows.get(i).getTime().matches("21:30")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					runOnUiThread(new Runnable() {
						public void run() {
							time.setVisibility(View.GONE);
							time.setText("10:00 PM");
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("10:00PM")
								|| App.shows.get(i).getTime().matches("22:00")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("10:30 PM");
					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("10:30PM")
								|| App.shows.get(i).getTime().matches("22:30")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("11:00 PM");
					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});
					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("11:00PM")
								|| App.shows.get(i).getTime().matches("23:00")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
				}
				{
					final TextView time = new TextView(App.getContext());
					time.setVisibility(View.GONE);
					time.setText("11:30 PM");
					runOnUiThread(new Runnable() {
						public void run() {
							ll.addView(time);
						}
					});

					for (int i = 0; i < App.shows.size(); i++)
						if (App.shows.get(i).getTime().replaceAll(" ", "")
								.equalsIgnoreCase("11:30PM")
								|| App.shows.get(i).getTime().matches("23:30")) {
							for (int j = 0; j < App.shows.get(i).episodes
									.size(); j++) {
								runOnUiThread(new Runnable() {
									public void run() {
										time.setVisibility(View.VISIBLE);
									}
								});
								noShows = false;
								try {
									if (App.shows.get(i).episodes.get(j).task != null)
										App.shows.get(i).episodes.get(j).task
												.join();
									final Episode temp = App.shows.get(i).episodes
											.get(j);
									runOnUiThread(new Runnable() {
										public void run() {
											ll.addView(temp.print());
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

				}
				if (noShows) {
					final TextView nothing = new TextView(App.getContext());
					nothing.setText("There is nothing new on tonight.");
					ll.post(new Runnable() {
						public void run() {
							ll.addView(nothing);
						}
					});
				}
				pd.dismiss();
			}
		}).start();
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
		case R.id.menu_export:
			File src = new File(getFilesDir(), "shows");
			File dir = new File(getExternalFilesDir(null), "NewOnTV");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File dest = new File(dir, "shows.txt");
			if (!dest.exists())
				try {
					dest.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			Toast toast = null;
			try {
				FileUtils.copyFile(src, dest);
				toast = Toast.makeText(
						App.getContext(),
						"File successfully exported to "
								+ dest.getAbsolutePath(), Toast.LENGTH_LONG);
			} catch (IOException e) {
				e.printStackTrace();
				toast = Toast.makeText(App.getContext(),
						dest.getAbsolutePath(), Toast.LENGTH_SHORT);
			}
			if (toast != null)
				toast.show();
			return true;
		case R.id.menu_import:
			dir = new File(getExternalFilesDir(null), "NewOnTV");
			if (!dir.exists()) {
				dir.mkdir();
			}
			src = new File(dir, "shows.txt");
			dest = new File(getFilesDir(), "shows");
			toast = null;
			try {
				FileUtils.copyFile(src, dest);
				toast = Toast.makeText(
						App.getContext(),
						"File successfully imported from "
								+ src.getAbsolutePath(), Toast.LENGTH_LONG);
			} catch (IOException e) {
				e.printStackTrace();
				toast = Toast.makeText(App.getContext(),
						"Failed to import file", Toast.LENGTH_SHORT);
			}
			if (toast != null)
				toast.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onForceRefresh(View view) {
		/*
		 * File dir = new File(getCacheDir(), "episodes"); dir.delete();
		 */
		refresh();
	}

	public void onChangeDateClick(View view) {
		DatePickerDialog dpd = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker dp, int year, int month,
							int day) {
						App.today = new GregorianCalendar(year, month, day);
					}
				}, App.today.get(GregorianCalendar.YEAR),
				App.today.get(GregorianCalendar.MONTH),
				App.today.get(GregorianCalendar.DATE));
		dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				refresh();
			}
		});
		dpd.show();
	}
}
