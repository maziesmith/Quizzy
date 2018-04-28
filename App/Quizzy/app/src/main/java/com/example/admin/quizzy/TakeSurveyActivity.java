package com.example.admin.quizzy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import okhttp3.OkHttpClient;

public class TakeSurveyActivity extends AppCompatActivity{
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
