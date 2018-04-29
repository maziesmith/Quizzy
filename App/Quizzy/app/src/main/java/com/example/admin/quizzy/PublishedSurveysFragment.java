package com.example.admin.quizzy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishedSurveysFragment extends SurveysFragment {
    private static final String TAG = "Quizzy_FragmentsDebug";

    // bind views
    @BindView(R.id.menuList)
    ListView _listView;

    // Create a list of surveys
    final ArrayList<MenuItem> menuItems = new ArrayList<>();

    @BindView(R.id.loadingProgress)
    ProgressBar loadingProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // context
        final FragmentActivity activity = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_published, parent, false);

        // bind the view
        ButterKnife.bind(this, rootView);

        // Create a list of surveys
        final ArrayList<MenuItem> menuItems = new ArrayList<>();

        // Create the adapter
        final MenuItemAdapter adapter = new MenuItemAdapter(activity, menuItems, false);

        // fill the menuItems list with stuff
        populateFromUrl("published", menuItems);

        loadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                loadingProgress.setVisibility(View.GONE);
                // set adapter for listview
                _listView.setAdapter(adapter);

            }
        };

        return rootView;
    }
}
