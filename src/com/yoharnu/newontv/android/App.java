package com.yoharnu.newontv.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

import com.yoharnu.newontv.android.shows.Series;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {

	public static final String MIRRORPATH = "http://thetvdb.com";
	public static final String LANGUAGE = "en";
	public static final String API_KEY = "768A3A72ACDABC4A";
	protected static Context context;
	public static SharedPreferences preferences;
	public static LinkedList<Series> shows;

	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		preferences = context.getSharedPreferences(
				getString(R.string.pref_default), Context.MODE_PRIVATE);
		shows = new LinkedList<Series>();
		load();
	}

	public static Context getContext() {
		return context;
	}

	public static void add(String seriesId) {
		boolean present = false;
		for (int i = 0; i < shows.size(); i++) {
			if (shows.get(i).getSeriesId().equals(seriesId)) {
				present = true;
			}
		}
		if (!present) {
			new Series(seriesId, Series.ID);
		}
	}

	public static void save() {
		try {
			PrintStream out = new PrintStream(new File(context.getFilesDir(),
					"shows"));
			for (int i = 0; i < shows.size(); i++) {
				out.println(shows.get(i).getSeriesId());
			}
		} catch (FileNotFoundException e) {
		}
	}

	private static void load() {
		try {
			Scanner s = new Scanner(new File(context.getFilesDir(), "shows"));
			shows.clear();
			while (s.hasNextLine()) {
				new Series(s.nextLine(), Series.ID);
			}
			if (s != null) {
				s.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
}