package com.example.admin.quizzy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    // binds
    @BindView(R.id.mainMenuPager)
    ViewPager mainManuPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind everything
        ButterKnife.bind(this);

        //if not logged in, go to loginactivity
        if(!loggedIn()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        //set username view

        //set onclick for logout

        // set up viewpager
        SurveyFragmentAdapter adapter = new SurveyFragmentAdapter(this, getSupportFragmentManager());
        mainManuPager.setAdapter(adapter);
    }

    // look in shareprefs for logged_in
    Boolean loggedIn(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "quizzy.pref", Context.MODE_PRIVATE);
        return prefs.getBoolean("logged_in", false);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the menu
        moveTaskToBack(true);
    }

    // onclick for log out button
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
