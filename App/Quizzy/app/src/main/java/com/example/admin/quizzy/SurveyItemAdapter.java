package com.example.admin.quizzy;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Admin on 3/21/2018.
 */

public class SurveyItemAdapter extends ArrayAdapter<SurveyItem> {

    public SurveyItemAdapter(Context context, ArrayList<SurveyItem> items) {
        super(context, 0, items);
    }


}
