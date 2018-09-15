package com.danny.gsatclock;
import android.content.*;
import com.danny.tools.*;

public class BoostReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO: Implement this method
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			//Intent it = new Intent(context, RebootBroadcastService.class);
			//Intent it = new Intent(BackgroundBroadcastService.class.getName());
			//context.startService(it);
			ServiceUtils.startService(context);
		}
	}
	
}
