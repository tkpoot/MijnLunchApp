package com.example.poott.newdesign;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;


public class BestelOverzichtFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_besteloverzicht, container, false);

        final OrderActivity orderactivity = (OrderActivity) getActivity();

        ListView lv_besteloverzicht = (ListView) view.findViewById(R.id.lv_besteloverzicht);
        lv_besteloverzicht.setAdapter(orderactivity.besteloverzichtadapter);

        //Set Button 'Close'
        ImageButton button_close = (ImageButton) view.findViewById(R.id.ib_close);
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DrawerLayout drawer = (DrawerLayout) orderactivity.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.RIGHT);
            }
        });

        Button button = (Button) view.findViewById(R.id.b_afrekenen);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(getActivity().getBaseContext(), PayActivity.class);
                Gson gson = new Gson();
                String order = gson.toJson(orderactivity.getOrder());
                myIntent.putExtra("Order", order);
                getActivity().startActivity(myIntent);
                getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                //drawer.closeDrawer(Gravity.RIGHT);
/*
                AfrekenFragment fragment = new AfrekenFragment();
                fragmentTransaction
                        .setCustomAnimations(R.anim.trans_left_in, R.anim.trans_right_out)
                        .replace(R.id.cl_all, fragment)
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        final TextView tv_prijs = (TextView) view.findViewById(R.id.tv_prijs);
        tv_prijs.setText(String.valueOf(orderactivity.getOrder().PrijsString()));

        // Inflate the layout for this fragment
        return view;
    }
}
