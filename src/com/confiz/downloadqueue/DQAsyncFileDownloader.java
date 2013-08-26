
package com.confiz.downloadqueue;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.SSLException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.confiz.downloadqueue.encryption.DQEncryptionAndDownloadManager;
import com.confiz.downloadqueue.interfaces.DQResponseListener;
import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQAppUtils;
import com.confiz.downloadqueue.utils.DQDebugHelper;
import com.confiz.downloadqueue.utils.DQErrors;
import com.confiz.downloadqueue.utils.DQExternalStorageHandler;
import com.example.downloadqueue.R;

public class DQAsyncFileDownloader extends AsyncTask<Void, Integer, Boolean> {


	/** The tag. */
	protected final String TAG = "AsynFileDwonloader";

	/** The context. */
	protected Context mContext = null;

	/** The caller. */
	// private DQRequestHanlder mCaller = null;

	protected String urlStr = null;

	protected long fileSize = -1;

	/** The current completed progress. */
	protected int currentCompletedProgress = 0;

	/** The downloading file directory. */
	protected String downloadingFileDirectory = null;

	protected String downloadingFileNameOrignalName = null;

	protected String downloadingFileNameTempName = null;

	/** The which message to display. */
	public DQErrors whichMessageToDisplay = DQErrors.UNABLE_TO_DOWNLOAD_FILE;

	/** The stop downloading. */
	public boolean stopDownloading = false;

	/** The should delete file. */
	public boolean shouldDeleteFile = false;

	public DQResponseListener queueListener = null;

	public DQRequest request = null;


//	public DQAsyncFileDownloader(Context context, DQRequestHanlder listener, DQRequest data,
//	        DQResponseListener queueListener) {
//
//		mContext = context;
//		request = data;
//		this.queueListener = queueListener;
//		request.currentError = DQErrors.NO_ERROR;
//	}


	@Override
	protected Boolean doInBackground(Void... params) {

		if (DQManager.isConfiguredNetworkAvailable(mContext) == false) {
			whichMessageToDisplay = DQErrors.NETWORK_WEAK;
			return false;
		}

		Boolean loadSuccess = false;
		boolean append = false;
		String fileName = null;

		downloadingFileNameOrignalName = new String(request.getFileName());
		downloadingFileNameTempName = new String(request.getTempFileName());
		fileName = downloadingFileNameTempName;

		String destPath = request.getFileFolderPath();
		downloadingFileDirectory = new String(destPath);

		try {
			urlStr = request.getFileUrl();

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			fileSize = conn.getContentLength();
			if (fileSize < 0) {
				whichMessageToDisplay = DQErrors.FILE_NOT_FOUND;
				return loadSuccess;
			}

			final long sizeIncreasedAfterEncryption = DQEncryptionAndDownloadManager.KB_TO_ENCRYPT * 8;

			File isPending = new File(destPath, downloadingFileNameOrignalName);
			long fileDownloadedSize = -1;
			if (isPending != null && isPending.exists()) {
				fileDownloadedSize = isPending.length();
				long valueOf300KB = DQExternalStorageHandler.SIZE_KB * 300;
				if (fileDownloadedSize > (valueOf300KB)) {
					conn.setRequestProperty("Range", "bytes=" + (fileDownloadedSize - sizeIncreasedAfterEncryption) + "-");
					append = true;
				}
			}

			if (conn != null) {
				InputStream in = conn.getInputStream();

				long availableSpace = DQExternalStorageHandler.getExternalStorageAvailableSpace();

				// 4*256 is the size increased in bytes i.e.1K
				// due to encryption
				if (availableSpace < fileSize + (sizeIncreasedAfterEncryption)) {
					whichMessageToDisplay = DQErrors.EXTERNAL_STORAGE_INSUFFICIENT_SPACE;
					return loadSuccess;
				}

				int tempCount = 0;
				do {
					if (DQExternalStorageHandler.isExternalStorageAvailable() == false) {
						whichMessageToDisplay = DQErrors.EXTERNAL_STORAGE_NOT_AVAILABLE;
						break;
					} else if (DQExternalStorageHandler.isExternalStorageWritable() == false) {
						whichMessageToDisplay = DQErrors.EXTERNAL_STORAGE_NOT_WRITABLE;
						break;
					}
					request.setTotalSize(fileSize);
					this.queueListener.onDownloadStart(request.getKey(), (int) fileSize);
//					loadSuccess = DQUtilityNetwork
//					        .downloadFile(in, fileSize, request.getFileFolderPath(), fileName, this, mContext, request, append, fileDownloadedSize);
//					tempCount++;

				} while (tempCount < 1);
				if (loadSuccess == false) {
					Log.i(TAG, "Falid to download");
				}
			}

		} catch (SSLException exception) {
			whichMessageToDisplay = DQErrors.NETWORK_NO_AVILABLE;
		} catch (final SocketTimeoutException exception) {
			whichMessageToDisplay = DQErrors.NETWORK_NO_AVILABLE;
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(this.mContext, exception);
		}
		return loadSuccess;
	}


