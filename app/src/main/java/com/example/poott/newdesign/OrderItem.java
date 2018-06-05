package com.example.poott.newdesign;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by poott on 24/01/2018.
 */

public class OrderItem {

    public Item item;
    public ArrayList<Option> options = new ArrayList<Option>();
    public String username ="anoniem";

    public OrderItem(){
        //General constructor
    }

    public OrderItem(Item item){
        this.item = item;
        this.options = new ArrayList<Option>();
    }

    public double getPrijs(){
        double prijs = item.getPrijs();
        if(options.size()>0){
            for (Option option: options) {
                for (OptionValue optionvalue: option.getWaarden()) {
                    if(optionvalue.isActief()) prijs+= optionvalue.getPrijs();
                }
            }

        }
        return prijs;
    }

    public String PrijsString(){
        double prijs = this.getPrijs();
        NumberFormat nf = new DecimalFormat("0.00");
        String prijsString = nf.format(prijs);
        prijsString = "€ " + prijsString;
        return prijsString;
    }

    public ArrayList<String> ActiveOptions(){
        ArrayList<String> activeoptions = new ArrayList<String>();
        for(Option option: options){
            for(OptionValue optionValue: option.getWaarden()){
                if(optionValue.isActief()){
                    activeoptions.add(optionValue.getNaam());
                }
            }
        }
        return activeoptions;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }
}
