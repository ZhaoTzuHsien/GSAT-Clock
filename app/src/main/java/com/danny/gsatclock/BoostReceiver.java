package com.danny.gsatclock;
import android.content.*;

public class BoostReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO: Implement this method
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Intent it = new Intent(context, RebootBroadcastService.class);
			context.startService(it);
		}
	}
	
}
