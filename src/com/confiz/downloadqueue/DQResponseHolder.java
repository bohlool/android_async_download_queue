/*
 * Property : Confiz Solutions
 * Created by : Arslan Anwar
 * Updated by : Arslan Anwar
 */

package com.confiz.downloadqueue;

import java.util.ArrayList;

import com.confiz.downloadqueue.interfaces.DQResponseListener;
import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQErrors;


/**
 * The listener interface for receiving dataSetChange events.
 * The class that is interested in processing a dataSetChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addDataSetChangeListener<code> method. When
 * the dataSetChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see DataSetChangeEvent
 */
public class DQResponseHolder {


	private static DQResponseHolder instance;

	/** The listener list. */
	private ArrayList<DQResponseListener> listenerList = null;


	/**
	 * Instantiates a new data set change listener.
	 */
	private DQResponseHolder() {

		listenerList = new ArrayList<DQResponseListener>();
	}


	public static DQResponseHolder getInstance() {

		if (instance == null) {
			instance = new DQResponseHolder();
		}
		return instance;
	}


	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(DQResponseListener listener) {

		this.listenerList.add(listener);
	}


	/**
	 * Contain.
	 * 
	 * @param listener
	 *            the listener
	 * @return true, if successful
	 */
	public boolean contain(DQResponseListener listener) {

		return this.listenerList.contains(listener);
	}


	/**
	 * Replace.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void replace(DQResponseListener listener) {

		if (this.contain(listener)) {
			this.listenerList.remove(listener);
		}
		this.addListener(listener);
	}


	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(DQResponseListener listener) {

		this.listenerList.remove(listener);
	}


	/**
	 * Removes the all listener.
	 */
	public void removeAllListener() {

		this.listenerList.clear();
	}


	/**
	 * Destory object.
	 */
	public void destoryObject() {

		this.removeAllListener();
		this.listenerList = null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#onDownloadStart(java.lang.String)
	 */

	public void onDownloadStart(String key) {

		for (final DQResponseListener change : listenerList) {
			change.onDownloadStart(key);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#onDownloadStart(java.lang.String, int)
	 */

	public void onDownloadStart(String key, int totalSize) {

		for (final DQResponseListener change : listenerList) {
			change.onDownloadStart(key, totalSize);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#updateProgress(java.lang.String, int)
	 */

	public void updateProgress(String key, int progress) {

		for (final DQResponseListener change : listenerList) {
			change.updateProgress(key, progress);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#onErrorOccurred(java.lang.String,
	 * com.confiz.downloadqueue.utils.DQErrors)
	 */

	public void onErrorOccurred(String key, DQErrors errorNo) {

		for (final DQResponseListener change : listenerList) {
			change.onErrorOccurred(key, errorNo);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#onComplete(java.lang.String)
	 */

	public void onComplete(String key) {

		for (final DQResponseListener change : listenerList) {
			change.onComplete(key);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#updateDownloadingEstimates(java.lang.String,
	 * java.lang.String[])
	 */

	public void updateDownloadingEstimates(String key, String[] details) {

		for (final DQResponseListener change : listenerList) {
			change.updateDownloadingEstimates(key, details);
		}
	}


	/**
	 * On data updated.
	 */
	public void onDataUpdated() {

		for (final DQResponseListener change : listenerList) {
			change.onDataUpdated();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.confiz.downloadqueue.DQResponseListener#getDownloadingRequester()
	 */

	public DQRequest getDownloadingRequester() {

		return null;
	}


	/**
	 * @param downloadingRequest
	 */
	public void updateStatusOf(DQRequest downloadingRequest) {

		for (final DQResponseListener change : listenerList) {
			change.updateStatusOf(downloadingRequest);
		}
	}


	/**
	 * @param dRequest
	 */
	public void updateFileExistanceStatusInDB(DQRequest dRequest) {

		for (final DQResponseListener change : listenerList) {
			change.updateFileExistanceStatusInDB(dRequest);
		}
	}
}