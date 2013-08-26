
package com.confiz.downloadqueue.model;

import com.confiz.downloadqueue.utils.DQAppConstants;
import com.confiz.downloadqueue.utils.DQErrors;

public class DQRequest extends com.antlersoft.android.dbimpl.ImplementationBase {


	public static final String GEN_TABLE_NAME = "DQ_REQUEST";

	public static final int GEN_COUNT = 22;

	// Field constants
	public static final String GEN_FIELD_ID = "ID";

	public static final int GEN_ID_ID = 0;

	public static final String GEN_FIELD_KEY = "KEY";

	public static final int GEN_ID_KEY = 1;

	public static final String GEN_FIELD_STATUS = "STATUS";

	public static final int GEN_ID_STATUS = 2;

	public static final String GEN_FIELD_POSITION = "POSITION";

	public static final int GEN_ID_POSITION = 3;

	public static final String GEN_FIELD_ERRORDISCRIPTION = "ERRORDISCRIPTION";

	public static final int GEN_ID_ERRORDISCRIPTION = 4;

	public static final String GEN_FIELD_TOTALSIZE = "TOTALSIZE";

	public static final int GEN_ID_TOTALSIZE = 5;

	public static final String GEN_FIELD_DOWNLOADEDSIZE = "DOWNLOADEDSIZE";

	public static final int GEN_ID_DOWNLOADEDSIZE = 6;

	public static final String GEN_FIELD_TYPE = "TYPE";

	public static final int GEN_ID_TYPE = 7;

	public static final String GEN_FIELD_DATAESTIMATIONS = "DATAESTIMATIONS";

	public static final int GEN_ID_DATAESTIMATIONS = 8;

	public static final String GEN_FIELD_TIMEESTIMATIONS = "TIMEESTIMATIONS";

	public static final int GEN_ID_TIMEESTIMATIONS = 9;

	public static final String GEN_FIELD_FILENAME = "FILENAME";

	public static final int GEN_ID_FILENAME = 10;

	public static final String GEN_FIELD_FILEPATH = "FILEPATH";

	public static final int GEN_ID_FILEPATH = 11;

	public static final String GEN_FIELD_FILEFOLDERPATH = "FILEFOLDERPATH";

	public static final int GEN_ID_FILEFOLDERPATH = 12;

	public static final String GEN_FIELD_FILEURL = "FILEURL";

	public static final int GEN_ID_FILEURL = 13;

	public static final String GEN_FIELD_PROGRESS = "PROGRESS";

	public static final int GEN_ID_PROGRESS = 14;

	public static final String GEN_FIELD_ERROR = "ERROR";

	public static final int GEN_ID_ERROR = 15;

	public static final String GEN_FIELD_DOWNLOADING = "DOWNLOADING";

	public static final int GEN_ID_DOWNLOADING = 16;

	public static final String GEN_FIELD_SAVED = "SAVED";

	public static final int GEN_ID_SAVED = 17;

	public static final String GEN_FIELD_PARTIALDOWNLOADED = "PARTIALDOWNLOADED";

	public static final int GEN_ID_PARTIALDOWNLOADED = 18;

	public static final String GEN_FIELD_CANENCRYPT = "CANENCRYPT";

	public static final int GEN_ID_CANENCRYPT = 19;

	public static final String GEN_FIELD_TITLE = "TITLE";

	public static final int GEN_ID_TITLE = 20;

	public static final String GEN_FIELD_USERID = "USERID";

	public static final int GEN_ID_USERID = 21;

