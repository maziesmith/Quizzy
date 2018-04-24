package com.example.admin.quizzy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by probu on 3/20/2018.
 */

public class MainActivity extends AppCompatActivity {
    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    // bind views
    @BindView(R.id.logoutButton) Button _logoutButton;

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

        // set logout onclick
        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });


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

    private void logOut(){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        // get email to log out
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        final String email = pref.getString("username", null);

        RequestBody body = RequestBody.create(mediaType, "{\n\t\"username\" : \"" +
                email +
                "\"}");


        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/user/logout")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "4126f96d-34dd-4ab5-8729-609bedbc22d0")
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

                            // use moshi to turn it into an object for easy access
                            JsonAdapter<logoutResponse> jsonAdapter = moshi.adapter(logoutResponse.class);
                            // throws JsonDataException if it doesn't fit in response class
                            logoutResponse l = jsonAdapter.fromJson(res);
                            if(l != null && !l.logged_in){
                                // write email and logged in flag to shared pref
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("logged_in", false);
                                editor.apply();

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                        } catch (JsonDataException ignored) {
                        } catch (IOException ignored) {
                        }
                    }
                });
    }

    static class logoutResponse{
        public int id;
        public String username;
        public Boolean logged_in;
    }

}
