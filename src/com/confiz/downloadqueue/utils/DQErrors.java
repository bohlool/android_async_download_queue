/*
 * Property : Confiz Solutions
 * Created by : Arslan Anwar
 * Updated by : Arslan Anwar
 */

package com.confiz.downloadqueue.utils;

import com.example.downloadqueue.R;

public enum DQErrors {

	/** The no error. */
	NO_ERROR(0), /** The downloading error. */
	
	DOWNLOADING_ERROR(R.string.error_msg_unable_to_download), /** The very old file not decrypted. */
	VERY_OLD_FILE_NOT_DECRYPTED(R.string.error_msg_decryption_failed),
	/** The network weak. */
	NETWORK_WEAK(R.string.error_msg_network_not_reachable),
	/** The file not found. */
	FILE_NOT_FOUND(R.string.error_msg_file_not_found_on_external_storage),
	/** The unable to download file. */
	UNABLE_TO_DOWNLOAD_FILE(R.string.error_msg_unable_to_download),
	/** The unable to store in db. */
	UNABLE_TO_STORE_IN_DB(R.string.error_msg_unable_to_store_data),
	/** The network no avilable. */
	NETWORK_NO_AVILABLE(R.string.error_msg_network_not_reachable),
	/** The no response. */
	NO_RESPONSE(R.string.error_msg_no_response_from_server),
	/** The invalid response. */
	INVALID_RESPONSE(R.string.error_msg_invalid_response_from_server),
	/** The no db. */
	NO_DB(R.string.error_msg_database_not_found),
	/** The no data found. */
	NO_DATA_FOUND(R.string.error_msg_no_item_found),
	/** The invalid index. */
	INVALID_INDEX(R.string.error_msg_invalid_server_response),
	/** The null pointer. */
	NULL_POINTER(R.string.error_msg_invalid_server_response),
	/** The empty string. */
	EMPTY_STRING(R.string.error_msg_invalid_server_response),
	/** The invalid data. */
	INVALID_DATA(R.string.error_msg_invalid_server_response),
	/** The external storage not available. */
	EXTERNAL_STORAGE_NOT_AVAILABLE(R.string.error_msg_external_storage_not_available),
	/** The external storage not writable. */
	EXTERNAL_STORAGE_NOT_WRITABLE(R.string.error_msg_external_storage_write_protected),
	/** The external storage insufficient space. */
	EXTERNAL_STORAGE_INSUFFICIENT_SPACE(R.string.error_msg_external_storage_insufficient_space),
	/** The storage insufficient space for vault. */
	STORAGE_INSUFFICIENT_SPACE_FOR_VAULT(R.string.error_msg_external_storage_insufficient_space_vault),
	/** The S3_ tim e_ skew. */
	S3_TIME_SKEW(R.string.msg_s3_time_skew),
	/** The smugmug session expired. */
	SMUGMUG_SESSION_EXPIRED(R.string.error_msg_smugmug_session_expired),
	/** The V2_ sessio n_ expired. */
	V2_SESSION_EXPIRED(R.string.error_msg_session_expired);


	/** The error value. */
	private int errorValue;


	/**
	 * Value.
	 * 
	 * @return the int
	 */
	public int value() {

		return this.errorValue;
	}


	/**
	 * Instantiates a new errors.
	 * 
	 * @param value
	 *            the value
	 */
	DQErrors(int value) {

		this.errorValue = value;
	}


	/**
	 * Gets the error.
	 * 
	 * @param ordinal
	 *            the ordinal
	 * @return the error
	 */
	public static DQErrors getError(int ordinal) {

		return DQErrors.values()[ordinal];
	}
};
