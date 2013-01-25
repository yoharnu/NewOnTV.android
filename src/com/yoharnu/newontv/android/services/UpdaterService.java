package com.yoharnu.newontv.android.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.yoharnu.newontv.android.App;
import com.yoharnu.newontv.android.shows.Series;
import com.yoharnu.newontv.android.shows.XMLParser;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Process;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class UpdaterService extends Service {
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	// Handler that receives messages from the thread
	@SuppressLint("HandlerLeak")
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			LinkedList<String> temp = new LinkedList<String>();
			for (Series series : App.shows) {
				temp.add(series.getSeriesId());
			}

			System.out.println("Checking for updates 0/1");

			long lastUpdated = 0;
			try {
				lastUpdated = App.preferences.getLong("last-updated", 0);
			} catch (ClassCastException e) {
				App.preferences.edit().remove("last-updated").apply();
			}
			File tempFile = new File(UpdaterService.this.getCacheDir(),
					"update");
			if (lastUpdated == 0) {
				System.out.println("Checking for updates 1/1");
				System.out.println("Updating changed shows 0/" + temp.size());
				for (int i = 0; i < temp.size(); i++) {
					File seriesCache = new File(UpdaterService.this
							.getCacheDir().getAbsolutePath(), "series/"
							+ temp.get(i));
					seriesCache.delete();
					File episodeCache = new File(App.getContext().getCacheDir()
							.getAbsolutePath(), "episodes/" + temp.get(i));
					episodeCache.delete();

					try {
						FileUtils.copyURLToFile(new URL(
								"http://services.tvrage.com/myfeeds/showinfo.php?key="
										+ App.API_KEY + "&sid=" + temp.get(i)),
								seriesCache);
						FileUtils.copyURLToFile(new URL(
								"http://services.tvrage.com/myfeeds/episode_list.php?key="
										+ App.API_KEY + "&sid=" + temp.get(i)),
								episodeCache);
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.out.println("Updating changed shows " + (i + 1)
							+ "/" + temp.size());
				}
				App.preferences
						.edit()
						.putLong("last-updated",
								new GregorianCalendar().getTimeInMillis())
						.apply();
			} else {
				LinkedList<String> updated = new LinkedList<String>();
				try {
					lastUpdated = new GregorianCalendar().getTimeInMillis()
							- lastUpdated;
					if (lastUpdated / 3600000.0 >= 1) {
						lastUpdated = (long) Math
								.ceil(lastUpdated / 3600000.0 + 12);
						FileUtils.copyURLToFile(new URL(
								"http://services.tvrage.com/feeds/last_updates.php?hours="
										+ lastUpdated), tempFile);
						App.preferences
								.edit()
								.putLong(
										"last-updated",
										new GregorianCalendar()
												.getTimeInMillis()).apply();

						System.out.println("Checking for updates 1/1");

						Scanner s1 = new Scanner(tempFile);
						while (s1.hasNextLine()) {
							String line = s1.nextLine();
							if (XMLParser.getTag(line).equals("show")) {
								for (String id : temp) {
									if (id.equals(line.split("<id>")[1]
											.split("</id>")[0])) {
										updated.add(id);
									}
								}
							}
						}
						s1.close();
					}
					for (String id : temp) {
						if (!new File(UpdaterService.this.getCacheDir()
								.getAbsolutePath(), "series/" + id).exists()) {
							updated.add(id);
						}
					}

					System.out.println("Updating changed shows 0/"
							+ updated.size());

					for (int i = 0; i < updated.size(); i++) {
						File cache = new File(UpdaterService.this.getCacheDir()
								.getAbsolutePath(), "series/" + temp.get(i));
						try {
							FileUtils.copyURLToFile(new URL(
									"http://services.tvrage.com/feeds/full_show_info.php?sid="
											+ updated.get(i)), cache);

							System.out.println("Updating changed shows "
									+ (i + 1) + "/" + updated.size());
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							cache.delete();
							System.err.println("Failed to download: "
									+ updated.get(i));
							// e.printStackTrace();
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Failed to download update file");
					// e.printStackTrace();
				}
			}
			tempFile.delete();

			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
