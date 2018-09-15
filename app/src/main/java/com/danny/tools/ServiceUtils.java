package com.danny.tools;
import android.content.*;
import android.util.*;
import android.preference.*;

public class ServiceUtils extends BroadcastReceiver
{
	public static final String SERVICE_RESTART = "com.danny.tools.ServiceUtils.RESTART";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO: Implement this method
		if (intent.getAction().equals(SERVICE_RESTART)) {
			Log.d(ServiceUtils.class.getName(), "onReceive " + SERVICE_RESTART);
			startService(context);
		}
	}
	
	public static void startService(Context context) {
		boolean isWidgetExist = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_widget_exist", true);
		if (isWidgetExist) {
			Intent it = new Intent(context, BackgroundBroadcastService.class);
			context.startService(it);
		}
	}
	
	public static void registerService(Context context) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		preference.edit().putBoolean("is_widget_exist", true).commit();
	}
	
	public static void unregisterService(Context context) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		preference.edit().putBoolean("is_widget_exist", false).commit();
	}
}
