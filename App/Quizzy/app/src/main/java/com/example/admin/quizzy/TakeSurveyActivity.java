package com.example.admin.quizzy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TakeSurveyActivity extends AppCompatActivity{

    private TakeSurveyItemAdapter _adapter;
    private RetainedFragment _dataFragment;
    private static final String TAG_TAKESURVEY_FRAGMENT = "Retained_TakeSurvey";
    private static final String TAG = "Quizzy_TakeQuizDebug";
    private int _surveyId;
    private int _ownerId;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_survey);

        _surveyId = getIntent().getIntExtra("surveyid", SurveyItem.DEFAULT_ID);

        FragmentManager fm = getSupportFragmentManager();
        _dataFragment = (RetainedFragment)fm.findFragmentByTag(TAG_TAKESURVEY_FRAGMENT);
        if(_dataFragment == null) {
            _dataFragment = new RetainedFragment();
            fm.beginTransaction().add(_dataFragment, TAG_TAKESURVEY_FRAGMENT).commit();
            _dataFragment.setData(new ArrayList<TakeSurveyItem>());
            getSurvey(_surveyId);
        }

        _adapter = new TakeSurveyItemAdapter(this, _dataFragment.getData());
        ListView surveyList = findViewById(R.id.surveyItemList);
        surveyList.setAdapter(_adapter);

        FloatingActionButton returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + dataToJson());

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), dataToJson());
                Request request = new Request.Builder()
                        .url("http://quizzybackend.herokuapp.com/quiz/all/guest")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cache-Control", "no-cache")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onSurveyFailure: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody returnedBody = response.body();
                        String jsonResponse = returnedBody.string();
                        Log.d(TAG, "onResponse: " + jsonResponse);
                        returnedBody.close();
                        switch (response.code()) {
                            case 200:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TakeSurveyActivity.this, "Responses submitted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TakeSurveyActivity.this, "Submission failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                throw new IOException("Submitting survey failed with code " + response.code());
                        }
                    }
                });
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

    private String dataToJson() {
        String jsonString = "{";

        TextView titleView = (TextView)findViewById(R.id.surveyTitleView);
        jsonString += "\"id\": " + _surveyId + ",";
        jsonString += "\"quizname\": " + "\"" + titleView.getText().toString() + "\",";

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "quizzy.pref", Context.MODE_PRIVATE);
        int userid = prefs.getInt("userid", SurveyItem.DEFAULT_ID);

        jsonString += "\"userid\": " + userid + ",";
        jsonString += "\"questions\": ";

        jsonString += _adapter.dataToJson(userid, _surveyId);

        jsonString += "}";
        return jsonString;
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
                        ArrayList<SurveyItem> data = parseSurvey(jsonResponse);
                        int defaultButtonId = getResources().getIdentifier("response1", "id", getPackageName());
                        final ArrayList<TakeSurveyItem> dataWithDefaultSelection = new ArrayList<TakeSurveyItem>();
                        for (int i = 0; i < data.size(); i++) {
                            dataWithDefaultSelection.add(new TakeSurveyItem(data.get(i), defaultButtonId));
                        }
                        final String title = parseSurveyTitle(jsonResponse);
                        final int owner = parseOwnerId(jsonResponse);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                _dataFragment.setData(dataWithDefaultSelection);
                                _adapter.addAll(dataWithDefaultSelection);
                                TextView titleView = (TextView) findViewById(R.id.surveyTitleView);
                                titleView.setText(title);
                                _ownerId = owner;
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

    private int parseOwnerId(String json) {
        int id = SurveyItem.DEFAULT_ID;
        JsonParser parser = new JsonParser();
        try {
            JsonObject fullJson = parser.parse(json).getAsJsonObject();
            id = fullJson.getAsJsonPrimitive("userid").getAsInt();

        } catch (Exception e) {
            Log.d(TAG, "parseOwnerId: " + e);
        }

        return id;
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


    /**
     * A fragment that is used to save the data in the adapter when
     * the screen orientation changes, so it can be restored on re-creation
     * of the activity.
     */
    public static class RetainedFragment extends Fragment {
        private ArrayList<TakeSurveyItem> data;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setData(ArrayList<TakeSurveyItem> data) {
            this.data = data;
        }

        public ArrayList<TakeSurveyItem> getData() {
            return data;
        }
    }
}
