package com.example.admin.quizzy;

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
                this.finish();
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
        finish();
    }

    // login failed - reenable login button and show toast for fail
    public void loginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    // TODO: check criteria
    // check to see if input is valid
    // criteria:
    // username is email
    // username not empty
    // password not empty
    // password over 6 chars ??
    // password under 30 chars ??
    public boolean checkForValidInput() {
        boolean valid = true;

        // get string for username
        String email = _usernameView.getText().toString();
        // get string for password
        String password = _passwordView.getText().toString();

        // check username
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _usernameView.setError("enter a valid email address");
            valid = false;
        } else {
            _usernameView.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 30) {
            _passwordView.setError("between 6 and 30 alphanumeric characters");
            valid = false;
        } else {
            _passwordView.setError(null);
        }

        return valid;
    }
}
