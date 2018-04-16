package com.example.admin.quizzy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.BindView;

// Adapted from: https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/

public class SignupActivity extends AppCompatActivity {
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
        String name = _nameView.getText().toString();
        String email = _emailView.getText().toString();
        String password = _passwordView.getText().toString();

        // handler for 3 sec delay
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        signupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void signupSuccess() {
        // enable button again
        _signupButton.setEnabled(true);
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
        if (checkName() && checkEmail() && checkPassword()){
            return true;
        } else {
            return false;
        }
    }
}
