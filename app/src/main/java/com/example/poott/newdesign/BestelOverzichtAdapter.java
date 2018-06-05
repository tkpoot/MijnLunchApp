package com.example.poott.newdesign;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BestelOverzichtAdapter extends BaseAdapter {

    Context context;

    Order order;
    Activity activity;
    TextView textviewBestellingsproduct;
    TextView textviewBestellingsopties;
    TextView textviewBestellingsprijs;

    public BestelOverzichtAdapter(Activity activity, Order order){
        super();
        this.activity = activity;
        this.order = order;
        this.context = context;
    }
    @Override
    public int getCount() {
        return order.orderitems.size();
    }
    @Override
    public Object getItem(int position) {
        return order.orderitems.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=activity.getLayoutInflater();
        boolean flag = true;

        if(convertView == null){
            convertView=inflater.inflate(R.layout.adapter_bestellingsoverzicht, null);
            flag = false;
            textviewBestellingsproduct =(TextView) convertView.findViewById(R.id.bestellingsitem_product);
            textviewBestellingsopties =(TextView) convertView.findViewById(R.id.bestellingsitem_optie);
            textviewBestellingsprijs =(TextView) convertView.findViewById(R.id.bestellingsitem_prijs);
        }

        OrderItem orderitem = order.orderitems.get(position);
        ArrayList<String> opties = orderitem.ActiveOptions();

        if(opties.size()>=1){
            textviewBestellingsopties.setText(opties.get(opties.size()-1));
        }
        for(int i = opties.size()-2; i >= 0; i-- ){
            textviewBestellingsopties.append(", ");
            textviewBestellingsopties.append(opties.get(i));
        }

        textviewBestellingsproduct.setText(orderitem.getItem().getNaam());
        textviewBestellingsprijs.setText(orderitem.PrijsString());

        if(flag == false) {
            Animation animation = AnimationUtils
                    .loadAnimation(activity, R.anim.trans_bottom_in);
            convertView.startAnimation(animation);
        }

        return convertView;
    }
}
