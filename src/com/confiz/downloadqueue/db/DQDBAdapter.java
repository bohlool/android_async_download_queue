/*
 * Property : Confiz Solutions
 * Created by : Arslan Anwar
 * Updated by : Arslan Anwar
 */

package com.confiz.downloadqueue.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.antlersoft.android.dbimpl.ImplementationBase;
import com.confiz.downloadqueue.model.DQDownloadingStatus;
import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQDebugHelper;

public class DQDBAdapter {


	private String TAG = "DQDBAdapter";

	/** The context. */
	public Context context = null;

	/** The DB helper. */
	private DQDatabaseHelper databaseHelper = null;

	/** The db. */
	private SQLiteDatabase database = null;

	/** The single ton object. */
	private static DQDBAdapter singleTonObject = null;

	private int databaseOpenedCount = 0;


	/**
	 * This method Initializes the context of the for further use and creates a
	 * new object of DQDatabaseHelper class.
	 * 
	 * @param ctx
	 *            Context of calling Activity
	 */
	private DQDBAdapter(Context ctx) {

		if (ctx != null) {
			if (this.context == null) {
				this.context = ctx;
			}
			if (this.databaseHelper == null) {
				this.databaseHelper = new DQDatabaseHelper(this.context);
			}
		}
	}


	/**
	 * Gets the single instance of DQDBAdapter.
	 * 
	 * @param context
	 *            the context
	 * @return single instance of DQDBAdapter
	 */
	public static DQDBAdapter getInstance(Context context) {

		if (DQDBAdapter.singleTonObject == null) {
			DQDBAdapter.singleTonObject = new DQDBAdapter(context);
		}
		return DQDBAdapter.singleTonObject;
	}


	/**
	 * This method opens a writableDatabase
	 * 
	 * @return <b>DQDBAdapter</b> object
	 * @throws SQLException
	 */

