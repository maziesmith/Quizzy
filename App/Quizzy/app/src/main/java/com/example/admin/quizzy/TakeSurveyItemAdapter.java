package com.example.admin.quizzy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TakeSurveyItemAdapter extends ArrayAdapter<SurveyItem> {
    ArrayList<SurveyItem> surveyItems;

    public TakeSurveyItemAdapter(Context context, ArrayList<SurveyItem> items) {
        super(context, 0, items);
        surveyItems = items;
    }

    public ArrayList<SurveyItem> getData() {
        return surveyItems;
    }

    @Override
    public int getViewTypeCount() {
        return SurveyItem.MAX_RESPONSES;
    }

    @Override
    public int getItemViewType(int position) {
        // Subtract 1 from numResponses to keep the type zero-based
        int type = surveyItems.get(position).getNumResponses() - 1;
        // Make sure type is non-negative, in case numResponses was 0 for some reason
        if(type < 0) {
            type = 0;
        }
        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View listItemView = convertView;
        final TakeSurveyViewHolder holder;

        int type = getItemViewType(position) + 1; // Add 1 to make type equal to number of responses

        /* *
         * Create item view if null
         * */
        if (convertView == null) {
            holder = new TakeSurveyViewHolder();
            switch (type) {
                case 0:  // same as case 1 so fall through
                case 1:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.take_survey_list_item, parent, false);
                    holder.questionView = (TextView)convertView.findViewById(R.id.question);
                    holder.openResponseEdit = (EditText)convertView.findViewById(R.id.responses_section);
                    holder.openResponseEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus) {
                                final int position = v.getId();
                                final EditText e = (EditText)v;
                                surveyItems.get(position).setResponse(0, e.getText().toString());
                            }
                        }
                    });
                    break;
                case 2:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.take_survey_list_item_boolean, parent, false);
                    holder.questionView = (TextView)convertView.findViewById(R.id.question);
                    holder.responseGroup = (RadioGroup)convertView.findViewById(R.id.responseGroup);

                    for(int i = 0; i < type; i++) {
                        String viewIdName = "response" + (i + 1);
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
                        //Log.i("ID", "getView: id=" + id);
                        holder.responseList.add((RadioButton) convertView.findViewById(id));
 /*                       holder.responseList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus) {
                                    int[] values = (int[])v.getTag();  // tag must be an array of two ints: the list position and the response position
                                    final int position = values[0];
                                    final EditText e = (EditText)v;
                                    surveyItems.get(position).setResponse(values[1], e.getText().toString());
                                }
                            }
                        });*/
                    }
                    break;
                default:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.take_survey_list_item_boolean, parent, false);
                    holder.questionView = (TextView)convertView.findViewById(R.id.question);
                    holder.responseGroup = (RadioGroup)convertView.findViewById(R.id.responseGroup);

                    for(int i = 3; i <= type; i++) {
                        RadioButton newResponse = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.response_radiobutton, holder.responseGroup, false);
                        String viewIdName = "response" + i;
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
                        newResponse.setId(id);
                        holder.responseGroup.addView(newResponse);
                    }

                    for(int i = 0; i < type; i++) {
                        String viewIdName = "response" + (i + 1);
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
                        //Log.i("ID", "getView: id=" + id);
                        holder.responseList.add((RadioButton) convertView.findViewById(id));
                        /*holder.responseList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus) {
                                    int[] values = (int[])v.getTag();  // tag must be an array of two ints: the list position and the response position
                                    final int position = values[0];
                                    final EditText e = (EditText)v;
                                    surveyItems.get(position).setResponse(values[1], e.getText().toString());
                                }
                            }
                        });*/
                    }
                    break;
            }
            convertView.setTag(holder);

        } else {
            holder = (TakeSurveyViewHolder)convertView.getTag();
        }

        /* *
         * Update item view from model
         * */
        final SurveyItem currentItem = getItem(position);

        holder.questionView.setText(currentItem.getQuestion());

        switch (type) {
            case 0:  // same as case 1 so fall through
            case 1:
                holder.openResponseEdit.setText(currentItem.getResponses().get(0));
                holder.openResponseEdit.setId(position);  // So position can be retrieved in OnFocusChange callback
                break;
            case 2:
                for(int i = 0; i < type; i++) {
                    // Assign response string from model to view
                    holder.responseList.get(i).setText( currentItem.getResponses().get(i) );

                    // Set tag info to be retrieved in OnFocusChange callback:
                    // First value is the currentItem position in list
                    // Second value is the index of the response string within currentItem
                    //holder.responseList.get(i).setTag(new int[]{position, i});
                }
                break;
            default:
                for (int i = 0; i < type; i++) {
                    holder.responseList.get(i).setText( currentItem.getResponses().get(i) );
                    //holder.responseList.get(i).setTag(new int[]{position, i});
                }
                break;
        }

        return convertView;
    }


    static class TakeSurveyViewHolder {
        TextView questionView;
        EditText openResponseEdit;
        RadioGroup responseGroup;
        ArrayList<RadioButton> responseList;

        public TakeSurveyViewHolder() {
            responseList = new ArrayList<RadioButton>(SurveyItem.MAX_RESPONSES);
        }
    }
}
