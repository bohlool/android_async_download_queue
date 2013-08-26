/*
 * Property : Confiz Solutions
 * Created by : Arslan Anwar
 * Updated by : Arslan Anwar
 */

package com.confiz.downloadqueue.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * The Class DQAppPreference.
 */
public class DQAppPreference {


	/** The Constant TAG. */
	private static final String TAG = "DQAppPreference";

	/** The user prefrence. */
	private static SharedPreferences preference = null;


	/**
	 * Inits the prefrence.
	 * 
	 * @param mContext
	 *            The context
	 */
	private static void initPrefrence(Context mContext) {

		try {
			DQAppPreference.preference = mContext.getSharedPreferences(DQAppConstants.DOWNLOAD_PREFRANCE_KEY, Context.MODE_PRIVATE);
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
	}


	/**
	 * Save num.
	 * 
	 * @param context
	 *            the context
	 * @param value
	 *            the value
	 * @param key
	 *            the key
	 */
	public static void saveNum(Context context, long value, String key) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			if (DQAppPreference.preference != null) {
				final Editor editing = DQAppPreference.preference.edit();
				try {
					editing.remove(key);
				} catch (final Exception exception) {
					DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
				}
				editing.putLong(key, value);
				editing.commit();
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
	}


	/**
	 * Gets the num.
	 * 
	 * @param context
	 *            the context
	 * @param key
	 *            the key
	 * @return the num
	 */
	public static long getNum(Context context, String key, long name) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			return DQAppPreference.preference.getLong(key, name);
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
		return -1;
	}


	/**
	 * Save int.
	 * 
	 * @param context
	 *            the context
	 * @param value
	 *            the value
	 * @param key
	 *            the key
	 */
	public static void saveInt(Context context, int value, String key) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			if (DQAppPreference.preference != null) {
				final Editor editing = DQAppPreference.preference.edit();
				try {
					editing.remove(key);
				} catch (final Exception exception) {
					DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
				}
				editing.putInt(key, value);
				editing.commit();
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
	}


	/**
	 * Gets the int.
	 * 
	 * @param context
	 *            the context
	 * @param key
	 *            the key
	 * @return the int
	 */
	public static int getInt(Context context, String key, int defult) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			return DQAppPreference.preference.getInt(key, defult);
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
		return -1;
	}


	/**
	 * Save value.
	 * 
	 * @param context
	 *            the context
	 * @param value
	 *            the value
	 * @param key
	 *            the key
	 */
	public static void saveValue(Context context, String value, String key) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			if (DQAppPreference.preference != null) {
				final Editor editing = DQAppPreference.preference.edit();
				try {
					editing.remove(key);
				} catch (final Exception exception) {
					DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
				}
				editing.putString(key, value);
				editing.commit();
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
	}


	/**
	 * Gets the value.
	 * 
	 * @param context
	 *            the context
	 * @param key
	 *            the key
	 * @return the value
	 */
	public static String getValue(Context context, String key, String defult) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			return DQAppPreference.preference.getString(key, defult);
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
		return null;
	}


	/**
	 * Save data.
	 * 
	 * @param context
	 *            the context
	 * @param values
	 *            the values
	 * @param key
	 *            the key
	 */
	public static void saveBoolean(Context context, boolean values, String key) {

		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			if (DQAppPreference.preference != null) {
				final Editor editing = DQAppPreference.preference.edit();
				try {
					editing.remove(key);
				} catch (final Exception exception) {
					DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
				}
				editing.putBoolean(key, values);
				editing.commit();
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
	}


	/**
	 * Gets the saved data.
	 * 
	 * @param context
	 *            the context
	 * @param key
	 *            the key
	 * @return the saved data
	 */
	public static boolean getBoolean(Context context, String key, boolean defult) {

		boolean flag = false;
		try {
			if (DQAppPreference.preference == null) {
				DQAppPreference.initPrefrence(context);
			}
			if (DQAppPreference.preference != null) {
				flag = DQAppPreference.preference.getBoolean(key, defult);
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(DQAppPreference.TAG, exception);
		}
		return flag;
	}

}
