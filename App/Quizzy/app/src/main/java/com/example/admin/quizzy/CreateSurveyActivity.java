package com.example.admin.quizzy;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Admin on 3/19/2018.
 */

public class CreateSurveyActivity extends AppCompatActivity {

    SurveyItemAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);

        _adapter = new SurveyItemAdapter(this, new ArrayList<SurveyItem>());
        ListView surveyList = findViewById(R.id.surveyEditList);
        surveyList.setAdapter(_adapter);

        FloatingActionButton addButton = findViewById(R.id.surveyAddItemButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<SurveyItem> a = new ArrayList<SurveyItem>();
                a.add(new SurveyItem("How was your day?", new String[]{"Hello"}));
                a.add(new SurveyItem("What time is it?", new String[]{"Hello"}));
                a.add(new SurveyItem("What are you up to?", new String[]{"Hello"}));

                _adapter.addAll(a);
            }
        });
    }

    /* Allows the EditTexts to lose focus when touching outside them.
     * Credit to zMan's answer on
     * https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
