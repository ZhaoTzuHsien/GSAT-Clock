package com.danny.tools;
import android.content.*;
import android.util.*;

public class ScreenStateReceiver extends BroadcastReceiver
{
	public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";
	public static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO: Implement this method
		if (intent.getAction().equals(SCREEN_ON)) {
			Log.d(ScreenStateReceiver.class.getName(), "onReceive " + SCREEN_ON);
			
			ServiceUtils.startService(context);
			
		} else if (intent.getAction().equals(SCREEN_OFF)) {
			Log.d(ScreenStateReceiver.class.getName(), "onReceive " + SCREEN_OFF);
		}
	}
}
