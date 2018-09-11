package com.danny.gsatclock;
import android.app.*;
import android.content.*;
import android.os.*;
import java.util.*;
import android.widget.*;

public class RebootBroadcastService extends Service
{

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: Implement this method
		broadcast();
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run()
			{
				// TODO: Implement this method
				broadcast();
				Handler handler2 = new Handler();
				handler2.postDelayed(new Runnable() {
					@Override
					public void run()
					{
						// TODO: Implement this method
						stopSelf();
					}
				}, 5 * 1000);
			}
		}, 20 * 1000);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void broadcast() {
		Intent itOut = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
		PendingIntent penIt = PendingIntent.getBroadcast(this, 0, itOut, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 1);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), penIt);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, penIt);

		MyAppWidget.saveAlarmManager(alarmManager, penIt);
	}
}
