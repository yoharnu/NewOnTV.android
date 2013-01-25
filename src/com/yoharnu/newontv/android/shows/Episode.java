package com.yoharnu.newontv.android.shows;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import android.widget.TextView;

import com.yoharnu.newontv.android.App;

public class Episode {
	private String season;
	private String episode;
	private String epName;
	private String summary;
	private GregorianCalendar airDate;
	private Series parent;

	public Episode(final String line, final String season, final Series parent) {
		this.season = season;
		this.parent = parent;
		System.out.println(line);
		episode = "e" + line.split("<seasonnum>")[1].split("</seasonnum>")[0];
		epName = line.split("<title>")[1].split("</title>")[0]
				.replaceAll("&#39;", "'").replaceAll("&", "&amp;")
				.replaceAll("&quot;", "\"");
		String[] splits = line.split("<summary>");
		summary = "Not Available";
		if (splits.length > 1) {
			splits = splits[1].split("</summary");
			if (splits.length > 0)
				summary = splits[0].replaceAll("&#39;", "'")
						.replaceAll("&", "&amp;").replaceAll("&quot;", "\"");
		}
		String temp = line.split("<airdate>")[1].split("</airdate>")[0];
		airDate = new GregorianCalendar(parent.getTimeZone());
		splits = temp.split("-");
		String[] splitParent = parent.getAirTime().split(":");
		airDate.set(Integer.valueOf(splits[0]), Integer.valueOf(splits[1]) - 1,
				Integer.valueOf(splits[2]), Integer.valueOf(splitParent[0]),
				Integer.valueOf(splitParent[1]));
		// airDate.add(Calendar.MILLISECOND,
		// -1*airDate.get(Calendar.ZONE_OFFSET));
		// airDate.add(Calendar.MILLISECOND, -1*new
		// GregorianCalendar().get(Calendar.ZONE_OFFSET));
		// airDate.set(Calendar.ZONE_OFFSET, new
		// GregorianCalendar().get(Calendar.ZONE_OFFSET));
	}

	public TextView print() {
		TextView returnVal = new TextView(App.getContext());
		String text = parent.getSeriesName() + "          " + season + episode
				+ "\n          " + epName + "\n          "
				+ parent.getNetwork() + "\n          Summary: " + summary;
		returnVal.setText(text);
		returnVal.setPadding(returnVal.getPaddingLeft() + 50,
				returnVal.getPaddingTop(), returnVal.getPaddingRight(),
				returnVal.getPaddingBottom());
		return returnVal;
	}

	public String getSeason() {
		return season;
	}

	public String getEpisode() {
		return episode;
	}

	public String getEpName() {
		return epName;
	}

	public GregorianCalendar getAirDate() {
		return airDate;
	}

	public static LinkedList<Episode> sortByTime(LinkedList<Episode> list) {
		for (int i = 0; i < list.size(); i++) {
			Episode temp = list.get(i);
			int iHole = i;
			while (iHole > 0 && list.get(iHole - 1).airDate.after(temp.airDate)) {
				list.set(iHole, list.get(iHole - 1));
				iHole--;
			}
			list.set(iHole, temp);
		}
		return list;
	}
}
