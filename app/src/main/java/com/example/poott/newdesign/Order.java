package com.example.poott.newdesign;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Order {

    public ArrayList<OrderItem> orderitems = new ArrayList<OrderItem>();
    public double prijs = 0.0;
    public String username;

    public Order(){

    }

    public String PrijsString(){
        prijs = 0.0;
        for(OrderItem orderitem : orderitems){
            prijs += orderitem.getPrijs();
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