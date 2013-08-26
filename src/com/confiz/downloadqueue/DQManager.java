
package com.confiz.downloadqueue;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.confiz.downloadqueue.db.DQDBAdapter;
import com.confiz.downloadqueue.interfaces.DQResponseListener;
import com.confiz.downloadqueue.model.DQActions;
import com.confiz.downloadqueue.model.DQDownloadingStatus;
import com.confiz.downloadqueue.model.DQQueue;
import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQAppConstants;
import com.confiz.downloadqueue.utils.DQAppPreference;
import com.confiz.downloadqueue.utils.DQAppUtils;
import com.confiz.downloadqueue.utils.DQDebugHelper;
import com.confiz.downloadqueue.utils.DQErrors;
import com.confiz.downloadqueue.utils.DQUtilityNetwork;
import com.example.downloadqueue.R;

public class DQManager {


	private final String TAG = "DownloadingQueueManger";

	private DQQueue downloadQueue = null;

	private static DQManager downloadingManger = null;

	private boolean destroyQueue = false;


	private DQManager() {

		downloadQueue = DQQueue.getInstance();
	}


	public static DQManager getInstance(Context context) {

		if (downloadingManger == null) {
			downloadingManger = new DQManager();
			downloadingManger.getDataFromDatabse(context);
			downloadingManger.updateMangerForData(context);
			startDQService(context, DQActions.START_DOWNLOAD);
		}
		if (downloadingManger.downloadQueue.isEmpty()) {
			downloadingManger.getDataFromDatabse(context);
		}
		return downloadingManger;
	}


	public void getDataFromDatabse(Context context) {

		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		database.updateDownloadStatus(userId);
		ArrayList<DQRequest> data = database.getDownloadRequests("" + userId);
		if (data != null && data.size() > 0) {
			downloadQueue.addAll(data);
		}
	}


	public void addToQueue(DQRequest downloadRequest, Context context) {

		String userId = getCurrentUser(context);
		downloadRequest.setUserId(userId);
		DQDBAdapter database = DQDBAdapter.getInstance(context);

		if (database.isInDownloadQueue(downloadRequest.getKey(), userId) == false) {
			boolean flag = database.insertRecord(downloadRequest);
			if (flag == true) {
				downloadQueue.add(downloadRequest);
			}
		}
		notifyDataUpdated();
		startDQService(context, DQActions.START_DOWNLOAD);
	}


