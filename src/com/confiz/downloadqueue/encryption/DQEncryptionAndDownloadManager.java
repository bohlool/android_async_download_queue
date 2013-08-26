/**
 * 
 */

package com.confiz.downloadqueue.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.confiz.downloadqueue.DQAsyncFileDownloader;
import com.confiz.downloadqueue.DQFileDownloader;
import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQDebugHelper;
import com.confiz.downloadqueue.utils.DQErrors;
import com.confiz.downloadqueue.utils.DQUtilityNetwork;


/**
 * The Class DQEncryptionAndDownloadManager.
 */
public class DQEncryptionAndDownloadManager {


	/** The Constant TAG. */
	private static final String TAG = "DQEncryptionAndDownloadManager";

	/** The context. */
	Context mContext = null;

	/** The encrypter. */
	DQDesEncrypter encrypter = null;

	/** The update after this time iteration. */
	final int updateAfterThisTimeIteration = 50;

	/** The bytes skiped limit. */
	final int bytesSkipedLimit = 1000;


	/**
	 * Instantiates a new encryption and download manager.
	 * 
	 * @param mContext
	 *            The context
	 */
	public DQEncryptionAndDownloadManager(Context mContext) {

		super();
		this.mContext = mContext;
		this.encrypter = DQDesEncrypterFactory.getDesEncrypter(this.mContext);
	}

	/**
	 * The number of initial KB of data which we have to encrypt, and then
	 * decrypt.
	 */
	public static final int KB_TO_ENCRYPT = 256;

	/** The buff size. */
	private final int BUFF_SIZE = 1024;

	/** The buffer. */
	private final byte[] mBuffer = new byte[this.BUFF_SIZE];

	/** The buffer eecrypted. */
	public byte[] mBufferEecrypted = new byte[DQEncryptionAndDownloadManager.BUFF_SIZE_ENC];

	// private long lengthOfFile = 0;
	/**
	 * The count of our calls to updateProgress() function of calling task.
	 */
	public final int NUM_OF_CALLS_TO_UPDATEPROGRESS = 101;

	/**
	 * <p>
	 * <b>BUFF_SIZE_ENC is 8bytes large than BUFF_SIZE.</b>
	 * </p>
	 * <p>
	 * When we encrypt data by our encrypted class our encrypted data is 8 bytes larger than plain
	 * data. So we have accomodated these lines in Buffer size.
	 * </p>
	 */
	static final int BUFF_SIZE_ENC = DQDesEncrypter.BUFF_SIZE + 8;


	/**
	 * This Method Encrypts first few bytes of data and copies the rest of data.
	 * We have devised this to enhance the performance of our algorithm. Number
	 * of bytes encrypted is specified in <b>KB_TO_ENCRYPT</b>
	 * 
	 * @param ins
	 *            Input Stream to be encrypted.
	 * @param out
	 *            Name of output file where we have to store encrypted data
	 * @param lengthOfFile
	 *            Length of Input stream. This is used to update progress on
	 *            progressIndicator.
	 * @param asynFileDwonloader
	 *            the asyn file dwonloader
	 * @param append
	 *            the append
	 * @param oldLength
	 *            the old length
	 * @param commonData
	 *            the common data
	 * @param dErrors
	 *            the d errors
	 * @return <ul>
	 *         <li><b>True</b> When file is encrypted successfyly.</li>
	 *         <li><b>False</b> When file encryption is failed.</li>
	 *         </ul>
	 */
	public boolean fixedSizedEncryption(InputStream ins, String out, long lengthOfFile, DQFileDownloader asynFileDwonloader,
	        boolean append, long oldLength, DQRequest commonData) {

		boolean downloadSuccess = false;

		try {

			final long startTime = System.currentTimeMillis();
			final OutputStream outs = new FileOutputStream(out, append);
			/*
			 * Total bytes of file read.
			 */
			long realTotal = 0;
			long total = 0;
			if (append) {
				total = oldLength;
			}
			/*
			 * <b>count</b> moves from <b>0</b> to <b>kbToUpdateProgress</b>
			 * when it is equal to kbToUpdateProgress we call updateProgress
			 * method and set count=0.
			 */
			int updateCounter = 0;
			/*
			 * number of bytes read on each read call to input stream.
			 */
			int numRead = 0;
			byte[] buffer = null;
			/*
			 * Loop to encrypt <b>KB_TO_ENCRYPT</b> KB of data.
			 */
			for (int i = 0; (numRead = this.readFullBuffer(this.mBuffer, ins)) >= 0 && asynFileDwonloader.stopDownloading == false; i++) {
				if (i < DQEncryptionAndDownloadManager.KB_TO_ENCRYPT && !append) {
					if (numRead >= this.BUFF_SIZE) {
						buffer = this.encrypter.eByteArray(this.mBuffer);
					} else {
						buffer = this.encrypter.eByteArray(this.subArray(this.mBuffer, numRead));
					}
				} else {
					buffer = this.mBuffer;
				}

				total += numRead;
				realTotal += numRead;
				if (outs != null && buffer != null) {
					outs.write(buffer, 0, buffer.length);
				}
				if (updateCounter >= this.updateAfterThisTimeIteration) {
					updateCounter = 0;
					final double percentage = total * 100 / lengthOfFile;
					asynFileDwonloader.updateProgress((int) percentage);
					final String[] allEstimatedData = DQUtilityNetwork
					        .getDownloadingEstimates(startTime, (int) lengthOfFile, (int) total, realTotal);
					allEstimatedData[4] = "" + percentage;
					asynFileDwonloader.updateProgress((int) percentage);
					asynFileDwonloader.updateDownloadingDetails(allEstimatedData);
				}
				updateCounter++;

			}
			outs.close();
			if (total > lengthOfFile - this.bytesSkipedLimit) {
				Log.i("Download", "Complete");
				downloadSuccess = true;
			} else {
				Log.i("Download", "IN-COMPLETE :: total: " + total + " lenghtOfFile: " + lengthOfFile);
			}
		} catch (final FileNotFoundException exception) {
			DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
			commonData.currentError = DQErrors.FILE_NOT_FOUND;
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
		} finally {
			asynFileDwonloader = null;
			try {
				ins.close();
			} catch (final IOException exception) {
				DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
			}
			commonData.setDownloading(false);
		}
		return downloadSuccess;
	}


