package com.example.admin.quizzy;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.FileNotFoundException;
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

// Adapted from: https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/

public class SignupActivity extends AppCompatActivity {
    // for logging
    private static final String TAG = "Quizzy_SignupDebug";

    // handle for name and email
    private String username;
    private int userid;

    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    // bind views
    @BindView(R.id.input_username) EditText _usernameView;
    @BindView(R.id.input_password) EditText _passwordView;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @OnClick(R.id.btn_signup)
    public void signup() {
        // disable signup button after it's pressed
        _signupButton.setEnabled(false);

        // process dialog
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // signup fails if input is not valid
        if (!checkForValidInput()) {
            signupFailed(progressDialog);
            return;
        }

        // get username, pwd
        username = _usernameView.getText().toString();
        final String password = _passwordView.getText().toString();

        Log.d(TAG, "Signing up user " + username);

        // make a signup request with these things
        signup_request(username, password, progressDialog);
    }

    @OnClick(R.id.link_login)
    public void loginLink(){
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    //save shared preferences
    void saveUserInfo(String username, int userid, Boolean logged_in){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", username);
        Log.d(TAG, "Writing username: " + username);
        editor.putInt("userid", userid);
        Log.d(TAG, "Writing userid: " + userid);
        editor.putBoolean("logged_in", logged_in);
        Log.d(TAG, "Writing logged_in: " + logged_in);
        editor.apply();
    }

    public void signupSuccess(final ProgressDialog progressDialog) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Signup successful");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    progressDialog.dismiss();
                    // enable button again
                    _signupButton.setEnabled(true);
                    saveUserInfo(username, userid, true);
                    // flag to tell if signup was successful
                    setResult(RESULT_OK, null);
                    finish();
                    }
                }, 2000);

            }
        });
    }

    public void signupFailed(final ProgressDialog progressDialog) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Signup failed");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                    progressDialog.dismiss();
                    // fail toast
                    Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
                    // enable button again
                    _signupButton.setEnabled(true);
                    }
                }, 1500);
            }
        });
    }


    // make sure username is non-empty and is an email
    public boolean checkUsername(){
        String email = _usernameView.getText().toString();
        if (email.isEmpty()) {
            Log.d(TAG, "Name invalid checkUsername()");
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
            Log.d(TAG, "Password invalid checkPassword()");
            _passwordView.setError("between 4 and 30 alphanumeric characters");
            return false;
        } else {
            _passwordView.setError(null);
            return true;
        }
    }

    // check input for signing up
    public boolean checkForValidInput() {
        return (checkUsername() && checkPassword());
    }

    // makes a request using okhttp3
    // if the call is successful, return true, else false
    public void signup_request(String username, String password, final ProgressDialog progressDialog) {
        // this is how the string will be parsed later
        MediaType mediaType = MediaType.parse("application/json");

        // makes json body of request with parameters
        RequestBody body = RequestBody.create(mediaType, "{\"username\" : \"" +
                username +
                "\",\n\"password\" : \"" +
                password +
                "\"}");

        // request with url
        Request request = new Request.Builder()
                .url("http://quizzybackend.herokuapp.com/user")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "d47c1c56-a232-4d19-af00-323dca9ee617")
                .build();

        // makes an asynchronous call for network io
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                        signupFailed(progressDialog);
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        // we got a response but we need to check if it's the one we want
                        try {
                            Log.d(TAG, "Trying to parse response");
                            signupResponse s = parseResponse(response);
                            if(s.logged_in) {
                                Log.d(TAG, "Successfully parsed response");
                                signupSuccess(progressDialog);
                            } else {
                                throw(new IllegalStateException("Not logged_in"));
                            }
                        } catch(Exception e){
                            Log.d(TAG, "Exception: " + e);
                            signupFailed(progressDialog);
                        }
                    }
                });
    }

    signupResponse parseResponse(final Response response)throws Exception {
        int code = response.code();
        Log.d(TAG, "Response is code " + code);
        JsonParser parser = new JsonParser();
        if(code == 200) {
            // turn our result into a string
            String res = response.body().string();
            Log.d(TAG, "Response is " + res);
            JsonObject json = parser.parse(res).getAsJsonObject();
            Log.d(TAG, "Parsed json: ");
            Log.d(TAG, "logged_in: " + json.get("logged_in").getAsBoolean());
            Log.d(TAG, "username: "+ json.get("username").getAsString());
            username = json.get("username").getAsString();
            Log.d(TAG, "userid: " + json.get("id").getAsInt());
            userid = json.get("id").getAsInt();
            return new signupResponse(code, json.get("logged_in").getAsBoolean(),
                    json.get("username").getAsString(), json.get("id").getAsInt());
        } else {
            throw new JsonDataException("Malformed Json in Signup Response");
        }
    }

    static class signupResponse {
        int code;
        Boolean logged_in;
        String username;
        int userid;
        signupResponse(int code, Boolean logged_in, String username, int userid){
            this.code=code;
            this.logged_in=logged_in;
            this.username=username;
            this.userid=userid;
        }
    }
}


