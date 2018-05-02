package com.example.admin.quizzy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

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

/**
 * Created by brianmedina on 3/27/18.
 */

public class MenuItemAdapter extends ArrayAdapter<MenuItem> implements MenuLoader{
    private static final String TAG = "Quizzy_MenuADebug";

    private MenuItem currentItem;
    private Context context;
    private Activity activity;
    private Boolean published;
    private static Handler loadHandler;

    public MenuItemAdapter(Context context, ArrayList<MenuItem> items, Boolean published, Handler loadHandler) {
        super(context, 0, items);
        this.context = context;
        this.published = published;
        this.loadHandler = loadHandler;
    }


    @Override
    public void onSurveySuccess() {
        notifyDataSetChanged();
    }

    @Override
    public void onSurveyFailure() {
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        // Get the object located at this position in the list
        currentItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            if(!published)
                v = LayoutInflater.from(getContext()).inflate(
                        R.layout.mysurveys_list_item, parent, false);
            else
                v = LayoutInflater.from(getContext()).inflate(
                        R.layout.published_list_item, parent, false);
        }

        // bind everything
        holder = new ViewHolder(v);

        // set name
        holder.titleView.setText(currentItem.getSurveyName());

        if(!published) {
            // set onclick for edit button
            holder.editButton.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    // get position we clicked
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent();
                    final int position = listView.getPositionForView(parentRow);
                    // get item from position
                    MenuItem thisItem = getItem(position);
                    // fail if survey is published
                    if(thisItem.isPublished()){
                        Log.d(TAG, "Tried to edit a published survey");
                        Toast.makeText(context, "Can't edit a published survey", Toast.LENGTH_LONG).show();
                    } else {
                        // edit this surveyid and surveyname
                        Intent intent = new Intent(context, CreateSurveyActivity.class);
                        intent.putExtra("surveyid", thisItem.getSurveyId());
                        intent.putExtra("surveyname", thisItem.getSurveyName());
                        context.startActivity(intent);
                    }

                    return true;
                }
            });
            holder.publishButton.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v){
                    // get position we clicked
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent();
                    final int position = listView.getPositionForView(parentRow);
                    // get item from position
                    final int surveyid = getItem(position).getSurveyId();
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Publishing...");
                    progressDialog.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            // update database
                            publish(surveyid);
                            // updates the listview
                            loadHandler.sendEmptyMessage(0);
                        }
                    }, 750);
                    return true;
                }
            });
        } else {
            holder.takeButton.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v){
                    // get position we clicked
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent();
                    final int position = listView.getPositionForView(parentRow);
                    // get item from position
                    MenuItem thisItem = getItem(position);
                    // edit this surveyid and surveyname
                    Intent intent = new Intent(context, TakeSurveyActivity.class);
                    intent.putExtra("surveyid", thisItem.getSurveyId());
                    context.startActivity(intent);
                    return true;
                }
            });

            holder.analysisButton.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v){
                    // get position we clicked
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent();
                    final int position = listView.getPositionForView(parentRow);
                    // get item from position
                    MenuItem thisItem = getItem(position);
                    // go to analysis page
                    Intent intent = new Intent(context, ViewResultsActivity.class);
                    intent.putExtra("surveyid", thisItem.getSurveyId());
                    context.startActivity(intent);
                    return true;
                }
            });

        }

        holder.deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                // gets the view's position
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent().getParent();
                final int position = listView.getPositionForView(parentRow);
                // gets the surveyid from the item
                final int surveyid = getItem(position).getSurveyId();
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Deleting...");
                progressDialog.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        // removes from the list
                        MenuItemAdapter.this.remove(getItem(position));
                        // removes from the database
                        delete(surveyid);
                        // updates the listview
                        loadHandler.sendEmptyMessage(0);
                    }
                }, 750);
                return true;
            }
        });

        if(published && currentItem.isPublished()){
            holder.deleteButton.setVisibility(View.INVISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.VISIBLE);
        }

        return v;
    }



    // holds all the views to bind with butterknife
    static class ViewHolder{
        @Nullable @BindView(R.id.menuItemTitle) TextView titleView;
        @Nullable @BindView(R.id.menuEditButton) ImageButton editButton;
        @Nullable @BindView(R.id.menuDeleteButton) ImageButton deleteButton;
        @Nullable @BindView(R.id.menuTakeButton) ImageButton takeButton;
        @Nullable @BindView(R.id.menuAnalysisButton) ImageButton analysisButton;
        @Nullable @BindView(R.id.menuPublishButton) ImageButton publishButton;

        // constructor binds them immediately
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    void delete(int surveyid){
        Log.d(TAG, "deleting surveyid: " + surveyid);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz/" + surveyid)
                .delete(null)
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "e63d0adb-0431-4193-a6f9-4ac71cb7b49d")
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        Log.d(TAG, "Exception: " + e);
                        deleteFailed();

                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        Log.d(TAG, "Response recieved");
                        if(response.code() == 200) {
                            Log.d(TAG, "Successful response to delete call");
                            deleteSuccess();
                        } else
                            deleteFailed();

                    }
                });
    }

    void publish(int surveyid){
        Log.d(TAG, "publishing surveyid: " + surveyid);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"id\" : \"" +
                surveyid +
                "\",\n\"published\": true}");

        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz")
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "d8ff8693-a3e6-42c0-92ed-986116d7cf23")
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        Log.d(TAG, "Exception: " + e);
                        publishFailed();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        Log.d(TAG, "Response recieved");
                        if(response.code() == 200) {
                            Log.d(TAG, "Successful response to publish call");
                            publishSuccess();
                        } else
                            publishFailed();

                    }
                });

    }

    void deleteFailed(){
        activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Delete failed");
                Toast.makeText(context, "Delete failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    void deleteSuccess(){
        activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Delete success");
                Toast.makeText(context, "Delete success", Toast.LENGTH_LONG).show();
            }
        });
    }

    void publishFailed(){
        activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Publish failed");
                Toast.makeText(context, "Publish failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    void publishSuccess(){
        activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Publish success");
                Toast.makeText(context, "Publish success", Toast.LENGTH_LONG).show();
            }
        });
    }
}

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