	// SQL Command for creating the table
	public static String GEN_CREATE = "CREATE TABLE IF NOT EXISTS DQ_REQUEST (" + "ID INTEGER," + "KEY TEXT," + "STATUS INTEGER," + "POSITION INTEGER," + "ERRORDISCRIPTION TEXT," + "TOTALSIZE INTEGER," + "DOWNLOADEDSIZE INTEGER," + "TYPE INTEGER," + "DATAESTIMATIONS TEXT," + "TIMEESTIMATIONS TEXT," + "FILENAME TEXT," + "FILEPATH TEXT," + "FILEFOLDERPATH TEXT," + "FILEURL TEXT," + "PROGRESS INTEGER," + "ERROR INTEGER," + "DOWNLOADING INTEGER," + "SAVED INTEGER," + "PARTIALDOWNLOADED INTEGER," + "CANENCRYPT INTEGER," + "TITLE TEXT," + "USERID TEXT" + ")";


	public String Gen_tableName() {

		return GEN_TABLE_NAME;
	}


	public android.content.ContentValues Gen_getValues() {

		android.content.ContentValues values = new android.content.ContentValues();
		values.put(GEN_FIELD_ID, Long.toString(this.id));
		values.put(GEN_FIELD_KEY, this.key);
		values.put(GEN_FIELD_STATUS, Integer.toString(this.status.ordinal()));
		values.put(GEN_FIELD_POSITION, Integer.toString(this.position));
		values.put(GEN_FIELD_ERRORDISCRIPTION, this.errorDiscription);
		values.put(GEN_FIELD_TOTALSIZE, Long.toString(this.totalSize));
		values.put(GEN_FIELD_DOWNLOADEDSIZE, Long.toString(this.downloadedSize));
		values.put(GEN_FIELD_TYPE, Long.toString(this.type));
		values.put(GEN_FIELD_DATAESTIMATIONS, this.dataEstimations);
		values.put(GEN_FIELD_TIMEESTIMATIONS, this.timeEstimations);
		values.put(GEN_FIELD_FILENAME, this.fileName);
		values.put(GEN_FIELD_FILEPATH, this.filePath);
		values.put(GEN_FIELD_FILEFOLDERPATH, this.fileFolderPath);
		values.put(GEN_FIELD_FILEURL, this.fileUrl);
		values.put(GEN_FIELD_PROGRESS, Integer.toString(this.progress));
		values.put(GEN_FIELD_ERROR, Integer.toString(this.currentError.ordinal()));
		values.put(GEN_FIELD_DOWNLOADING, (this.downloading ? "1" : "0"));
		values.put(GEN_FIELD_SAVED, (this.saved ? "1" : "0"));
		values.put(GEN_FIELD_PARTIALDOWNLOADED, (this.partialDownloaded ? "1" : "0"));
		values.put(GEN_FIELD_CANENCRYPT, (this.canEncrypt ? "1" : "0"));
		values.put(GEN_FIELD_TITLE, this.title);
		values.put(GEN_FIELD_USERID, this.userId);
		return values;
	}


	/**
	 * Return an array that gives the column index in the cursor for each field
	 * defined
	 * 
	 * @param cursor
	 *            Database cursor over some columns, possibly including this
	 *            table
	 * @return array of column indices; -1 if the column with that id is not in
	 *         cursor
	 */
	public int[] Gen_columnIndices(android.database.Cursor cursor) {

		int[] result = new int[GEN_COUNT];
		result[0] = cursor.getColumnIndex(GEN_FIELD_ID);
		result[1] = cursor.getColumnIndex(GEN_FIELD_KEY);
		result[2] = cursor.getColumnIndex(GEN_FIELD_STATUS);
		result[3] = cursor.getColumnIndex(GEN_FIELD_POSITION);
		result[4] = cursor.getColumnIndex(GEN_FIELD_ERRORDISCRIPTION);
		result[5] = cursor.getColumnIndex(GEN_FIELD_TOTALSIZE);
		result[6] = cursor.getColumnIndex(GEN_FIELD_DOWNLOADEDSIZE);
		result[7] = cursor.getColumnIndex(GEN_FIELD_TYPE);
		result[8] = cursor.getColumnIndex(GEN_FIELD_DATAESTIMATIONS);
		result[9] = cursor.getColumnIndex(GEN_FIELD_TIMEESTIMATIONS);
		result[10] = cursor.getColumnIndex(GEN_FIELD_FILENAME);
		result[11] = cursor.getColumnIndex(GEN_FIELD_FILEPATH);
		result[12] = cursor.getColumnIndex(GEN_FIELD_FILEFOLDERPATH);
		result[13] = cursor.getColumnIndex(GEN_FIELD_FILEURL);
		result[14] = cursor.getColumnIndex(GEN_FIELD_PROGRESS);
		result[15] = cursor.getColumnIndex(GEN_FIELD_ERROR);
		result[16] = cursor.getColumnIndex(GEN_FIELD_DOWNLOADING);
		result[17] = cursor.getColumnIndex(GEN_FIELD_SAVED);
		result[18] = cursor.getColumnIndex(GEN_FIELD_PARTIALDOWNLOADED);
		result[19] = cursor.getColumnIndex(GEN_FIELD_CANENCRYPT);
		result[20] = cursor.getColumnIndex(GEN_FIELD_TITLE);
		result[21] = cursor.getColumnIndex(GEN_FIELD_USERID);
		return result;
	}


