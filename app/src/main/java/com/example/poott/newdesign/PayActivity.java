package com.example.poott.newdesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

/**
 * Created by poott on 11/02/2018.
 */

public class PayActivity extends AppCompatActivity {

    MyFirebase firebase = new MyFirebase();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);



        Gson gson = new Gson();
        final Order order = gson.fromJson(getIntent().getStringExtra("Order"), Order.class);

        //Sticky immersive
        MyApp.Fullscreen(getWindow());

        Button button = (Button) findViewById(R.id.b_afrekenen);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.editText);
                if(et.getText().toString().matches("")){
                    order.setUsername("Anoniem");
                }
                else {
                    order.setUsername(et.getText().toString());
                }
                firebase.NewOrder(order);


                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        //Set Button 'Back'
        ImageButton button_back = (ImageButton) findViewById(R.id.ib_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void Button(View view){
        Intent myIntent = new Intent(getBaseContext(), OrderActivity.class);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

}