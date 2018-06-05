package com.example.poott.newdesign;

/**
 * Created by poott on 24/01/2018.
 */

public class OptionValue {

    public String naam;
    public boolean actief;
    public double prijs;

    public OptionValue(){
        //General constructor
    }

    public OptionValue(String naam, boolean actief, double prijs){
        this.naam = naam;
        this.actief = actief;
        this.prijs = prijs;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public boolean isActief() {
        return actief;
    }

    public void setActief(boolean actief) {
        this.actief = actief;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }
}


