package com.example.admin.quizzy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.EOFException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.BindView;

import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by probu on 4/8/2018.
 */

// Adapted from: https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "QUIZZYDEBUG";
    // flag to see if we need to signup
    private static final int SIGNUP = 0;

    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();

    // bind views
    @BindView(R.id.input_email) EditText _usernameView;
    @BindView(R.id.input_password) EditText _passwordView;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupButton;

    @OnClick(R.id.btn_login)
    public void loginButtonOnclick() {
        Log.d(TAG, "Login Button Clicked");
        // disable button when logging in
        _loginButton.setEnabled(false);

        // create ProgressDialog when logging in
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        if (!checkForValidInput()) {
            loginFailed(progressDialog);
            return;
        }

        // get email string
        final String username = _usernameView.getText().toString();
        // get password string
        final String password = _passwordView.getText().toString();

        login(username, password, progressDialog);
    }

    @OnClick(R.id.link_signup)
    public void goToSignIn() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        // use startActivityForResult so that we can return from signup after
        startActivityForResult(intent, SIGNUP);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // skip past this screen if we're logged in
        if(loggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void login(final String username, final String password, final ProgressDialog progressDialog) {
        Log.d(TAG, "Calling");
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"username\":\"" +
                username +
                "\",\n\t\"password\":\"" +
                password +
                "\"\n}");
        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/user/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "af192735-9852-47ed-afdf-029de5065962")
                .build();

        // makes an asynchronous call for network io
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                        Log.d(TAG, "Call onFailure: " + e);
                        loginFailed(progressDialog);
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        // we got a response but we need to check if it's the one we want
                        try {
                            Log.d(TAG, "Trying to parse response");
                            loginResponse l = parseResponse(response);
                            if (l.logged_in){
                                Log.d(TAG, "We are logged in so login success");
                                saveUserInfo(l.username, l.userid, true);
                                loginSuccess(progressDialog);
                            } else {
                                Log.d(TAG, "logged_in is false");
                                loginFailed(progressDialog);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: " + e);
                            // data doesn't match the proper response format
                            loginFailed(progressDialog);
                        }
                    }
                });

        // after our calls,
    }

    loginResponse parseResponse(Response response) throws Exception {
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
            return new loginResponse(code, json.get("id").getAsInt(),
                    json.get("username").getAsString(), json.get("logged_in").getAsBoolean());
        } else {
            Log.d(TAG, "Code was not 200");
            return new loginResponse(code, 0, "", false);
        }
    }

    //save shared preferences
    void saveUserInfo(String username, int userid, Boolean logged_in){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", username);
        editor.putInt("userid", userid);
        editor.putBoolean("logged_in", logged_in);
        editor.apply();
    }

    // look in shareprefs for logged_in
    Boolean loggedIn(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "quizzy.pref", Context.MODE_PRIVATE);
        return prefs.getBoolean("logged_in", false);
    }

    // override onActivityResult for after using startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGNUP) {
            if (resultCode == RESULT_OK) {
                // if our request and result are ok, signup worked
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    // disable back button for login activity
    @Override
    public void onBackPressed() {
        // disable going back to the menu
        moveTaskToBack(true);
    }

    public void loginSuccess(final ProgressDialog progressDialog) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "in login success method");
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
                Log.d(TAG, "sending us from loginactivity to mainactivity");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loginFailed(final ProgressDialog progressDialog) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "in login fail method");
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
                _loginButton.setEnabled(true);
            }
        });
    }

    // make sure username is non-empty and is an email
    public boolean checkUsername(){
        String email = _usernameView.getText().toString();
        if (email.isEmpty()) {
            _usernameView.setError("enter a valid username");
            return false;
        } else {
            _usernameView.setError(null);
            return true;
        }
    }

    // make sure password is valid
    public boolean checkPassword(){
        String password = _passwordView.getText().toString();
        if (password.isEmpty() || password.length() < 4 || password.length() > 30) {
            _passwordView.setError("between 4 and 30 alphanumeric characters");
            return false;
        } else {
            _passwordView.setError(null);
            return true;
        }
    }

    // check for valid input by checking both username and password
    public boolean checkForValidInput() {
        return (checkUsername() && checkPassword());
    }

    static class loginResponse {
        int code;
        int userid;
        String username;
        Boolean logged_in;

        loginResponse(int code, int userid, String username, Boolean logged_in){
            this.code = code;
            this.userid = userid;
            this.username = username;
            this.logged_in = logged_in;
        }
    }
}