	/**
	 * Populate one instance from a cursor
	 */
	public void Gen_populate(android.database.Cursor cursor, int[] columnIndices) {

		if (columnIndices[GEN_ID_ID] >= 0 && !cursor.isNull(columnIndices[GEN_ID_ID])) {
			id = cursor.getLong(columnIndices[GEN_ID_ID]);
		}
		if (columnIndices[GEN_ID_KEY] >= 0 && !cursor.isNull(columnIndices[GEN_ID_KEY])) {
			key = cursor.getString(columnIndices[GEN_ID_KEY]);
		}
		if (columnIndices[GEN_ID_STATUS] >= 0 && !cursor.isNull(columnIndices[GEN_ID_STATUS])) {
			setStatus((int) cursor.getInt(columnIndices[GEN_ID_STATUS]));
		}
		if (columnIndices[GEN_ID_POSITION] >= 0 && !cursor.isNull(columnIndices[GEN_ID_POSITION])) {
			position = (int) cursor.getInt(columnIndices[GEN_ID_POSITION]);
		}
		if (columnIndices[GEN_ID_ERRORDISCRIPTION] >= 0 && !cursor.isNull(columnIndices[GEN_ID_ERRORDISCRIPTION])) {
			errorDiscription = cursor.getString(columnIndices[GEN_ID_ERRORDISCRIPTION]);
		}
		if (columnIndices[GEN_ID_TOTALSIZE] >= 0 && !cursor.isNull(columnIndices[GEN_ID_TOTALSIZE])) {
			totalSize = cursor.getLong(columnIndices[GEN_ID_TOTALSIZE]);
		}
		if (columnIndices[GEN_ID_DOWNLOADEDSIZE] >= 0 && !cursor.isNull(columnIndices[GEN_ID_DOWNLOADEDSIZE])) {
			downloadedSize = cursor.getLong(columnIndices[GEN_ID_DOWNLOADEDSIZE]);
		}
		if (columnIndices[GEN_ID_TYPE] >= 0 && !cursor.isNull(columnIndices[GEN_ID_TYPE])) {
			type = cursor.getInt(columnIndices[GEN_ID_TYPE]);
		}
		if (columnIndices[GEN_ID_DATAESTIMATIONS] >= 0 && !cursor.isNull(columnIndices[GEN_ID_DATAESTIMATIONS])) {
			dataEstimations = cursor.getString(columnIndices[GEN_ID_DATAESTIMATIONS]);
		}
		if (columnIndices[GEN_ID_TIMEESTIMATIONS] >= 0 && !cursor.isNull(columnIndices[GEN_ID_TIMEESTIMATIONS])) {
			timeEstimations = cursor.getString(columnIndices[GEN_ID_TIMEESTIMATIONS]);
		}
		if (columnIndices[GEN_ID_FILENAME] >= 0 && !cursor.isNull(columnIndices[GEN_ID_FILENAME])) {
			fileName = cursor.getString(columnIndices[GEN_ID_FILENAME]);
		}
		if (columnIndices[GEN_ID_FILEPATH] >= 0 && !cursor.isNull(columnIndices[GEN_ID_FILEPATH])) {
			filePath = cursor.getString(columnIndices[GEN_ID_FILEPATH]);
		}
		if (columnIndices[GEN_ID_FILEFOLDERPATH] >= 0 && !cursor.isNull(columnIndices[GEN_ID_FILEFOLDERPATH])) {
			fileFolderPath = cursor.getString(columnIndices[GEN_ID_FILEFOLDERPATH]);
		}
		if (columnIndices[GEN_ID_FILEURL] >= 0 && !cursor.isNull(columnIndices[GEN_ID_FILEURL])) {
			fileUrl = cursor.getString(columnIndices[GEN_ID_FILEURL]);
		}
		if (columnIndices[GEN_ID_PROGRESS] >= 0 && !cursor.isNull(columnIndices[GEN_ID_PROGRESS])) {
			progress = (int) cursor.getInt(columnIndices[GEN_ID_PROGRESS]);
		}
		if (columnIndices[GEN_ID_ERROR] >= 0 && !cursor.isNull(columnIndices[GEN_ID_ERROR])) {
			setCurrentError((int) cursor.getInt(columnIndices[GEN_ID_ERROR]));
		}
		if (columnIndices[GEN_ID_DOWNLOADING] >= 0 && !cursor.isNull(columnIndices[GEN_ID_DOWNLOADING])) {
			downloading = (cursor.getInt(columnIndices[GEN_ID_DOWNLOADING]) != 0);
		}
		if (columnIndices[GEN_ID_SAVED] >= 0 && !cursor.isNull(columnIndices[GEN_ID_SAVED])) {
			saved = (cursor.getInt(columnIndices[GEN_ID_SAVED]) != 0);
		}
		if (columnIndices[GEN_ID_PARTIALDOWNLOADED] >= 0 && !cursor.isNull(columnIndices[GEN_ID_PARTIALDOWNLOADED])) {
			partialDownloaded = (cursor.getInt(columnIndices[GEN_ID_PARTIALDOWNLOADED]) != 0);
		}
		if (columnIndices[GEN_ID_CANENCRYPT] >= 0 && !cursor.isNull(columnIndices[GEN_ID_CANENCRYPT])) {
			canEncrypt = (cursor.getInt(columnIndices[GEN_ID_CANENCRYPT]) != 0);
		}
		if (columnIndices[GEN_ID_TITLE] >= 0 && !cursor.isNull(columnIndices[GEN_ID_TITLE])) {
			title = cursor.getString(columnIndices[GEN_ID_TITLE]);
		}
		if (columnIndices[GEN_ID_USERID] >= 0 && !cursor.isNull(columnIndices[GEN_ID_USERID])) {
			userId = cursor.getString(columnIndices[GEN_ID_USERID]);
		}
	}


