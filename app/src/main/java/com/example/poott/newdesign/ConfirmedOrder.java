package com.example.poott.newdesign;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class ConfirmedOrder {

    public HashMap<String, OrderItem> orderitems = new HashMap<String, OrderItem>();
    public double prijs = 0.0;
    public String username;

    public ConfirmedOrder(){

    }

    public ConfirmedOrder(Order order){
        this.prijs = order.prijs;
        this.username = order.username;
    }

    public String PrijsString(){
        prijs = 0.0;
        for(Map.Entry<String, OrderItem> orderitem : orderitems.entrySet()){
            prijs += orderitem.getValue().getPrijs();
        }
        NumberFormat nf = new DecimalFormat("0.00");
        String prijsString = nf.format(prijs);
        prijsString = "€ " + prijsString;
        return prijsString;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}