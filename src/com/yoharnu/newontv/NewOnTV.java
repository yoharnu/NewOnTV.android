package com.yoharnu.newontv;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.yoharnu.newontv.android.R;
import com.yoharnu.newontv.events.LoadingEvent;
import com.yoharnu.newontv.events.LoadingListener;
import com.yoharnu.newontv.services.UpdaterService;
import com.yoharnu.newontv.shows.EditShowsList;
import com.yoharnu.newontv.shows.Episode;
import com.yoharnu.newontv.shows.Series;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewOnTV extends Activity {
	ProgressDialog pd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_on_tv);

		LoadingEvent.addLoadingListener(new LoadingListener() {
			@Override
			public void onDoneLoading() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						NewOnTV.this.startService(new Intent(NewOnTV.this,
								UpdaterService.class));
						refresh();
					}
				});
			}
		});
		try {
			App.load(NewOnTV.this);
		}
		catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		App.today = new GregorianCalendar();
	}

	protected void onDestroy() {
		super.onDestroy();
		if ( new File(this.getCacheDir(), "images").listFiles() != null )
			for ( File f : new File(this.getCacheDir(), "images").listFiles() ) {
				if ( f.isDirectory() && f.listFiles() != null )
					for ( File f2 : f.listFiles() )
						f2.delete();
				f.delete();
			}
		new File(this.getCacheDir(), "images").delete();
	}

	protected void onResume() {
		super.onResume();
		if ( !App.isLoading() ) {
			refresh();
		}
	}

	private void refresh() {
		{
			GregorianCalendar temp = new GregorianCalendar();
			if ( temp.get(GregorianCalendar.MONTH) == App.today
					.get(GregorianCalendar.MONTH)
					&& temp.get(GregorianCalendar.DATE) == App.today
							.get(GregorianCalendar.DATE)
					&& temp.get(GregorianCalendar.YEAR) == App.today
							.get(GregorianCalendar.YEAR) ) {
				Button today = (Button) findViewById(R.id.today);
				today.setEnabled(false);
			}
			else {
				Button today = (Button) findViewById(R.id.today);
				today.setEnabled(true);
			}
		}

		final Button date = (Button) findViewById(R.id.changeDate);
		String today = "";

		if ( App.today.get(GregorianCalendar.MONTH) < 9 )
			today += "0";
		today += Integer.toString(App.today.get(GregorianCalendar.MONTH) + 1)
				+ "-";

		if ( App.today.get(GregorianCalendar.DATE) < 10 )
			today += "0";
		today += Integer.toString(App.today.get(GregorianCalendar.DATE)) + "-";

		today += Integer.toString(App.today.get(GregorianCalendar.YEAR));

		date.setText(today);

		final LinearLayout ll = (LinearLayout) findViewById(R.id.dynamicLayout);
		ll.removeAllViews();

		LinkedList<Episode> tonight = new LinkedList<Episode>();

		for ( Series s : App.shows ) {
			for ( Episode e : s.getTodaysEpisodes() ) {
				tonight.add(e);
			}
		}

		tonight = Episode.sortByTime(tonight);
		String currTime = "";
		for ( Episode e : tonight ) {
			System.out.println(e.getSeriesId());
			String newTime = e.getAirDate().get(Calendar.HOUR) + ":";
			if ( e.getAirDate().get(Calendar.MINUTE) < 10 ) {
				newTime += "0";
			}
			newTime += e.getAirDate().get(Calendar.MINUTE);
			if ( e.getAirDate().get(Calendar.AM_PM) == Calendar.AM )
				newTime += " AM";
			else
				newTime += " PM";
			if ( !newTime.equals(currTime) ) {
				TextView time = new TextView(App.getContext());
				time.setText(newTime);
				time.setTextSize(20);
				ll.addView(time);
			}
			currTime = newTime;
			ll.addView(e.print(this));
		}

		if ( tonight.isEmpty() ) {
			TextView nothing = new TextView(App.getContext());
			nothing.setText("There is nothing new on tonight.");
			nothing.setTextSize(20);
			ll.addView(nothing);
		}
		App.setChanged(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_on_tv, menu);
		return true;
	}

	public void onTodayClick(View view) {
		App.today = new GregorianCalendar();
		refresh();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
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
		case R.id.menu_add_remove:
			startActivity(new Intent(this, EditShowsList.class));
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onChangeDateClick(View view) {
		DatePickerDialog dpd = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker dp, int year, int month,
							int day) {
						App.today = new GregorianCalendar(year, month, day);
					}
				}, App.today.get(GregorianCalendar.YEAR),
				App.today.get(GregorianCalendar.MONTH),
				App.today.get(GregorianCalendar.DATE));
		dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				Button today = (Button) findViewById(R.id.today);
				today.setEnabled(false);
				refresh();
			}
		});
		dpd.show();
	}
}
