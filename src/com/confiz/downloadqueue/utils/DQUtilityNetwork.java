
package com.confiz.downloadqueue.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.confiz.downloadqueue.DQFileDownloader;
import com.confiz.downloadqueue.encryption.DQDesEncrypter;
import com.confiz.downloadqueue.encryption.DQEncryptionAndDownloadManager;
import com.confiz.downloadqueue.model.DQRequest;

public class DQUtilityNetwork {


	private static String TAG = "DQUtilityNetwork";


	public static boolean isNetworkAvailable(Context context) {

		boolean available = false;
		try {

			ConnectivityManager connectivity = (ConnectivityManager) context
			        .getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							available = true;
						}
					}
				}
			}
			if (available == false) {
				NetworkInfo wiMax = connectivity.getNetworkInfo(6);

				if (wiMax != null && wiMax.isConnected()) {
					available = true;
				}
			}
		} catch (Exception e) {
			DQDebugHelper.printException(TAG, e);
		}

		return available;
	}


	public static boolean isConnectedToWifi(Context context) {

		try {
			ConnectivityManager connManager = (ConnectivityManager) context
			        .getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mWifi.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			DQDebugHelper.printException(TAG, e);
		}
		return false;
	}


	public static String getServerResponse(String urlRequest) {

		Log.d("urlRequest", urlRequest);
		String response = "";
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(urlRequest).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			response = read(conn.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("response", response);
		return response.trim();
	}


	private static String read(InputStream in) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}


	/**
	 * Gets the downloading estimates.
	 * 
	 * @param statTime
	 *            the stat time
	 * @param expectedBytes
	 *            the expected bytes
	 * @param bytesReceived
	 *            the bytes received
	 * @param reallTotal
	 *            the reall total
	 * @return the downloading estimates
	 */
	public static String[] getDownloadingEstimates(long statTime, int expectedBytes, int bytesReceived,
	        long reallTotal) {

		final String facts[] = new String[5];
		try {
			String totalBytesStr;
			String bytesRecStr;
			String speedStr;
			String timeRemainingStr;

			if (bytesReceived < 1024) {
				bytesRecStr = String.format("%d B", bytesReceived);
			} else if (bytesReceived < 1024 * 1024) {
				final float value = (float) bytesReceived / 1024;
				bytesRecStr = String.format("%1.2f KB", value);
			} else {
				final float value = (float) bytesReceived / (1024 * 1024);
				bytesRecStr = String.format("%1.2f MB", value);
			}

			if (expectedBytes < 1024) {
				totalBytesStr = String.format("%d B", expectedBytes);
			} else if (expectedBytes < 1024 * 1024) {
				final float value = (float) expectedBytes / 1024;
				totalBytesStr = String.format("%1.2f KB", value);
			} else {
				final float value = (float) expectedBytes / (1024 * 1024);
				totalBytesStr = String.format("%1.2f MB", value);
			}

			long timeInterval = System.currentTimeMillis() - statTime;

			timeInterval = timeInterval / 1000;
			if (timeInterval == 0) {
				timeInterval = 1;
			}
			float speed = 0.0f;

			if (reallTotal > 0) {
				speed = reallTotal / timeInterval;
			} else {
				speed = bytesReceived / timeInterval;
			}

			if (speed < 0) {
				speed = speed * -1;
			}

			if (speed < 1024) {
				speedStr = String.format("%1.2f Bytes/s", speed);
			} else if (speed < 1024 * 1024) {
				final float value = speed / 1024;
				speedStr = String.format("%1.2f KB/s", value);
			} else {
				final float value = speed / (1024 * 1024);
				speedStr = String.format("%1.2f MB/s", value);
			}

			int timeRemaining = new Float((expectedBytes - bytesReceived) / speed).intValue();
			if (timeRemaining < 0) {
				timeRemaining = timeRemaining * -1;
			}

			if (timeRemaining < 60) {
				timeRemainingStr = String.format("%d seconds remaining", timeRemaining);
			} else if (timeRemaining < 3600) {
				final int seconds = (timeRemaining % 60);
				timeRemainingStr = String
				        .format("%d minutes, %d seconds remaining", timeRemaining / 60, seconds);
			} else {
				final int minutes = (timeRemaining / 60 % 60);
				timeRemainingStr = String
				        .format("%d hours, %d minutes remaining", timeRemaining / 3600, minutes);
			}

			facts[0] = bytesRecStr;
			facts[1] = totalBytesStr;
			facts[2] = speedStr;
			facts[3] = timeRemainingStr;
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQUtilityNetwork.TAG, exception);
		}
		return facts;
	}


	/**
	 * Download file.
	 * 
	 * @param in
	 *            the in
	 * @param sizeOfFile
	 *            the size of file
	 * @param localFilePath
	 *            the local file path
	 * @param fileName
	 *            the file name
	 * @param asynFileDwonloader
	 *            the asyn file dwonloader
	 * @param context
	 *            the context
	 * @param commonData
	 *            the common data
	 * @param append
	 *            the append
	 * @param downloadedFileLength
	 *            the downloaded file length
	 * @param error
	 *            the error
	 * @return the boolean
	 */
	public static Boolean downloadFile(InputStream in, long sizeOfFile, String localFilePath,
	        String fileName, DQFileDownloader asynFileDwonloader, Context context, DQRequest commonData,
	        boolean append, long downloadedFileLength) {

		boolean downloadSuccess = false;
		File file = null;
		int updateCounter = 0;
		final long startinTime = System.currentTimeMillis();
		final int updateAfterThisTimeIteration = 75;
		final int bytesSkipedLimit = 1000;
		long currentDownloadBytes = 0;
		long previouslyDownloadedFileSize = 0;
		try {
			if (DQUtilityNetwork.isNetworkAvailable(context)) {

				file = new File(localFilePath, "");
				file.mkdirs();
				if (commonData.isCanEncrypt() == true) {
					return new DQEncryptionAndDownloadManager(context)
					        .fixedSizedEncryption(in, localFilePath + fileName, sizeOfFile, asynFileDwonloader, append, downloadedFileLength, commonData);
				} else {
					file = new File(localFilePath, fileName);

					final FileOutputStream fos = new FileOutputStream(file, append);

					if (append) {
						previouslyDownloadedFileSize = downloadedFileLength;
					}

					final byte buf[] = new byte[DQDesEncrypter.BUFF_SIZE];
					int byteReadedCount = 0;
					do {
						if (asynFileDwonloader.stopDownloading == false) {
							byteReadedCount = in.read(buf);
							if (byteReadedCount < 0) {
								break;
							}
							previouslyDownloadedFileSize += byteReadedCount;
							if (currentDownloadBytes != -1) {
								currentDownloadBytes = currentDownloadBytes + byteReadedCount;
							}
							if (updateCounter >= updateAfterThisTimeIteration) {
								updateCounter = 0;
								final double percentage = previouslyDownloadedFileSize * 100 / sizeOfFile;
								asynFileDwonloader.updateProgress((int) percentage);
								final String[] allEstimatedData = DQUtilityNetwork
								        .getDownloadingEstimates(startinTime, (int) sizeOfFile, (int) previouslyDownloadedFileSize, currentDownloadBytes);
								allEstimatedData[4] = "" + percentage;
								asynFileDwonloader.updateDownloadingDetails(allEstimatedData);
							}
							updateCounter++;
							fos.write(buf, 0, byteReadedCount);
						} else {
							break;
						}
					} while (true && asynFileDwonloader.stopDownloading == false);

					fos.close();
					if (previouslyDownloadedFileSize > sizeOfFile - bytesSkipedLimit && previouslyDownloadedFileSize > 0) {
						downloadSuccess = true;

					} else {
						downloadSuccess = false;
					}
					
				}
			} else {
				// Display message of network not available in callerAsyncTask
				asynFileDwonloader.whichMessageToDisplay = DQErrors.NETWORK_NO_AVILABLE;
			}

		} catch (final SocketTimeoutException exception) {
			DQDebugHelper.printAndTrackException(context, exception);
		} catch (final FileNotFoundException exception) {

			asynFileDwonloader.whichMessageToDisplay = DQErrors.FILE_NOT_FOUND;
			DQDebugHelper.printAndTrackException(context, exception);

		} catch (final IOException exception) {
			DQDebugHelper.printAndTrackException(context, exception);
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(context, exception);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException exception) {
				DQDebugHelper.printAndTrackException(DQUtilityNetwork.TAG, exception);
			}
			final long valueOf300KB = DQExternalStorageHandler.SIZE_KB * 300;
			if (previouslyDownloadedFileSize > valueOf300KB) {
				commonData.setPartialDownloaded(true);
			}
			commonData.setDownloading(false);
		}
		return downloadSuccess;
	}

	// public static int downloadFile(Context context, DQRequest request ,
	// DQAsyncFileDownloader asynctask) {
	//
	// String fileURL = request.getFileUrl();
	// URL url = new URL(fileURL);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	//
	//
	// long fileSize = -1;
	// int status = DQErrors.NO_ERROR.value();
	// String fileName = request.getFileName();
	// String destPath = request.getFileFolderPath();
	// boolean append = false;
	//
	// fileSize = conn.getContentLength();
	// if (fileSize < 0) {
	// status= DQErrors.FILE_NOT_FOUND.value();
	// return status;
	// }
	//
	// final long sizeIncreasedAfterEncryption = DQAppConstants.KB_TO_ENCRYPT * 8;
	// if(conn != null){
	// conn.disconnect();
	// conn = (HttpURLConnection) url.openConnection();
	// }
	//
	// File isPending = new File(destPath, fileName);
	// long fileDownloadedSize = -1;
	// if (isPending != null && isPending.exists()) {
	// fileDownloadedSize = isPending.length();
	// long valueOf300KB = DQExternalStorageHandler.SIZE_KB * 300;
	// if (fileDownloadedSize > (valueOf300KB)) {
	// conn.setRequestProperty("Range", "bytes="+(fileDownloadedSize -
	// sizeIncreasedAfterEncryption)+"-");
	// append = true;
	// }
	// }
	//
	// InputStream in = conn.getInputStream();
	//
	// long availableSpace =
	// DQExternalStorageHandler.getExternalStorageAvailableSpace();
	//
	// // 4*256 is the size increased in bytes i.e.1K
	// // due to encryption
	// if (availableSpace < fileSize + (sizeIncreasedAfterEncryption)) {
	// status= DQErrors.EXTERNAL_STORAGE_INSUFFICIENT_SPACE.value();
	// return status;
	// }
	//
	// int tempCount = 0;
	// do {
	// if (DQExternalStorageHandler.isExternalStorageAvailable() == false) {
	// status = DQErrors.EXTERNAL_STORAGE_NOT_AVAILABLE.value();
	// break;
	// } else if (DQExternalStorageHandler.isExternalStorageWritable() == false) {
	// status = DQErrors.EXTERNAL_STORAGE_NOT_WRITABLE.value();
	// break;
	// }
	// request.setTotalSize(fileSize);
	// asynctask.queueListener.onDownloadStart(request.getKey(), (int)
	// fileSize);
	//
	// boolean downloadSuccess = false;
	// File file = null;
	// int updateCounter = 0;
	// final long startinTime = System.currentTimeMillis();
	// final int updateAfterThisTimeIteration = 50;
	// final int bytesSkipedLimit = 1000;
	// long currentDownloadBytes = 0;
	// long previouslyDownloadedFileSize = 0;
	// tempCount++;
	// file = new File(localFilePath, "");
	// file.mkdirs();
	// if (commonData.isCanEncrypt() == true) {
	// return new DQEncryptionAndDownloadManager(context)
	// .fixedSizedEncryption(in, localFilePath + fileName,
	// sizeOfFile, asynFileDwonloader, append,
	// downloadedFileLength, commonData, error);
	// } else {
	// file = new File(localFilePath, fileName);
	//
	// final FileOutputStream fos = new FileOutputStream(file,
	// append);
	//
	// if (append) {
	// previouslyDownloadedFileSize = downloadedFileLength;
	// }
	//
	// final byte buf[] = new byte[DQDesEncrypter.BUFF_SIZE];
	// int byteReadedCount = 0;
	// do {
	// if (asynFileDwonloader.stopDownloading == false) {
	// byteReadedCount = in.read(buf);
	// if (byteReadedCount < 0) {
	// break;
	// }
	// previouslyDownloadedFileSize += byteReadedCount;
	// if (currentDownloadBytes != -1) {
	// currentDownloadBytes = currentDownloadBytes
	// + byteReadedCount;
	// }
	// if (updateCounter >= updateAfterThisTimeIteration) {
	// updateCounter = 0;
	// final double percentage = previouslyDownloadedFileSize
	// * 100 / sizeOfFile;
	// asynFileDwonloader
	// .updateProgress((int) percentage);
	// final String[] allEstimatedData = DQUtilityNetwork
	// .getDownloadingEstimates(
	// startinTime,
	// (int) sizeOfFile,
	// (int) previouslyDownloadedFileSize,
	// currentDownloadBytes);
	// allEstimatedData[4] = "" + percentage;
	// asynFileDwonloader
	// .updateDownloadingDetails(allEstimatedData);
	// }
	// updateCounter++;
	// fos.write(buf, 0, byteReadedCount);
	// } else {
	// break;
	// }
	// } while (true && asynFileDwonloader.stopDownloading == false);
	//
	// fos.close();
	// if (previouslyDownloadedFileSize > sizeOfFile
	// - bytesSkipedLimit
	// && previouslyDownloadedFileSize > 0) {
	// downloadSuccess = true;
	//
	// } else {
	// downloadSuccess = false;
	// }
	// }
	// } else {
	// // Display message of network not available in callerAsyncTask
	// status= DQErrors.NETWORK_NO_AVILABLE;
	// }
	// } while (tempCount < 1);
	// if (loadSuccess == false) {
	// Log.i(TAG, "Falid to download");
	// }
	// }
	// } else {
	// status = DQErrors.NETWORK_WEAK.;
	// }
	//
	//
	// try {
	// if (DQUtilityNetwork.isNetworkAvailable(context)) {
	//
	// file = new File(localFilePath, "");
	// file.mkdirs();
	// if (commonData.isCanEncrypt() == true) {
	// return new DQEncryptionAndDownloadManager(context)
	// .fixedSizedEncryption(in, localFilePath + fileName,
	// sizeOfFile, asynFileDwonloader, append,
	// downloadedFileLength, commonData, error);
	// } else {
	// file = new File(localFilePath, fileName);
	//
	// final FileOutputStream fos = new FileOutputStream(file,
	// append);
	//
	// if (append) {
	// previouslyDownloadedFileSize = downloadedFileLength;
	// }
	//
	// final byte buf[] = new byte[DQDesEncrypter.BUFF_SIZE];
	// int byteReadedCount = 0;
	// do {
	// if (asynFileDwonloader.stopDownloading == false) {
	// byteReadedCount = in.read(buf);
	// if (byteReadedCount < 0) {
	// break;
	// }
	// previouslyDownloadedFileSize += byteReadedCount;
	// if (currentDownloadBytes != -1) {
	// currentDownloadBytes = currentDownloadBytes
	// + byteReadedCount;
	// }
	// if (updateCounter >= updateAfterThisTimeIteration) {
	// updateCounter = 0;
	// final double percentage = previouslyDownloadedFileSize
	// * 100 / sizeOfFile;
	// asynFileDwonloader
	// .updateProgress((int) percentage);
	// final String[] allEstimatedData = DQUtilityNetwork
	// .getDownloadingEstimates(
	// startinTime,
	// (int) sizeOfFile,
	// (int) previouslyDownloadedFileSize,
	// currentDownloadBytes);
	// allEstimatedData[4] = "" + percentage;
	// asynFileDwonloader
	// .updateDownloadingDetails(allEstimatedData);
	// }
	// updateCounter++;
	// fos.write(buf, 0, byteReadedCount);
	// } else {
	// break;
	// }
	// } while (true && asynFileDwonloader.stopDownloading == false);
	//
	// fos.close();
	// if (previouslyDownloadedFileSize > sizeOfFile
	// - bytesSkipedLimit
	// && previouslyDownloadedFileSize > 0) {
	// downloadSuccess = true;
	//
	// } else {
	// downloadSuccess = false;
	// }
	// }
	// } else {
	// // Display message of network not available in callerAsyncTask
	// status= DQErrors.NETWORK_NO_AVILABLE;
	// }
	//
	// } catch (final SocketTimeoutException exception) {
	// DQDebugHelper.printAndTrackException(context, exception);
	// } catch (final FileNotFoundException exception) {
	//
	// status= DQErrors.FILE_NOT_FOUND;
	// DQDebugHelper.printAndTrackException(context, exception);
	//
	// } catch (final IOException exception) {
	// DQDebugHelper.printAndTrackException(context, exception);
	// } catch (final Exception exception) {
	// DQDebugHelper.printAndTrackException(context, exception);
	// } finally {
	// try {
	// if (in != null) {
	// in.close();
	// }
	// } catch (final IOException exception) {
	// DQDebugHelper.printAndTrackException(DQUtilityNetwork.TAG,
	// exception);
	// }
	// final long valueOf300KB = DQExternalStorageHandler.SIZE_KB * 300;
	// if (previouslyDownloadedFileSize > valueOf300KB) {
	// commonData.setPartialDownloaded(true);
	// }
	// }
	// return downloadSuccess;
	// }

	// public static int downloadFile(Context context, DQRequest request ,
	// DQAsyncFileDownloader asynctask) {
	//
	// String fileURL = request.getFileUrl();
	// URL url = new URL(fileURL);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	//
	//
	// long fileSize = -1;
	// int status = DQErrors.NO_ERROR.value();
	// String fileName = request.getFileName();
	// String destPath = request.getFileFolderPath();
	// boolean append = false;
	//
	// fileSize = conn.getContentLength();
	// if (fileSize < 0) {
	// status= DQErrors.FILE_NOT_FOUND.value();
	// return false;
	// }
	//
	// final long sizeIncreasedAfterEncryption = DQAppConstants.KB_TO_ENCRYPT * 8;
	// if(conn != null){
	// conn.disconnect();
	// conn = (HttpURLConnection) url.openConnection();
	// }
	//
	// File isPending = new File(destPath, fileName);
	// long fileDownloadedSize = -1;
	// if (isPending != null && isPending.exists()) {
	// fileDownloadedSize = isPending.length();
	// long valueOf300KB = DQExternalStorageHandler.SIZE_KB * 300;
	// if (fileDownloadedSize > (valueOf300KB)) {
	// conn.setRequestProperty("Range", "bytes="+(fileDownloadedSize -
	// sizeIncreasedAfterEncryption)+"-");
	// append = true;
	// }
	// }
	//
	// InputStream in = conn.getInputStream();
	//
	// long availableSpace =
	// DQExternalStorageHandler.getExternalStorageAvailableSpace();
	//
	// // 4*256 is the size increased in bytes i.e.1K
	// // due to encryption
	// if (availableSpace < fileSize + (sizeIncreasedAfterEncryption)) {
	// status= DQErrors.EXTERNAL_STORAGE_INSUFFICIENT_SPACE.value();
	// return status;
	// }
	//
	// int tempCount = 0;
	// do {
	// if (DQExternalStorageHandler.isExternalStorageAvailable() == false) {
	// status = DQErrors.EXTERNAL_STORAGE_NOT_AVAILABLE.value();
	// break;
	// } else if (DQExternalStorageHandler.isExternalStorageWritable() == false) {
	// status = DQErrors.EXTERNAL_STORAGE_NOT_WRITABLE.value();
	// break;
	// }
	// request.setTotalSize(fileSize);
	// asynctask.queueListener.onDownloadStart(request.getKey(), (int)
	// fileSize);
	//
	// boolean downloadSuccess = false;
	// File file = null;
	// int updateCounter = 0;
	// final long startinTime = System.currentTimeMillis();
	// final int updateAfterThisTimeIteration = 50;
	// final int bytesSkipedLimit = 1000;
	// long currentDownloadBytes = 0;
	// long previouslyDownloadedFileSize = 0;
	// tempCount++;
	// file = new File(localFilePath, "");
	// file.mkdirs();
	// if (commonData.isCanEncrypt() == true) {
	// return new DQEncryptionAndDownloadManager(context)
	// .fixedSizedEncryption(in, localFilePath + fileName,
	// sizeOfFile, asynFileDwonloader, append,
	// downloadedFileLength, commonData, error);
	// } else {
	// file = new File(localFilePath, fileName);
	//
	// final FileOutputStream fos = new FileOutputStream(file,
	// append);
	//
	// if (append) {
	// previouslyDownloadedFileSize = downloadedFileLength;
	// }
	//
	// final byte buf[] = new byte[DQDesEncrypter.BUFF_SIZE];
	// int byteReadedCount = 0;
	// do {
	// if (asynFileDwonloader.stopDownloading == false) {
	// byteReadedCount = in.read(buf);
	// if (byteReadedCount < 0) {
	// break;
	// }
	// previouslyDownloadedFileSize += byteReadedCount;
	// if (currentDownloadBytes != -1) {
	// currentDownloadBytes = currentDownloadBytes
	// + byteReadedCount;
	// }
	// if (updateCounter >= updateAfterThisTimeIteration) {
	// updateCounter = 0;
	// final double percentage = previouslyDownloadedFileSize
	// * 100 / sizeOfFile;
	// asynFileDwonloader
	// .updateProgress((int) percentage);
	// final String[] allEstimatedData = DQUtilityNetwork
	// .getDownloadingEstimates(
	// startinTime,
	// (int) sizeOfFile,
	// (int) previouslyDownloadedFileSize,
	// currentDownloadBytes);
	// allEstimatedData[4] = "" + percentage;
	// asynFileDwonloader
	// .updateDownloadingDetails(allEstimatedData);
	// }
	// updateCounter++;
	// fos.write(buf, 0, byteReadedCount);
	// } else {
	// break;
	// }
	// } while (true && asynFileDwonloader.stopDownloading == false);
	//
	// fos.close();
	// if (previouslyDownloadedFileSize > sizeOfFile
	// - bytesSkipedLimit
	// && previouslyDownloadedFileSize > 0) {
	// downloadSuccess = true;
	//
	// } else {
	// downloadSuccess = false;
	// }
	// }
	// } else {
	// // Display message of network not available in callerAsyncTask
	// status= DQErrors.NETWORK_NO_AVILABLE;
	// }
	// } while (tempCount < 1);
	// if (loadSuccess == false) {
	// Log.i(TAG, "Falid to download");
	// }
	// }
	// } else {
	// status = DQErrors.NETWORK_WEAK.;
	// }
	//
	//
	// try {
	// if (DQUtilityNetwork.isNetworkAvailable(context)) {
	//
	// file = new File(localFilePath, "");
	// file.mkdirs();
	// if (commonData.isCanEncrypt() == true) {
	// return new DQEncryptionAndDownloadManager(context)
	// .fixedSizedEncryption(in, localFilePath + fileName,
	// sizeOfFile, asynFileDwonloader, append,
	// downloadedFileLength, commonData, error);
	// } else {
	// file = new File(localFilePath, fileName);
	//
	// final FileOutputStream fos = new FileOutputStream(file,
	// append);
	//
	// if (append) {
	// previouslyDownloadedFileSize = downloadedFileLength;
	// }
	//
	// final byte buf[] = new byte[DQDesEncrypter.BUFF_SIZE];
	// int byteReadedCount = 0;
	// do {
	// if (asynFileDwonloader.stopDownloading == false) {
	// byteReadedCount = in.read(buf);
	// if (byteReadedCount < 0) {
	// break;
	// }
	// previouslyDownloadedFileSize += byteReadedCount;
	// if (currentDownloadBytes != -1) {
	// currentDownloadBytes = currentDownloadBytes
	// + byteReadedCount;
	// }
	// if (updateCounter >= updateAfterThisTimeIteration) {
	// updateCounter = 0;
	// final double percentage = previouslyDownloadedFileSize
	// * 100 / sizeOfFile;
	// asynFileDwonloader
	// .updateProgress((int) percentage);
	// final String[] allEstimatedData = DQUtilityNetwork
	// .getDownloadingEstimates(
	// startinTime,
	// (int) sizeOfFile,
	// (int) previouslyDownloadedFileSize,
	// currentDownloadBytes);
	// allEstimatedData[4] = "" + percentage;
	// asynFileDwonloader
	// .updateDownloadingDetails(allEstimatedData);
	// }
	// updateCounter++;
	// fos.write(buf, 0, byteReadedCount);
	// } else {
	// break;
	// }
	// } while (true && asynFileDwonloader.stopDownloading == false);
	//
	// fos.close();
	// if (previouslyDownloadedFileSize > sizeOfFile
	// - bytesSkipedLimit
	// && previouslyDownloadedFileSize > 0) {
	// downloadSuccess = true;
	//
	// } else {
	// downloadSuccess = false;
	// }
	// }
	// } else {
	// // Display message of network not available in callerAsyncTask
	// status= DQErrors.NETWORK_NO_AVILABLE;
	// }
	//
	// } catch (final SocketTimeoutException exception) {
	// DQDebugHelper.printAndTrackException(context, exception);
	// } catch (final FileNotFoundException exception) {
	//
	// status= DQErrors.FILE_NOT_FOUND;
	// DQDebugHelper.printAndTrackException(context, exception);
	//
	// } catch (final IOException exception) {
	// DQDebugHelper.printAndTrackException(context, exception);
	// } catch (final Exception exception) {
	// DQDebugHelper.printAndTrackException(context, exception);
	// } finally {
	// try {
	// if (in != null) {
	// in.close();
	// }
	// } catch (final IOException exception) {
	// DQDebugHelper.printAndTrackException(DQUtilityNetwork.TAG,
	// exception);
	// }
	// final long valueOf300KB = DQExternalStorageHandler.SIZE_KB * 300;
	// if (previouslyDownloadedFileSize > valueOf300KB) {
	// commonData.setPartialDownloaded(true);
	// }
	// }
	// return downloadSuccess;
	// }
}
