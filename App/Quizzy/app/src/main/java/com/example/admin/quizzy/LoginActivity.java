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

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created by probu on 4/8/2018.
 */

// Adapted from: https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/

public class LoginActivity extends AppCompatActivity {
    // flag to see if we need to signup
    private static final int SIGNUP = 0;

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

        // TODO: skip login if already logged in



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
        String email = _usernameView.getText().toString();
        // get password string
        String password = _passwordView.getText().toString();

        // TODO: authenticate email and password

        // write email and password to shared pref
        SharedPreferences pref = getApplicationContext().getSharedPreferences("quizzy.pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", email);
        editor.putBoolean("logged_in", true);
        editor.apply();

        // create handler to delay login by 3 seconds, because that's what logins do I guess
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // On complete call either loginSuccess or loginFailed
                loginSuccess();
                // loginFailed();
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

    // login successful - reenable login button and finish
    public void loginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // login failed - reenable login button and show toast for fail
    public void loginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
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
    // criteria:
    // non-empty
    // 6 >= password >= 30
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
        if (checkUsername() && checkPassword()){
            return true;
        } else {
            return false;
        }
    }
}
