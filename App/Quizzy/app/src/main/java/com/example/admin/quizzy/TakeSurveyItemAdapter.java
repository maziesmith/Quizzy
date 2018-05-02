package com.example.admin.quizzy;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TakeSurveyItemAdapter extends ArrayAdapter<TakeSurveyItem> {
    ArrayList<TakeSurveyItem> surveyItems;

    public TakeSurveyItemAdapter(Context context, ArrayList<TakeSurveyItem> items) {
        super(context, 0, items);
        surveyItems = items;
    }

    public ArrayList<TakeSurveyItem> getData() {
        return surveyItems;
    }

    @Override
    public int getViewTypeCount() {
        return SurveyItem.MAX_RESPONSES;
    }

    @Override
    public int getItemViewType(int position) {
        // Subtract 1 from numResponses to keep the type zero-based
        int type = surveyItems.get(position).getSurveyItem().getNumResponses() - 1;
        // Make sure type is non-negative, in case numResponses was 0 for some reason
        if(type < 0) {
            type = 0;
        }
        return type;
    }

    public String dataToJson(int userId, int quizId) {
        String jsonString = "[";

        int numItems = surveyItems.size();
        for(int i = 0; i < numItems; i++) {
            int resIndex = surveyItems.get(i).getSelectedResponseIndex();
            jsonString += surveyItems.get(i).getSurveyItem().toJsonForSubmit(resIndex, userId, quizId);
            if(i < (numItems - 1)) {
                jsonString += ",";
            }
        }

        jsonString += "]";
        return jsonString;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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

                    holder.openResponseEdit.setInputType(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    holder.openResponseEdit.setSingleLine(true);
                    holder.openResponseEdit.setMaxLines(4);
                    holder.openResponseEdit.setHorizontallyScrolling(false);
                    holder.openResponseEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    holder.openResponseEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus) {
                                final int position = v.getId();
                                final EditText e = (EditText)v;
                                surveyItems.get(position).getSurveyItem().setResponse(0, e.getText().toString());
                            }
                        }
                    });
                    break;
                case 2:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.take_survey_list_item_boolean, parent, false);
                    holder.questionView = (TextView)convertView.findViewById(R.id.question);
                    holder.responseGroup = (RadioGroup)convertView.findViewById(R.id.responseGroup);
                    holder.responseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                            surveyItems.get(position).setSelectedId(checkedId);
                            View radioButton = holder.responseGroup.findViewById(checkedId);
                            int responseIndex = holder.responseGroup.indexOfChild(radioButton);
                            surveyItems.get(position).setSelectedResponseIndex(responseIndex);
                        }
                    });

                    for(int i = 0; i < type; i++) {
                        String viewIdName = "response" + (i + 1);
                        int id = getContext().getResources().getIdentifier(viewIdName, "id", getContext().getPackageName());
                        holder.responseList.add((RadioButton) convertView.findViewById(id));
                    }
                    break;
                default:
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.take_survey_list_item_boolean, parent, false);
                    holder.questionView = (TextView)convertView.findViewById(R.id.question);
                    holder.responseGroup = (RadioGroup)convertView.findViewById(R.id.responseGroup);
                    holder.responseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                            surveyItems.get(position).setSelectedId(checkedId);
                            View radioButton = holder.responseGroup.findViewById(checkedId);
                            int responseIndex = holder.responseGroup.indexOfChild(radioButton);
                            surveyItems.get(position).setSelectedResponseIndex(responseIndex);
                        }
                    });

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
                        holder.responseList.add((RadioButton) convertView.findViewById(id));
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
        final TakeSurveyItem currentItem = getItem(position);

        holder.questionView.setText(currentItem.getSurveyItem().getQuestion());

        switch (type) {
            case 0:  // same as case 1 so fall through
            case 1:
                holder.openResponseEdit.setText(currentItem.getSurveyItem().getResponses().get(0));
                holder.openResponseEdit.setId(position);  // So position can be retrieved in OnFocusChange callback
                break;
            case 2:
                for(int i = 0; i < type; i++) {
                    // Assign response string from model to view
                    holder.responseList.get(i).setText( currentItem.getSurveyItem().getResponses().get(i) );
                    holder.responseGroup.check(currentItem.getSelectedId());
                }
                break;
            default:
                for (int i = 0; i < type; i++) {
                    holder.responseList.get(i).setText( currentItem.getSurveyItem().getResponses().get(i) );
                    holder.responseGroup.check(currentItem.getSelectedId());
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
