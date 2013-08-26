package com.confiz.downloadqueue.model;

import java.util.ArrayList;

public class DQQueue extends ArrayList<DQRequest> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8795974568256285345L;
	
	private static DQQueue downloadingQueue = null;

	private DQQueue() {
	}

	public static DQQueue getInstance() {
		if (downloadingQueue == null) {
			downloadingQueue = new DQQueue();
		}
		return downloadingQueue;
	}
}
