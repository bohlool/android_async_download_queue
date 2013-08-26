
package com.confiz.downloadqueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.confiz.downloadqueue.db.DQDBAdapter;
import com.confiz.downloadqueue.model.DQActions;
import com.confiz.downloadqueue.utils.DQDebugHelper;
import com.confiz.downloadqueue.utils.DQUtilityNetwork;

public class DQNetworkManger extends BroadcastReceiver {


	private final String TAG = "DQNetworkManger";


	@Override
	public void onReceive(Context context, Intent intent) {

		DQDebugHelper.printData(TAG, "Receiver receive notification");

		boolean canConnect = DQManager.isAutoStartOnNetworkConnect(context);

		if (canConnect) {
			String userId = "-1";// get current user id to support multiple user
			// queue;
			if (intent.getAction().equals(android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (intent.getAction().equals(android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
					boolean noConnectivity = intent
					        .getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

					if (noConnectivity) {
						Log.i("Network", "Network is now DISCONNECTED");
					} else {
						Log.i("Network", "Network is now connected");
						boolean onlyOnWifi = DQManager.isDownloadOnlyOnWifi(context);
						if (onlyOnWifi) {
							if (DQUtilityNetwork.isConnectedToWifi(context)) {
								startDownloadQueue(context.getApplicationContext(), userId);
							}
						} else {
							startDownloadQueue(context.getApplicationContext(), userId);
						}
					}
				}
			}
		}
	}


	private void startDownloadQueue(Context context, String userId) {

		if (context != null) {
			boolean flag = DQDBAdapter.getInstance(context).isItemAvilableForDownload(userId);
			if (flag == true) {
				DQManager.getInstance(context);
				DQManager.startDQService(context, DQActions.START_DOWNLOAD);
			}
		}
	}
}
