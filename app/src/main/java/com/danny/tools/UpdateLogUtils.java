package com.danny.tools;
import android.content.*;
import android.app.*;
import com.danny.gsatclock.*;

public class UpdateLogUtils
{
	private static final int UPDATE_LOG_TITLE = R.string.prefs_update_log;
	private static final int UPDATE_LOG1 = R.string.update_log1;
	private static final int UPDATE_LOG2 = R.string.update_log2;
	private static final int UPDATE_LOG3 = R.string.update_log3;
	private static final int UPDATE_LOG4 = R.string.update_log4;
	private static final int UPDATE_LOG5 = R.string.update_log5;
	private static final int UPDATE_LOG6 = R.string.update_log6;
	private static final int UPDATE_LOG7 = R.string.update_log7;
	
	public static void showUpdateLog(Context context) {
		String updateLog1 = context.getResources().getString(UPDATE_LOG1);
		String updateLog2 = context.getResources().getString(UPDATE_LOG2);
		String updateLog3 = context.getResources().getString(UPDATE_LOG3);
		String updateLog4 = context.getResources().getString(UPDATE_LOG4);
		String updateLog5 = context.getResources().getString(UPDATE_LOG5);
		String updateLog6 = context.getResources().getString(UPDATE_LOG6);
		String updateLog7 = context.getResources().getString(UPDATE_LOG7);
		String sMessage = updateLog7 + "\n\n" + updateLog6 + "\n\n" + updateLog5 + "\n\n" + updateLog4 + "\n\n" + updateLog3 + "\n\n" + updateLog2 + "\n\n" + updateLog1;
		
		new AlertDialog.Builder(context).setTitle(UPDATE_LOG_TITLE).setMessage(sMessage).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				// TODO: Implement this method
			}
		}).show();
	}
}
