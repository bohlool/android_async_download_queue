package com.confiz.downloadqueue.interfaces;

import java.util.ArrayList;

import com.confiz.downloadqueue.model.DQDownloadingStatus;
import com.confiz.downloadqueue.model.DQRequest;

public interface DQRequestDBInterface {

	public boolean insertDownloadRequest(DQRequest dRequest, String userId);

	public ArrayList<DQRequest> getDownloadRequests(String userId);

	public boolean deleteDownloadRequest(DQRequest dRequest, String userId);

	public boolean deleteDownloadQueue(String userId);

	public DQDownloadingStatus getDownloadStatus(String key, String userId);

	public boolean isInDownloadQueue(String key, String userId);

	public boolean updateDownloadPositions(ArrayList<DQRequest> list, String userId);

	public boolean updateDownloadStatus(DQRequest dRequest, String userId);

	public boolean updateDownloadedSize(DQRequest dRequest, String userId);

	public boolean updateDownloadTotalSize(DQRequest dRequest, String userId);

	public boolean updateErrorDiscription(DQRequest dRequest, String userId);
}
