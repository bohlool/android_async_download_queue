/*
 * Property    : Confiz Solutions
 * Created by  : Arslan Anwar
 * Updated by  : Arslan Anwar
 * 
 */

package com.confiz.downloadqueue.db;


/**
 * The Class DQDBUtility.
 */
public class DQDBUtility {


	/**
	 * Gets the string of only supported chars.
	 * 
	 * @param value
	 *            the value
	 * @return the string of only supported chars
	 */
	public static String getStringOfOnlySupportedChars(String value) {

		if (value == null) {
			return "";
		}
		// This character creates problem while being inserted in DB
		if (value.contains("\'")) {
			value = value.replace("\'", "`");
		}
		// This character creates problem when assigned to TextView with
		// ellipsoid
		if (value.contains("’")) {
			value = value.replaceAll("’", "`");
		}
		// This character creates problem when assigned to TextView with
		// ellipsoid
		if (value.contains("–")) {
			value = value.replaceAll("–", "-");
		}
		// This character creates problem when assigned to TextView with
		// ellipsoid
		if (value.contains("\"")) {
			value = value.replace("\"", "");
		}

		return value.trim();
	}


	/**
	 * Gets the string with reverted chars.
	 * 
	 * @param value
	 *            the value
	 * @return the string with reverted chars
	 */
	public static String getStringWithRevertedChars(String value) {

		if (value == null) {
			return "";
		}
		// Revert characters as they were converted to avoid DB problems.
		if (value.contains("`")) {
			value = value.replaceAll("`", "\'");
		}
		return value;
	}

}
