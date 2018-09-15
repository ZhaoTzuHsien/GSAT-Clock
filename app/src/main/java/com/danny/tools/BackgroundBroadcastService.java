package com.danny.tools;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import java.util.*;
import android.support.v7.preference.*;

public class BackgroundBroadcastService extends Service
{

	@Override
	public IBinder onBind(Intent p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void onCreate() {
		// TODO: Implement this method
		//ServiceUtils.startService(getApplicationContext());
		
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Implement this method
		Log.d(BackgroundBroadcastService.class.getName(), "onStartCommand");
		
		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO: Implement this method
				Intent it = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
				sendBroadcast(it);
				Log.d(BackgroundBroadcastService.class.getName(), "broadcast widget update has sent");
			}		
		}, 1 * 1000);*/
		
		// send alarm manager
		/*Intent itOut = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
		PendingIntent penIt = PendingIntent.getBroadcast(this, 0, itOut, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 1);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), penIt);*/
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, penIt);
		
		// send broadcast
		Intent it = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
		sendBroadcast(it);
		Log.d(BackgroundBroadcastService.class.getName(), "broadcast widget update has sent");
		
		// get battery save mode
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isBatterySaveMode = sharedPreferences.getBoolean("battery_save_mode", false);
		
		int delaySecond = 3;
		if (isBatterySaveMode) 
			delaySecond = 30;
		
		// destroy service
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO: Implement this method
				stopSelf();
			}	
		}, delaySecond * 1000);
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO: Implement this method
		Intent it = new Intent(ServiceUtils.SERVICE_RESTART);
		sendBroadcast(it);
		Log.d(BackgroundBroadcastService.class.getName(), "onDestroy");
		super.onDestroy();
	}
}
