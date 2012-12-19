package com.yoharnu.newontv.android;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.dropbox.client2.session.AccessTokenPair;
import com.yoharnu.newontv.android.shows.EditShowsList;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Settings extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		addPreferencesFromResource(R.xml.preferences);

		Preference pref = findPreference("export");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Toast toast = null;
				if (App.isExternalStorageAvailable()
						&& App.isExternalStorageWriteable()) {
					File export = new File(Environment
							.getExternalStorageDirectory(), "NewOnTV/shows.txt");
					File current = new File(App.getContext().getFilesDir(),
							"shows");
					try {
						FileUtils.copyFile(current, export);
					} catch (IOException e) {
						e.printStackTrace();
						toast = Toast.makeText(App.getContext(),
								"Export failed. Please try again.",
								Toast.LENGTH_LONG);
						toast.show();
						return false;
					}
					toast = Toast.makeText(App.getContext(),
							"Successfully exported to " + export.getPath(),
							Toast.LENGTH_LONG);
					toast.show();
					return true;
				}
				toast = Toast.makeText(App.getContext(),
						"Export failed. Cannot write to SD card.",
						Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		});

		final Activity tempAct = this;
		Preference toImport = findPreference("import");
		toImport.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Toast toast = null;
				if (App.isExternalStorageAvailable()) {
					final File toImport = new File(Environment
							.getExternalStorageDirectory(), "NewOnTV/shows.txt");
					if (!toImport.exists()) {
						toast = Toast.makeText(App.getContext(),
								toImport.getPath() + " does not exist.",
								Toast.LENGTH_LONG);
						toast.show();
						return false;
					}
					final File current = new File(App.getContext()
							.getFilesDir(), "shows");
					final ProgressDialog pd = new ProgressDialog(tempAct);
					pd.setMessage("Loading...");
					pd.setIndeterminate(true);
					pd.setCancelable(false);
					pd.show();
					new Thread(new Runnable() {
						public void run() {
							try {
								FileUtils.copyFile(toImport, current);
								App.loadFromFile();
								runOnUiThread(new Runnable() {
									public void run() {
										Toast toast = Toast.makeText(
												App.getContext(),
												"Successfully imported from "
														+ toImport.getPath(),
												Toast.LENGTH_LONG);
										toast.show();
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
							pd.dismiss();
						}
					}).start();
					return true;
				}
				toast = Toast.makeText(App.getContext(),
						"Import failed. Cannot read SD card.",
						Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		});

		Preference clearList = findPreference("clear_list");
		clearList.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(tempAct)
						.setCancelable(true)
						.setTitle("Delete your entire show list?")
						.setMessage("This action is irreversible")
						.setNegativeButton("Cancel", null)
						.setPositiveButton("Delete All",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										new File(App.getContext()
												.getFilesDir(), "shows").delete();
										new File(App.getContext().getCacheDir(), "series").delete();
										new File(App.getContext().getCacheDir(), "episodes").delete();
										App.shows.clear();
										App.preferences.edit()
												.remove("db-shows-rev")
												.commit();
										App.save();
									}
								}).show();
				return true;
			}
		});

		final Preference activateDropbox = findPreference("activate_Dropbox");
		final Preference backupToDropbox = findPreference("backup_to_Dropbox");
		final Preference restoreFromDropbox = findPreference("restore_from_Dropbox");
		activateDropbox
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						if (App.mDBApi.getSession().isLinked()) {
							App.mDBApi.getSession().unlink();
							App.preferences.edit().remove("db-key")
									.remove("db-secret").remove("db-shows-rev")
									.commit();
							runOnUiThread(new Runnable() {
								public void run() {
									activateDropbox
											.setTitle("Link with Dropbox");
									backupToDropbox.setEnabled(false);
									restoreFromDropbox.setEnabled(false);
								}
							});
						} else {
							App.mDBApi.getSession().startAuthentication(
									Settings.this);
						}
						return true;
					}
				});
		backupToDropbox
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						App.saveToDropbox();
						return true;
					}
				});
		restoreFromDropbox
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				App.loadFromDropbox(Settings.this);
				return true;
			}
		});
	}

	protected void onResume() {
		super.onResume();

		if (App.mDBApi.getSession().authenticationSuccessful()) {
			try {
				// MANDATORY call to complete auth.
				// Sets the access token on the session
				App.mDBApi.getSession().finishAuthentication();

				AccessTokenPair tokens = App.mDBApi.getSession()
						.getAccessTokenPair();

				// Provide your own storeKeys to persist the access token pair
				// A typical way to store tokens is using SharedPreferences
				storeKeys(tokens.key, tokens.secret);
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}

		@SuppressWarnings("deprecation")
		Preference activateDropbox = findPreference("activate_Dropbox");
		@SuppressWarnings("deprecation")
		Preference backupToDropbox = findPreference("backup_to_Dropbox");
		@SuppressWarnings("deprecation")
		Preference restoreFromDropbox = findPreference("restore_from_Dropbox");
		if (App.mDBApi.getSession().isLinked()) {
			activateDropbox.setTitle("Unlink Dropbox");
			backupToDropbox.setEnabled(true);
			restoreFromDropbox.setEnabled(true);
		} else {
			activateDropbox.setTitle("Link with Dropbox");
			backupToDropbox.setEnabled(false);
			restoreFromDropbox.setEnabled(false);
		}
	}

	private void storeKeys(String key, String secret) {
		SharedPreferences.Editor e = App.preferences.edit();
		e.putString("db-key", key);
		e.putString("db-secret", secret);
		e.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
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
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