	@Override
	protected void onPostExecute(Boolean downloadCompleted) {

		try {
			request.setDownloading(false);
			request.setSaved(downloadCompleted);
			updateRequestStatus();
			updateRequestItemNewData();

			String key = request.getKey();

			this.updateStatusAndDeletFile(this.shouldDeleteFile);
			// DQDBAdapter.getInstance(mContext).updateFileExistanceStatusInDB(
			// LCVideo);
			if (downloadCompleted) {

				final File fileForRename = new File(this.downloadingFileDirectory,
				        this.downloadingFileNameTempName);
				if (fileForRename != null && fileForRename.exists()) {
					final boolean success = fileForRename.renameTo(new File(this.downloadingFileDirectory,
					        this.downloadingFileNameOrignalName));
					if (success) {
						Log.i(this.TAG, "File suessfull renamed to " + this.downloadingFileNameOrignalName);
					}
				}
				this.queueListener.onComplete(key);
			} else {

				if (whichMessageToDisplay == DQErrors.NO_ERROR && request.currentError != DQErrors.NO_ERROR) {
					whichMessageToDisplay = request.currentError;
				}
				// this.videoData.setDownloading(false);
				if (whichMessageToDisplay == DQErrors.EXTERNAL_STORAGE_INSUFFICIENT_SPACE) {
					DQAppUtils.showInsufficientSpaceDailog(this.mContext, this.mContext
					        .getString(R.string.error_msg_external_storage_insufficient_space), this.mContext
					        .getString(R.string.dlg_title_insufficient_space), this.getFileSize());
					this.queueListener.onErrorOccurred(key, whichMessageToDisplay);
				} else {
					this.queueListener.onErrorOccurred(key, whichMessageToDisplay);
				}
				// RESET VALUE OF ERROR_NO
				whichMessageToDisplay = DQErrors.UNABLE_TO_DOWNLOAD_FILE;
			}
			if (downloadCompleted) {
				File fileForRename = new File(downloadingFileDirectory, downloadingFileNameTempName);
				if (fileForRename != null && fileForRename.exists()) {
					boolean success = fileForRename.renameTo(new File(downloadingFileDirectory,
					        downloadingFileNameOrignalName));
					if (success) {
						Log.i(TAG, "File suessfull renamed to " + downloadingFileNameOrignalName);
					}
				}
				this.queueListener.onComplete(request.getKey());
			} else {

				if (whichMessageToDisplay == DQErrors.NO_ERROR && request.currentError != DQErrors.NO_ERROR) {
					whichMessageToDisplay = request.currentError;
				}
				// if (whichMessageToDisplay ==
				// DQErrors.EXTERNAL_STORAGE_INSUFFICIENT_SPACE) {
				// SMUtility.getInstance(mContext).showInsufficientSpaceDailog(mContext,
				// mContext.getString(R.string.error_msg_external_storage_insufficient_space),
				// mContext.getString(R.string.dlg_title_insufficient_space),
				// getFileSize());
				// queueListener.onErrorOccurred(request.getKey(),
				// whichMessageToDisplay);
				// } else {
				queueListener.onErrorOccurred(request.getKey(), whichMessageToDisplay);
				// }

			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(mContext, TAG, exception);
		}
	}


	@Override
	protected void onPreExecute() {

		whichMessageToDisplay = DQErrors.UNABLE_TO_DOWNLOAD_FILE;
		if (queueListener != null) {
			queueListener.onDownloadStart(request.getKey());
		}
	}


	public void doWorkForCancel() {

		stopDownloading = true;
		updateStatusAndDeletFile(shouldDeleteFile);
	}


	@Override
	protected void onCancelled() {

		super.onCancelled();
	}


	public void updateStatusAndDeletFile(boolean shouldDeleteFile) {

		request.setDownloading(false);
		String filePath = request.getFilePath();
		if (filePath != null && filePath.length() > 0) {
			File fileToDelete = new File(filePath);

			if (fileToDelete.exists() && fileToDelete.isFile()) {
				if (shouldDeleteFile == false) {
					if (fileToDelete.length() > DQExternalStorageHandler.SIZE_KB * 300) {
						request.setPartialDownloaded(true);
						// DQDBAdapter.getInstance(mContext).updateFileExistanceStatusInDB(request);
						request.setDownloadedSize(fileToDelete.length());
						updateRequestItemNewData();
					}
				} else {
					fileToDelete.delete();
					request.setPartialDownloaded(false);
					request.setTotalSize(0);
					request.setDownloadedSize(0);
					updateRequestItemData();
				}
			}
		}
	}


	public void updateProgress(int progressPercentage) {

		if (progressPercentage < 0) {
			progressPercentage = 0;
		} else if (progressPercentage > 100) {
			progressPercentage = 100;
		}
		currentCompletedProgress = progressPercentage;
		if (queueListener != null) {
			queueListener.updateProgress(request.getKey(), progressPercentage);
			request.setProgress(progressPercentage);
		}
	}


	public long getFileSize() {

		return fileSize;
	}


	public void setFileSize(long fileSize) {

		this.fileSize = fileSize;
	}


	public void updateDownloadingDetails(String details[]) {

		if (queueListener != null) {
			this.queueListener.updateDownloadingEstimates(request.getKey(), details);
		}
	}


	public int getCurrentCompletedProgress() {

		return currentCompletedProgress;
	}


	public void updateRequestStatus() {

		String filePath = request.getFilePath();
		if (filePath != null && filePath.length() > 0) {
			File fileToDelete = new File(filePath);

			if (fileToDelete.exists() && fileToDelete.isFile()) {
				if (shouldDeleteFile == false) {
					if (fileToDelete.length() > DQExternalStorageHandler.SIZE_KB * 300) {
						request.setDownloadedSize(fileToDelete.length());
						if (fileSize > 0) {
							request.setTotalSize(fileSize);
						}
					}
				}
			}
		}
	}


	private void updateRequestItemNewData() {

		if (request.getTotalSize() > 0) {
			DQManager.getInstance(mContext).updateDownloadTotalSize(request, mContext);
		}

		if (request.getDownloadedSize() > 0) {
			DQManager.getInstance(mContext).updateDownloadedSize(request, mContext);
		}
	}


	private void updateRequestItemData() {

		DQManager.getInstance(mContext).updateDownloadTotalSize(request, mContext);
		DQManager.getInstance(mContext).updateDownloadedSize(request, mContext);

	}
}