	public boolean deleteDownloadRequest(DQRequest itemToBeRemove, Context context) {

		boolean result = false;
		try {
			itemToBeRemove.setStatus(DQDownloadingStatus.DELETED);
			if (itemToBeRemove != null) {
				String tempSku = itemToBeRemove.getKey();
				String userId = getCurrentUser(context);
				if (tempSku != null && tempSku.length() > 0) {
					result = DQDBAdapter.getInstance(context).deleteDQRequest(itemToBeRemove, userId);
					DQResponseHolder.getInstance().updateFileExistanceStatusInDB(itemToBeRemove);
					if (result == true) {
						downloadQueue.remove(itemToBeRemove);
						updateDownloadPositions(context);
						notifyDataUpdated();
					} else {
						DQAppUtils.showDialogMessage(context, context
						        .getString(R.string.error_msg_failed_to_delete_download_request), context
						        .getString(R.string.dlg_title_error));
					}
				}
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(exception);
		}
		return result;
	}


	public boolean deleteDownloadQueue(Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		flag = database.deleteDownloadQueue(userId);
		if (flag == true) {
			downloadQueue.clear();
			notifyDataUpdated();
		}
		startDQService(context, DQActions.STOP_DQ);
		return flag;
	}


	public DQDownloadingStatus getDownloadStatus(String key, Context context) {

		DQDownloadingStatus status = DQDownloadingStatus.WAITING;
		if (key != null && key.length() > 0) {
			ArrayList<DQRequest> temp = downloadQueue;
			if (temp != null && temp.size() > 0) {
				for (int i = 0; i < temp.size(); i++) {
					DQRequest data = temp.get(i);
					if (data != null) {
						if (key.equals(data.getKey())) {
							status = data.getStatus();
						}
					}
				}
			}
		}
		return status;
	}
	
	public boolean isDownloading(DQRequest downloadRequest , Context context){
		String userId = getCurrentUser(context);
		downloadRequest.setUserId(userId);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		if (database.isInDownloadQueue(downloadRequest.getKey(), userId)) {
			return true;
		}
		return false;
	}


	public boolean updateDownloadRequestData(Context context, DQRequest dRequest) {

		boolean flag = false;
		try {
			String userId = getCurrentUser(context);
			if (context != null) {
				flag = DQDBAdapter.getInstance(context).updateDQRequestData(dRequest, userId);
				DQResponseHolder.getInstance().updateFileExistanceStatusInDB(dRequest);
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(context, exception);
		}
		return flag;
	}


	public boolean updateDownloadPositions(Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		ArrayList<DQRequest> temp = downloadQueue;
		if (temp != null && temp.size() > 0) {
			flag = DQDBAdapter.getInstance(context).updateDownloadPositions(downloadQueue, userId);
		}
		return flag;
	}


	private boolean updatePositions(ArrayList<DQRequest> temp, Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		if (downloadQueue != null && downloadQueue.size() > 0) {
			flag = DQDBAdapter.getInstance(context).updateDownloadPositions(downloadQueue, userId);
		}
		return flag;
	}


	public boolean updateDownloadPositions(ArrayList<DQRequest> temp, Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		if (temp != null && temp.size() > 0) {
			synchronized (downloadQueue) {
				for (int i = 0; i < temp.size(); i++) {
					DQRequest data = temp.get(i);
					int index = downloadQueue.indexOf(data);
					DQRequest newData = downloadQueue.remove(index);
					downloadQueue.add(newData);
				}
			}
			flag = DQDBAdapter.getInstance(context).updateDownloadPositions(downloadQueue, userId);
		}
		return flag;
	}


	public boolean updateDownloadStatus(String key, DQDownloadingStatus status, Context context) {

		DQRequest item = getItems(key);
		if (item != null) {
			item.setStatus(status);
		}
		return updateDownloadStatus(item, context);
	}


	public boolean updateDownloadStatus(DQRequest downloadRequest, Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		flag = database.updateDownloadStatus(downloadRequest, userId);
		return flag;
	}


	public boolean updateErrorDescription(DQRequest downloadRequest, Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		flag = database.updateErrorDiscription(downloadRequest, userId);
		return flag;
	}


	public boolean updateDownloadedSize(DQRequest downloadRequest, Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		flag = database.updateDownloadedSize(downloadRequest, userId);
		return flag;
	}


	public boolean updateDownloadTotalSize(DQRequest downloadRequest, Context context) {

		boolean flag = false;
		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		flag = database.updateDownloadTotalSize(downloadRequest, userId);
		return flag;
	}


	public boolean stopDownloading(Context context, String key, boolean shouldDeleteFile) {

		try {
			DQRequest itemToBeStop = getItems(key);
			if (itemToBeStop == null || itemToBeStop.getStatus() == DQDownloadingStatus.DELETE_REQUEST || itemToBeStop
			        .getStatus() == DQDownloadingStatus.DELETED) {
				return true;
			}
			itemToBeStop
			        .setStatus(shouldDeleteFile ? DQDownloadingStatus.DELETE_REQUEST : DQDownloadingStatus.PAUSED_REQUEST);
			updateDownloadStatus(itemToBeStop, context);
			if (shouldDeleteFile) {
				startDQService(context, DQActions.DELETE_ITEM);
			} else {
				startDQService(context, DQActions.PAUSE_ITEM);
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(exception);
		}
		return true;
	}


	public boolean startDownloading(Context context, String key) {

		DQRequest itemToBeStart = getItems(key);
		itemToBeStart.setStatus(DQDownloadingStatus.DOWNLOAD_REQUEST);
		updateDownloadStatus(itemToBeStart, context);
		startDQService(context, DQActions.START_DOWNLOAD);
		return true;
	}


	public boolean startDownloadingFromPause(Context context, String key) {

		DQRequest itemToBeStart = getItems(key);
		itemToBeStart.setStatus(DQDownloadingStatus.DOWNLOAD_REQUEST);
		updateDownloadStatus(itemToBeStart, context);
		startDQService(context, DQActions.START_DOWNLOAD_FROM_PAUSE);
		return true;
	}


	public ArrayList<DQRequest> getQueuedItemList() {

		return downloadQueue;
	}


	public boolean canDownloadFurthurItems(Context context) {

		boolean flag = false;
		flag = isLimitAvailable(context);
		if (flag == true) {
			DQRequest data = getItemWithStatus(DQDownloadingStatus.DOWNLOAD_REQUEST);
			if (data == null) {
				data = getItemWithStatus(DQDownloadingStatus.WAITING);
				if (data == null) {
					if (DQUtilityNetwork.isNetworkAvailable(context) == true) {
						data = getItemWithStatus(DQDownloadingStatus.FAILED);
						if (data == null) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			}
		}
		return flag;
	}


	public DQRequest getNextItemToDownload(Context context) {

		DQRequest request = null;
		if (canDownloadFurthurItems(context)) {
			for (int i = 0; i < downloadQueue.size(); i++) {
				DQRequest temp = downloadQueue.get(i);
				if (temp != null && (temp.getStatus() == DQDownloadingStatus.DOWNLOAD_REQUEST)) {
					request = temp;
					break;
				}
			}
			if (request == null) {
				for (int i = 0; i < downloadQueue.size(); i++) {
					DQRequest temp = downloadQueue.get(i);
					if (temp != null && (temp.getStatus() == DQDownloadingStatus.WAITING || temp.getStatus() == DQDownloadingStatus.FAILED)) {
						request = temp;
						break;
					}
				}
			}
		}
		return request;
	}


	public int getDownloadSection() {

		int index = -1;
		for (int i = 0; i < downloadQueue.size(); i++) {
			DQRequest temp = downloadQueue.get(i);
			if (temp != null && (temp.getStatus() == DQDownloadingStatus.WAITING)) {
				index = i;
				break;
			}
		}
		return index;
	}


	public DQRequest getPausedDownloadQequest(Context context) {

		DQRequest request = null;
		if (downloadQueue != null) {
			for (int i = 0; i < downloadQueue.size(); i++) {
				DQRequest temp = downloadQueue.get(i);
				if (temp != null && (temp.getStatus() == DQDownloadingStatus.DOWNLOAD_REQUEST)) {
					request = temp;
					break;
				}
			}
		}
		return request;
	}


	public static void startDQService(Context context, DQActions action) {

		Intent intent = new Intent();
		intent.setClass(context, DQService.class);
		intent.putExtra("action", action.ordinal());
		context.startService(intent);
	}


	public boolean downloadedRequestCompleted(Context context, DQRequest itemToBeRemove) {

		boolean result = false;
		if (itemToBeRemove != null) {
			itemToBeRemove.setDownloading(false);
			itemToBeRemove.setSaved(true);
			result = deleteDownloadRequest(itemToBeRemove, context);
		}
		itemToBeRemove = null;
		return result;
	}


	public DQRequest getItems(String key) {

		DQRequest req = null;
		ArrayList<DQRequest> requets = DQQueue.getInstance();
		if (requets != null && requets.size() > 0) {
			for (int i = 0; i < requets.size(); i++) {
				DQRequest tempRequest = requets.get(i);
				if (tempRequest != null) {
					String reqKey = tempRequest.getKey();
					if (reqKey != null && reqKey.equals(key)) {
						req = tempRequest;
						break;
					}
				}
			}
		}
		return req;
	}


	public boolean isThereAnyItemWithStatus(DQDownloadingStatus status) {

		boolean flag = false;
		for (int s = 0; s < downloadQueue.size(); s++) {
			DQRequest temp = downloadQueue.get(s);
			if (temp != null && (temp.getStatus() == status)) {
				flag = true;
				break;
			}
		}
		return flag;
	}


	public DQRequest getItemWithStatus(DQDownloadingStatus status) {

		DQRequest request = null;
		ArrayList<DQRequest> requets = DQQueue.getInstance();
		if (requets != null && requets.size() > 0) {
			for (int i = 0; i < requets.size(); i++) {
				DQRequest tempRequest = requets.get(i);
				if (tempRequest != null && (tempRequest.getStatus() == status)) {
					request = tempRequest;
					break;
				}
			}
		}
		return request;
	}


	public void updateDBandQueue(Context context, String key, DQDownloadingStatus status) {

		DQRequest itemToBeRemove = getItems(key);
		if(itemToBeRemove == null){
			return ;
		}
		switch (status) {
			case DELETED:
				itemToBeRemove.setDownloading(false);
				itemToBeRemove.setSaved(false);
				itemToBeRemove.setPartialDownloaded(false);
				deleteDownloadRequest(itemToBeRemove, context);
				break;
			case PAUSED:
				itemToBeRemove.setDownloading(true);
				updateDownloadRequestData(context, itemToBeRemove);
				break;
			case COMPLETED:
				itemToBeRemove.setStatus(DQDownloadingStatus.COMPLETED);
				downloadedRequestCompleted(context, itemToBeRemove);
				break;
			case FAILED:
				break;
			default:
				break;
		}
		startDQService(context, DQActions.START_DOWNLOAD);
		updateMangerAndNotify(key, itemToBeRemove);
	}


	public void updateMangerAndNotify(String key, DQRequest downloadingRequest) {

		if (downloadingRequest == null) {
			downloadingRequest = getItems(key);
		}
		if (downloadingRequest != null) {
			if (downloadingRequest.getStatus() != DQDownloadingStatus.COMPLETED && downloadingRequest
			        .getStatus() != DQDownloadingStatus.DELETED && downloadingRequest.getStatus() != DQDownloadingStatus.DELETE_REQUEST) {
				downloadingRequest.setPartialDownloaded(true);
			} else {
				downloadingRequest.setPartialDownloaded(false);
			}
			if (downloadingRequest.getStatus() == DQDownloadingStatus.FAILED || downloadingRequest
			        .getStatus() == DQDownloadingStatus.DOWNLOADING || downloadingRequest.getStatus() == DQDownloadingStatus.PAUSED || downloadingRequest
			        .getStatus() == DQDownloadingStatus.PAUSED_REQUEST) {
				downloadingRequest.setPartialDownloaded(true);
			}

			DQResponseHolder.getInstance().updateStatusOf(downloadingRequest);
			notifyDataUpdated();
		}
	}


	public void updateMangerAndNotify(String key) {

		DQRequest downloadingRequest = getItems(key);
		updateMangerAndNotify(key, downloadingRequest);
	}


	public void errorOccured(Context mContext, String key, DQErrors errorNo) {

		DQRequest downloadingRequest = getItems(key);
		if (downloadingRequest != null) {
			downloadingRequest.setStatus(DQDownloadingStatus.FAILED);
			updateDownloadStatus(downloadingRequest, mContext);
			downloadingRequest.setErrorDiscription(mContext.getString(errorNo.value()));
			updateErrorDescription(downloadingRequest, mContext);
			int index = downloadQueue.indexOf(downloadingRequest);
			downloadQueue.remove(index);
			int newIndex = getDownloadSection();
			if (newIndex != -1) {
				downloadQueue.add(newIndex, downloadingRequest);
			} else {
				downloadQueue.add(downloadingRequest);
			}
			updatePositions(downloadQueue, mContext);
		}
		updateDBandQueue(mContext, key, DQDownloadingStatus.FAILED);
	}


	public void destroyManager() {

		downloadQueue.clear();
		destroyQueue = false;
		downloadingManger = null;
	}


	public boolean isAnyDownloadingInProgressPending() {

		boolean flag = false;
		for (int s = 0; s < downloadQueue.size(); s++) {
			DQRequest temp = downloadQueue.get(s);
			if (temp != null && temp.getStatus() == DQDownloadingStatus.DOWNLOADING) {
				flag = true;
				break;
			}
		}
		return flag;
	}


	public void updateMangerForData(Context context) {

		if (downloadQueue != null) {
			for (int i = 0; i < downloadQueue.size(); i++) {
				DQRequest request = downloadQueue.get(i);
				updateMangerAndNotify(request.getKey(), request);
			}
			startDQService(context, DQActions.START_DOWNLOAD);
		}
	}


	public static boolean isMangerAlive() {

		return downloadingManger == null ? false : true;
	}


	public void stopDownloadingQueue(Context context) {

		destroyQueue = true;
		for (int index = 0; index < downloadQueue.size(); index++) {
			DQRequest temp = downloadQueue.get(index);
			if (temp != null && (temp.getStatus() == DQDownloadingStatus.DOWNLOADING || temp.getStatus() == DQDownloadingStatus.DOWNLOAD_REQUEST)) {
				temp.setStatus(DQDownloadingStatus.PAUSED_REQUEST);
			}
		}
		startDQService(context, DQActions.PAUSE_ITEM);
	}

	public boolean deleteAllItemsFromQueue(Context context) {

		destroyQueue = true;
		boolean flag = false;
		String userId = getCurrentUser(context);
		DQDBAdapter database = DQDBAdapter.getInstance(context);
		flag = database.deleteDownloadQueue(userId);
		if (flag == true) {
			downloadQueue.clear();
			notifyDataUpdated();
		}
		startDQService(context, DQActions.REMOVE_ITEM);
		return flag;
	}

	public boolean isDestoryQueue() {

		return destroyQueue;
	}


	public boolean isLimitAvailable(Context context) {

		boolean flag = false;
		int limit = getMaxParallelDownloads(context);
		int curDownloading = countCurrentDownloading();
		if (curDownloading < limit) {
			flag = true;
		}
		return flag;
	}


	public boolean isDownloadingLimitAvailable(Context context) {

		boolean flag = false;
		int limit = getMaxParallelDownloads(context);
		int curDownloading = countCurrentDownloadingStatus();
		if (curDownloading < limit && curDownloading < downloadQueue.size()) {
			flag = true;
		}
		return flag;
	}


	public int countCurrentDownloadingStatus() {

		int curDownloading = 0;
		for (int s = 0; s < downloadQueue.size(); s++) {
			DQRequest temp = downloadQueue.get(s);
			if (temp != null && (temp.getStatus() == DQDownloadingStatus.DOWNLOADING)) {
				curDownloading++;
			}
		}
		return curDownloading;
	}


	public int countCurrentWatingOrFailed() {

		int curDownloading = 0;
		for (int s = 0; s < downloadQueue.size(); s++) {
			DQRequest temp = downloadQueue.get(s);
			if (temp != null && (temp.getStatus() == DQDownloadingStatus.FAILED || temp.getStatus() == DQDownloadingStatus.WAITING)) {
				curDownloading++;
			}
		}
		return curDownloading;
	}


	public int countCurrentDownloading() {

		int curDownloading = 0;
		for (int s = 0; s < downloadQueue.size(); s++) {
			DQRequest temp = downloadQueue.get(s);
			if (temp != null && (temp.getStatus() == DQDownloadingStatus.DOWNLOADING || temp.getStatus() == DQDownloadingStatus.PAUSED || temp
			        .getStatus() == DQDownloadingStatus.PAUSED_REQUEST)) {
				curDownloading++;
			}
		}
		return curDownloading;
	}


	public boolean putIntoDownloadingSection(Context mContext, String key) {

		boolean flag = false;
		DQRequest downloadingItem = getItems(key);
		try {
			if (downloadingItem != null) {
				downloadingItem.setStatus(DQDownloadingStatus.WAITING);
				flag = updateDownloadStatus(downloadingItem, mContext);
				notifyDataUpdated();
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(exception);
		}
		return flag;
	}


	public boolean canAddItems() {

		boolean flag = false;
		if (downloadQueue != null && downloadQueue.size() > 0) {
			int waiting = countCurrentWating();
			int limit = getMaxParallelDownloads(null);
			if (waiting < limit) {
				flag = true;
			}
		} else {
			flag = true;
		}
		return flag;
	}


	public int countCurrentWating() {

		int curDownloading = 0;
		for (int s = 0; s < downloadQueue.size(); s++) {
			DQRequest temp = downloadQueue.get(s);
			if (temp != null && !(temp.getStatus() == DQDownloadingStatus.DOWNLOADING || temp.getStatus() == DQDownloadingStatus.PAUSED || temp
			        .getStatus() == DQDownloadingStatus.PAUSED_REQUEST)) {
				curDownloading++;
			}
		}
		return curDownloading;
	}


	/**
	 * @param context
	 */
	public static String getCurrentUser(Context context) {

		return DQAppPreference.getValue(context, DQAppConstants.KEY_USER_ID, DQAppConstants.VALUE_USER_ID);

	}


	/**
	 * @param context
	 */
	public static void setCurrentUser(Context context, String user) {

		DQAppPreference.saveValue(context, user, DQAppConstants.KEY_USER_ID);
	}


	/**
	 * @param context
	 */
	public static boolean isNotficationOn(Context context) {

		return DQAppPreference
		        .getBoolean(context, DQAppConstants.KEY_SHOW_NOTIFICATION, DQAppConstants.VALUE_SHOW_NOTIFICATION);

	}


	/**
	 * @param context
	 */
	public static void setNotificationOn(Context context, boolean flag) {

		DQAppPreference.saveBoolean(context, flag, DQAppConstants.KEY_SHOW_NOTIFICATION);
	}


	/**
	 * @param context
	 */
	public static boolean isAutoStartOnNetworkConnect(Context context) {

		return DQAppPreference
		        .getBoolean(context, DQAppConstants.KEY_AUTO_START_ON_NETWORK_CONNECTED, DQAppConstants.VALUE_AUTO_START_ON_NETWORK_CONNECTED);

	}


	/**
	 * @param context
	 */
	public static void setAutoStartOnNetworkConnect(Context context, boolean flag) {

		DQAppPreference.saveBoolean(context, flag, DQAppConstants.KEY_AUTO_START_ON_NETWORK_CONNECTED);
	}


	/**
	 * @param context
	 */
	public static boolean isAutoStartOnAppStart(Context context) {

		return DQAppPreference
		        .getBoolean(context, DQAppConstants.KEY_AUTO_START_ON_APP_START, DQAppConstants.VALUE_AUTO_START_ON_APP_START);

	}


	/**
	 * @param context
	 */
	public static void setAutoStartOnAppStart(Context context, boolean flag) {

		DQAppPreference.saveBoolean(context, flag, DQAppConstants.KEY_AUTO_START_ON_NETWORK_CONNECTED);
	}


	/**
	 * @param context
	 */
	public static boolean isDownloadOnlyOnWifi(Context context) {

		return DQAppPreference
		        .getBoolean(context, DQAppConstants.KEY_ONLY_ON_WIFI, DQAppConstants.VALUE_ONLY_ON_WIFI);

	}


	/**
	 * @param context
	 */
	public static void setDownloadOnlyOnWifi(Context context, boolean flag) {

		DQAppPreference.saveBoolean(context, flag, DQAppConstants.KEY_ONLY_ON_WIFI);
	}


	/**
	 * @param context
	 */
	public static boolean isNewItemGoOnTop(Context context) {

		return DQAppPreference
		        .getBoolean(context, DQAppConstants.KEY_PIORATIES_NEW_ITEM_TO_TOP, DQAppConstants.VALUE_PIORATIES_NEW_ITEM_TO_TOP);

	}


	/**
	 * @param context
	 */
	public static void setNewItemGoOnTop(Context context, boolean flag) {

		DQAppPreference.saveBoolean(context, flag, DQAppConstants.KEY_PIORATIES_NEW_ITEM_TO_TOP);
	}


	/**
	 * @param context
	 */
	public static int getMaxParallelDownloads(Context context) {

		return DQAppPreference
		        .getInt(context, DQAppConstants.KEY_MAX_PARALLEL_DOWNLOADS, DQAppConstants.VALUE_MAX_PARALLEL_DOWNLOADS);
	}


	public static void setMaxParallelDownloads(Context context, int max) {

		if (max < 4) {
			DQAppPreference.saveInt(context, max, DQAppConstants.KEY_MAX_PARALLEL_DOWNLOADS);
		} else {
			throw new RuntimeException("Max parallel download must be less then 5");
		}
	}


	/**
	 * @param context
	 */
	public static int getMaxQueueItemLimit(Context context) {

		return DQAppPreference
		        .getInt(context, DQAppConstants.KEY_MAX_QUEUE_LIMIT, DQAppConstants.VALUE_MAX_QUEUE_LIMIT);
	}


	public static void setMaxQueueItemLimit(Context context, int max) {

		if (max <= DQAppConstants.VALUE_MAX_QUEUE_LIMIT) {
			DQAppPreference.saveInt(context, max, DQAppConstants.KEY_MAX_QUEUE_LIMIT);
		} else {
			throw new RuntimeException(
			        "Max item limit must be less then " + DQAppConstants.VALUE_MAX_QUEUE_LIMIT);
		}
	}


	/**
	 * @param context
	 */
	public static int getMaxRetries(Context context) {

		return DQAppPreference
		        .getInt(context, DQAppConstants.KEY_NO_OF_RETRIES, DQAppConstants.VALUE_NO_OF_RETRIES);
	}


	public static void setMaxRetries(Context context, int max) {

		if (max < DQAppConstants.VALUE_NO_OF_RETRIES) {
			DQAppPreference.saveInt(context, max, DQAppConstants.KEY_NO_OF_RETRIES);
		} else {
			throw new RuntimeException("Max retries must be less then " + DQAppConstants.VALUE_NO_OF_RETRIES);
		}
	}


	/**
	 * @param context
	 */
	public static int getMaxFileSizeAllowed(Context context) {

		return DQAppPreference
		        .getInt(context, DQAppConstants.KEY_MAX_FILE_SIZE, DQAppConstants.VALUE_MAX_FILE_SIZE);
	}


	/**
	 * @param context
	 * 
	 * @param maxSize
	 *            set -1 if you for no limit
	 */
	public static void setMaxFileSizeAllowed(Context context, int maxSize) {

		if (maxSize < 0) {
			DQAppPreference.saveInt(context, maxSize, DQAppConstants.KEY_MAX_FILE_SIZE);
		} else {
			throw new RuntimeException("Max retries must be less then 10");
		}
	}


	/**
	 * @param mContext
	 * @return
	 */
	public static boolean isConfiguredNetworkAvailable(Context mContext) {

		boolean isNetwrokOn = false;
		if (DQUtilityNetwork.isNetworkAvailable(mContext)) {
			boolean onlyOnWifi = DQManager.isDownloadOnlyOnWifi(mContext);
			if (onlyOnWifi) {
				if (DQUtilityNetwork.isConnectedToWifi(mContext)) {
					isNetwrokOn = true;
				}
			} else {
				isNetwrokOn = true;
			}
		}
		return isNetwrokOn;
	}


	public void addDQResponseListiner(DQResponseListener listener) {

		DQResponseHolder.getInstance().addListener(listener);
	}


	public void removeDQResponseListiner(DQResponseListener listener) {

		DQResponseHolder.getInstance().removeListener(listener);
	}


	public void removeAllDQResponseListiner(DQResponseListener listener) {

		DQResponseHolder.getInstance().removeAllListener();
	}


	private void notifyDataUpdated() {

		DQResponseHolder.getInstance().onDataUpdated();
	}
}
