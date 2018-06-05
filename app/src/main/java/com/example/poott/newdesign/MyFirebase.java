package com.example.poott.newdesign;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.HashMap;

/**
 * Created by poott on 16/09/2017.
 */

public class MyFirebase {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference();

    public void MyFirebase(){
    }

    public void DeleteMenu(){
        db.child("menu").child("producten").removeValue();
    }

    public void NewProduct (String categorie, String naam, Item item){
        db.child("menu").child("producten").child(categorie).child(naam).setValue(item);
        db.child("menu").child("categorieen").child(categorie).setValue(true);
    }

    public void NewOptie (String naam, String type, String omschrijving, String waarde, Boolean actief, Double prijs){
        db.child("menu").child("opties").child(naam).child("naam").setValue(naam);
        db.child("menu").child("opties").child(naam).child("omschrijving").setValue(omschrijving);
        db.child("menu").child("opties").child(naam).child("type").setValue(type);
        db.child("menu").child("opties").child(naam).child("waarden").child(waarde).child("naam").setValue(waarde);
        db.child("menu").child("opties").child(naam).child("waarden").child(waarde).child("actief").setValue(actief);
        db.child("menu").child("opties").child(naam).child("waarden").child(waarde).child("prijs").setValue(prijs);
    }

    public void NewOrder(Order order){
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.of( "Europe/Brussels" );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        String date = MyApp.GetDate();
        ConfirmedOrder confirmedOrder = new ConfirmedOrder(order);
        for (OrderItem orderitem: order.orderitems){
            orderitem.username = order.username;
            String key = db.child("orderitems").child(date).push().getKey();
            db.child("orderitems").child(date).child(key).setValue(orderitem);
            confirmedOrder.orderitems.put(key,orderitem);
        }
        db.child("orders").child(date).push().setValue(confirmedOrder);
    }
}