	/**
	 * This Method Encrypts first few bytes of data and copies the rest of data.
	 * We have devised this to enhance the performance of our algorithm. Number
	 * of bytes encrypted is specified in <b>KB_TO_ENCRYPT</b>
	 * 
	 * @param ins
	 *            Input Stream to be encrypted.
	 * @param out
	 *            Name of output file where we have to store encrypted data
	 * @param lengthOfFile
	 *            Length of Input stream. This is used to update progress on
	 *            progressIndicator.
	 * @param asynFileDwonloader
	 *            the asyn file dwonloader
	 * @param append
	 *            the append
	 * @param oldLength
	 *            the old length
	 * @param commonData
	 *            the common data
	 * @param dErrors
	 *            the d errors
	 * @return <ul>
	 *         <li><b>True</b> When file is encrypted successfyly.</li>
	 *         <li><b>False</b> When file encryption is failed.</li>
	 *         </ul>
	 */
	public boolean fixedSizedEncryption(InputStream ins, String out, long lengthOfFile, DQAsyncFileDownloader asynFileDwonloader,
	        boolean append, long oldLength) {

		boolean downloadSuccess = false;

		try {

			final long startTime = System.currentTimeMillis();
			final OutputStream outs = new FileOutputStream(out, append);
			/*
			 * Total bytes of file read.
			 */
			long realTotal = 0;
			long total = 0;
			if (append) {
				total = oldLength;
			}
			/*
			 * <b>count</b> moves from <b>0</b> to <b>kbToUpdateProgress</b>
			 * when it is equal to kbToUpdateProgress we call updateProgress
			 * method and set count=0.
			 */
			int updateCounter = 0;
			/*
			 * number of bytes read on each read call to input stream.
			 */
			int numRead = 0;
			byte[] buffer = null;
			/*
			 * Loop to encrypt <b>KB_TO_ENCRYPT</b> KB of data.
			 */
			for (int i = 0; (numRead = this.readFullBuffer(this.mBuffer, ins)) >= 0 && asynFileDwonloader.stopDownloading == false; i++) {
				if (i < DQEncryptionAndDownloadManager.KB_TO_ENCRYPT && !append) {
					if (numRead >= this.BUFF_SIZE) {
						buffer = this.encrypter.eByteArray(this.mBuffer);
					} else {
						buffer = this.encrypter.eByteArray(this.subArray(this.mBuffer, numRead));
					}
				} else {
					buffer = this.mBuffer;
				}

				total += numRead;
				realTotal += numRead;
				if (outs != null && buffer != null) {
					outs.write(buffer, 0, buffer.length);
				}
				if (updateCounter >= this.updateAfterThisTimeIteration) {
					updateCounter = 0;
					final double percentage = total * 100 / lengthOfFile;
					asynFileDwonloader.updateProgress((int) percentage);
					final String[] allEstimatedData = DQUtilityNetwork
					        .getDownloadingEstimates(startTime, (int) lengthOfFile, (int) total, realTotal);
					allEstimatedData[4] = "" + percentage;
					asynFileDwonloader.updateProgress((int) percentage);
					asynFileDwonloader.updateDownloadingDetails(allEstimatedData);
				}
				updateCounter++;

			}
			outs.close();
			if (total > lengthOfFile - this.bytesSkipedLimit) {
				Log.i("Download", "Complete");
				downloadSuccess = true;
			} else {
				Log.i("Download", "IN-COMPLETE :: total: " + total + " lenghtOfFile: " + lengthOfFile);
			}
		} catch (final FileNotFoundException exception) {
			DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
			// dErrors.currentError = DQErrors.FILE_NOT_FOUND;
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
		} finally {
			asynFileDwonloader = null;
			try {
				ins.close();
			} catch (final IOException exception) {
				DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
			}
		}
		return downloadSuccess;
	}


