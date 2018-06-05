package com.example.poott.newdesign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MyFirebase firebase = new MyFirebase();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final private DatabaseReference myRef = database.getReference();

    ArrayList<ConfirmedOrder> listOrder = new ArrayList<ConfirmedOrder>();

    BroadcastReceiver mMessageReceiver1 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean opened = intent.getBooleanExtra("message",true);
            if(opened)OpeningTime();
            else ClosingTime();
        }
    };

    BroadcastReceiver mMessageReceiver2 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            FillData();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sticky immersive
        MyApp.Fullscreen(getWindow());

        if(!MyApp.StoreOpen()){
            ClosingTime();
        }

        //Update Menu
        try {
            this.UpdateMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Besteloverzicht invullen
        FillData();

        //Button settings
        ImageButton ib_settings = (ImageButton) findViewById(R.id.ib_settings);
        ib_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver1,
                        new IntentFilter("closingtime"));

        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver2,
                        new IntentFilter("update"));
    }

    public void setTotaalPrijs(){
        double totaalprijs = 0;
        for(ConfirmedOrder order: listOrder){
            for(Map.Entry<String, OrderItem> orderitem : order.orderitems.entrySet()){
                totaalprijs += orderitem.getValue().getPrijs();
            }
        }
        TextView tv_prijs = (TextView) findViewById(R.id.tv_totaalprijs);
        NumberFormat nf = new DecimalFormat("0.00");
        String prijsString = nf.format(totaalprijs);
        tv_prijs.setText("Totaal: €" + prijsString);
    }

    public void ClosingTime(){
        Button b_order = (Button) findViewById(R.id.button);
        TextClock tc_textclock = (TextClock) findViewById(R.id.hk_time);
        TextView tv_closed = (TextView) findViewById(R.id.tv_einde);

        b_order.setVisibility(View.GONE);
        tc_textclock.setVisibility(View.GONE);
        tv_closed.setVisibility(View.VISIBLE);
    }

    public void OpeningTime(){
        Button b_order = (Button) findViewById(R.id.button);
        TextClock tc_textclock = (TextClock) findViewById(R.id.hk_time);
        TextView tv_closed = (TextView) findViewById(R.id.tv_einde);

        b_order.setVisibility(View.VISIBLE);
        tc_textclock.setVisibility(View.VISIBLE);
        tv_closed.setVisibility(View.GONE);
    }

    public void FillData() {
        Log.e("Filldata","Filldata");
        //Listview layout
        final RecyclerView lv_order = (RecyclerView) findViewById(R.id.lv_bestellijst);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        lv_order.setLayoutManager(mLayoutManager);
        lv_order.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        listOrder = new ArrayList<ConfirmedOrder>();

        final TextView tv_leeg = (TextView) findViewById(R.id.tv_leeg);
        final TextView tv_besteloverzicht = (TextView) findViewById(R.id.tv_besteloverzicht);
        final TextView tv_totaal = (TextView) findViewById(R.id.tv_totaalprijs);

        //Set orderlist adapter
        final BestellijstAdapter2 orderAdapter = new BestellijstAdapter2(listOrder, getBaseContext());
        lv_order.setAdapter(orderAdapter);

        //Listview datalistener
        myRef.child("orders").child(MyApp.GetDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get data
                if(!dataSnapshot.exists()){
                    myRef.child("orders").child(MyApp.GetDate()).setValue(true);
                    tv_besteloverzicht.setVisibility(View.GONE);
                    tv_totaal.setVisibility(View.GONE);
                    tv_leeg.setVisibility(View.VISIBLE);
                }
                myRef.child("orders").child(MyApp.GetDate()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        tv_besteloverzicht.setVisibility(View.VISIBLE);
                        tv_totaal.setVisibility(View.VISIBLE);
                        tv_leeg.setVisibility(View.GONE);
                        listOrder.add(dataSnapshot.getValue(ConfirmedOrder.class));
                        orderAdapter.notifyItemInserted(listOrder.size());
                        mLayoutManager.scrollToPositionWithOffset(listOrder.size()-1,0);
                        setTotaalPrijs();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s){

                    }
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void Button(View view){
        Intent myIntent = new Intent(getBaseContext(), OrderActivity.class);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    public void UpdateMenu() throws IOException {
        firebase.DeleteMenu();
        Upload();
        Upload2();
    }

    public void Upload() throws IOException {
        InputStreamReader is = new InputStreamReader(getAssets()
                .open("testupload.csv"));
        BufferedReader reader = new BufferedReader(is);
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(";");
            ArrayList<String> opties = new ArrayList<String>();
            for(int i = 4; i < values.length; i++ ){
                opties.add(values[i]);}
            firebase.NewProduct(values[1],values[0], new Item(values[0], values[1], values[2], Double.parseDouble(values[3]), opties));
        }
    }
    public void Upload2() throws IOException {
        InputStreamReader is = new InputStreamReader(getAssets()
                .open("testupload2.csv"));
        BufferedReader reader = new BufferedReader(is);
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(";");
            Boolean actief = false;
            if(Double.parseDouble(values[4])==1){actief = true;}
            firebase.NewOptie(values[0], values[1], values[2], values[3], actief, Double.parseDouble(values[5]));
        }
    }
}