	/**
	 * Populate one instance from a ContentValues
	 */
	public void Gen_populate(android.content.ContentValues values) {

		id = values.getAsLong(GEN_FIELD_ID);
		key = values.getAsString(GEN_FIELD_KEY);
		setStatus((int) values.getAsInteger(GEN_FIELD_STATUS));
		position = (int) values.getAsInteger(GEN_FIELD_POSITION);
		errorDiscription = values.getAsString(GEN_FIELD_ERRORDISCRIPTION);
		totalSize = values.getAsLong(GEN_FIELD_TOTALSIZE);
		downloadedSize = values.getAsLong(GEN_FIELD_DOWNLOADEDSIZE);
		type = values.getAsInteger(GEN_FIELD_TYPE);
		dataEstimations = values.getAsString(GEN_FIELD_DATAESTIMATIONS);
		timeEstimations = values.getAsString(GEN_FIELD_TIMEESTIMATIONS);
		fileName = values.getAsString(GEN_FIELD_FILENAME);
		filePath = values.getAsString(GEN_FIELD_FILEPATH);
		fileFolderPath = values.getAsString(GEN_FIELD_FILEFOLDERPATH);
		fileUrl = values.getAsString(GEN_FIELD_FILEURL);
		progress = (int) values.getAsInteger(GEN_FIELD_PROGRESS);
		setCurrentError((int) values.getAsInteger(GEN_FIELD_ERROR));
		downloading = (values.getAsInteger(GEN_FIELD_DOWNLOADING) != 0);
		saved = (values.getAsInteger(GEN_FIELD_SAVED) != 0);
		partialDownloaded = (values.getAsInteger(GEN_FIELD_PARTIALDOWNLOADED) != 0);
		canEncrypt = (values.getAsInteger(GEN_FIELD_CANENCRYPT) != 0);
		title = values.getAsString(GEN_FIELD_TITLE);
		userId = values.getAsString(GEN_FIELD_USERID);
	}

