package com.danny.gsatclock;

import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.content.*;
import android.appwidget.*;
import android.app.*;
import java.util.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import android.widget.*;
import com.danny.tools.*;
import android.preference.*;
import android.util.*;
import java.io.*;
import android.net.*;
import org.json.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.*;

public class MainActivity extends AppCompatActivity implements WidgetPreference.OnFragmentInteractionListener
{
    private int mAppWidgetId;
	private long jsonDownloadId;
	private long apkDownloadId;

	private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		// start gui
		setContentView(R.layout.activity_main);
		FragmentTransaction frmTrans = getFragmentManager().beginTransaction();
		WidgetPreference widgetPreference = new WidgetPreference();
		frmTrans.replace(R.id.mainFrame, widgetPreference, "Widget Preference").commit();
		
		init();
		
		// get widget id
		Intent it = getIntent();
		Bundle extras = it.getExtras();
		if (extras != null)
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		//if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
		//	finish();
		
		/*Intent itOut = new Intent("com.android.MY_OWN_WIDGET_UPDATE");
		PendingIntent penIt = PendingIntent.getBroadcast(this, 0, itOut, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 1);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), penIt);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, penIt);
		
		MyAppWidget.saveAlarmManager(alarmManager, penIt);*/
		
		// set result
		Intent itAppWdgetConfigResult = new Intent();
		itAppWdgetConfigResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, itAppWdgetConfigResult);
		
		// start background broadcast service
		Intent itService = new Intent(getApplicationContext(), BackgroundBroadcastService.class);
		startService(itService);
		
		//finish();
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
					// If request is cancelled, the result arrays are empty.
					if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						// permission was granted, yay! Do the
						// contacts-related task you need to do.
						getJsonObject();
					} else {
						// permission denied, boo! Disable the
						// functionality that depends on this permission.
						requestPermission();
					}
					break;
				}
		}
	}

	@Override
	public void handleCheckUpdate()
	{
		// TODO: Implement this method
		//new JsonAsyncTask().execute("https://www.dropbox.com/s/0mr2mubrikai7vn/version.json?dl=1");
		requestPermission();
	}
	
	private void getJsonObject() {
		downloadFile("https://www.dropbox.com/s/0mr2mubrikai7vn/version.json?dl=1", "/version", "version.json");
		registerReceiver(onCompleteListener, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	private void downloadFile(String url, String location, String fileName) {
		// check network state
		ConnectivityManager cm =
			(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
			activeNetwork.isConnectedOrConnecting();
		
		if (!isConnected) {
			Toast.makeText(MainActivity.this, R.string.internet_disconnect, Toast.LENGTH_LONG).show();
			return;
		}
		
		// check and add download location
		File direct = new File(Environment.getExternalStorageDirectory() + "/GSAT Clock" + location);
		if (!direct.exists()) {
			direct.mkdirs();
		}
		
		File targetFile = new File(Environment.getExternalStorageDirectory() + "/GSAT Clock" + location + "/" + fileName);
		if (targetFile.exists())
			targetFile.delete();
		
		// start download manager
		DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		
		// start request
		Uri downloadUri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(downloadUri);
		
		// set request
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
		request.setAllowedOverRoaming(false);
		//request.setTitle("Downloading");
		//request.setDescription("Downloading file, please wait!");
		request.setDestinationInExternalPublicDir("/GSAT Clock" + location, fileName);
		
		// start download
		if (fileName.equals("version.json"))
			jsonDownloadId = downloadManager.enqueue(request);
		else if (fileName.equals("update.apk"))
			apkDownloadId = downloadManager.enqueue(request);
	}
	
	private BroadcastReceiver onCompleteListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context p1, Intent intent) {
			// TODO: Implement this method
			long completedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (completedId == jsonDownloadId) {
				try {
					File file = new File(Environment.getExternalStorageDirectory(), "/GSAT Clock/version/version.json");
					FileInputStream stream = new FileInputStream(file);
					
					String jsonStr = null;
					
					try {
						FileChannel fileChannel = stream.getChannel();
						MappedByteBuffer bytebuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
						
						jsonStr = Charset.defaultCharset().decode(bytebuffer).toString();
					} catch (IOException e) {
						Log.e(MainActivity.class.getName(), e.toString());
						e.printStackTrace();
					} finally {
						try {
							stream.close();
						} catch (IOException e) {
							Log.e(MainActivity.class.getName(), e.toString());
							e.printStackTrace();
						}
					}
					
					try {
						JSONObject rootObject = new JSONObject(jsonStr);
						int versionCode = rootObject.getInt("version_code");
						String versionName = rootObject.getString("version_name");
						String url = rootObject.getString("url");
						int currentVersionCode = 0;
						try {
							currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
						} catch (PackageManager.NameNotFoundException e) {
							Log.e(MainActivity.class.getName(), e.toString());
							e.printStackTrace();
						} finally {
							if (versionCode > currentVersionCode) {
								showSureUpdateDialog(versionName, url);
							} else {
								Toast.makeText(MainActivity.this, R.string.latest_version_toast, Toast.LENGTH_LONG).show();
							}
						}
					} catch (JSONException e) {
						Log.e(MainActivity.class.getName(), e.toString());
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					Log.e(MainActivity.class.getName(), e.toString());
					e.printStackTrace();
				}
			} else if (completedId == apkDownloadId) {
				File file = new File(Environment.getExternalStorageDirectory(), "/GSAT Clock/apk/update.apk");
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				install.setDataAndType(Uri.fromFile(file),  "application/vnd.android.package-archive");
				startActivity(install);
				unregisterReceiver(onCompleteListener);
			}
		}
	};
	
	private void showSureUpdateDialog(String versionName, final String url) {
		String sTtile = getResources().getString(R.string.check_update_dialog_title);
		String sMessage = String.format(getResources().getString(R.string.check_update_dialog_message), versionName);
		new AlertDialog.Builder(this).setTitle(sTtile).setMessage(sMessage).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					downloadFile(url, "/apk", "update.apk");
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
				}
			}).show();
	}
	
	private void requestPermission() {
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			// Permission is not granted
			// Should we show an explanation?
			//if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
			//} else {
				// No explanation needed; request the permission
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			//}
		} else {
			// Permission has already been granted
			getJsonObject();
		}
	}
	
	private void init() {
		SharedPreferences sharedPreferences = getSharedPreferences("package_info", 0);
		int recordVersionCode = sharedPreferences.getInt("version_code", 0);
		try {
			int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			if (recordVersionCode != versionCode) {
				UpdateLogUtils.showUpdateLog(this);
				sharedPreferences.edit().putInt("version_code", versionCode).commit();
			}
		}
		catch (PackageManager.NameNotFoundException e){
			e.printStackTrace();
		}
	}
}
