/**
 * 
 */

package com.confiz.downloadqueue.utils;

public class DQAppConstants {


	private static final String TAG = "DQAppConstants.java";

	public static final String DOWNLOAD_PREFRANCE_KEY = "download_queue_prafrence";

	public static final String KEY_AUTO_START_ON_NETWORK_CONNECTED = "on_network_connect";

	public static final String KEY_AUTO_START_ON_APP_START = "on_app_start";

	public static final String KEY_MAX_PARALLEL_DOWNLOADS = "parallel_downloads";

	public static final String KEY_MAX_QUEUE_LIMIT = "max_download_items";

	public static final String KEY_NO_OF_RETRIES = "max_retries";

	public static final String KEY_ONLY_ON_WIFI = "download_on_wifi_only";

	public static final String KEY_PIORATIES_NEW_ITEM_TO_TOP = "new_item_at_top";

	public static final String KEY_MAX_FILE_SIZE = "max_file_size";

	public static final String KEY_USER_ID = "user_id";
	
	public static final String KEY_SHOW_NOTIFICATION = "notification";

	// Values

	public static final boolean VALUE_AUTO_START_ON_NETWORK_CONNECTED = true;

	public static final boolean VALUE_AUTO_START_ON_APP_START = true;

	public static final int VALUE_MAX_PARALLEL_DOWNLOADS = 100; // 1 to 4

	public static final int VALUE_MAX_QUEUE_LIMIT = 150;

	public static final int VALUE_NO_OF_RETRIES = 5;

	public static final boolean VALUE_ONLY_ON_WIFI = true;

	public static final boolean VALUE_PIORATIES_NEW_ITEM_TO_TOP = false;
	
	public static final boolean VALUE_SHOW_NOTIFICATION = true;

	public static final int VALUE_MAX_FILE_SIZE = -1;

	public static final String VALUE_USER_ID = "-1";

	public static final String DOWNLOAD_FILE_NAME_PREFIX = "temp_";

	public static final int KB_TO_ENCRYPT = 256;

	public static final int BUFF_SIZE = 1024;
}
