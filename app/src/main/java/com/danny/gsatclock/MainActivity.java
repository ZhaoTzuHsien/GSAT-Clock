package com.danny.gsatclock;

import android.os.*;
import android.support.v7.app.*;
import android.content.*;
import android.appwidget.*;
import android.app.*;
import java.util.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import android.widget.*;

public class MainActivity extends AppCompatActivity 
{
    private int mAppWidgetId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		Intent it = getIntent();
		Bundle extras = it.getExtras();
		if (extras != null)
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
			finish();
		
		Intent itOut = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
		PendingIntent penIt = PendingIntent.getBroadcast(this, 0, itOut, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 1);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), penIt);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, penIt);
		
		MyAppWidget.saveAlarmManager(alarmManager, penIt);
		
		Intent itAppWdgetConfigResult = new Intent();
		itAppWdgetConfigResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, itAppWdgetConfigResult);
		finish();
    }
}
