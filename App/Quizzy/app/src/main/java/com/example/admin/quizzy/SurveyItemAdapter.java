package com.example.admin.quizzy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
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
    public int getViewTypeCount() {
        return SurveyItem.MAX_RESPONSES;
    }

    @Override
    public int getItemViewType(int position) {
        return surveyItems.get(position).getNumResponses();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View listItemView = convertView;
        final SurveyEditViewHolder holder;

        int type = getItemViewType(position);

        /* *
         * Create item view if null
         * */
        if (convertView == null) {
            holder = new SurveyEditViewHolder();
            switch (type) {
                case 0:  // same as case 1 so fall through
                case 1:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.survey_list_item, parent, false);
                    holder.questionEdit = (EditText)convertView.findViewById(R.id.question);
                    holder.questionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus) {
                                final int position = v.getId();
                                final EditText e = (EditText)v;
                                surveyItems.get(position).setQuestion(e.getText().toString());
                            }
                        }
                    });
                    holder.responsesView = (TextView)convertView.findViewById(R.id.responses_section);
                    break;
                case 2:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.survey_list_item_boolean, parent, false);
                    holder.questionEdit = (EditText)convertView.findViewById(R.id.question);
                    holder.questionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus) {
                                final int position = v.getId();
                                final EditText e = (EditText)v;
                                surveyItems.get(position).setQuestion(e.getText().toString());
                            }
                        }
                    });
                    for(int i = 0; i < type; i++) {
                        String viewName = "response" + (i + 1);
                        int id = getContext().getResources().getIdentifier(viewName, "id", getContext().getPackageName());
                        Log.i("ID", "getView: id=" + id);
                        holder.responseList.add((EditText)convertView.findViewById(id));
                        holder.responseList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus) {
                                    int[] values = (int[])v.getTag();  // tag must be an array of two ints: the list position and the response position
                                    final int position = values[0];
                                    final EditText e = (EditText)v;
                                    surveyItems.get(position).setResponse(values[1], e.getText().toString());
                                }
                            }
                        });
                    }
                    break;
                default:
                    break;
            }

            convertView.setTag(holder);
        }
        else {
            holder = (SurveyEditViewHolder)convertView.getTag();
        }

        /* *
         * Update item view from model
         * */
        SurveyItem currentItem = getItem(position);
        holder.questionEdit.setText(currentItem.getQuestion());
        holder.questionEdit.setId(position);  // So position can be retrieved in OnFocusChange callback

        switch (type) {
            case 0:  // same as case 1 so fall through
            case 1:
                break;
            case 2:
                for(int i = 0; i < type; i++) {
                    // Assign response string from model to view
                    holder.responseList.get(i).setText( currentItem.getResponses().get(i) );

                    // Set tag info to be retrieved in OnFocusChange callback:
                    // First value is the currentItem position in list
                    // Second value is the index of the response string within currentItem
                    holder.responseList.get(i).setTag(new int[]{position, i});
                }
                break;
            default:
                break;
        }

        return convertView;
    }


    static class SurveyEditViewHolder {
        EditText questionEdit;
        TextView responsesView;
        ArrayList<EditText> responseList;

        public SurveyEditViewHolder() {
            responseList = new ArrayList<EditText>(SurveyItem.MAX_RESPONSES);
        }
    }
}


