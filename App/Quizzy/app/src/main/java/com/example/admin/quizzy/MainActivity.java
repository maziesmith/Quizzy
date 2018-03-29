package com.example.admin.quizzy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by probu on 3/20/2018.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        // Create a list of surveys
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        // Create the adapter
        MenuItemAdapter adapter = new MenuItemAdapter(MainActivity.this, menuItems);

        // find the listview
        ListView listView = (ListView) rootView.findViewById(R.id.menuList);

        // set adapter for listview
        listView.setAdapter(adapter);

        // onclick for the list will bring up edit and delete buttons
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // find the item

                // show the images

                // set the onclick event for edit button

                // set the onclick event for delete button

            }
        });

        return rootView;
    }



    public MainActivity() {
        // Required empty public constructor
    }



}

