package com.yoharnu.newontv.shows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;

import com.yoharnu.newontv.App;

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
	private String summary;
	public LinkedList<Episode> episodes;
	public static LinkedList<Series> options = null;
	private File seriesCache = null;
	public String seriesUrl;
	public String seriesFile;
	private File episodeCache = null;
	public String episodeUrl;
	public String episodeFile;
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
		seriesUrl = "http://services.tvrage.com/myfeeds/search.php?key="
				+ App.API_KEY + "&show=" + search.replaceAll(" ", "%20");
		seriesFile = App.getContext().getCacheDir().getAbsolutePath()
				+ "/search/search";
	}

	public static void parseSearch(File search) {
		options = new LinkedList<Series>();
		try {
			Scanner s = new Scanner(search);
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tag = XMLParser.getTag(line);
				String data = XMLParser.getData(line);

				if (tag.equals("showid")) {
					Series temp = new Series();
					temp.seriesFile = App.getContext().getCacheDir()
							.getAbsolutePath()
							+ "/search/" + data;
					temp.seriesCache = new File(temp.seriesFile);
					if (!temp.seriesCache.exists()) {
						try {
							temp.seriesUrl = "http://services.tvrage.com/myfeeds/showinfo.php?key="
									+ App.API_KEY + "&sid=" + data;
							FileUtils.copyURLToFile(new URL(temp.seriesUrl),
									temp.seriesCache);
						} catch (IOException e) {
						}
					}
					temp.parse();
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
		seriesFile = App.getContext().getCacheDir().getAbsolutePath()
				+ "/series/" + seriesid;
		seriesCache = new File(seriesFile);
		if (!seriesCache.exists()) {
			try {
				seriesUrl = "http://services.tvrage.com/myfeeds/showinfo.php?key="
						+ App.API_KEY + "&sid=" + seriesid;
				FileUtils.copyURLToFile(new URL(seriesUrl), seriesCache);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		parse();
		fetchEpisodeInfo();
	}

	private void fetchEpisodeInfo() {
		episodeFile = App.getContext().getCacheDir().getAbsolutePath()
				+ "/episodes/" + seriesid;
		episodeCache = new File(episodeFile);
		if (!episodeCache.exists()) {
			try {
				episodeUrl = "http://services.tvrage.com/myfeeds/episode_list.php?key="
						+ App.API_KEY + "&sid=" + seriesid;
				FileUtils.copyURLToFile(new URL(episodeUrl), episodeCache);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		Scanner s;
		try {
			s = new Scanner(episodeCache);
			String currSeason = "";
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tag = XMLParser.getTag(line);
				String data = XMLParser.getData(line);
				if (data == null) {
					data = "Not Available";
				}
				if (tag.equals("episode")) {
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
			e.printStackTrace();
		}
	}

	public String getSeriesName() {
		return this.seriesName;
	}

	public String getSeriesId() {
		return this.seriesid;
	}

	public void parse() {
		try {
			Scanner s = new Scanner(seriesCache);
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tag = XMLParser.getTag(line);
				String data = XMLParser.getData(line);
				if (data == null) {
					data = "Not Available";
				}
				if (tag.equals("name") || tag.equals("showname")) {
					seriesName = data.replaceAll("&#39;", "'")
							.replaceAll("&quot;", "\"")
							.replaceAll("&amp;", "&");
				} else if (tag.equals("started") || tag.equals("startdate")) {
					firstAired = data;
				} else if (tag.equals("summary")) {
					summary = data.replaceAll("&#39;", "'")
							.replaceAll("&quot;", "\"")
							.replaceAll("&amp;", "&");
				} else if (tag.equals("airtime")) {
					airTime = data;
				} else if (tag.equals("showid")) {
					seriesid = data;
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

	public void redownload() throws IOException {
		deleteCache();
		seriesFile = App.getContext().getCacheDir().getAbsolutePath()
				+ "/series/" + seriesid;
		seriesCache = new File(seriesFile);
		if (!seriesCache.exists()) {
			seriesUrl = "http://services.tvrage.com/myfeeds/showinfo.php?key="
					+ App.API_KEY + "&sid=" + seriesid;
			FileUtils.copyURLToFile(new URL(seriesUrl), seriesCache);
		}
		episodeFile = App.getContext().getCacheDir().getAbsolutePath()
				+ "/episodes/" + seriesid;
		episodeCache = new File(episodeFile);
		if (!episodeCache.exists()) {
			episodeUrl = "http://services.tvrage.com/myfeeds/episode_list.php?key="
					+ App.API_KEY + "&sid=" + seriesid;
			FileUtils.copyURLToFile(new URL(episodeUrl), episodeCache);
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
		return seriesCache;
	}

	public String getSummary() {
		return summary;
	}

	public void deleteCache() {
		seriesCache.delete();
		episodeCache.delete();
	}

}