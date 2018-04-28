package com.example.admin.quizzy;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MySurveysFragment  extends SurveysFragment {
    public MySurveysFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_surveys, parent, false);

        // Create a list of surveys
        final ArrayList<MenuItem> menuItems = new ArrayList<>();

        // get userid
        int userid = getUserId();

        // fill the menuItems list with stuff
        populateFromUrl("mine/" + userid, menuItems);

        // Create the adapter
        MenuItemAdapter adapter = new MenuItemAdapter(getActivity(), menuItems);

        // bind the view
        ButterKnife.bind(this, rootView);

        // set adapter for listview
        _listView.setAdapter(adapter);

        return rootView;
    }

    int getUserId(){
        SharedPreferences pref = getActivity().getSharedPreferences("quizzy.pref", 0);
        return pref.getInt("userid", 0);

    }

}
