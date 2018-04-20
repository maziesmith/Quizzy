package com.example.admin.quizzy;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Adapted from: https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/

public class SignupActivity extends AppCompatActivity {
    // handle for name and email
    private String name, email;

    // for okhttp3 requests
    private final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    // bind views
    @BindView(R.id.input_name) EditText _nameView;
    @BindView(R.id.input_email) EditText _emailView;
    @BindView(R.id.input_password) EditText _passwordView;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        // onclick for signup button signs us up
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // login link finishes signup and goes back to login
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void signup() {

        // signup fails if input is not valid
        if (!checkForValidInput()) {
            signupFailed();
            return;
        }

        // disable signup button after it's pressed
        _signupButton.setEnabled(false);

        // very good process dialog for signing up
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // get name, email, pwd from edittexts
        name = _nameView.getText().toString();
        email = _emailView.getText().toString();
        final String password = _passwordView.getText().toString();

        // handler for 3 sec delay
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        signup_request(email, password);
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void signupSuccess() {
        // enable button again
        _signupButton.setEnabled(true);

        // shared prefs for signup
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putBoolean("logged_in", true);
        editor.apply();

        // flag to tell if signup was successful
        setResult(RESULT_OK, null);
        finish();
    }

    public void signupFailed() {
        // fail toast
        Toast.makeText(getBaseContext(), "log in failed", Toast.LENGTH_LONG).show();
        // enable button again
        _signupButton.setEnabled(true);
    }

    // check if name is valid before sending request
    public boolean checkName(){
        String name = _nameView.getText().toString();
        if (name.isEmpty() || name.length() < 3) {
            _nameView.setError("at least 3 characters");
            return false;
        } else {
            _nameView.setError(null);
            return true;
        }
    }

    // check if email is valid before sending request
    public boolean checkEmail(){
        String email = _emailView.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailView.setError("enter a valid email address");
            return false;
        } else {
            _emailView.setError(null);
            return true;
        }
    }

    // check if password is valid before sending request
    public boolean checkPassword(){
        String password = _passwordView.getText().toString();

        if (password.isEmpty() || password.length() < 6 || password.length() > 30) {
            _passwordView.setError("between 4 and 10 alphanumeric characters");
            return false;
        } else {
            _passwordView.setError(null);
            return true;
        }
    }

    // check input for signing up
    public boolean checkForValidInput() {
        return (checkName() && checkEmail() && checkPassword());
    }

    // makes a request using okhttp3
    // if the call is successful, return true, else false
    public void signup_request(String username, String password) {
        // this is how the string will be parsed later
        MediaType mediaType = MediaType.parse("application/json");
        /*
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // main thread stuff here
                                signupFailed();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        // we got a response but we need to check if it's the one we want
                        try {
                            // turn our result into a string
                            String res = response.body().string();
                            // use moshi to turn it into an object for easy access
                            JsonAdapter<signupResponse> jsonAdapter = moshi.adapter(signupResponse.class);
                            // throws JsonDataException if it doesn't fit in signupResponse class
                            signupResponse s = jsonAdapter.fromJson(res);
                            // if we get here signup is successful
                            signupSuccess();
                        } catch(JsonDataException e){
                            // data doesn't match the proper response format
                            signupFailed();
                        }
                    }
                });
        */
    }

    static class signupResponse {
        public Boolean logged_in;
        public String username;
        public String id;

    }
}