	/**
	 * This Method Decrypts first few bytes of data and copies the rest of data.
	 * We have devised this to enhance the performance of our algorithm. Number
	 * of bytes decrypted is specified in <b>KB_TO_ENCRYPT</b>
	 * 
	 * @param in
	 *            the in
	 * @param out
	 *            Name of output file where we have to store decrypted data
	 * @param asyncDecryptFile
	 *            the async decrypt file
	 */
	public void fixedSizedDecryption(String in, String out, DQAsyncDecryptFile asyncDecryptFile) {

		try {
			final int lengthOfFile = (int) new File(in).length();

			final InputStream ins = new FileInputStream(in);
			final OutputStream outs = new FileOutputStream(out);
			/*
			 * number of bytes read on each read call to input stream.
			 */
			int numRead = 0;
			/*
			 * Total bytes of file read.
			 */
			long total = 0;
			/*
			 * <b>count</b> moves from <b>0</b> to <b>kbToUpdateProgress</b>
			 * when it is equal to kbToUpdateProgress we call updateProgress
			 * method and set count=0.
			 */
			int count = 0;
			/*
			 * <b>kbToUpdateProgress</b> is an integer it contains the number of
			 * KB after which we would call updateProgress method.
			 */
			final long kbToUpdateProgress = lengthOfFile / (this.BUFF_SIZE * this.NUM_OF_CALLS_TO_UPDATEPROGRESS);
			byte buffer[] = null;
			/*
			 * Loop to decrypt <b>KB_TO_ENCRYPT</b> KB of data.
			 */
			for (int i = 0; i < DQEncryptionAndDownloadManager.KB_TO_ENCRYPT && (numRead = ins.read(this.mBufferEecrypted)) >= 0; i++) {
				if (numRead >= DQEncryptionAndDownloadManager.BUFF_SIZE_ENC) {
					buffer = this.encrypter.dByteArray(this.mBufferEecrypted);
				} else {
					buffer = this.encrypter.dByteArray(this.subArray(this.mBufferEecrypted, numRead));
				}
				if (buffer == null) {
					asyncDecryptFile.whichMessageToDisplay = 2;
					return;
				} else {
					outs.write(buffer, 0, buffer.length);
					total += numRead;
					if (count >= kbToUpdateProgress) {
						count = 0;
						asyncDecryptFile.updateProgress((int) (total * 100 / lengthOfFile));
					}
					count++;
				}
			}
			/*
			 * Loop to copy rest of the data which we do not encrypt.
			 */
			while ((numRead = ins.read(this.mBuffer)) >= 0 && asyncDecryptFile.forceStop == false) {
				outs.write(this.mBuffer, 0, numRead);
				total += numRead;
				if (count >= kbToUpdateProgress) {
					count = 0;
					asyncDecryptFile.updateProgress((int) (total * 100 / lengthOfFile));
				}
				count++;
			}
			outs.close();
			ins.close();
			Log.i("Special Decryption", "Completed");
		} catch (final FileNotFoundException exception) {
			DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQEncryptionAndDownloadManager.TAG, exception);
		} finally {
			asyncDecryptFile = null;
		}
	}


	/**
	 * Read full buffer.
	 * 
	 * @param mBuffer
	 *            The buffer
	 * @param ins
	 *            the ins
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	private int readFullBuffer(byte[] mBuffer, InputStream ins) throws Exception {

		/**
		 * number of bytes read on each read call to input stream.
		 */
		int numRead = 0;
		int tempNumRead = 0;

		while (true) {
			tempNumRead = ins.read(mBuffer, numRead, this.BUFF_SIZE - numRead);
			if (tempNumRead > 0) {
				numRead += tempNumRead;
			}
			if (numRead >= this.BUFF_SIZE || tempNumRead < 0) {
				break;
			}
		}

		if (numRead == 0 && tempNumRead == -1) {
			numRead = -1;
		}
		return numRead;
	}


	/**
	 * Method to return a sub-array of byte array.
	 * 
	 * @param arr
	 *            byte array
	 * @param length
	 *            Number of first bytes to be returned.
	 * @return byte array containing only bytes specified in <b>length</b>
	 *         parameter.
	 */
	private byte[] subArray(byte[] arr, int length) {

		final byte temp[] = new byte[length];
		for (int i = 0; i < length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

}
