package com.example.poott.newdesign;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.opencsv.CSVWriter;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by poott on 24/02/2018.
 */

public class MyApp extends Application {
    public static MyApp singleInstance = null;

    // Firebase
    static FirebaseDatabase database;
    static DatabaseReference myRef;

    //Singelton
    public static MyApp getInstance() {return singleInstance;}

    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
        AndroidThreeTen.init(this);
        SetAlarmToClose();
        SetAlarmToReset();

        Log.e("path",android.os.Environment.getExternalStorageDirectory().getAbsolutePath());

        Calendar calendar = Calendar.getInstance();
        int currenthour = calendar.HOUR_OF_DAY;
        int currentminute = calendar.MINUTE;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public static void sendMessageOfStatus(boolean opened) {
        Intent intent = new Intent("closingtime");
        intent.putExtra("message", opened);
        LocalBroadcastManager.getInstance(singleInstance.getBaseContext()).sendBroadcast(intent);
    }

    public static void sendMessageToUpdate() {
        Intent intent = new Intent("update");
        LocalBroadcastManager.getInstance(singleInstance.getBaseContext()).sendBroadcast(intent);
    }

    public static void SetAlarmToReset(){
        final int RQS_2 = 2;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,0);
        calendar.add(Calendar.DATE, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("E - HH:mm:ss");
        String test = sdf.format(calendar.getTime());

        Log.e("SetAlarmToReset","Alarm ingesteld voor reset om " + test);

        AlarmManager alarmManager = (AlarmManager) singleInstance.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent2 = new Intent(singleInstance.getBaseContext(), MyAlarmReceiver2.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                singleInstance.getBaseContext(), RQS_2, alarmIntent2, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void SetAlarmToClose(){
        final int RQS_1 = 1;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(singleInstance.getBaseContext());
        int minutesAfterMidnight = prefs.getInt("t_closingtimeint2",9999999);

        int hours = minutesAfterMidnight / 60;
        int minutes = minutesAfterMidnight % 60;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE,minutes);
        calendar.set(Calendar.SECOND,0);

        if(!StoreOpen()){
            calendar.add(Calendar.DATE, 1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("E - HH:mm:ss");
        String test = sdf.format(calendar.getTime());

        Log.e("SetAlarmToClose","Alarm ingesteld om " + test);

        AlarmManager alarmManager = (AlarmManager) singleInstance.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(singleInstance.getBaseContext(), MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                singleInstance.getBaseContext(), RQS_1, alarmIntent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static String GetDate(){
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.of( "Europe/Brussels" );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyyMMdd" );
        String date = zdt.format(formatter);
        return date;
    }

    public static boolean StoreOpen(){
        Boolean open = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(singleInstance.getBaseContext());
        int minutesAfterMidnight = prefs.getInt("t_closingtimeint2",9999999);

        Calendar calendar = Calendar.getInstance();
        int currenthour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentminute = calendar.get(Calendar.MINUTE);
        int currenttime = (currenthour*60) + currentminute;

        SimpleDateFormat sdf = new SimpleDateFormat("E - HH:mm:ss");
        String test = sdf.format(calendar.getTime());

        Log.e("Store open?","Currenttime = " + currenttime + ", alarmtime = " + minutesAfterMidnight);

        if(currenttime+1 > minutesAfterMidnight){
            open = false;
        }
        return open;
    }

    public static void SendOrder(){
        final List<Order> listOrder = new ArrayList<Order>();
        myRef.child("bestellingen").child(MyApp.GetDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get data
                for (DataSnapshot order: dataSnapshot.getChildren()) {
                    listOrder.add(order.getValue(Order.class));
                }
                if(listOrder.size() > 0){
                    SendMailWithAttachment(listOrder);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void Fullscreen(final Window window){
        //Sticky immersive
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        View decorView = window.getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            window.getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
// again hide it
                        } else {
                            window.getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        }
                    }
                });
    }

    public static void SendMailWithAttachment(List<Order> listOrder){
        List<String[]> lijst = new ArrayList<String[]>();
        for(Order order: listOrder){
            for(OrderItem orderitem: order.orderitems){
                String naam = order.getUsername();
                String item = orderitem.getItem().getNaam();
                String prijs = String.valueOf(orderitem.getPrijs());
                String opties = "";
                for(String option: orderitem.ActiveOptions()){
                    opties += option + ", ";
                }
                lijst.add(new String[] {naam, item,opties, prijs});
            }
        }

        File folder = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath(),"mydir");
        Log.e("path",android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
        if(!folder.exists()){
            folder.getParentFile().mkdirs();
        }
        final File csvfile = new File(folder,"bestelling.csv");
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csvfile),';');
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CSVwriter","Eerst nog toestemming geven aan de app op de tablet");
        }
        writer.writeAll(lijst);
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("onReceive", "Ready to send mail");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("mijnlunch.mailing@gmail.com", "tuuch7av");
                    sender.addAttachment(csvfile.getAbsolutePath(),"TESTsubject");
                    sender.sendMail("Bestelling voor vandaag", "In bijlage kan je de bestelling terugvinden.",
                            "mijnlunch.mailing@gmail.com", "tijs.poot@hotmail.com");
                } catch (Exception e) {
                    Log.e("SendMailWithAttachment", e.getMessage(), e);
                }
            }

        }).start();
        /*
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tijs.poot@hotmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Graag deze broodjes leveren");
        i.putExtra(Intent.EXTRA_TEXT   , "zie bijlage");
        Uri uri = Uri.fromFile(csvfile);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        singleInstance.startActivityForResult(Intent.createChooser(i, "Send mail..."),12);*/
    }

}