	// Members corresponding to defined fields

	private long id = -1;

	private String key = null;

	private DQDownloadingStatus status = null;

	private int position;

	private String errorDiscription;

	private long totalSize = 0, downloadedSize = 0;

	private int type = -1;

	private String dataEstimations = null;

	private String timeEstimations = null;

	private String fileName, filePath, fileFolderPath, fileUrl;

	private int progress = 0;

	public DQErrors currentError = DQErrors.NO_ERROR;

	private boolean downloading = false;

	private boolean saved = false;

	private boolean partialDownloaded = false;

	private boolean canEncrypt = false;

	private String title;

	private String userId;


	public DQRequest() {

		// TODO Auto-generated constructor stub
	}


	public DQRequest(long id, String key, DQDownloadingStatus status, int position, String errorDiscription, long totalSize,
	        long downloadedSize, int type, String dataEstimations, String timeEstimations, String fileName, String filePath,
	        String fileFolderPath, String fileUrl, int progress, DQErrors currentError, boolean downloading, boolean saved,
	        boolean partialDownloaded, boolean canEncrypt, String title) {

		super();
		this.id = id;
		this.key = key;
		this.status = status;
		this.position = position;
		this.errorDiscription = errorDiscription;
		this.totalSize = totalSize;
		this.downloadedSize = downloadedSize;
		this.type = type;
		this.dataEstimations = dataEstimations;
		this.timeEstimations = timeEstimations;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileFolderPath = fileFolderPath;
		this.fileUrl = fileUrl;
		this.progress = progress;
		this.currentError = currentError;
		this.downloading = downloading;
		this.saved = saved;
		this.partialDownloaded = partialDownloaded;
		this.canEncrypt = canEncrypt;
		this.title = title;
	}


	public DQRequest(String key, int type, String title, boolean canEncrypt, String fileName, String filePath,
	        String fileFolderPath, String fileUrl) {

		super();
		this.key = key;
		this.type = type;
		this.title = title;
		this.canEncrypt = canEncrypt;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileFolderPath = fileFolderPath;
		this.fileUrl = fileUrl;
		status = DQDownloadingStatus.WAITING;
		position = -1;

	}


	public long getId() {

		return this.id;
	}


	public void setId(long id) {

		this.id = id;
	}


	public DQDownloadingStatus getStatus() {

		return this.status;
	}


	public void setStatus(DQDownloadingStatus status) {

		this.status = status;
	}


	public void setStatus(int statusOrdinal) {

		if (statusOrdinal > -1) {
			this.status = DQDownloadingStatus.values()[statusOrdinal];
		} else {
			this.status = DQDownloadingStatus.WAITING;
		}
	}


	public int getPosition() {

		return this.position;
	}


	public void setPosition(int position) {

		this.position = position;
	}


	public String getErrorDiscription() {

		return this.errorDiscription;
	}


	public void setErrorDiscription(String errorDiscription) {

		this.errorDiscription = errorDiscription;
	}


