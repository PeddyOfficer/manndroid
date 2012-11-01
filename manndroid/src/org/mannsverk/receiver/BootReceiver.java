package org.mannsverk.receiver;

import org.mannsverk.common.util.Util;
import org.mannsverk.service.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";
	

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "BootReceiver");
		//Intent service = new Intent(context, UpdateService.class);
		//context.startService(service);
		Util.registerAlarm(context, false);
	}

}
