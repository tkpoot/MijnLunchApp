package com.example.poott.newdesign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by poott on 19/03/2018.
 */

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("E - HH:mm:ss");
        String test = sdf.format(calendar.getTime());

        Log.e("alarm om", test);
        MyApp.sendMessageOfStatus(false);
        MyApp.SendOrder();
        MyApp.SetAlarmToClose();
    }
}
