package com.yoharnu.newontv.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import android.os.AsyncTask;

public class DownloadFilesTask extends AsyncTask<String, Void, Boolean> {

	@Override
	public Boolean doInBackground(final String... params) {
		try {
			FileUtils.copyURLToFile(new URL((String) params[0]), new File(
					(String) params[1]));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
