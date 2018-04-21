package com.example.admin.quizzy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        final ArrayList<MenuItem> menuItems = new ArrayList<>();

        // fill the menuItems list with stuff
        populate(menuItems);

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

    public void populate(final ArrayList<MenuItem> m){
        Request request = new Request.Builder()
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
                        try {
                            String res = response.body().string();
                            Type listMenuItem = Types.newParameterizedType(List.class, MenuItem.class);
                            JsonAdapter<List<MenuItem>> adapter = moshi.adapter(listMenuItem);
                            m.addAll(adapter.fromJson(res));
                        } catch(IOException ignored){
                        } catch(JsonDataException ignored) {
                        }
                    }
                });
    }
}
