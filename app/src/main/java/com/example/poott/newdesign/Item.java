package com.example.poott.newdesign;

import java.util.ArrayList;

public class Item {

    public String naam;
    public String omschrijving;
    public String categorie;
    public Double prijs;
    public ArrayList<String> opties;

    //CONSTRUCTORS
    public Item() {
    }

    public Item(String categorie, String omschrijving, Double prijs) {
        this.omschrijving = omschrijving;
        this.categorie = categorie;
        this.prijs = prijs;
    }

    public Item(String categorie, String omschrijving, Double prijs, ArrayList<String> opties) {
        this.omschrijving = omschrijving;
        this.categorie = categorie;
        this.prijs = prijs;
        this.opties = opties;
    }

    public Item(String naam, String categorie, String omschrijving, Double prijs, ArrayList<String> opties) {
        this.naam = naam;
        this.omschrijving = omschrijving;
        this.categorie = categorie;
        this.prijs = prijs;
        this.opties = opties;
    }

    //GETTERS AND SETTERS
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
    public String getCategorie() {
        return categorie;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public Double getPrijs() {
        return prijs;
    }
    public void setPrijs(Double prijs) {
        this.prijs = prijs;
    }
    public ArrayList<String> getOpties() {return opties;}
    public void setOpties(ArrayList<String> opties) {this.opties = opties;}
}

