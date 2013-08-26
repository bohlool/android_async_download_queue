/*
 * Property : Confiz Solutions
 * Created by : Arslan Anwar
 * Updated by : Arslan Anwar
 */

package com.confiz.downloadqueue.utils;

import android.content.Context;
import android.util.Log;


/**
 * The Class DQDebugHelper.
 */
public class DQDebugHelper {


	/** The mode debug. */
	private static boolean MODE_DEBUG = true;

	/** The Constant TAG. */
	private static final String TAG = "DQDebugHelper";


	/**
	 * Prints the and track exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackError(String TAG, Error exception) {

		DQDebugHelper.printAndTrackException(null, TAG, exception);
	}


	/**
	 * Prints the and track error.
	 * 
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackError(Error exception) {

		DQDebugHelper.printAndTrackException(null, DQDebugHelper.TAG, exception);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackException(Exception exception) {

		DQDebugHelper.printAndTrackException(null, DQDebugHelper.TAG, exception);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackException(String TAG, Exception exception) {

		DQDebugHelper.printAndTrackException(null, TAG, exception);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param context
	 *            the context
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackException(Context context, Exception exception) {

		DQDebugHelper.printAndTrackException(context, DQDebugHelper.TAG, exception);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param context
	 *            the context
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackException(Context context, String TAG, Error exception) {

		if (DQDebugHelper.MODE_DEBUG == true) {
			DQDebugHelper.printData(TAG, "Exception = " + exception.toString());
			exception.printStackTrace();
		}
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param context
	 *            the context
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printAndTrackException(Context context, String TAG, Exception exception) {

		if (DQDebugHelper.MODE_DEBUG == true) {
			exception.printStackTrace();
			DQDebugHelper.printData(TAG, "Exception = " + exception.toString());

		}
	}


	/**
	 * Prints the data.
	 * 
	 * @param tag
	 *            the tag
	 * @param data
	 *            the data
	 */
	public static void printData(String tag, String data) {

		if (DQDebugHelper.MODE_DEBUG == true) {
			Log.e("" + tag, "DQDebugHelper = " + data);
		}
	}


	/**
	 * Prints the data.
	 * 
	 * @param data
	 *            the data
	 */
	public static void printData(String data) {

		if (DQDebugHelper.MODE_DEBUG == true) {
			Log.e(DQDebugHelper.TAG, "DQDebugHelper = " + data);
		}
	}


	/**
	 * Prints the exception.
	 * 
	 * @param tag
	 *            the tag
	 * @param string
	 *            the string
	 */
	public static void printException(String tag, String string) {

		DQDebugHelper.printData(tag, string);
	}


	/**
	 * Prints the exception.
	 * 
	 * @param tag
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printException(String tag, Exception exception) {

		DQDebugHelper.printException(null, tag, exception);
	}


	/**
	 * Prints the exception.
	 * 
	 * @param exception
	 *            the exception
	 */
	public static void printException(Exception exception) {

		DQDebugHelper.printException(null, DQDebugHelper.TAG, exception);
	}


	/**
	 * Prints the exception.
	 * 
	 * @param context
	 *            the context
	 * @param exception
	 *            the exception
	 */
	public static void printException(Context context, Exception exception) {

		DQDebugHelper.printException(context, DQDebugHelper.TAG, exception);
	}


	/**
	 * Prints the exception.
	 * 
	 * @param context
	 *            the context
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printException(Context context, String TAG, Exception exception) {

		if (DQDebugHelper.MODE_DEBUG == true) {
			exception.printStackTrace();
			DQDebugHelper.printData(TAG, "Exception = " + exception.toString());
		}
	}


	/**
	 * Prints the data.
	 * 
	 * @param tAG2
	 *            the t a g2
	 * @param string
	 *            the string
	 * @param exception
	 *            the exception
	 */
	public static void printData(String tAG2, String string, Exception exception) {

		DQDebugHelper.printException(null, tAG2, exception);
	}
}
