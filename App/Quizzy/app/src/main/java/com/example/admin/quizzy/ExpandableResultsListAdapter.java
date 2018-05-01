package com.example.admin.quizzy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Based on the tutorial at http://www.androhub.com/android-expandablelistview/
 */

public class ExpandableResultsListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "Quizzy_ExpListDebug";
    private Activity _context;
    private List<SurveyItem> surveyItems;
    private List<String> header; // header titles
    // Child data in format of header title, child title
    private HashMap<String, List<ResponseItem>> child;

    private final OkHttpClient client = new OkHttpClient();

    public ExpandableResultsListAdapter(Context context, List<SurveyItem> items) {
        this._context = (Activity)context;
        surveyItems = items;

        this.header = new ArrayList<String>();
        for(int i = 0; i < surveyItems.size(); i++) {
            header.add(surveyItems.get(i).getQuestion());
        }

        this.child = new HashMap<String, List<ResponseItem>>();
        for(int i = 0; i < header.size(); i++) {
            List<ResponseItem> responses = new ArrayList<ResponseItem>();
            ArrayList<String> tempResponses = surveyItems.get(i).getResponses();
            for (int j = 0; j < tempResponses.size(); j++) {
                responses.add(new ResponseItem(tempResponses.get(j), 0));
            }
            child.put(header.get(i), responses);
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
        // Getting child text
        final ResponseItem childItem = (ResponseItem)getChild(groupPosition, childPosition);

        // Inflating child layout and setting textview
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.results_answer_view, parent, false);
        }

        TextView child_text = (TextView) convertView.findViewById(R.id.child);
        TextView child_count = (TextView) convertView.findViewById(R.id.count);

        final String childText = childItem.responseText;
        final int childCount = childItem.count;

        child_text.setText(childText);
        child_count.setText(String.valueOf(childCount));

        if (surveyItems.get(groupPosition).getNumResponses() < 2) {
            child_count.setVisibility(View.INVISIBLE);
        } else {
            child_count.setVisibility(View.VISIBLE);
        }
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
    public void onGroupExpanded(final int groupPosition) {
        Log.d(TAG, "onGroupExpanded: calling function");

        // Make call to get all responses for the question at groupPosition
        final SurveyItem groupItem = surveyItems.get(groupPosition);
        int questionId = groupItem.getQuestionId();
        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz/questionanswer/" + questionId)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onSurveyFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                final String jsonResponse = body.string();
                Log.d(TAG, "onResponse: " + jsonResponse);
                body.close();
                switch (response.code()) {
                    case 200:
                        _context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseResponses(groupPosition, jsonResponse);
                            }
                        });

                        break;
                    default:
                        throw new IOException("getResponses failed with code " + response.code());
                }
            }
        });

    }

    private void parseResponses(int groupPosition, String responsesArray) {

        List<String> responses = new ArrayList<String>();
        JsonParser parser = new JsonParser();
        try {
            JsonArray fullJson = parser.parse(responsesArray).getAsJsonArray();
            for(int i = 0; i < fullJson.size(); i++) {
                JsonObject responseObj = fullJson.get(i).getAsJsonObject();
                String responseText = responseObj.getAsJsonPrimitive("text").getAsString();
                responses.add(responseText);
            }

            int numResp = surveyItems.get(groupPosition).getNumResponses();
            List<ResponseItem> responseItems = new ArrayList<ResponseItem>();

            // if question was open response, save all individual responses
            if(numResp < 2) {
                for (int i = 0; i < responses.size(); i++) {
                    if( !responses.get(i).isEmpty() ) {
                        responseItems.add(new ResponseItem(responses.get(i), 0));
                    }
                }
                child.put(header.get(groupPosition), responseItems);
            // else count the number of occurrences of each possible response
            } else {
                for(int i = 0; i < numResp; i++) {
                    String text = child.get(header.get(groupPosition)).get(i).responseText;
                    int occurrences = Collections.frequency(responses, text);
                    Log.d(TAG, "parseResponses: NUMBER OF \"" + text + "\" = " + (occurrences - 1));
                    responseItems.add(new ResponseItem(text, occurrences - 1));
                }
                child.put(header.get(groupPosition), responseItems);
            }

            notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "parseSurveyTitle: " + e);
        }

    }

    static class ResponseItem {

        String responseText;
        int count;

        public ResponseItem(String text, int cnt) {
            responseText = text;
            count = cnt;
        }
    }
}
