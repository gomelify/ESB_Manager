package com.ronschka.david.esb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class CardViewClass extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> array = new ArrayList<>();
        array.add("Allahu Akabar!");
        array.add("Tomaten sind in der Regel rot!");
        array.add("Ich mag Gulasch");
        array.add("Pickups sind kein Obst");
        array.add("Hitler war fies!");

        recyclerView.setAdapter(new RecyclerAdapter(array){
        });
    }
}
