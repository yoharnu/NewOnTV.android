package com.yoharnu.newontv.events;

import java.util.ArrayList;

public class LoadingEvent {

		static ArrayList<LoadingListener> listeners = new ArrayList<LoadingListener>();
		
		static public void addLoadingListener(LoadingListener toAdd){
			listeners.add(toAdd);
		}
		
		static public void done(){
			for(LoadingListener l : listeners) l.onDoneLoading();
			listeners.clear();
		}
}
