package com.danny.gsatclock;
import android.appwidget.*;
import android.app.*;
import android.content.*;
import android.util.*;
import android.widget.RemoteViews.*;
import android.widget.*;
import com.danny.gsatclock.*;
import java.util.*;
import android.text.*;
import android.text.style.*;

public class MyAppWidget extends AppWidgetProvider
{
	private static AlarmManager mAlarmManager;
	private static PendingIntent mPendingIntent;
	
	public static void saveAlarmManager(AlarmManager alarmManager,PendingIntent pendingIntent) {
		mAlarmManager = alarmManager;
		mPendingIntent = pendingIntent;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO: Implement this method
		super.onReceive(context, intent);
		
		if (intent.getAction().equals("com.android.MY_OWN_WIDGET_UPDATE")) {
			
			// update views
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MyAppWidget.class.getName());
			int[] appWidgetIds = manager.getAppWidgetIds(thisAppWidget);
			onUpdate(context, manager, appWidgetIds);
			
			// cancel alarm manager
			mAlarmManager.cancel(mPendingIntent);
			
			// add new alarm manager
			Intent itOut = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
			PendingIntent penIt = PendingIntent.getBroadcast(context, 0, itOut, 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, 1);
			alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), penIt);
			saveAlarmManager(alarmManager, penIt);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// TODO: Implement this method
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(MyAppWidget.class.getName(), "update");
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		Calendar currentTime = Calendar.getInstance();
		Calendar gsatTime = Calendar.getInstance();
		gsatTime.set(Calendar.SECOND, 0);
        gsatTime.set(Calendar.MINUTE, 15);
        gsatTime.set(Calendar.HOUR, 9);
        gsatTime.set(Calendar.AM_PM, Calendar.AM);
        gsatTime.set(Calendar.MONTH, Calendar.JANUARY);
        gsatTime.set(Calendar.DAY_OF_MONTH, 25);
        gsatTime.set(Calendar.YEAR, 2019);
		
		// get division
		int dayDevision = gsatTime.get(Calendar.DAY_OF_YEAR) + 365 - currentTime.get(Calendar.DAY_OF_YEAR);
		int hourDevision = gsatTime.get(Calendar.HOUR_OF_DAY) - currentTime.get(Calendar.HOUR_OF_DAY);
		int minuteDivision = gsatTime.get(Calendar.MINUTE) - currentTime.get(Calendar.MINUTE);
		
		// adapt division
		if (minuteDivision < 0) {
			minuteDivision += 60;
			hourDevision -= 1;
		}
		if (hourDevision < 0) {
			hourDevision += 24;
			dayDevision -= 1;
		}
		String sRestTime = Integer.toString(dayDevision) + "天 " + Integer.toString(hourDevision) + "小時 " + Integer.toString(minuteDivision) + "分";
		
		// set size
		SpannableString spannedString = new SpannableString(sRestTime);
		int dayIndex = sRestTime.indexOf("天");
		//int hourIndex = sRestTime.indexOf("小時");
		//int minuteIndex = sRestTime.indexOf("分");
		spannedString.setSpan(new RelativeSizeSpan(1.8f), 0, dayIndex + 1, 0);
		//spannedString.setSpan(new RelativeSizeSpan(1.5f), dayIndex + 2, hourIndex, 0);
		//spannedString.setSpan(new RelativeSizeSpan(1.5f), hourIndex + 2, minuteIndex, 0);
		
		views.setTextViewText(R.id.appWidgetTxtRestTime, spannedString);
		
		appWidgetManager.updateAppWidget(appWidgetIds[0], views);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		// TODO: Implement this method
		try {
			mAlarmManager.cancel(mPendingIntent);
		} catch(Exception e) {
			Toast.makeText(context, "Null Pointer Exception Occurs", Toast.LENGTH_LONG).show();
		}
		super.onDeleted(context, appWidgetIds);
	}
	
}
