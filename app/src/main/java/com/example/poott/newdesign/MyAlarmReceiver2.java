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

public class MyAlarmReceiver2 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("E - HH:mm:ss");
        String test = sdf.format(calendar.getTime());

        Log.e("Reset!", test);
        MyApp.sendMessageOfStatus(true);
        MyApp.sendMessageToUpdate();
        MyApp.SetAlarmToReset();
    }
}
