package com.example.poott.newdesign;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BestellijstAdapter2 extends RecyclerView.Adapter<BestellijstAdapter2.ViewHolder> {
    Context context;
    ArrayList<ConfirmedOrder> orderlist;
    private int lastPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_naam;
        TextView tv_prijs;
        public ViewHolder(ConstraintLayout v) {
            super(v);
            tv_naam = (TextView) v.findViewById(R.id.tv_naam);
            tv_prijs = (TextView) v.findViewById(R.id.tv_prijs);
        }
    }

    public BestellijstAdapter2(ArrayList<ConfirmedOrder> orderlist, Context context){
        super();
        this.orderlist = orderlist;
        this.context = context;
    }

    @Override
    public BestellijstAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.adapter_bestellijst, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConfirmedOrder order = orderlist.get(position);

        holder.tv_naam.setText(order.getUsername());
        holder.tv_prijs.setText(order.PrijsString());
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return orderlist.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
