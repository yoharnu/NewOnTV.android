package com.yoharnu.newontv.android;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Settings extends Activity {
	protected boolean mExternalStorageAvailable = false;
	protected boolean mExternalStorageWriteable = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPermissions();
		if (!mExternalStorageAvailable) {
			Button button = (Button) findViewById(R.id.exportButton);
			button.setEnabled(false);
			button = (Button) findViewById(R.id.importButton);
			button.setEnabled(false);
		} else if (!mExternalStorageWriteable) {
			Button button = (Button) findViewById(R.id.exportButton);
			button.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void checkPermissions() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

	public void onImportClick(View view) {
		File dir = new File(getExternalFilesDir(null), "NewOnTV");
		if(!dir.exists()){
			dir.mkdir();
		}
		File src = new File(dir, "shows.txt");
		File dest = new File(getFilesDir(), "shows");
		Toast toast = null;
		try {
			FileUtils.copyFile(src, dest);
			toast = Toast.makeText(App.getContext(),
					"File successfully imported from " + src.getAbsolutePath(),
					Toast.LENGTH_LONG);
		} catch (IOException e) {
			e.printStackTrace();
			toast = Toast.makeText(App.getContext(),
					"Failed to import file",
					Toast.LENGTH_SHORT);
		}
		if (toast != null)
			toast.show();
	}

	public void onExportClick(View view) {
		File src = new File(getFilesDir(), "shows");
		File dir = new File(getExternalFilesDir(null), "NewOnTV");
		if(!dir.exists()){
			dir.mkdir();
		}
		File dest = new File(dir, "shows.txt");
		if(!dest.exists())
			try {
				dest.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		Toast toast = null;
		try {
			FileUtils.copyFile(src, dest);
			toast = Toast.makeText(App.getContext(),
					"File successfully exported to " + dest.getAbsolutePath(),
					Toast.LENGTH_LONG);
		} catch (IOException e) {
			e.printStackTrace();
			toast = Toast.makeText(App.getContext(),
					dest.getAbsolutePath(),
					Toast.LENGTH_SHORT);
		}
		if (toast != null)
			toast.show();
	}

}
