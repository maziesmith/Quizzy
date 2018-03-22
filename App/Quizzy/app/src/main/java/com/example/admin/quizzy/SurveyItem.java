package com.example.admin.quizzy;

import java.util.ArrayList;

/**
 * Created by Admin on 3/21/2018.
 *
 * Model to represent one item in a survey - i.e. a question and its associated answers
 */

public class SurveyItem {

    String _question;
    int _numResponses;
    ArrayList<String> _responses;

    public SurveyItem(int numResponses) {
        _numResponses = numResponses;
        _question = "";
        _responses = new ArrayList<>(numResponses);
        for(int i = 0; i < numResponses; i++) {
            _responses.add("");
        }
    }

    public SurveyItem(String question, ArrayList<String> responses) {
        _question = question;
        _responses = responses;
        _numResponses = _responses.size();
    }

    public void setQuestion(String question) {
        _question = question;
    }

    public boolean setResponse(int index, String value) {
        if(index < 0 || index >= _numResponses) {
            return false;
        } else {
            _responses.set(index, value);
            return true;
        }
    }
}
