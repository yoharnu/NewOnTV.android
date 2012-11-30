package com.yoharnu.newontv.android.shows;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.yoharnu.newontv.android.App;

@SuppressWarnings("unused")
public class Series {
	public static final int NAME = 0;
	public static final int ID = 1;
	private String seriesid;
	private String language;
	private String seriesName;
	private String overview;
	private String firstAired;
	private String imdb_id;
	private String airTime;
	private String network;
	// private LinkedList<Episode> episodes;
	private boolean inCache;

	Series(String text, int mode) {
		if (mode == NAME) {

		} else if (mode == ID) {

		} else {
			throw new UnsupportedOperationException();
		}
	}
}