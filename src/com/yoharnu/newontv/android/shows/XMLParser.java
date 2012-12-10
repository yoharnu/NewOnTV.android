package com.yoharnu.newontv.android.shows;

public class XMLParser {

	private XMLParser() {
	}

	public static String getTag(final String line) {
		String[] splits = line.split(">");
		if (splits.length > 0) {
			splits = splits[0].split("<");
			if (splits.length > 1) {
				return splits[1];
			}
		}
		return "";
	}

	public static String getData(final String line) {
		String[] splits = line.split(">");
		if (splits.length > 1) {
			splits = splits[1].split("<");
			if (splits.length > 0) {
				return splits[0];
			}
		}
		return "";
	}
}
