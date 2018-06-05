package com.example.poott.newdesign;

import java.util.ArrayList;

/**
 * Created by poott on 24/01/2018.
 */

public class Option {

    public String naam;
    public String omschrijving;
    public String type;
    public ArrayList<OptionValue> waarden;


    public Option(){
        //General constructor
    }

    public Option(String naam, String omschrijving, String type, ArrayList<OptionValue> waarden){
        this.naam = naam;
        this.omschrijving = omschrijving;
        this.type = type;
        this.waarden = waarden;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<OptionValue> getWaarden() {
        return waarden;
    }

    public void setWaarden(ArrayList<OptionValue> waarden) {
        this.waarden = waarden;
    }
}
