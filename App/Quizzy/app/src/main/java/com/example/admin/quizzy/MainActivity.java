package com.example.admin.quizzy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.BindView;

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
        final ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        // fill the menuItems list with stuff
        //populate(menuItems);
        menuItems.add(new MenuItem("New Survey", 100));
        menuItems.add(new MenuItem("Survey", 101));
        menuItems.add(new MenuItem("vey", 102));
        menuItems.add(new MenuItem("ey", 103));
        menuItems.add(new MenuItem("vey", 104));
        menuItems.add(new MenuItem("Survey", 105));

        // Create the adapter
        MenuItemAdapter adapter = new MenuItemAdapter(MainActivity.this, menuItems);

        // bind the view
        ListView _listView = (ListView) rootView.findViewById(R.id.menuList);

        // set adapter for listview
        _listView.setAdapter(adapter);

        /*_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // get the survey name
                // set new intent for the survey
            }
        });*/

        return rootView;
    }

    public MainActivity() {
        // Required empty public constructor
    }

    // TODO: populate the listview
    public void populate(ArrayList<MenuItem> menuItems){

    }



}

