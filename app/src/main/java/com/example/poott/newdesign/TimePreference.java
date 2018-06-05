package com.example.poott.newdesign;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;

import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by poott on 19/03/2018.
 */

public class TimePreference extends DialogPreference {
    private Calendar calendar;
    private TimePicker picker = null;
    private int mTime;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText("set");
        setNegativeButtonText("cancel");
        calendar = new GregorianCalendar();
    }

    public int getTime() {
        return mTime;
    }
    public void setTime(int time) {
        mTime = time;
        // Save to Shared Preferences
        persistInt(time);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        // Get the time from the related Preference
        Integer minutesAfterMidnight = null;
        minutesAfterMidnight = this.getTime();

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            picker.setIs24HourView(is24hour);
            picker.setCurrentHour(hours);
            picker.setCurrentMinute(minutes);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // generate value to save
            int hours = picker.getCurrentHour();
            int minutes = picker.getCurrentMinute();
            int minutesAfterMidnight = (hours * 60) + minutes;

            // Get the related Preference and save the value
            DialogPreference preference = this;
            TimePreference timePreference = ((TimePreference) preference);
            if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    timePreference.setTime(minutesAfterMidnight);
            }
            MyApp.SetAlarmToClose();
            if(MyApp.StoreOpen()){
                MyApp.sendMessageOfStatus(true);
            } else {
                MyApp.sendMessageOfStatus(false);
            }

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getInt(index,0));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setTime(restoreValue ?
                getPersistedInt(mTime) : (int) defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        int minutesAfterMidnight = 0;
        minutesAfterMidnight = this.getTime();

        int hours = minutesAfterMidnight / 60;
        int minutes = minutesAfterMidnight % 60;

        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE,minutes);
        return DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
    }
}