	public long getTotalSize() {

		return this.totalSize;
	}


	public void setTotalSize(long totalSize) {

		this.totalSize = totalSize;
	}


	public long getDownloadedSize() {

		return this.downloadedSize;
	}


	public void setDownloadedSize(long downloadedSize) {

		this.downloadedSize = downloadedSize;
	}


	public int getType() {

		return this.type;
	}


	public void setType(int type) {

		this.type = type;
	}


	public String getDataEstimations() {

		return this.dataEstimations;
	}


	public void setDataEstimations(String dataEstimations) {

		this.dataEstimations = dataEstimations;
	}


	public String getTimeEstimations() {

		return this.timeEstimations;
	}


	public void setTimeEstimations(String timeEstimations) {

		this.timeEstimations = timeEstimations;
	}


	public String getFileName() {

		return this.fileName;
	}


	public void setFileName(String fileName) {

		this.fileName = fileName;
	}


	public String getFilePath() {

		return this.filePath;
	}


	public void setFilePath(String filePath) {

		this.filePath = filePath;
	}


	public String getFileFolderPath() {

		return this.fileFolderPath;
	}


	public void setFileFolderPath(String fileFolderPath) {

		this.fileFolderPath = fileFolderPath;
	}


	public int getProgress() {

		return this.progress;
	}


	public void setProgress(int progress) {

		this.progress = progress;
	}


	public String getFileUrl() {

		return this.fileUrl;
	}


	public void setFileUrl(String fileUrl) {

		this.fileUrl = fileUrl;
	}


	public DQErrors getCurrentError() {

		return this.currentError;
	}


	public void setCurrentError(DQErrors currentError) {

		this.currentError = currentError;
	}


	public boolean isDownloading() {

		return this.downloading;
	}


	public void setDownloading(boolean downloading) {

		this.downloading = downloading;
	}


	public boolean isSaved() {

		return this.saved;
	}


	public void setSaved(boolean saved) {

		this.saved = saved;
	}


	public boolean isPartialDownloaded() {

		return this.partialDownloaded;
	}


	public void setPartialDownloaded(boolean partialDownloaded) {

		this.partialDownloaded = partialDownloaded;
	}


	public String getKey() {

		return this.key;
	}


	public void setKey(String key) {

		this.key = key;
	}


	public boolean isCanEncrypt() {

		return this.canEncrypt;
	}


	public void setCanEncrypt(boolean canEncrypt) {

		this.canEncrypt = canEncrypt;
	}


	public String getTitle() {

		return this.title;
	}


	public void setTitle(String title) {

		this.title = title;
	}


	public String getUserId() {

		return this.userId;
	}


	public void setUserId(String userId) {

		this.userId = userId;
	}


	private void setCurrentError(int errorOridinal) {

		if (errorOridinal > -1) {
			this.currentError = DQErrors.values()[errorOridinal];
		} else {
			this.currentError = DQErrors.NO_ERROR;
		}
	}


	public String getTempFileName() {

		return DQAppConstants.DOWNLOAD_FILE_NAME_PREFIX + fileName;
	}


	/**
	 * @param details
	 */
	public void setEstimates(String[] details) {

		this.downloadingEstimaes = details;

	}


	public String[] getEstimates() {

		return downloadingEstimaes;
	}

	private String[] downloadingEstimaes = null;


	@Override
	public String toString() {

		return "DQRequest [id=" + this.id + ", status=" + this.status + ", position=" + this.position + ", errorDiscription=" + this.errorDiscription + ", totalSize=" + this.totalSize + ", downloadedSize=" + this.downloadedSize + ", type=" + this.type + ", dataEstimations=" + this.dataEstimations + ", timeEstimations=" + this.timeEstimations + ", fileName=" + this.fileName + ", FilePath=" + this.filePath + ", fileFolderPath=" + this.fileFolderPath + ", progress=" + this.progress + "]";
	}

}
