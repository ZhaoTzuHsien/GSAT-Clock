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
import android.preference.*;
import com.danny.tools.*;

public class MyAppWidget extends AppWidgetProvider
{
	
	@Override
	public void onEnabled(Context context)
	{
		// TODO: Implement this method
		ServiceUtils.registerService(context);
		
		/*RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		Intent configIntent = new Intent(context, MainActivity.class);
		PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
		views.setOnClickPendingIntent(R.id.main_widget, configPendingIntent);
		ComponentName appWidget = new ComponentName(context, MyAppWidget.class);
		appWidgetManager.updateAppWidget(appWidget, views);*/
		
		/*RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		Intent it = new Intent(context, MainActivity.class);
		PendingIntent pdIt = PendingIntent.getActivity(context, 1, it, 0);
		views.setOnClickPendingIntent(R.id.main_widget, pdIt);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName appWidget = new ComponentName(context, MyAppWidget.class);
		appWidgetManager.updateAppWidget(appWidget, views);*/
	}
	
	//private static AlarmManager mAlarmManager;
	//private static PendingIntent mPendingIntent;
	
	/*public static void saveAlarmManager(AlarmManager alarmManager,PendingIntent pendingIntent) {
		mAlarmManager = alarmManager;
		mPendingIntent = pendingIntent;
	}*/

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
			//mAlarmManager.cancel(mPendingIntent);
			
			// add new alarm manager
			/*Intent itOut = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
			PendingIntent penIt = PendingIntent.getBroadcast(context, 0, itOut, 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, 1);
			alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), penIt);
			saveAlarmManager(alarmManager, penIt);*/
		} 
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// TODO: Implement this method
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(MyAppWidget.class.getName(), "update");
		
		// get Preference data
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		int year = preference.getInt("date_picker_year", 2019);
		int month = preference.getInt("date_picker_month", 0);
		int day = preference.getInt("date_picker_day", 25);
		int hour = preference.getInt("time_picker_hour", 9);
		int minute = preference.getInt("time_picker_minute", 15);
		
		// get widget views
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		
		// set on click listener
		//views.setOnClickPendingIntent(R.id.main_widget, PendingIntent.getBroadcast(context, 5, new Intent(context, MyAppWidget.class).setAction(WIDGET_CLICKED), 0));
		Intent it = new Intent(context, MainActivity.class);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pdIt = PendingIntent.getActivity(context, 1, it, 0);
		views.setOnClickPendingIntent(R.id.main_widget, pdIt);
		
		// set goal time
		Calendar currentTime = Calendar.getInstance();
		Calendar gsatTime = Calendar.getInstance();
		gsatTime.set(Calendar.SECOND, 0);
        gsatTime.set(Calendar.MINUTE, minute);
        gsatTime.set(Calendar.HOUR, hour);
        gsatTime.set(Calendar.AM_PM, Calendar.AM);
        gsatTime.set(Calendar.MONTH, month);
        gsatTime.set(Calendar.DAY_OF_MONTH, day);
        gsatTime.set(Calendar.YEAR, year);
		
		// get division
		int yearDevision = gsatTime.get(Calendar.YEAR) - currentTime.get(Calendar.YEAR);
		int dayDevision = gsatTime.get(Calendar.DAY_OF_YEAR) + yearDevision * 365 - currentTime.get(Calendar.DAY_OF_YEAR);
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
		
		try {
			appWidgetManager.updateAppWidget(appWidgetIds[0], views);
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e(MyAppWidget.class.getName(), e.toString());
		}
	}

	@Override
	public void onDisabled(Context context)
	{
		// TODO: Implement this method
		ServiceUtils.unregisterService(context);
		super.onDisabled(context);
	}

	/*@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		// TODO: Implement this method
		try {
			mAlarmManager.cancel(mPendingIntent);
		} catch(Exception e) {
			Toast.makeText(context, "Null Pointer Exception Occurs", Toast.LENGTH_LONG).show();
		}
		super.onDeleted(context, appWidgetIds);
	}*/
	
}
