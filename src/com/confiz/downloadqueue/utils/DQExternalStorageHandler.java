/*
 * Property    : Confiz Solutions
 * Created by  : Arslan Anwar
 * Updated by  : Arslan Anwar
 * 
 */

package com.confiz.downloadqueue.utils;

import android.os.Environment;
import android.os.StatFs;


/**
 * The Class DQExternalStorageHandler.
 * 
 * @author Muhammad Arslan Anwar
 */
public class DQExternalStorageHandler {


	/** The Constant TAG. */
	private static final String TAG = "DQExternalStorageHandler";

	/** The stat. */
	private static StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

	/** Number of bytes in one KB = 2<sup>10</sup>. */
	public final static long SIZE_KB = 1024L;

	/** Number of bytes in one MB = 2<sup>20</sup>. */
	public final static long SIZE_MB = DQExternalStorageHandler.SIZE_KB * DQExternalStorageHandler.SIZE_KB;

	/** Number of bytes in one GB = 2<sup>30</sup>. */
	public final static long SIZE_GB = DQExternalStorageHandler.SIZE_KB * DQExternalStorageHandler.SIZE_KB * DQExternalStorageHandler.SIZE_KB;


	/**
	 * Checks if is external storage available.
	 * 
	 * @return <ul>
	 *         <li><b>true: </b>If external storage is available</li>
	 *         <li><b>false: </b>If external storage is not available</li>
	 *         </ul>
	 */
	public static boolean isExternalStorageAvailable() {

		boolean isAvailable = false;
		try {
			final String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can read the media
				isAvailable = true;
			} else {
				// Something else is wrong. It may be one of many other states,
				// but all we need
				// to know is we cannot read
				isAvailable = false;
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return isAvailable;
	}


	/**
	 * Checks if is external storage writable.
	 * 
	 * @return <ul>
	 *         <li><b>true: </b>If external storage is writable</li>
	 *         <li><b>false: </b>If external storage is not writable</li>
	 *         </ul>
	 */
	public static boolean isExternalStorageWritable() {

		boolean isWriteable = false;
		try {
			final String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can write the media
				isWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media but we can't write
				isWriteable = false;
			} else {
				// Something else is wrong. It may be one of many other
				// states, but all we need
				// to know is we can neither read nor write
				isWriteable = false;
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return isWriteable;
	}


	/**
	 * Gets the external storage total space.
	 * 
	 * @return Size of external storage in bytes
	 */
	public static long getExternalStorageTotalSpace() {

		long temp = -1L;
		try {
			DQExternalStorageHandler.stat.restat(Environment.getExternalStorageDirectory().getPath());
			temp = (long) DQExternalStorageHandler.stat.getBlockCount() * (long) DQExternalStorageHandler.stat
			        .getBlockSize();
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return temp;
	}


	/**
	 * Gets the external storage available space.
	 * 
	 * @return Number of bytes available on external storage
	 */
	public static long getExternalStorageAvailableSpace() {

		long availableSpace = -1L;
		try {
			DQExternalStorageHandler.stat.restat(Environment.getExternalStorageDirectory().getPath());
			availableSpace = (long) DQExternalStorageHandler.stat.getAvailableBlocks() * (long) DQExternalStorageHandler.stat
			        .getBlockSize();
		} catch (final Exception exception) {

			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}
		return availableSpace;
	}


	/**
	 * Gets the external storage used space.
	 * 
	 * @return Number of bytes used by external storage.
	 */
	public static long getExternalStorageUsedSpace() {

		long temp = -1L;
		try {
			DQExternalStorageHandler.stat.restat(Environment.getExternalStorageDirectory().getPath());
			temp = (long) (DQExternalStorageHandler.stat.getBlockCount() - DQExternalStorageHandler.stat
			        .getAvailableBlocks()) * (long) DQExternalStorageHandler.stat.getBlockSize();
		} catch (final Exception exception) {

			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return temp;
	}


	/**
	 * Gets the external storage block size.
	 * 
	 * @return Size of external storage's block in bytes
	 */
	public static long getExternalStorageBlockSize() {

		long temp = -1L;
		try {
			DQExternalStorageHandler.stat.restat(Environment.getExternalStorageDirectory().getPath());
			temp = DQExternalStorageHandler.stat.getBlockSize();
		} catch (final Exception exception) {

			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return temp;
	}


	/**
	 * Gets the external storage block count.
	 * 
	 * @return Total number of blocks on external storage
	 */
	public static long getExternalStorageBlockCount() {

		long temp = -1L;
		try {
			DQExternalStorageHandler.stat.restat(Environment.getExternalStorageDirectory().getPath());
			temp = DQExternalStorageHandler.stat.getBlockCount();
		} catch (final Exception exception) {

			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return temp;
	}


	/**
	 * Gets the external storage available blocks.
	 * 
	 * @return Total number of available blocks on external storage
	 */
	public static long getExternalStorageAvailableBlocks() {

		long temp = -1L;
		try {
			DQExternalStorageHandler.stat.restat(Environment.getExternalStorageDirectory().getPath());
			temp = DQExternalStorageHandler.stat.getAvailableBlocks();
		} catch (final Exception exception) {

			DQDebugHelper.printAndTrackException(DQExternalStorageHandler.TAG, exception);
		}

		return temp;
	}

}
