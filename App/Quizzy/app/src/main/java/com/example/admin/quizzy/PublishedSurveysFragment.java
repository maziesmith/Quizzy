package com.example.admin.quizzy;

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

public class PublishedSurveysFragment extends SurveysFragment {

    // bind views
    @BindView(R.id.addSurveyButton)
    FloatingActionButton _addSurveyButton;
    @BindView(R.id.menuList)
    ListView _listView;

    @OnClick(R.id.addSurveyButton)
    public void run() {
        addSurvey();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_surveys, parent, false);

        // Create a list of surveys
        final ArrayList<MenuItem> menuItems = new ArrayList<>();

        // fill the menuItems list with stuff
        populateFromUrl("published", menuItems);

        // Create the adapter
        MenuItemAdapter adapter = new MenuItemAdapter(getActivity(), menuItems);

        // bind the view
        ButterKnife.bind(this, parent);

        // set adapter for listview
        _listView.setAdapter(adapter);

        return rootView;
    }
}
