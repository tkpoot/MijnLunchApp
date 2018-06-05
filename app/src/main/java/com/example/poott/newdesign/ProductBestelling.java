package com.example.poott.newdesign;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by poott on 12/11/2017.
 */

public class ProductBestelling {

    public String product = "404";
    public Double prijs = 3.0;
    public HashMap<String, HashMap<String, Double>> opties;

    public ProductBestelling() {
    }

    public ProductBestelling(String product, HashMap<String, HashMap<String, Double>> opties) {
        this.product = product;
        this.opties = opties;
    }

    public void addOptie(String optie, String waarde, Double prijs){
        HashMap<String, Double> optiewaarden;
        if(opties.get(optie) == null){optiewaarden = new HashMap<>();}
        else{optiewaarden = opties.get(optie);}
        optiewaarden.put(waarde, prijs);
        opties.put(optie,optiewaarden);
    }

    public void removeOptie(String optie, String waarde){
        HashMap<String, Double> optiewaarden;
        optiewaarden = opties.get(optie);
        optiewaarden.remove(waarde);
        if (optiewaarden.isEmpty()) {opties.remove(optie);}
        else {opties.put(optie, optiewaarden);}
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getPrijs() {
        return prijs;
    }

    public void setPrijs(Double prijs) {
        this.prijs = prijs;
    }

    public HashMap<String, HashMap<String, Double>> getOpties() {
        return opties;
    }

    public void setOpties(HashMap<String, HashMap<String, Double>> opties) {
        this.opties = opties;
    }

    public void berekenPrijs(Double productprijs){
        prijs = productprijs;
        for (HashMap<String, Double> optie : opties.values()) {
            for (Double waarde : optie.values()) {
                prijs += waarde;
            }
        }
    }

    public String getPrijsString(Double productprijs){
        this.berekenPrijs(productprijs);
        NumberFormat nf = new DecimalFormat("0.00");
        String prijsString = nf.format(prijs);
        prijsString = "€ " + prijsString;
        return prijsString;
    }
}
