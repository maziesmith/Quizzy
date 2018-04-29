package com.example.admin.quizzy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SurveysFragment extends android.support.v4.app.Fragment {
    public SurveysFragment() { }

    private static final String TAG = "Quizzy_FragmentsDebug";

    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    // handler for load main menu
    protected static Handler loadHandler;

    // bind views
    @BindView(R.id.addSurveyButton)
    FloatingActionButton _addSurveyButton;
    @BindView(R.id.menuList)
    ListView _listView;

    public void addSurvey(Boolean published){
        Log.d(TAG, "Adding Survey");
        final int userid = getUserId();
        String date = getDateString();

        // context
        final FragmentActivity activity = getActivity();

        // create ProgressDialog when logging in
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding...");
        progressDialog.show();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"quizname\": \"" +
                "New" + date +
                "\",\n\t\"userid\": " +
                userid +
                ",\n\t\"published\": " +
                published +
                "\n}");
        Log.d(TAG, "addSurvey: " + "{\n\t\"quizname\": \"" +
                "New" + date +
                "\",\n\t\"userid\": " +
                userid +
                ",\n\t\"published\": " +
                published +
                "\n}");
        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "d6970e83-6404-4cc1-b2cf-e3a0738c07b3")
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        addFailed(progressDialog);
                    }
                    @Override
                    public void onResponse(Call call, final Response response) {
                        try {
                            addSurveyResponse a = parseAddResponse(response);
                            addSuccess(a, progressDialog);
                        } catch (Exception e){
                            Log.d(TAG, "Exception: " + e);
                            addFailed(progressDialog);
                        }
                    }
                });

    }

    private void addSuccess(final addSurveyResponse a, final ProgressDialog progressDialog){
        // context
        final FragmentActivity activity = getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Add Succeeded");
                progressDialog.dismiss();
                Intent intent = new Intent(activity, CreateSurveyActivity.class);
                intent.putExtra("surveyid", a.surveyid);
                intent.putExtra("surveyname", a.quizname);
                Log.d(TAG, "Extras for intent: " + a.surveyid + " " + a.quizname);
                activity.startActivity(intent);
            }
        });
    }

    private void addFailed(final ProgressDialog progressDialog){
        // context
        final FragmentActivity activity = getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Add Failed");
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Failed to create survey.");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    addSurveyResponse parseAddResponse(Response response) throws Exception{
        int code = response.code();
        Log.d(TAG, "Response is code " + code);
        JsonParser parser = new JsonParser();
        if(code == 200) {
            // turn our result into a string
            String res = response.body().string();
            Log.d(TAG, "Response is " + res);
            JsonObject json = parser.parse(res).getAsJsonObject();
            return new addSurveyResponse(code, json.get("quizname").getAsString(),
                    json.get("userid").getAsInt(), json.get("id").getAsInt());
        }else {
            Log.d(TAG, "Code was not 200");
            throw new Exception("SurveysFragment:parseAddResponse failed with code " + code);
        }
    }

    static class addSurveyResponse {
        int code;
        String quizname;
        int userid;
        int surveyid;
        addSurveyResponse(int code, String quizname, int userid, int surveyid){
            this.code = code;
            this.quizname=quizname;
            this.userid=userid;
            this.surveyid=surveyid;
        }
    }

    public void populateFromUrl(String url, final ArrayList<MenuItem> m){
        // context
        final FragmentActivity activity = getActivity();
        Log.d(TAG, "Url for request is http://quizzybackend.herokuapp.com/quiz/" + url);
        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz/" + url)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "4226f3f9-8f85-46ee-a56a-158129333908")
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // main thread stuff here
                                Log.d(TAG, "Failed the call");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        try {
                            // turns a json string into an arraylist of menuitems to be inflated
                            String res = response.body().string();
                            Log.d(TAG, "Got a response: " + res);
                            populate(m, res);
                        } catch(Exception e){
                            Log.d(TAG, "Exception: " + e);
                        }
                    }
                });


    }

    public void populate(final ArrayList<MenuItem> m, final String jsonInput) throws Exception{
        Log.d(TAG, "Populating arraylist...");
        Type listMenuItem = Types.newParameterizedType(List.class, MenuItem.class);
        final JsonAdapter<List<MenuItem>> adapter = moshi.adapter(listMenuItem);
        m.addAll(adapter.fromJson(jsonInput));
        Log.d(TAG, "Populated");
    }

    int getUserId(){
        // context
        final FragmentActivity activity = getActivity();
        SharedPreferences pref = activity.getSharedPreferences("quizzy.pref", 0);
        return pref.getInt("userid", 0);
    }

    // the date string makes sure the survey names are unique when making them
    String getDateString(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date now = new Date();
        return sdfDate.format(now);
    }
}
