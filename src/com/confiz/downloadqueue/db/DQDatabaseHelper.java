/*
 * Property : Confiz Solutions
 * Created by : Arslan Anwar
 * Updated by : Arslan Anwar
 */

package com.confiz.downloadqueue.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQDebugHelper;


/**
 * DQDatabaseHelper class. Overrides methods of onCreate and on Upgrade for
 * version control. It also contains the methods to upgrade the database.
 * 
 * @author Muhammad Arslan Anwar	
 */
class DQDatabaseHelper extends SQLiteOpenHelper {


	/** The context. */
	private Context mContext = null;

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/** The Constant DB_NAME. */
	private static final String DB_NAME = "download_queue.db";


	/**
	 * Instantiates a new database helper.
	 * 
	 * @param context
	 *            the context
	 */
	DQDatabaseHelper(Context context) {

		super(context, DQDatabaseHelper.DB_NAME, null, DQDatabaseHelper.DATABASE_VERSION);
		this.mContext = context;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();
		try {
//			DQDebugHelper.printData("dbAdapter-version", "" + db.getVersion());
//			DQDebugHelper.printData("dbadapter-transaction", "" + db.inTransaction());
			db.execSQL(DQRequest.GEN_CREATE);
//			DQDebugHelper.printData("dbAHelper-create", DQRequest.GEN_CREATE);
			db.execSQL("COMMIT");
			db.setTransactionSuccessful();
		} catch (Exception e) {
			DQDebugHelper.printAndTrackException(e);
		} finally {
			db.endTransaction();
		}


		db.beginTransaction();

		boolean success = false;

		for (int i = 2; i <= DQDatabaseHelper.DATABASE_VERSION; i++) {

			switch (i) {
				case 2:
					success = this.upgradeToVersion2(db);
					break;
				default:
					break;
			}
		}

		if (success) {
			db.setTransactionSuccessful();
		}
		db.endTransaction();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
	 * int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		try {
			if (newVersion > oldVersion) {
				db.beginTransaction();

				boolean success = true;
				for (int i = oldVersion + 1; i <= newVersion; ++i) {

					switch (i) {
						case 2:
							success = this.upgradeToVersion2(db);
							break;
						default:
							break;
					}
				}

				if (success) {
					db.setTransactionSuccessful();
				}
				db.endTransaction();
			} else {
				// clearDatabase(db)
				// onCreate(db);
			}
		} catch (final Exception exception) {
			DQDebugHelper.printAndTrackException(this.mContext, exception);
		}
	}


	// ************************************************************************//
	// ************************************************************************//
	// *************************** UPGRADE METHODS ***************************//
	// ************************************************************************//
	// ************************************************************************//

	/**
	 * Upgrade to version2.
	 * 
	 * @param db
	 *            the db
	 * @return true, if successful
	 */
	private boolean upgradeToVersion2(SQLiteDatabase db) {

		boolean upgraded = false;

		// DQDebugHelper.printData("query", "in method upgradeToVersion2");
		try {

			upgraded = true;
		} catch (final SQLException exception) {
			DQDebugHelper.printAndTrackException(exception);
		}

		return upgraded;
	}

}