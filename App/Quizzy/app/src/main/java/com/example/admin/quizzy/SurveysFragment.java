package com.example.admin.quizzy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.solver.widgets.Helper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    // context
    final FragmentActivity activity = getActivity();

    // bind views
    @BindView(R.id.addSurveyButton)
    FloatingActionButton _addSurveyButton;
    @BindView(R.id.menuList)
    ListView _listView;

    @OnClick(R.id.addSurveyButton)
    public void addSurvey(){
        SharedPreferences pref = getActivity().getSharedPreferences("quizzy.pref", 0);
        final int userid = pref.getInt("userid", 0);

        // this is a datetime string to make sure quiz names are unique
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date now = new Date();
        String date = sdfDate.format(now);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"quizname\" : \"" +
                "New Quiz " +
                date +
                "\",\n\t\"userid\" : \"" +
                userid +
                "\"\n}");
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
                        // Error
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // main thread stuff here
                                addFailed();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        try {
                            String res = response.body().string();
                            // use moshi to turn it into an object for easy access
                            JsonAdapter<addSurveyResponse> jsonAdapter = moshi.adapter(addSurveyResponse.class);
                            // throws JsonDataException if it doesn't fit in response class
                            final addSurveyResponse a = jsonAdapter.fromJson(res);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // main thread stuff here
                                    Intent intent = new Intent(getActivity(), CreateSurveyActivity.class);
                                    intent.putExtra("surveyid", a.id);
                                    intent.putExtra("surveyname", a.quizname);
                                    getActivity().startActivity(intent);
                                }
                            });
                        } catch (JsonDataException ignored) {
                            addFailed();
                        } catch (IOException ignored) {
                            addFailed();
                        }
                    }
                });

    }

    public void populateFromUrl(String url, final ArrayList<MenuItem> m){
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
                    public void onResponse(Call call, final Response response) throws IOException {
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

    public void populate(ArrayList<MenuItem> m, String jsonInput) throws Exception{
        Log.d(TAG, "Populating arraylist...");
        Type listMenuItem = Types.newParameterizedType(List.class, MenuItem.class);
        JsonAdapter<List<MenuItem>> adapter = moshi.adapter(listMenuItem);
        m.addAll(adapter.fromJson(jsonInput));
        Log.d(TAG, "Populated");
    }



    private void addFailed(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Failed to create survey.");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    static class addSurveyResponse {
        String quizname;
        int userid;
        int id;
        Boolean published;
    }

}
