package com.example.poott.newdesign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class ItemDetailsFragment extends Fragment {

    MyFirebase firebase = new MyFirebase();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    OrderItem orderitem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_itemdetails, container, false);

        //Get params from orderactivity
        final OrderActivity orderactivity = (OrderActivity) getActivity();
        Bundle args = getArguments();
        String index = args.getString("item", "404 error");
        final Item item = new Gson().fromJson(index, Item.class);
        orderitem = new OrderItem(item);

        //Set titel text
        final TextView tv = (TextView) view.findViewById(R.id.tv_titel);
        tv.setText(item.getNaam());

        //Set Button 'Close'
        ImageButton button_close = (ImageButton) view.findViewById(R.id.ib_close);
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DrawerLayout drawer = (DrawerLayout) orderactivity.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.RIGHT);
            }
        });

        //Set Button 'Add to order'
        Button button_add = (Button) view.findViewById(R.id.b_toevoegen);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DrawerLayout drawer = (DrawerLayout) orderactivity.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.RIGHT);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                orderactivity.addOrderItem(orderitem);
                orderactivity.Animate();
            }
        });

        //Set Button 'Pay'
        Button button_pay = (Button) view.findViewById(R.id.b_afrekenen);
        button_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderactivity.addOrderItem(orderitem);
                orderactivity.Afrekenen();
                orderactivity.Animate();
            }
        });

        //Fill options
        LinearLayout optie_container = (LinearLayout) view.findViewById(R.id.ll_optiecontainer);
        final TextView tv_prijs = (TextView) view.findViewById(R.id.tv_prijs);
        NewItemFillOptions(item, optie_container, tv_prijs);
        tv_prijs.setText(String.valueOf(orderitem.PrijsString()));

        // Inflate the layout for this fragment
        return view;
    }

    public void NewItemFillOptions(final Item item, final LinearLayout optie_container, final TextView tv_prijs){
        myRef.child("menu").child("opties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(item.getOpties() != null){
                    for (String optiecode : item.getOpties()) {
                        DataSnapshot option = dataSnapshot.child(optiecode);
                        String naam = option.child("naam").getValue().toString();
                        String omschrijving = option.child("omschrijving").getValue().toString();
                        String type = option.child("type").getValue().toString();
                        ArrayList<OptionValue> waarden = new ArrayList<OptionValue>();
                        for (DataSnapshot optionvalue : option.child("waarden").getChildren()) {
                            String valuenaam = optionvalue.child("naam").getValue().toString();
                            boolean actief = Boolean.parseBoolean(optionvalue.child("actief").getValue().toString());
                            double prijs = Double.parseDouble(optionvalue.child("prijs").getValue().toString());
                            waarden.add(new OptionValue(valuenaam, actief, prijs));
                        }
                        orderitem.options.add(new Option(naam, omschrijving,type,waarden));
                    }
                    FillOptions(optie_container, orderitem, tv_prijs);
                }
                else{
                    final LayoutInflater optie_inflater = LayoutInflater.from(getActivity());
                    LinearLayout titel = (LinearLayout) optie_inflater.inflate(R.layout.optie, optie_container, false);
                    TextView optietitel = (TextView) titel.findViewById(R.id.optietitel);
                    optietitel.setText(item.getOmschrijving());
                    optie_container.addView(titel);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void FillOptions(final LinearLayout ll, final OrderItem orderitem, final TextView tv_prijs) {
        //Inflater voor container initializeren en leegmaken
        final LayoutInflater optie_inflater = LayoutInflater.from(getActivity());
        final LinearLayout optie_container = ll;
        if(orderitem.options.size()>0) {
            // Elke optie uitwerken
            for (Option option : orderitem.options) {
                //Optietitel inflaten en invullen
                LinearLayout titel = (LinearLayout) optie_inflater.inflate(R.layout.optie, optie_container, false);
                TextView optietitel = (TextView) titel.findViewById(R.id.optietitel);
                optietitel.setText(option.getOmschrijving());
                optie_container.addView(titel);

                //Optie inflaten en invullen
                switch (option.getType()) {
                    case "RADIO":
                        RadioGroup radiogroup = (RadioGroup) optie_inflater.inflate(R.layout.optie_radio, optie_container, false);
                        // Elke radiooptie uitwerken
                        for (final OptionValue optionvalue : option.getWaarden()) {
                            RadioButton option_button = (RadioButton) optie_inflater.inflate(R.layout.optie_radiobutton, radiogroup, false);
                            option_button.setText(optionvalue.getNaam());

                            // Prijs van optie bijzetten
                            double prijs = optionvalue.getPrijs();
                            if (prijs > 0) {
                                NumberFormat nf = new DecimalFormat("0.00");
                                String prijsString = nf.format(prijs);
                                option_button.append(" ");
                                option_button.append((Html.fromHtml("<font color='Grey'>" + "(+ €" + prijsString + ")" + "</font>")));
                            }
                            if (prijs < 0) {
                                NumberFormat nf = new DecimalFormat("0.00");
                                String prijsString = nf.format(prijs*-1);
                                option_button.append(" ");
                                option_button.append((Html.fromHtml("<font color='Grey'>" + "(- €" + prijsString + ")" + "</font>")));
                            }

                            // Actieve optie eerst zetten en actief zetten
                            if (optionvalue.isActief()) {
                                radiogroup.addView(option_button, 0);
                                option_button.setChecked(true);
                                optionvalue.setActief(true);
                            } else {
                                radiogroup.addView(option_button, -1);
                            }

                            // On click optie
                            option_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        optionvalue.setActief(true);
                                    } else {
                                        optionvalue.setActief(false);
                                    }
                                    tv_prijs.setText(String.valueOf(orderitem.PrijsString()));
                                }
                            });
                        }
                        optie_container.addView(radiogroup);
                        break;

                    case "CHECK":
                        // Elke radiooptie uitwerken
                        for (final OptionValue optionvalue : option.getWaarden()) {
                            CheckBox option_button = (CheckBox) optie_inflater.inflate(R.layout.optie_checkbox, optie_container, false);
                            option_button.setText(optionvalue.getNaam());

                            // Prijs van optie bijzetten
                            double prijs = optionvalue.getPrijs();
                            if (prijs > 0) {
                                NumberFormat nf = new DecimalFormat("0.00");
                                String prijsString = nf.format(prijs);
                                option_button.append(" ");
                                option_button.append((Html.fromHtml("<font color='Grey'>" + "(+ €" + prijsString + ")" + "</font>")));
                            }
                            if (prijs < 0) {
                                NumberFormat nf = new DecimalFormat("0.00");
                                String prijsString = nf.format(prijs); prijsString.replace("-","");
                                option_button.append(" ");
                                option_button.append((Html.fromHtml("<font color='Grey'>" + "(- €" + prijsString + ")" + "</font>")));
                            }

                            // Actieve optie eerst zetten en actief zetten
                            if (optionvalue.isActief()) {
                                option_button.setChecked(true);
                                optionvalue.setActief(true);
                            }

                            // On click optie
                            option_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        optionvalue.setActief(true);
                                    } else {
                                        optionvalue.setActief(false);
                                    }
                                    tv_prijs.setText(String.valueOf(orderitem.PrijsString()));
                                }
                            });
                            optie_container.addView(option_button);
                        }
                        break;
                }
            }
        }
    }
}
