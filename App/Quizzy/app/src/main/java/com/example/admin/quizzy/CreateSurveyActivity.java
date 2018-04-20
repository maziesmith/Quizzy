package com.example.admin.quizzy;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class CreateSurveyActivity extends AppCompatActivity
        implements AddSurveyItemDialog.AddSurveyItemDialogListener, AddMultipleChoiceDialog.AddMultipleChoiceDialogListener {

    private SurveyItemAdapter _adapter;
    private RetainedFragment _dataFragment;
    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);

        FragmentManager fm = getSupportFragmentManager();
        _dataFragment = (RetainedFragment)fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);
        if(_dataFragment == null) {
            _dataFragment = new RetainedFragment();
            fm.beginTransaction().add(_dataFragment, TAG_RETAINED_FRAGMENT).commit();
            _dataFragment.setData(new ArrayList<SurveyItem>());
        }

        _adapter = new SurveyItemAdapter(this, _dataFragment.getData());
        ListView surveyList = findViewById(R.id.surveyEditList);
        surveyList.setAdapter(_adapter);
        surveyList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        FloatingActionButton addButton = findViewById(R.id.surveyAddItemButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSurveyItemDialog dialog = new AddSurveyItemDialog();
                dialog.show(getSupportFragmentManager(), "AddItem");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        _dataFragment.setData(_adapter.getData());

        if(isFinishing()) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(_dataFragment).commit();
        }
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

    @Override
    public void onOpenResponseClick(DialogFragment dialog) {
        SurveyItem newItem = new SurveyItem(1);
        _adapter.add(newItem);
    }

    @Override
    public void onTrueFalseClick(DialogFragment dialog) {
        SurveyItem newItem = new SurveyItem(2);
        newItem.setResponse(0, "True");
        newItem.setResponse(1, "False");
        _adapter.add(newItem);
    }

    @Override
    public void onMultipleChoiceClick(DialogFragment dialog) {
        AddMultipleChoiceDialog newDialog = new AddMultipleChoiceDialog();
        newDialog.show(getSupportFragmentManager(), "ChooseResponseCount");
    }

    @Override
    public void onConfirm(DialogFragment dialog, int chosenValue) {
        SurveyItem newItem = new SurveyItem(chosenValue);
        _adapter.add(newItem);
    }


    public static class RetainedFragment extends Fragment {
        private ArrayList<SurveyItem> data;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setData(ArrayList<SurveyItem> data) {
            this.data = data;
        }

        public ArrayList<SurveyItem> getData() {
            return data;
        }
    }
}
