/**
 * 
 */

package com.confiz.downloadqueue.encryption;

import android.os.Environment;


/**
 * The Class DQAppFolders.
 */
public class DQAppFolders {

    private static final String TAG = "DQAppFolders";

    /** The Constant MY_VIDEO_LIBRARY. */
    public static final String MY_VIDEO_LIBRARY = "my-video-library/videos/";

    /** The Constant MY_VIDEO_STORE. */
    public static final String MY_VIDEO_STORE = "my-video-store/videos/";

    /** The Constant THUMBS. */
    public static final String THUMBS = "thumbs";

    /** The Constant SMALL_VIDEO_THUMB_PATH. */
    public static final String SMALL_VIDEO_THUMB_PATH = "videos/small-thumbs/";

    /** The Constant USER_THUMBS. */
    public static final String USER_THUMBS = "user-infor/thumbs";

    public static final String TEMP_IMAGE_FOLDER = ".temp_image/";

    /** The Constant FILE_PATH_BASE. */
    public static final String FILE_PATH_BASE = "/Android/data/com.touchadventures.littlecast/.files/";

    /** The Constant HIDDEN_FOLDER. */
    public static final String HIDDEN_FOLDER = ".temp/";

    /**
     * Gets the my video library path.
     * 
     * @return the my video library path
     */
    public static String getMyVideoLibraryPath() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.MY_VIDEO_LIBRARY;
    }

    /**
     * Gets the my video library path.
     * 
     * @return the my video library path
     */
    public static String getVideosThumnailPath() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.MY_VIDEO_LIBRARY
		+ DQAppFolders.THUMBS;
    }

    /**
     * Gets the video store library path.
     * 
     * @return the video store library path
     */
    public static String getVideoStoreLibraryPath() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.MY_VIDEO_STORE;
    }

    /**
     * Gets the user thumbnail path.
     * 
     * @return the user thumbnail path
     */
    public static String getUserThumbnailPath() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.USER_THUMBS;
    }

    /**
     * Gets the video small thumb path.
     * 
     * @return the video small thumb path
     */
    public static String getVideoSmallThumbPath() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.SMALL_VIDEO_THUMB_PATH;
    }

    /**
     * Gets the hidden folder path.
     * 
     * @return the hidden folder path
     */
    public static String getHiddenFolderPath() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.HIDDEN_FOLDER;
    }

    /**
     * Gets the base path.
     * 
     * @return the base path
     */
    public static String getBasePath() {

	return Environment.getExternalStorageDirectory().toString()
		+ DQAppFolders.FILE_PATH_BASE;
    }

    /**
     * 
     */
    public static String getTempImageFolder() {

	return Environment.getExternalStorageDirectory()
		+ DQAppFolders.FILE_PATH_BASE + DQAppFolders.TEMP_IMAGE_FOLDER;

    }

}