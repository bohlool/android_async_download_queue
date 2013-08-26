
package com.confiz.downloadqueue.interfaces;

import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQErrors;

public interface DQResponseListener {


	public void onDownloadStart(String key);


	public void onDownloadStart(String key, int totalSize);


	public void updateProgress(String key, int progress);


	public void onErrorOccurred(String key, DQErrors errorNo);


	public void onComplete(String key);


	public void updateDownloadingEstimates(String key, String details[]);


	public void onDataUpdated();


	public void updateStatusOf(DQRequest downloadingReques);


	public DQRequest getDownloadingRequester();


	public void updateFileExistanceStatusInDB(DQRequest dRequest);
}