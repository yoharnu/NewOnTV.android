package com.yoharnu.newontv.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.yoharnu.newontv.android.shows.Series;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {

	public static final String MIRRORPATH = "http://thetvdb.com";
	public static final String LANGUAGE = "en";
	public static final String API_KEY = "768A3A72ACDABC4A";
	protected static Context context;
	public static SharedPreferences preferences;
	public static LinkedList<Series> shows;
	public static GregorianCalendar today;
	private static boolean mExternalStorageAvailable = false;
	private static boolean mExternalStorageWriteable = false;
	final static private String APP_KEY = "dik24sgjbrpvnqm";
	final static private String APP_SECRET = "ijw8q9xd90lo7h2";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	static DropboxAPI<AndroidAuthSession> mDBApi;

	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		shows = new LinkedList<Series>();
		today = new GregorianCalendar();
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys,
				ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		AccessTokenPair access = getStoredKeys();
		if (access != null)
			mDBApi.getSession().setAccessTokenPair(access);
		checkPermissions();
	}

	private AccessTokenPair getStoredKeys() {
		String key = preferences.getString("db-key", null);
		String secret = preferences.getString("db-secret", null);
		if (key == null || secret == null)
			return null;
		return new AccessTokenPair(key, secret);
	}

	public static Context getContext() {
		return context;
	}

	public static void add(String seriesId) {
		Series temp = new Series(seriesId, Series.ID);
		boolean present = false;
		for (int i = 0; i < shows.size(); i++) {
			if (shows.get(i).getSeriesId().equals(seriesId)) {
				present = true;
			}
		}
		if (!present) {
			shows.add(temp);
		}
	}

	public static void add(Series series) {
		boolean present = false;
		for (int i = 0; i < shows.size(); i++) {
			if (shows.get(i).getSeriesId().equals(series.getSeriesId())) {
				present = true;
				break;
			}
		}
		if (!present) {
			shows.add(series);
		}
	}

	public static void save() {
		saveToFile();
	}

	private static void saveToFile() {
		try {
			File temp = new File(context.getFilesDir(), "shows-temp");
			PrintStream out = new PrintStream(new File(context.getFilesDir(),
					"shows-temp"));
			for (int i = 0; i < shows.size(); i++) {
				out.println(shows.get(i).getSeriesId());
			}
			File save = new File(context.getFilesDir(), "shows");
			FileUtils.copyFile(temp, save);
			temp.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void saveToDropbox() {
		new Thread(new Runnable() {
			public void run() {
				FileInputStream inputStream = null;
				try {
					File local = new File(context.getFilesDir(), "shows");
					if (!local.exists())
						return;
					Entry existingEntry = mDBApi.metadata("/shows", 1, null,
							false, null);
					Log.i("Dropbox", "The file's rev is now: "
							+ existingEntry.rev);
					inputStream = new FileInputStream(local);
					if (existingEntry != null && !existingEntry.isDeleted) {
						mDBApi.delete("/shows.txt");
					}
					Entry newEntry = mDBApi.putFile("/shows.txt", inputStream,
							local.length(), null, null);
					Log.i("DbExampleLog", "The uploaded file's rev is: "
							+ newEntry.rev);
					SharedPreferences.Editor e = preferences.edit();
					e.putString("db-shows-rev", newEntry.rev);
					e.commit();
				} catch (DropboxUnlinkedException e) {
					// User has unlinked, ask them to link again here.
					Log.e("Dropbox", "User has unlinked.");
				} catch (DropboxException e) {
					Log.e("Dropbox", "Something went wrong while uploading.");
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					Log.e("Dropbox", "File not found.");
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}).start();
	}

	static void load(Activity activity) {
		loadFromFile(activity);
	}

	static void loadFromDropbox(final Activity activity) {
		final ProgressDialog pd = new ProgressDialog(activity);
		pd.setCancelable(false);
		pd.setMessage("Downloading from Dropbox...");
		pd.setIndeterminate(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				pd.show();
			}
		});
		new Thread(new Runnable() {
			public void run() {
				FileOutputStream outputStream = null;
				try {
					Entry existingEntry = mDBApi.metadata("/shows", 1, null,
							false, null);
					Log.i("Dropbox", "The file's rev is now: "
							+ existingEntry.rev);
					if (existingEntry.rev.equals(preferences.getString(
							"db-shows-rev", null))
							|| existingEntry == null
							|| existingEntry.isDeleted) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								pd.dismiss();
							}
						});
						return;
					}
					File local = new File(App.context.getFilesDir(), "shows");
					outputStream = new FileOutputStream(local);
					DropboxFileInfo info = mDBApi.getFile("/shows", null,
							outputStream, new ProgressListener() {
								@Override
								public void onProgress(final long bytes,
										final long total) {
									activity.runOnUiThread(new Runnable() {
										public void run() {
											pd.setMax((int) total);
											pd.setProgress((int) bytes);
											if (bytes == total) {
												pd.dismiss();
											}
										}
									});
								}
							});
					Log.i("Dropbox", "The file's rev is: "
							+ info.getMetadata().rev);
					SharedPreferences.Editor e = preferences.edit();
					e.putString("db-shows-rev", info.getMetadata().rev);
					e.commit();
				} catch (DropboxException e) {
					Log.e("Dropbox", "Something went wrong while downloading.");
				} catch (FileNotFoundException e) {
					Log.e("Dropbox", "File not found.");
				} finally {
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
						}
					}
					if (pd.isShowing())
						pd.dismiss();
					loadFromFile(activity);
				}
			}
		}).start();

	}

	protected static void loadFromFile(Activity activity) {
		final ProgressDialog pd = new ProgressDialog(activity);
		pd.setMessage("Loading...");
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					File shows = new File(context.getFilesDir(), "shows");
					Scanner s = new Scanner(shows);
					/*
					 * int max = 0; while (s.hasNextLine()) { s.nextLine();
					 * max++; } if (s != null) s.close(); pd.dismiss();
					 * pd.setIndeterminate(false);
					 * pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					 * pd.setProgress(0); pd.setMax(max); pd.show(); s = new
					 * Scanner(shows);
					 */
					App.shows.clear();
					int counter = 0;
					while (s.hasNextLine()) {
						add(s.nextLine());
						counter++;
						pd.setProgress(counter);
					}
					if (s != null) {
						s.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (pd.isShowing())
						pd.dismiss();
				}
			}
		}).start();
	}

	public static void sort() {
		for (int i = 0; i < App.shows.size(); i++) {
			Series temp = App.shows.get(i);
			int iHole = i;
			while (iHole > 0
					&& App.shows.get(iHole - 1).getSeriesName()
							.compareTo(temp.getSeriesName()) > 0) {
				App.shows.set(iHole, App.shows.get(iHole - 1));
				iHole--;
			}
			App.shows.set(iHole, temp);
		}
	}

	public static void cleanUpCache() {
		GregorianCalendar today = new GregorianCalendar();
		today.add(
				GregorianCalendar.DATE,
				-1
						* Integer.valueOf(App.preferences.getString(
								"past-days-cache", "1")));
		String todayString = Integer
				.toString(today.get(GregorianCalendar.YEAR));
		if (today.get(GregorianCalendar.MONTH) + 1 < 10)
			todayString += "0";
		todayString += Integer.toString(today.get(GregorianCalendar.MONTH) + 1);
		if (today.get(GregorianCalendar.DATE) < 10)
			todayString += "0";
		todayString += Integer.toString(today.get(GregorianCalendar.DATE));
		String newString = Integer.toString(App.today
				.get(GregorianCalendar.YEAR));
		if (App.today.get(GregorianCalendar.MONTH) + 1 < 10)
			newString += "0";
		newString += Integer
				.toString(App.today.get(GregorianCalendar.MONTH) + 1);
		if (App.today.get(GregorianCalendar.DATE) < 10)
			newString += "0";
		newString += Integer.toString(App.today.get(GregorianCalendar.DATE));
		File[] files = new File(getContext().getCacheDir(), "episodes")
				.listFiles();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().compareTo(todayString) < 0
						&& !files[i].getName().equals(newString)) {
					File[] temp = files[i].listFiles();
					for (int j = 0; j < temp.length; j++) {
						temp[j].delete();
					}
				}
			}
		today = new GregorianCalendar();
		today.add(GregorianCalendar.DATE, Integer.valueOf(App.preferences
				.getString("future-days-cache", "1")));
		todayString = Integer.toString(today.get(GregorianCalendar.YEAR));
		if (today.get(GregorianCalendar.MONTH) + 1 < 10)
			todayString += "0";
		todayString += Integer.toString(today.get(GregorianCalendar.MONTH) + 1);
		if (today.get(GregorianCalendar.DATE) < 10)
			todayString += "0";
		todayString += Integer.toString(today.get(GregorianCalendar.DATE));
		files = new File(getContext().getCacheDir(), "episodes").listFiles();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().compareTo(todayString) > 0
						&& !files[i].getName().equals(newString)) {
					File[] temp = files[i].listFiles();
					for (int j = 0; j < temp.length; j++) {
						temp[j].delete();
					}
				}
			}
	}

	protected void checkPermissions() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = false;
			mExternalStorageWriteable = false;
		}
	}

	public static boolean isExternalStorageWriteable() {
		return mExternalStorageWriteable;
	}

	public static boolean isExternalStorageAvailable() {
		return mExternalStorageAvailable;
	}
}