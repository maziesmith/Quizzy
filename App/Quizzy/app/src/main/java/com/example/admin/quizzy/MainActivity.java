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

    // binding the listview, must do outside of onCreate
    @BindView(R.id.menuList) ListView _listView;

    // binding edit button
    @BindView(R.id.menuEditButton) ImageView editButton;

    // binding delete button
    @BindView(R.id.menuDeleteButton) ImageView deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        // Create a list of surveys
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        // Create the adapter
        MenuItemAdapter adapter = new MenuItemAdapter(MainActivity.this, menuItems);

        // set adapter for listview
        _listView.setAdapter(adapter);

        populateMenu(_listView, menuItems);

        // TODO: click on item opens the survey
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // get the survey
                // set new intent for the survey
            }
        });

        return rootView;
    }



    public MainActivity() {
        // Required empty public constructor
    }

    // TODO: populate the listview
    public void populateMenu(ListView listView, ArrayList<MenuItem> menuItems){
        
    }



}

