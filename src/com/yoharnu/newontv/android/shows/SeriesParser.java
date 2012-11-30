package com.yoharnu.newontv.android.shows;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.yoharnu.newontv.android.App;

public class SeriesParser {
	private static XmlPullParser p;
	private static ArrayList<String> entries = null;
	
	static public ArrayList<String> parseSeries(String name){
		try {
			URL url = new URL(App.MIRRORPATH
					+ "/api/GetSeries.php?seriesname="
					+ name.replaceAll(" ", "%20") + "&language="
					+ App.LANGUAGE);
			p = Xml.newPullParser();
			p.setInput(new InputStreamReader(url.openStream()));
			p.next();

			while (p.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (p.getName().equals("Series")) {
					if(entries == null){
						entries = new ArrayList<String>();
					}
					entries.add(readSeries());
				} else {
					skip();
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String readSeries() throws XmlPullParserException,
			IOException {

		p.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, "Series");
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			// Starts by looking for the entry tag
			if (name.equals("SeriesName")) {
				return p.getText();
			} else {
				skip();
			}
		}
		throw new UnsupportedOperationException();
	}

	private static void skip() throws XmlPullParserException,
			IOException {
		if (p.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (p.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
