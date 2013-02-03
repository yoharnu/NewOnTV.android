package com.yoharnu.newontv.shows;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.yoharnu.newontv.App;

public class Episode {
	private String season;
	private String episode;
	private String epName;
	private String summary;
	private String screencap;
	private String airTime;
	private GregorianCalendar airDate;
	private Series parent;

	public Episode(final String line, final String season, final Series parent) {
		this.season = season;
		this.parent = parent;
		airTime = parent.airTime;
		episode = "e" + line.split("<seasonnum>")[1].split("</seasonnum>")[0];
		epName = line.split("<title>")[1].split("</title>")[0]
				.replaceAll("&#39;", "'").replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&");
		String[] splits = line.split("<summary>");
		summary = "Not Available";
		if (splits.length > 1) {
			splits = splits[1].split("</summary>");
			if (splits.length > 0)
				summary = splits[0].replaceAll("&#39;", "'")
						.replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
		}
		screencap = null;
		splits = line.split("<screencap>");
		if (splits.length > 1) {
			splits = splits[1].split("</screencap>");
			if (splits.length > 0)
				screencap = splits[0];
		}
		splits = line.split("<alternatetime>");
		if (splits.length > 1) {
			splits = splits[1].split("</alternatetime>");
			if (splits.length > 0) {
				airTime = splits[0];
				if (airTime.matches("[0-9]{2}:[0-9]{2} pm")
						&& Integer
								.parseInt(airTime.split(" ")[0].split(":")[0]) < 12) {
					airTime = (Integer.parseInt(airTime.split(" ")[0]
							.split(":")[0]) + 12)
							+ ":"
							+ airTime.split(" ")[0].split(":")[1];
				} else if (airTime.matches("[0-9]{2}:[0-9]{2} am")
						|| Integer
								.parseInt(airTime.split(" ")[0].split(":")[0]) == 12) {
					airTime = airTime.split(" ")[0];
				}
			}
		}
		String temp = line.split("<airdate>")[1].split("</airdate>")[0];
		airDate = new GregorianCalendar();
		splits = temp.split("-");
		String[] splitParent = airTime.split(":");
		airDate.set(Integer.valueOf(splits[0]), Integer.valueOf(splits[1]) - 1,
				Integer.valueOf(splits[2]), Integer.valueOf(splitParent[0]),
				Integer.valueOf(splitParent[1]));
	}

	public TextView print(final Activity activity) {
		TextView returnVal = new TextView(App.getContext());
		String text = parent.getSeriesName() + "          "
				+ parent.getNetwork();
		returnVal.setText(text);
		returnVal.setPadding(returnVal.getPaddingLeft() + 50,
				returnVal.getPaddingTop(), returnVal.getPaddingRight(),
				returnVal.getPaddingBottom());

		returnVal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity, EpisodeDisplay.class);
				intent.putExtra("series", parent.getSeriesId());
				intent.putExtra("season", season);
				intent.putExtra("episode", episode);
				activity.startActivity(intent);
			}
		});
		returnVal.setTextSize(20);
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

	public String getScreencap() {
		return screencap;
	}

	public String getSummary() {
		return summary;
	}

}
