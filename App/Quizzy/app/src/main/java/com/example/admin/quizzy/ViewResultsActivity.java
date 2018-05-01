package com.example.admin.quizzy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ViewResultsActivity extends AppCompatActivity{
    private static ExpandableListView expandableListView;
    private static ExpandableResultsListAdapter _adapter;
    private static final String TAG = "Quizzy_ViewResultsDebug";
    private int _surveyId;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);

        _surveyId = 32; //getIntent().getIntExtra("surveyid", SurveyItem.DEFAULT_ID);

        expandableListView = (ExpandableListView) findViewById(R.id.resultsExpandableList);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);

        getSurvey(_surveyId);
        //setItems();
        setListener();

        FloatingActionButton returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getSurvey(int id) {
        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz/" + id)
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
                String jsonResponse = body.string();
                Log.d(TAG, "onResponse: " + jsonResponse);
                body.close();
                switch (response.code()) {
                    case 200:
                        final ArrayList<SurveyItem> data = parseSurvey(jsonResponse);
                        final String title = parseSurveyTitle(jsonResponse);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setItems(data);
                                TextView titleView = (TextView) findViewById(R.id.surveyTitleView);
                                titleView.setText(title);
                            }
                        });
                        break;
                    default:
                        throw new IOException("getSurvey failed with code " + response.code());
                }
            }
        });

    }

    private String parseSurveyTitle(String json) {
        String title = "";
        JsonParser parser = new JsonParser();
        try {
            JsonObject fullJson = parser.parse(json).getAsJsonObject();
            title = fullJson.getAsJsonPrimitive("quizname").getAsString();

        } catch (Exception e) {
            Log.d(TAG, "parseSurveyTitle: " + e);
        }

        return title;
    }

    private ArrayList<SurveyItem> parseSurvey(String json) {
        ArrayList<SurveyItem> surveyItems = new ArrayList<SurveyItem>();

        JsonParser parser = new JsonParser();
        try {
            JsonObject fullJson = parser.parse(json).getAsJsonObject();
            // Find the array of survey questions in the json
            JsonArray itemsArray = fullJson.getAsJsonArray("questions");

            // Build a SurveyItem from each array element
            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject item = itemsArray.get(i).getAsJsonObject();
                int questionId = item.getAsJsonPrimitive("id").getAsInt();
                String questionText = item.getAsJsonPrimitive("text").getAsString();
                JsonArray responseArray = item.getAsJsonArray("answers");

                String[] questionResponses = new String[responseArray.size()];
                int[] responseIds = new int[responseArray.size()];
                for (int j = 0; j < responseArray.size(); j++) {
                    JsonObject response = responseArray.get(j).getAsJsonObject();
                    String responseText = response.getAsJsonPrimitive("text").getAsString();
                    int responseId = response.getAsJsonPrimitive("id").getAsInt();
                    questionResponses[j] = responseText;
                    responseIds[j] = responseId;
                }

                SurveyItem newItem = new SurveyItem(questionText, questionResponses, questionId, responseIds);
                surveyItems.add(newItem);
            }

        } catch (Exception e) {
            Log.d(TAG, "parseSurvey: " + e);
        }

        return surveyItems;
    }

    // Setting headers and childs to expandable listview
    void setItems(ArrayList<SurveyItem> items) {
        _adapter = new ExpandableResultsListAdapter(ViewResultsActivity.this, items);
        expandableListView.setAdapter(_adapter);
    }

    // Setting different listeners to expandablelistview
    void setListener() {

/*        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView listview, View view,
                                        int group_pos, long id) {

                Toast.makeText(ViewResultsActivity.this,
                        "You clicked : " + _adapter.getGroup(group_pos),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
/*        expandableListView
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    // Default position
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)

                            // Collapse the expanded group
                            expandableListView.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }

                });*/

/*        // This listener will show toast on child click
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView listview, View view,
                                        int groupPos, int childPos, long id) {
                Toast.makeText(
                        ViewResultsActivity.this,
                        "You clicked : " + _adapter.getChild(groupPos, childPos),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/
    }
}
