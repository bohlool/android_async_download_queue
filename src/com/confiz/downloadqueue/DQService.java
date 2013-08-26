
package com.confiz.downloadqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.confiz.downloadqueue.interfaces.DQResponseListener;
import com.confiz.downloadqueue.model.DQActions;
import com.confiz.downloadqueue.model.DQDownloadingStatus;
import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQDebugHelper;
import com.confiz.downloadqueue.utils.DQErrors;
import com.confiz.downloadqueue.utils.DQUtilityNetwork;

public class DQService extends Service {


	// private String TAG = "DQService";
	private Context mContext = null;

	private final String extraParameterName = "action";

	HashMap<String, DQFileDownloader> dqTheadHashmap = new HashMap<String, DQFileDownloader>();

	HashMap<String, DQRequest> dqKeysRequestsHashMap = new HashMap<String, DQRequest>();


	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}


	@Override
	public void onCreate() {

		super.onCreate();
		mContext = this;
	}


	@Override
	public void onDestroy() {

		Log.i("LTDDownloadingQueue", "Downloading service is destroying");
		super.onDestroy();
	}


	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		try {
			if (mContext == null) {
				mContext = this;
			}
			if (intent == null || intent.getExtras() == null || intent.hasExtra(extraParameterName) == false) {
				return;
			}
			DQActions action = DQActions.get(intent.getIntExtra(extraParameterName, 0));
			DQManager dqm = DQManager.getInstance(mContext);
			switch (action) {
				case START_DOWNLOAD:
					startDownload(dqm);
					break;
				case START_DOWNLOAD_FROM_PAUSE:
					startDownloadFromPause(dqm);
					break;
				case STOP_DQ:
					stopCompleteDownload();
					break;
				case UPDATE_DQ:
					break;
				case PAUSE_ITEM:
					makeItemPause(dqm);
					break;
				case DELETE_ITEM:
					deleteItem(dqm);
					break;
				case REMOVE_ITEM:
					stopCompleteDownload();
					if (dqm.isDestoryQueue()) {
						dqm.destroyManager();
					}
					break;
			}

		} catch (NullPointerException exception) {
			DQDebugHelper.printException(mContext, exception);
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(mContext, exception);
		}
	}


	public void startDownload(DQManager dqm) {

		// verifyDownloadingItems(dqm);
		if (dqm.canDownloadFurthurItems(mContext)) {
			while (dqm.canDownloadFurthurItems(mContext)) {
				DQRequest dr = dqm.getNextItemToDownload(mContext);
				if (DQUtilityNetwork.isNetworkAvailable(mContext) == false) {
					break;
				}

				DQFileDownloader atQueue = null;
				if (dqKeysRequestsHashMap.containsKey(dr.getKey()) == false) {
					atQueue = new DQFileDownloader(mContext, null, dr, downloadQueue);
					atQueue.start();
					dqKeysRequestsHashMap.put(dr.getKey(), dr);
					dqTheadHashmap.put(dr.getKey(), atQueue);
					dr.setStatus(DQDownloadingStatus.DOWNLOADING);
					dqm.updateDownloadStatus(dr, mContext);
					dqm.updateMangerAndNotify(dr.getKey(), dr);
				} else {
					dr.setStatus(DQDownloadingStatus.DOWNLOADING);
					dqm.updateDownloadStatus(dr, mContext);
					dqm.updateMangerAndNotify(dr.getKey(), dr);
				}
			}
		}
	}


	private void verifyDownloadingItems(DQManager dqm) {

		try {
			ArrayList<DQRequest> downloadingItems = dqm.getQueuedItemList();
			if (downloadingItems != null && downloadingItems.isEmpty() == false) {
				for (DQRequest temp : downloadingItems) {
					if (temp.getStatus() == DQDownloadingStatus.DOWNLOADING) {
						if (dqTheadHashmap.containsKey(temp.getKey()) == false) {
							DQDebugHelper.printData("Restarting this = " + temp.getKey());
							temp.setStatus(DQDownloadingStatus.WAITING);
							dqm.updateDownloadStatus(temp, mContext);
						}
					}
				}
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		}

	}


	public void deleteItem(DQManager dqm) {

		while (dqm.isThereAnyItemWithStatus(DQDownloadingStatus.DELETE_REQUEST)) {
			DQRequest dr = dqm.getItemWithStatus(DQDownloadingStatus.DELETE_REQUEST);
			String key = dr.getKey();
			if (dqKeysRequestsHashMap.containsKey(dr.getKey())) {
				DQFileDownloader temp = dqTheadHashmap.get(key);
				temp.shouldDeleteFile = true;
				temp.doWorkForCancel();
				temp.cancel(true);
				temp.updateRequestStatus();
				dqTheadHashmap.remove(key);
				dqKeysRequestsHashMap.remove(key);
			}
			dr.setStatus(DQDownloadingStatus.DELETED);
			dqm.updateDownloadStatus(dr, mContext);
			dr.setDownloading(false);
			dr.setSaved(false);
			dr.setPartialDownloaded(false);
			dqm.updateDBandQueue(mContext, key, DQDownloadingStatus.DELETED);
		}
	}


	public void makeItemPause(DQManager dqm) {

		while (dqm.isThereAnyItemWithStatus(DQDownloadingStatus.PAUSED_REQUEST)) {
			DQRequest dr = dqm.getItemWithStatus(DQDownloadingStatus.PAUSED_REQUEST);
			String key = dr.getKey();
			if (dqKeysRequestsHashMap.containsKey(dr.getKey())) {
				DQFileDownloader temp = dqTheadHashmap.get(key);
				temp.shouldDeleteFile = false;
				temp.doWorkForCancel();
				temp.cancel(true);
				temp.updateRequestStatus();
				dqTheadHashmap.remove(key);
				dqKeysRequestsHashMap.remove(key);
				dr.setDataEstimations(null);
				dr.setTimeEstimations(null);
			}
			dr.setStatus(DQDownloadingStatus.PAUSED);
			dqm.updateDownloadStatus(dr, mContext);
			dqm.updateDBandQueue(mContext, key, DQDownloadingStatus.PAUSED);
		}
		if (dqm.isDestoryQueue()) {
			dqm.destroyManager();
		}
	}


	public void startDownloadFromPause(DQManager dqm) {

		if (dqm.isDownloadingLimitAvailable(mContext) == true) {
			DQRequest dr = dqm.getPausedDownloadQequest(mContext);
			if (DQUtilityNetwork.isNetworkAvailable(mContext) == false) {
				return;
			}
			if (dr == null) {
				return;
			}
			DQFileDownloader atQueue = null;
			if (dqKeysRequestsHashMap.containsKey(dr.getKey()) == false) {
				atQueue = new DQFileDownloader(mContext, null, dr, downloadQueue);
				atQueue.start();
				dqKeysRequestsHashMap.put(dr.getKey(), dr);
				dqTheadHashmap.put(dr.getKey(), atQueue);
				dr.setStatus(DQDownloadingStatus.DOWNLOADING);
				dqm.updateDownloadStatus(dr, mContext);
				dqm.updateMangerAndNotify(dr.getKey(), dr);
			} else {
				dr.setStatus(DQDownloadingStatus.DOWNLOADING);
				dqm.updateDownloadStatus(dr, mContext);
				dqm.updateMangerAndNotify(dr.getKey(), dr);
			}
		}
	}


	public void stopCompleteDownload() {

		Collection<DQFileDownloader> cAsyncTasks = dqTheadHashmap.values();
		for (DQFileDownloader asyncFileDownloaderForQueue : cAsyncTasks) {
			asyncFileDownloaderForQueue.cancel(true);
			asyncFileDownloaderForQueue.doWorkForCancel();
		}
	}

	DQResponseListener downloadQueue = new DQResponseListener() {


		@Override
		public void updateProgress(String key, int progress) {

			if (DQResponseHolder.getInstance() != null) {
				DQResponseHolder.getInstance().updateProgress(key, progress);
			}
			DQManager manager = DQManager.getInstance(mContext);
			if (manager != null) {
				DQRequest item = manager.getItems(key);
				if (item != null) {
					item.setProgress(progress);
				}
			}
		}


		@Override
		public void updateDownloadingEstimates(String key, String[] details) {

			if (DQResponseHolder.getInstance() != null) {
				DQResponseHolder.getInstance().updateDownloadingEstimates(key, details);
			}

			DQManager manager = DQManager.getInstance(mContext);
			if (manager != null) {
				DQRequest item = manager.getItems(key);
				if (item != null) {
					item.setEstimates(details);
				}
			}
		}


		@Override
		public void onErrorOccurred(String key, DQErrors errorNo) {

			if (dqKeysRequestsHashMap != null) {
				if (dqKeysRequestsHashMap.containsKey(key)) {
					dqKeysRequestsHashMap.remove(key);
				}
				if (dqTheadHashmap.containsKey(key)) {
					dqTheadHashmap.remove(key);
				}
			}

			try {
				// DQRequest itemToBeDell = DQManager.getInstance(mContext).getItems(key)
				// .getRequestedObject();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			DQManager manager = DQManager.getInstance(mContext);
			manager.errorOccured(mContext, key, errorNo);
			if (DQUtilityNetwork.isNetworkAvailable(mContext) == false) {
				if (manager.isAnyDownloadingInProgressPending() == false) {
					// manager.destroyManger();
					stopSelf();
				}
			} else if (DQUtilityNetwork.isNetworkAvailable(mContext) == true && manager.canDownloadFurthurItems(mContext) == false) {
				// manager.destroyManger();
				stopSelf();
			}
			if (DQResponseHolder.getInstance() != null) {
				DQResponseHolder.getInstance().onErrorOccurred(key, errorNo);
			}
		}


		@Override
		public void onDownloadStart(String key, int totalSize) {

			if (DQResponseHolder.getInstance() != null) {
				DQResponseHolder.getInstance().onDownloadStart(key, totalSize);
			}
			DQManager.getInstance(mContext).updateMangerAndNotify(key);
		}


		@Override
		public void onDownloadStart(String key) {

			if (DQResponseHolder.getInstance() != null) {
				DQResponseHolder.getInstance().onDownloadStart(key);
			}
			DQManager.getInstance(mContext).updateMangerAndNotify(key);
		}


		@Override
		public void onComplete(String key) {

			DQManager manager = DQManager.getInstance(mContext);
			DQRequest testRequest = manager.getItems(key);
			if (testRequest != null) {
				testRequest.setStatus(DQDownloadingStatus.COMPLETED);
				manager.updateDownloadStatus(testRequest, mContext);
			}

			manager.updateDBandQueue(mContext, key, DQDownloadingStatus.COMPLETED);
			if (DQUtilityNetwork.isNetworkAvailable(mContext) == false) {
				if (manager.isAnyDownloadingInProgressPending() == false) {
					// manager.destroyManger();
					stopSelf();
				}
			} else if (DQUtilityNetwork.isNetworkAvailable(mContext) == true && manager.canDownloadFurthurItems(mContext) == false) {
				// manager.destroyManger();
				stopSelf();
			}
			if (dqTheadHashmap.containsKey(key)) {
				dqTheadHashmap.remove(key);
			}
			if (DQResponseHolder.getInstance() != null) {
				DQResponseHolder.getInstance().onComplete(key);
			}
			if (dqKeysRequestsHashMap != null) {
				if (dqKeysRequestsHashMap.containsKey(key)) {
					DQRequest temp = dqKeysRequestsHashMap.remove(key);
					// if (DQManager.isNotficationOn(mContext)) {
					// DQUtilityNotifiacation.showNotification(mContext, "" + temp.getTitle(),
					// "Downloaded Successfully");
					// }
				}
			}

		}


		@Override
		public DQRequest getDownloadingRequester() {

			return null;
		}


		@Override
		public void onDataUpdated() {

		}


		@Override
		public void updateStatusOf(DQRequest downloadingReques) {

			// TODO Auto-generated method stub

		}


		@Override
		public void updateFileExistanceStatusInDB(DQRequest dRequest) {

			// TODO Auto-generated method stub

		}
	};
}