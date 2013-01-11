package com.yoharnu.newontv.android.shows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;

import com.yoharnu.newontv.android.App;

public class Series {
	public static final int NAME = 0;
	public static final int ID = 1;
	private String seriesid;
	private String seriesName;
	private String firstAired;
	private String airTime = null;
	private String network = null;
	private String status;
	private String numSeasons;
	public LinkedList<Episode> episodes;
	private File cache = null;
	public static LinkedList<Series> options = null;
	public String url;
	public String file;
	private String classification;
	private TimeZone timeZone;

	public Series(final String text, int mode) {
		episodes = new LinkedList<Episode>();
		if (mode == NAME) {
			setupSeriesByName(text);
		} else if (mode == ID) {
			setupSeriesById(text);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public Series() {
	}

	private void setupSeriesByName(final String search) {
		url = "http://services.tvrage.com/feeds/full_search.php?show="
				+ search.replaceAll(" ", "%20");
		file = App.getContext().getCacheDir().getAbsolutePath() + "/search";
	}

	public static void parseSearch(File search) {
		options = new LinkedList<Series>();
		try {
			Scanner s = new Scanner(search);
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if ((XMLParser.getTag(line).equals("show") || XMLParser.getTag(
						line).equals("name"))
						&& s.hasNextLine()) {
					Series temp = new Series();
					while (!XMLParser.getTag(line).equals("/show")
							&& s.hasNextLine()) {
						line = s.nextLine();
						String tag = XMLParser.getTag(line);
						String data = XMLParser.getData(line);
						if (tag.equals("showid")) {
							temp.seriesid = data;
						} else if (tag.equals("name")) {
							temp.seriesName = data.replaceAll("&#39;", "'");
						} else if (tag.equals("started")) {
							temp.firstAired = data;
						} else if (tag.equals("status")) {
							temp.status = data;
						}
					}
					options.add(temp);
				}
			}
			if (s != null) {
				s.close();
			}
		} catch (FileNotFoundException e) {
		}

	}

	public void setupSeriesById(final String id) {
		this.seriesid = id;
		file = App.getContext().getCacheDir().getAbsolutePath() + "/series/"
				+ id;
		cache = new File(file);
		if (!cache.exists()) {
			try {
				url = "http://services.tvrage.com/feeds/full_show_info.php?sid="
						+ id;
				FileUtils.copyURLToFile(new URL(url), cache);
			} catch (IOException e) {
			}
		}
		parse();
	}

	public String getSeriesName() {
		return this.seriesName;
	}

	public String getSeriesId() {
		return this.seriesid;
	}

	public void parse() {
		try {
			Scanner s = new Scanner(cache);
			String currSeason = "";
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tag = XMLParser.getTag(line);
				String data = XMLParser.getData(line);
				if (data == null) {
					data = "Not Available";
				}
				if (tag.equals("name") || tag.equals("showname")) {
					seriesName = data.replaceAll("&#39;", "'");
				} else if (tag.equals("started") || tag.equals("startdate")) {
					firstAired = data;
				} else if (tag.equals("airtime")) {
					airTime = data;
				} else if (tag.contains("etwork") && !tag.contains("/")) {
					network = data;
				} else if (tag.equals("status")) {
					status = data;
				} else if (tag.equals("seasons")) {
					numSeasons = data;
				} else if (tag.equals("timezone")) {
					String temp = data;
					String[] splits = temp.split(" ");
					timeZone = TimeZone.getTimeZone(splits[0]);
				} else if (tag.equals("classification")) {
					classification = data;
				} else if (tag.equals("episode")) {
					episodes.add(new Episode(line, currSeason, this));
				} else if (tag.contains("Season no=")) {
					String temp = tag.split("\"")[1].split("\"")[0];
					currSeason = "s";
					if (Integer.parseInt(temp) < 10) {
						currSeason += "0";
					}
					currSeason += temp;
				} else if (tag.equals("Special")) {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		}
	}

	public LinkedList<Episode> getTodaysEpisodes() {
		LinkedList<Episode> returnVal = new LinkedList<Episode>();
		for (Episode e : episodes) {
			if (e.getAirDate().get(Calendar.YEAR) == App.today
					.get(Calendar.YEAR)
					&& e.getAirDate().get(Calendar.MONTH) == App.today
							.get(Calendar.MONTH)
					&& e.getAirDate().get(Calendar.DATE) == App.today
							.get(Calendar.DATE)) {
				returnVal.add(e);
			}
		}
		return returnVal;
	}

	public void redownload() {
		try {
			url = "http://services.tvrage.com/feeds/full_show_info.php?sid="
					+ seriesid;
			FileUtils.copyURLToFile(new URL(url), cache);
		} catch (IOException e) {
		}
	}

	public String getTime() {
		return airTime;
	}

	public String getNetwork() {
		return network;
	}

	public String getFirstAired() {
		return firstAired;
	}

	public String getStatus() {
		return status;
	}

	public String getNumSeasons() {
		return numSeasons;
	}

	public String getClassification() {
		return classification;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public String getAirTime() {
		return airTime;
	}

	public void setAirTime(String airTime) {
		this.airTime = airTime;
	}

	public File getCache() {
		return cache;
	}

}