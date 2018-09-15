package com.danny.gsatclock;
import android.support.v14.preference.PreferenceFragment;
import android.os.*;
import android.support.v7.preference.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import android.app.*;
import android.widget.DatePicker.*;
import android.widget.*;
import android.content.*;
import java.util.*;
import android.util.*;
import com.danny.tools.*;

public class WidgetPreference extends PreferenceFragment
{
	private OnFragmentInteractionListener mCallback;

	@Override
	public void onAttach(Activity activity)
	{
		// TODO: Implement this method
		super.onAttach(activity);
		try {
			mCallback = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement login_input.LoginInputCallBack");
		}
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		// TODO: Implement this method
		addPreferencesFromResource(R.xml.preferences);
		
		// set check update summary
		Preference checkUpdate = findPreference("check_update");
		String originalSummary = (String) checkUpdate.getSummary();
		try {
			String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			String sCheckUpdateSummary = originalSummary + versionName;
			checkUpdate.setSummary(sCheckUpdateSummary);
		}
		catch (PackageManager.NameNotFoundException e){
			e.printStackTrace();
		}
		
		// set choose date on click listener
		Preference chooseDate = findPreference("date_picker");
		chooseDate.setOnPreferenceClickListener(onPreferenceClick);
		
		// set choose time on click listener
		Preference chooseTime = findPreference("time_picker");
		chooseTime.setOnPreferenceClickListener(onPreferenceClick);
		
		// set update log on click listener
		Preference updateLog = findPreference("update_log");
		updateLog.setOnPreferenceClickListener(onPreferenceClick);
		
		// set check update on click listener
		checkUpdate.setOnPreferenceClickListener(onPreferenceClick);
	}

	@Override
	public void onDetach()
	{
		// TODO: Implement this method
		super.onDetach();
	}
	
	private Preference.OnPreferenceClickListener onPreferenceClick = new Preference.OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference clickedPreference)
		{
			// TODO: Implement this method
			String key = clickedPreference.getKey();
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());
			
			switch (key) {
				case "date_picker":
					Log.d(WidgetPreference.class.getName(), "onPickDateClick");
					int year = preference.getInt("date_picker_year", 2019);
					int month = preference.getInt("date_picker_month", 0);
					int day = preference.getInt("date_picker_day", 25);
					DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener,year, month, day);
					datePickerDialog.show();
					break;
				case "time_picker":
					Log.d(WidgetPreference.class.getName(), "onPickTimeClick");
					int hour = preference.getInt("time_picker_hour", 9);
					int minute = preference.getInt("time_picker_minute", 15);
					TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);
					timePickerDialog.show();
					break;
				case "update_log":
					UpdateLogUtils.showUpdateLog(getContext());
					break;
				case "check_update":
					mCallback.handleCheckUpdate();
					break;
			}
			return true;
		}
	};
	
	private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
		{
			// TODO: Implement this method
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());

			Calendar calendar = Calendar.getInstance();
			int startYear = calendar.get(Calendar.YEAR);
			int startMonth = calendar.get(Calendar.MONTH);
			int startDay = calendar.get(Calendar.DATE);

			boolean isValid = true;

			if (year < startYear) {
				showInvalidToast();
				isValid = false;
			} else if (year == startYear) {
				if (month < startMonth) {
					showInvalidToast();
					isValid = false;
				} else if (month == startMonth) {
					if (dayOfMonth < startDay) {
						showInvalidToast();
						isValid = false;
					}
				}
			}

			if (isValid) {
				preference.edit().putInt("date_picker_year", year).putInt("date_picker_month", month).putInt("date_picker_day", dayOfMonth).commit();
			}
		}
	};
	
	private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute)
		{
			// TODO: Implement this method
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());
			
			preference.edit().putInt("time_picker_hour", hour).putInt("time_picker_minute", minute).commit();
		}
	};
	
	private void showInvalidToast() {
		Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_LONG).show();
	}
	
	
	public interface OnFragmentInteractionListener {
		void handleCheckUpdate();
	}
}
