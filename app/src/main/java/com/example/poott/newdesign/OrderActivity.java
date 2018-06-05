package com.example.poott.newdesign;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    //Lists for listviews
    ArrayList<String> listCategory;
    LinkedHashMap<String, List<Item>> listItem;
    HashMap<String, Option> listOption;

    //Map for scroll positions
    LinkedHashMap<Integer, Integer> itemcount;

    //Current Order
    Order order = new Order();

    //Adapter of menu
    BestelOverzichtAdapter besteloverzichtadapter = new BestelOverzichtAdapter(this,order);

    //Fragmentmanager
    public final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    public void onResume() {
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BestelOverzichtFragment fragment = new BestelOverzichtFragment();
        fragmentTransaction.replace(R.id.drawer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Sticky immersive
        MyApp.Fullscreen(getWindow());

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Set Button 'Back'
        ImageButton button_back = (ImageButton) findViewById(R.id.ib_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Get options
        GetOptions();

        //Fill data in listviews
        final ListView lv_category = (ListView) findViewById(R.id.lv_category);
        final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.lv_menu);
        expListView.setGroupIndicator(null);
        FillData(lv_category,expListView);

        AdapterView.OnItemClickListener categorieMessageClickedHandler = new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            public void onItemClick(AdapterView parent, View v,final int position, long id) {
                //expListView.smoothScrollToPositionFromTop(itemcount.get(position),0);
                expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                        if (scrollState == SCROLL_STATE_IDLE) {
                            view.setOnScrollListener(null);

                            // Fix for scrolling bug
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    view.setSelection(itemcount.get(position));
                                }
                            });
                        }
                    }

                    @Override
                    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                                         final int totalItemCount) { }
                });
                // Perform scrolling to position
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        expListView.smoothScrollToPositionFromTop(itemcount.get(position), 0);
                    }
                });
            }
        };
        lv_category.setOnItemClickListener(categorieMessageClickedHandler);

        //On click item
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Item item = listItem.get(listCategory.get(groupPosition)).get(childPosition);

                ItemDetailsFragment fragment = new ItemDetailsFragment();
                Bundle args = new Bundle();
                args.putString("item", new Gson().toJson(item));
                fragment.setArguments(args);

                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null)
                        .replace(R.id.drawer, fragment)
                        .commit();

                drawer.openDrawer(Gravity.RIGHT);

                return false;
            }
        });

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    public void Button(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BestelOverzichtFragment fragment = new BestelOverzichtFragment();
        fragmentTransaction.replace(R.id.drawer, fragment);
        fragmentTransaction.commit();
        drawer.openDrawer(Gravity.RIGHT);
    }

    public void Animate(){
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_winkelmand);
        fab.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectanimator1, objectanimator2;
        objectanimator1 = ObjectAnimator.ofFloat(fab, "scaleX", 2f);
        objectanimator2 = ObjectAnimator.ofFloat(fab, "scaleY", 2f);
        objectanimator1.setStartDelay(100);
        objectanimator2.setStartDelay(100);
        objectanimator1.setDuration(300);
        objectanimator2.setDuration(300);

        objectanimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator objectanimator3, objectanimator4;
                objectanimator3 = ObjectAnimator.ofFloat(fab, "scaleX", 1f);
                objectanimator4 = ObjectAnimator.ofFloat(fab, "scaleY", 1f);
                objectanimator3.setStartDelay(300);
                objectanimator4.setStartDelay(300);
                objectanimator3.setDuration(500);
                objectanimator4.setDuration(500);
                objectanimator3.start();
                objectanimator4.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectanimator1.start();
        objectanimator2.start();
    }

    public void Afrekenen() {
        Intent myIntent = new Intent(getBaseContext(), PayActivity.class);
        Gson gson = new Gson();
        String order = gson.toJson(getOrder());
        myIntent.putExtra("Order", order);
        startActivity(myIntent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }


    public void FillData(final ListView lv, final ExpandableListView elv) {
        listCategory = new ArrayList<String>();
        listItem = new LinkedHashMap<String, List<Item>>();
        itemcount = new LinkedHashMap<Integer, Integer>();
        myRef.child("menu").child("producten").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Get data
                for (DataSnapshot categorie : dataSnapshot.getChildren()) {
                    String tempcategorie = categorie.getKey();
                    List<Item> tempitem = new ArrayList<Item>();
                    for (DataSnapshot product : categorie.getChildren()) {
                        Item item = product.getValue(Item.class);
                        tempitem.add(item);
                    }
                    listCategory.add(tempcategorie);
                    listItem.put(tempcategorie,tempitem);
                }

                //Set categorylist adapter
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getBaseContext(),
                        R.layout.listitem_category, R.id.tv_categoryitem, listCategory);
                lv.setAdapter(categoryAdapter);

                //Set menulist adapter
                ExpandableListAdapter listAdapter = new ExpandableListAdapter(getBaseContext(),
                        listCategory, listItem);
                elv.setAdapter(listAdapter);

                //Expand all
                for (int i = 0; i < listCategory.size(); i++) {
                    elv.expandGroup(i);
                }

                //Set position of categories in list
                int cat = 0;
                int number = 0;
                for (Map.Entry<String, List<Item>> entry : listItem.entrySet()) {
                    itemcount.put(cat, number);
                    number += entry.getValue().size() + 1;
                    cat++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void GetOptions() {
        listOption = new HashMap<>();
        myRef.child("menu").child("opties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot option : dataSnapshot.getChildren()) {
                    String naam = option.child("naam").getValue().toString();
                    String omschrijving = option.child("omschrijving").getValue().toString();
                    String type = option.child("type").getValue().toString();
                    ArrayList<OptionValue> waarden = new ArrayList<OptionValue>();
                    for (DataSnapshot optionvalue : option.child("waarden").getChildren()) {
                        String valuenaam = optionvalue.child("naam").getValue().toString();
                        boolean actief = Boolean.parseBoolean(optionvalue.child("actief").getValue().toString());
                        double prijs = Double.parseDouble(optionvalue.child("prijs").getValue().toString());
                        waarden.add(new OptionValue(valuenaam, actief, prijs));
                    }
                    listOption.put(naam,new Option(naam, omschrijving, type, waarden ));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addOrderItem(OrderItem orderitem){
        order.orderitems.add(orderitem);
        besteloverzichtadapter.notifyDataSetChanged();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