	private boolean openToWrite() {

		boolean flag = false;
		try {
			if (database == null || database.isOpen() == false) {
				database = databaseHelper.getWritableDatabase();
				flag = true;
			} else if (database != null && database.isOpen()) {
				flag = true;
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(TAG, exception);
			flag = false;
		}
		databaseOpenedCount++;
		return flag;

	}


	private boolean openToRead() {

		boolean flag = false;
		try {
			if (database == null || database.isOpen() == false) {
				database = databaseHelper.getReadableDatabase();
				flag = true;
			} else if (database != null && database.isOpen()) {
				flag = true;
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(TAG, exception);
			flag = false;
		}
		databaseOpenedCount++;
		return flag;

	}


	/**
	 * Closes the database
	 */
	private void close() {

		try {
			if (databaseOpenedCount > 0) {
				--databaseOpenedCount;
			}
			// AppDebuger.printData( "Close" , "Now count is = " + openedCount )
			// ;
			if (databaseOpenedCount <= 0 && database != null && database.isOpen()) {
				database.close();
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(TAG, exception);
		}
	}


	/**
	 * Deletes the data from all Database tables.
	 */
	public void truncateTables() {

		truncateTable(DQRequest.GEN_TABLE_NAME);
	}


	/**
	 * Deletes the data from a specific table.
	 */
	public void truncateTable(String tableName) {

		executeWriteQuery("DELETE from " + tableName);
	}


	public static void destroy() {

		singleTonObject = null;
	}


	public synchronized boolean executeWriteQuery(String sqlCommand) {

		boolean flag = false;
		try {
			if (openToWrite()) {
				if (database.isDbLockedByCurrentThread() == false) {
					if (sqlCommand != null && sqlCommand.length() > 0) {
						database.execSQL(sqlCommand);
						flag = true;
					}
				}
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(TAG, exception);
			flag = false;
		} finally {
			close();
		}
		return flag;
	}


	public Cursor executeReadQuery(String sqlQuery) {

		Cursor cursor = null;
		try {
			if (openToRead()) {
				if (sqlQuery != null && sqlQuery.length() > 0) {
					cursor = database.rawQuery(sqlQuery, null);
				}
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(TAG, exception);
			cursor = null;
		}
		return cursor;
	}


	public void endReadOperation() {

		close();
	}


	public boolean insertRecord(ImplementationBase request) {

		boolean flag = false;
		try {
			if (openToWrite()) {
				long id = database.insert(request.Gen_tableName(), null, request.Gen_getValues());
				if (id != -1) {
					flag = true;
				}
			}
		} catch (Exception exception) {
			DQDebugHelper.printException(TAG, exception);
			flag = false;
		} finally {
			close();
		}
		return flag;
	}


	public ArrayList<? extends ImplementationBase> getDownloadRequests(String className, String query) {

		ArrayList<ImplementationBase> requestedList = null;
		Cursor cursor = executeReadQuery(query);
		int[] columnIndex = null;
		try {
			if (cursor != null && cursor.moveToFirst()) {
				requestedList = new ArrayList<ImplementationBase>();
				do {
					Object tempObject = Class.forName(className).newInstance();
					if (tempObject instanceof ImplementationBase) {
						ImplementationBase convertedObject = (ImplementationBase) tempObject;
						if (columnIndex == null) {
							columnIndex = convertedObject.Gen_columnIndices(cursor);
						}
						convertedObject.Gen_populate(cursor, columnIndex);
						requestedList.add(convertedObject);
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
			close();
		}
		return requestedList;
	}


	public boolean deleteItem(String query) {

		boolean flag = false;
		flag = executeWriteQuery(query);
		return flag;
	}


	@SuppressWarnings("unchecked")
	public ArrayList<DQRequest> getDownloadRequests(String userId) {

		String query = "SELECT * FROM " + DQRequest.GEN_TABLE_NAME + " WHERE " + DQRequest.GEN_FIELD_USERID + " = '" + userId + "' ORDER BY position ASC";
		return (ArrayList<DQRequest>) getDownloadRequests(DQRequest.class.getName(), query);
	}


	public boolean updateDownloadStatus(String userId) {
		String query = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_STATUS + " =" + DQDownloadingStatus.WAITING
		        .ordinal() + " WHERE (" + DQRequest.GEN_FIELD_STATUS + " =" + DQDownloadingStatus.DOWNLOADING.ordinal() + " OR " + DQRequest.GEN_FIELD_STATUS + " =" + DQDownloadingStatus.DOWNLOAD_REQUEST
		        .ordinal() + ") AND " + DQRequest.GEN_FIELD_USERID + " ='" + userId + "'";
//		String query = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_STATUS + " =" + DQDownloadingStatus.WAITING
//		        .ordinal() + " WHERE " + DQRequest.GEN_FIELD_USERID + " ='" + userId + "'";
		boolean flag = executeWriteQuery(query);
		return flag;
	}


	public boolean deleteDQRequest(DQRequest dRequest, String userId) {

		boolean flag = false;
		String query = "DELETE FROM  " + DQRequest.GEN_TABLE_NAME + " WHERE " + DQRequest.GEN_FIELD_KEY + " = '" + dRequest
		        .getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + " = '" + userId + "'";
		flag = executeWriteQuery(query);
		return flag;
	}


	public boolean deleteDownloadQueue(String userId) {

		boolean flag = false;
		String query = "DELETE FROM " + DQRequest.GEN_TABLE_NAME + " WHERE " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
		flag = executeWriteQuery(query);

		return flag;
	}


	public DQDownloadingStatus getDownloadStatus(String key, String userId) {

		DQDownloadingStatus status = DQDownloadingStatus.WAITING;
		String query = "SELECT * FROM " + DQRequest.GEN_TABLE_NAME + " WHERE " + DQRequest.GEN_FIELD_KEY + " = '" + key + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
		Cursor cursor = executeReadQuery(query);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				status = DQDownloadingStatus.get(cursor.getInt(2));
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			if (cursor != null && !cursor.isClosed()) {

				cursor.close();
				cursor = null;
			}
			close();
		}
		return status;
	}


	public boolean isInDownloadQueue(String key, String userId) {

		boolean flag = false;
		Cursor cursor = null;
		try {
			String qCount = "Select * From " + DQRequest.GEN_TABLE_NAME + " WHERE " + DQRequest.GEN_FIELD_KEY + " = '" + key + "'";
			cursor = executeReadQuery(qCount.toString());
			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				flag = true;
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			if (cursor != null && !cursor.isClosed()) {

				cursor.close();
				cursor = null;
			}
			close();
		}
		return flag;
	}


	public boolean updateDownloadPositions(ArrayList<DQRequest> list, String userId) {

		boolean flag = false;
		try {
			if (list != null) {
				DQRequest data = null;
				for (int newPos = 0; newPos < list.size(); newPos++) {
					data = list.get(newPos);
					String uQuery = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_POSITION + "=" + newPos + " WHERE " + DQRequest.GEN_FIELD_KEY + "  ='" + data
					        .getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
					flag = executeWriteQuery(uQuery);
					if (flag == false) {
						break;
					}
				}
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			close();
		}
		return flag;
	}


	public boolean updateDownloadStatus(DQRequest dRequest, String userId) {

		boolean flag = false;

		try {
			if (dRequest != null) {
				String uQuery = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_STATUS + " = " + dRequest
				        .getStatus().ordinal() + " WHERE " + DQRequest.GEN_FIELD_KEY + "  ='" + dRequest.getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
				flag = executeWriteQuery(uQuery);
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			close();
		}
		return flag;
	}


	public boolean updateDownloadedSize(DQRequest dRequest, String userId) {

		boolean flag = false;

		try {
			if (dRequest != null) {
				String uQuery = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_DOWNLOADEDSIZE + " = " + dRequest
				        .getDownloadedSize() + " WHERE " + DQRequest.GEN_FIELD_KEY + "  ='" + dRequest.getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
				flag = executeWriteQuery(uQuery);
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			close();
		}
		return flag;
	}


	public boolean updateDownloadTotalSize(DQRequest dRequest, String userId) {

		boolean flag = false;

		try {
			if (dRequest != null) {
				String uQuery = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_TOTALSIZE + " = " + dRequest
				        .getTotalSize() + " WHERE " + DQRequest.GEN_FIELD_KEY + "  ='" + dRequest.getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
				flag = executeWriteQuery(uQuery);
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			close();
		}
		return flag;
	}


	public boolean updateDQRequestData(DQRequest dRequest, String userId) {

		boolean flag = false;

		try {
			if (dRequest != null) {
				String uQuery = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_TIMEESTIMATIONS + " = " + dRequest
				        .getTotalSize() + ", " + DQRequest.GEN_FIELD_DOWNLOADEDSIZE + " = " + dRequest.getDownloadedSize() + ", " + DQRequest.GEN_FIELD_STATUS + " = " + dRequest
				        .getStatus().ordinal() + " WHERE " + DQRequest.GEN_FIELD_KEY + "  ='" + dRequest.getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
				flag = executeWriteQuery(uQuery);
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			close();
		}
		return flag;
	}


	public boolean updateErrorDiscription(DQRequest dRequest, String userId) {

		boolean flag = false;

		try {
			if (dRequest != null) {
				String uQuery = "UPDATE " + DQRequest.GEN_TABLE_NAME + " set " + DQRequest.GEN_FIELD_ERRORDISCRIPTION + " = '" + dRequest
				        .getErrorDiscription() + "' WHERE key ='" + dRequest.getKey() + "' AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
				flag = executeWriteQuery(uQuery);
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			close();
		}
		return flag;
	}


	public boolean isItemAvilableForDownload(String userId) {

		boolean flag = false;
		Cursor cursor = null;
		try {
			String uQuery = "SELECT count(*) FROM " + DQRequest.GEN_TABLE_NAME + " WHERE " + DQRequest.GEN_FIELD_STATUS + " !=" + DQDownloadingStatus.PAUSED
			        .ordinal() + " AND " + DQRequest.GEN_FIELD_USERID + "  = '" + userId + "'";
			cursor = executeReadQuery(uQuery);

			if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
				flag = true;
			}
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(exception);
		} finally {
			if (cursor != null && !cursor.isClosed()) {

				cursor.close();
				cursor = null;
			}
			close();
		}
		return flag;
	}

}