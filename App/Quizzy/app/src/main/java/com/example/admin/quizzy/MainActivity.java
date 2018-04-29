package com.example.admin.quizzy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;

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
 * Created by probu on 3/20/2018.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Quizzy_MainDebug";
    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    // binds
    @BindView(R.id.mainMenuPager)
    ViewPager mainManuPager;

    @BindView(R.id.mainMenuLogoutButton)
    Button _logoutButton;

    @BindView(R.id.mainMenuUsernameView)
    TextView _usernameView;

    @OnClick(R.id.mainMenuRefreshButton)
    public void refreshUI(){
        finish();
        startActivity(getIntent());
    }

    @OnClick(R.id.mainMenuLogoutButton)
    public void logOut(){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        _logoutButton.setEnabled(false);

        // create ProgressDialog when logging in
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        // get username to log out
        final String username = getUsername();

        RequestBody body = RequestBody.create(mediaType, "{\n\t\"username\" : \"" +
                username +
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
                                logoutFailed(progressDialog);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        try {
                            logoutResponse l = parseResponse(response);
                            if(!l.logged_in){
                                logoutSuccess(progressDialog);
                            }
                        } catch (Exception e){
                            Log.d(TAG, "Exception: " + e);
                            logoutFailed(progressDialog);
                        }
                    }
                });
    }

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
        String username = getUsername();
        _usernameView.setText(username);

        // set up viewpager
        SurveyFragmentAdapter adapter = new SurveyFragmentAdapter(this, getSupportFragmentManager());
        mainManuPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the menu
        moveTaskToBack(true);
    }

    String getUsername(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        return pref.getString("username", null);
    }

    // look in shareprefs for logged_in
    Boolean loggedIn(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "quizzy.pref", Context.MODE_PRIVATE);
        return prefs.getBoolean("logged_in", false);
    }

    void setLogged_In(Boolean logged_in){
        // write email and logged in flag to shared pref
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("logged_in", logged_in);
        editor.apply();
    }

    logoutResponse parseResponse(Response response) throws Exception {
        int code = response.code();
        Log.d(TAG, "Response is code " + code);
        JsonParser parser = new JsonParser();
        if(code == 200) {
            Log.d(TAG, "Code " + code + "means we parse JSON");
            // turn our result into a string
            String res = response.body().string();
            Log.d(TAG, "Response is " + res);
            JsonObject json = parser.parse(res).getAsJsonObject();
            Log.d(TAG, "Parsed json");
            return new logoutResponse(code, json.get("id").getAsInt(),
                    json.get("username").getAsString(), json.get("logged_in").getAsBoolean());
        } else {
            Log.d(TAG, "Code was not 200");
            return new logoutResponse(code, 0, "", false);
        }
    }

    void logoutSuccess(final ProgressDialog progressDialog) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                _logoutButton.setEnabled(true);
                Log.d(TAG, "sending us from mainactivity to login");
                setLogged_In(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    void logoutFailed(final ProgressDialog progressDialog) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Log out fail");
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Logout failed", Toast.LENGTH_LONG).show();
                _logoutButton.setEnabled(true);
            }
        });

    }

    static class logoutResponse{
        int code;
        int userid;
        String username;
        Boolean logged_in;
        logoutResponse(int code, int userid, String username, Boolean logged_in){
            this.code=code;
            this.userid=userid;
            this.username=username;
            this.logged_in=logged_in;
        }
    }



}
