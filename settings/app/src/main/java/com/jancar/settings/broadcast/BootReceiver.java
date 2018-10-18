package com.jancar.settings.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import static com.jancar.settings.util.Tool.setAutoTime;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("FirstRun", 0);
		Boolean first_run = sharedPreferences.getBoolean("Time", true);
		Log.w("BootReceiver","afa");
		if (first_run) {
			/*sharedPreferences.edit().putBoolean("Time", false).commit();
			setAutoTime(context,true);*/
		}

	}

	private void setServiceIsStart(int flag, SharedPreferences sharedPreferences) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		//editor.putInt(AppContants.KEY_IS_SERVICE_START, flag);
		editor.commit();
	}
}
