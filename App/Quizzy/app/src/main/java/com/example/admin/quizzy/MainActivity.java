package com.example.admin.quizzy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by probu on 3/20/2018.
 */

public class MainActivity extends AppCompatActivity {
    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a list of surveys
        final ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        // fill the menuItems list with stuff
        //populate(menuItems);
        menuItems.add(new MenuItem("New Survey", 100));
        menuItems.add(new MenuItem("Survey", 101));
        menuItems.add(new MenuItem("vey", 102));
        menuItems.add(new MenuItem("ey", 103));
        menuItems.add(new MenuItem("vey", 104));
        menuItems.add(new MenuItem("Survey", 105));

        // Create the adapter
        MenuItemAdapter adapter = new MenuItemAdapter(MainActivity.this, menuItems);

        // bind the view
        ListView _listView = (ListView) findViewById(R.id.menuList);

        // set adapter for listview
        _listView.setAdapter(adapter);
    }

    public MainActivity() {
        // Required empty public constructor
    }

    // TODO: populate the listview
    public void populate(ArrayList<MenuItem> menuItems){
        /*Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/quiz/all")
                .get()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "4226f3f9-8f85-46ee-a56a-158129333908")
                .build();
        // makes an asynchronous call for network io
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // main thread stuff here
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();
                        // parse string
                    }
                });*/
    }
}
