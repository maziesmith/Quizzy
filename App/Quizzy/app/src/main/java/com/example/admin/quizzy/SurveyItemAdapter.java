package com.example.admin.quizzy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 3/21/2018.
 */

public class SurveyItemAdapter extends ArrayAdapter<SurveyItem> {

    ArrayList<SurveyItem> surveyItems;

    public SurveyItemAdapter(Context context, ArrayList<SurveyItem> items) {
        super(context, 0, items);
        surveyItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View listItemView = convertView;
        final SurveyEditViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.survey_list_item, parent, false);
            holder = new SurveyEditViewHolder();
            holder.questionEdit = (EditText)convertView.findViewById(R.id.question);
            holder.responsesView = (TextView)convertView.findViewById(R.id.responses_section);
            convertView.setTag(holder);
        }
        else {
            holder = (SurveyEditViewHolder)convertView.getTag();
        }

        SurveyItem currentItem = getItem(position);
        holder.questionEdit.setText(currentItem.getQuestion());
        holder.responsesView.setText("Response");

        //EditText questionEdit = listItemView.findViewById(R.id.question);
        //questionEdit.setText(currentItem.getQuestion());

        holder.questionEdit.setId(position);  // So position can be retrieved in callback below
        holder.questionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    final int position = v.getId();
                    final EditText e = (EditText)v;
                    surveyItems.get(position).setQuestion(e.getText().toString());
                }
            }
        });

        return convertView;
    }

/*    @Override
    public int getViewTypeCount() {
        return SurveyItem.MAX_RESPONSES;
    }

    @Override
    public int getItemViewType(int position) {

    }*/

    static class SurveyEditViewHolder {
        EditText questionEdit;
        TextView responsesView;
    }
}


