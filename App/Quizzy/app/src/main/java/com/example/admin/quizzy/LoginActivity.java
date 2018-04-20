package com.example.admin.quizzy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.BindView;

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
    // flag to see if we need to signup
    private static final int SIGNUP = 0;

    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    // handle for login response
    loginResponse loginResponse;

    // bind views
    @BindView(R.id.input_email) EditText _usernameView;
    @BindView(R.id.input_password) EditText _passwordView;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "quizzy.pref", Context.MODE_PRIVATE);

        // if we already set logged_in, we don't need to log in
        if(prefs.getBoolean("logged_in", true)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // OnClick for login button logs us in
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // OnClick for signup button
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                // use startActivityForResult so that we can return from signup after
                startActivityForResult(intent, SIGNUP);
            }
        });
    }

    public void login() {
        // check for valid input
        if (!checkForValidInput()) {
            loginFailed();
            return;
        }

        // disable button when logging in
        _loginButton.setEnabled(false);

        // create ProgressDialog when logging in
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // get email string
        final String email = _usernameView.getText().toString();
        // get password string
        final String password = _passwordView.getText().toString();

        final int userid = authenticateRequest(email, password);

        // create handler to delay login by 3 seconds, because that's what logins do I guess
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // write email and logged in flag to shared pref
                SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", email);
                editor.putInt("userid", userid);
                editor.putBoolean("logged_in", true);
                editor.apply();
                progressDialog.dismiss();
            }
        }, 3000);
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

    public void loginSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // main thread stuff here
                // write email and logged in flag to shared pref
                SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", loginResponse.username);
                editor.putInt("userid", loginResponse.userid);
                editor.putBoolean("logged_in", true);
                editor.apply();
                _loginButton.setEnabled(true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loginFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // main thread stuff here
                Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

                _loginButton.setEnabled(true);
            }
        });
    }

    // make sure username is non-empty and is an email
    public boolean checkUsername(){
        String email = _usernameView.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _usernameView.setError("enter a valid email address");
            return false;
        } else {
            _usernameView.setError(null);
            return true;
        }
    }

    // make sure password is valid
    public boolean checkPassword(){
        String password = _passwordView.getText().toString();
        if (password.isEmpty() || password.length() < 6 || password.length() > 30) {
            _passwordView.setError("between 6 and 30 alphanumeric characters");
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

    public int authenticateRequest(final String username, final String password) {
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
                        loginFailed();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        // we got a response but we need to check if it's the one we want
                        try {
                            // turn our result into a string
                            String res = response.body().string();
                            // use moshi to turn it into an object for easy access
                            JsonAdapter<loginResponse> jsonAdapter = moshi.adapter(loginResponse.class);
                            // throws JsonDataException if it doesn't fit in response class
                            loginResponse = jsonAdapter.fromJson(res);
                            if (loginResponse.logged_in){
                                loginSuccess();
                            } else {
                                loginFailed();
                            }
                            // if we get here login is successful
                        } catch (JsonDataException e) {
                            // data doesn't match the proper response format
                            loginFailed();
                        } catch (IOException e) {
                            loginFailed();
                        }
                    }
                });

        return loginResponse.userid;
    }


    static class loginResponse {
        int userid;
        String username;
        Boolean logged_in;
    }

}
