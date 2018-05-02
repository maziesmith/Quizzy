package com.example.admin.quizzy;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

public class MySurveysFragment  extends SurveysFragment {
    private static final String TAG = "Quizzy_FragmentsDebug";
    public MySurveysFragment() {}

    @BindView(R.id.addSurveyButton)
    FloatingActionButton _addSurveyButton;

    @BindView(R.id.loadingProgress)
    ProgressBar loadingProgress;

    // Create a list of surveys
    final ArrayList<MenuItem> menuItems = new ArrayList<>();

    // handler for load main menu
    protected static Handler loadHandler;


    @OnClick(R.id.addSurveyButton)
    public void run() {
        addSurvey(false, menuItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mLoader = (MenuLoader) getActivity();
        // context
        final FragmentActivity activity = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_surveys, parent, false);

        // bind the view
        ButterKnife.bind(this, rootView);

        // get userid
        int userid = getUserId();

        loadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // set adapter for listview
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(MySurveysFragment.this).attach(MySurveysFragment.this).commit();
                loadingProgress.setVisibility(View.GONE);
            }
        };

        // Create the adapter
        final MenuItemAdapter adapter = new MenuItemAdapter(activity, menuItems, false, loadHandler);

        // fill the menuItems list with stuff
        populateFromUrl("mine/" + userid, menuItems);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingProgress.setVisibility(View.GONE);
                // set adapter for listview
                _listView.setAdapter(adapter);

            }
        }, 3000);

        return rootView;
    }
}
