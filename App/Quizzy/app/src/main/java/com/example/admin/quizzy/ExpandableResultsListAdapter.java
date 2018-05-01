package com.example.admin.quizzy;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableResultsListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "Quizzy_ExpListDebug";
    private Context _context;
    private List<SurveyItem> surveyItems;
    private List<String> header; // header titles
    // Child data in format of header title, child title
    private HashMap<String, List<String>> child;

    public ExpandableResultsListAdapter(Context context, List<SurveyItem> items) {
        this._context = context;
        surveyItems = items;

        this.header = new ArrayList<String>();
        for(int i = 0; i < surveyItems.size(); i++) {
            header.add(surveyItems.get(i).getQuestion());
        }

        this.child = new HashMap<String, List<String>>();
        for(int i = 0; i < header.size(); i++) {
            child.put(header.get(i), surveyItems.get(i).getResponses());
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        // This will return the child
        return this.child.get(this.header.get(groupPosition)).get(
                childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d(TAG, "getChildView: calling function");
        // Getting child text
        final String childText = (String) getChild(groupPosition, childPosition);

        // Inflating child layout and setting textview
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.results_answer_view, parent, false);
        }

        TextView child_text = (TextView) convertView.findViewById(R.id.child);

        child_text.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        // return children count
        return this.child.get(this.header.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        // Get header position
        return this.header.get(groupPosition);
    }

    @Override
    public int getGroupCount() {

        // Get header size
        return this.header.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Log.d(TAG, "getGroupView: Calling function");
        // Getting header title
        String headerTitle = (String) getGroup(groupPosition);

        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.results_header, parent, false);
        }

        TextView header_text = (TextView) convertView.findViewById(R.id.header);

        header_text.setText(headerTitle);

        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            header_text.setTypeface(null, Typeface.BOLD);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    android.R.drawable.arrow_up_float, 0);
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon

            header_text.setTypeface(null, Typeface.NORMAL);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    android.R.drawable.arrow_down_float, 0);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        Log.d(TAG, "onGroupExpanded: calling function");
        // Make call to get all responses for the question at groupPosition

        SurveyItem groupItem = surveyItems.get(groupPosition);
        if(groupItem.getNumResponses() < 2) {
            int questionId = groupItem.getQuestionId();
        } else {

        }
    }

}
