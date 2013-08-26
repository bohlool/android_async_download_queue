/**
 * 
 */

package com.confiz.downloadqueue.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.downloadqueue.R;


/**
 * @author Confiz
 * 
 */
public class DQUtilityNotifiacation {


	private static final String TAG = "DQUtilityNotifiacation.java";
	
	private static int notificationID = 1;

	public static int showNotification(Context context, String title, String message) {

		try {
			notificationID++;
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
			        .setContentText(message);
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(notificationID, mBuilder.getNotification());
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(TAG, exception);
		}
		return notificationID;
	}
	

	public static int destroyNotification(Context context, int id) {

		try {
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.cancel(id);
		} catch (Exception exception) {
			DQDebugHelper.printAndTrackException(TAG, exception);
		}
		return notificationID;
	}
}
