package com.example.admin.quizzy;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        final SurveyEditViewHolder holder;

        int type = getItemViewType(position) + 1; // Add 1 to make type equal to number of responses

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
                    holder.deleteBtn = (ImageButton)convertView.findViewById(R.id.delete_btn);
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
                    holder.deleteBtn = (ImageButton)convertView.findViewById(R.id.delete_btn);
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
                        String viewIdName = "response" + (i + 1);
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
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
                default:  // number of responses greater than 2
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.survey_list_item_boolean, parent, false);

                    // Add remaining response views beyond the first 2 already in layout
                    LinearLayout parentLayout = (LinearLayout)convertView.findViewById(R.id.responses_section);
                    for(int i = 3; i <= type; i++) {
                        EditText newResponse = (EditText)LayoutInflater.from(getContext()).inflate(R.layout.response_edittext, parentLayout, false);
                        String viewIdName = "response" + i;
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
                        newResponse.setId(id);
                        parentLayout.addView(newResponse);
                    }

                    holder.deleteBtn = (ImageButton)convertView.findViewById(R.id.delete_btn);
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
                        String viewIdName = "response" + (i + 1);
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
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
            }

            convertView.setTag(holder);
        }
        else {
            holder = (SurveyEditViewHolder)convertView.getTag();
        }

        /* *
         * Update item view from model
         * */
        final SurveyItem currentItem = getItem(position);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(currentItem);
            }
        });

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
                for (int i = 0; i < type; i++) {
                    holder.responseList.get(i).setText( currentItem.getResponses().get(i) );
                    holder.responseList.get(i).setTag(new int[]{position, i});
                }
                break;
        }

        return convertView;
    }


    static class SurveyEditViewHolder {
        EditText questionEdit;
        TextView responsesView;
        ArrayList<EditText> responseList;
        ImageButton deleteBtn;

        public SurveyEditViewHolder() {
            responseList = new ArrayList<EditText>(SurveyItem.MAX_RESPONSES);
        }
    }
}


