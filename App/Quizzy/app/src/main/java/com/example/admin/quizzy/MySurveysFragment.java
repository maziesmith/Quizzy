package com.example.admin.quizzy;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MySurveysFragment  extends SurveysFragment {
    private static final String TAG = "Quizzy_FragmentsDebug";
    public MySurveysFragment() {}

    @OnClick(R.id.addSurveyButton)
    public void run() {
        addSurvey(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // context
        final FragmentActivity activity = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_surveys, parent, false);

        // bind the view
        ButterKnife.bind(this, rootView);

        // Create a list of surveys
        final ArrayList<MenuItem> menuItems = new ArrayList<>();

        // get userid
        int userid = getUserId();

        // Create the adapter
        final MenuItemAdapter adapter = new MenuItemAdapter(activity, menuItems);

        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

        // fill the menuItems list with stuff
        populateFromUrl("mine/" + userid, menuItems);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set adapter for listview
                progress.dismiss();
                _listView.setAdapter(adapter);

            }
        }, 2000);

        return rootView;
    }
}
