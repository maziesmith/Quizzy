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
    static final int MAX_RESPONSES = 6;

    public SurveyItem(int numResponses) {
        if(numResponses < 1) {
            _numResponses = 1;
        } else if(numResponses > MAX_RESPONSES) {
            _numResponses = MAX_RESPONSES;
        } else {
            _numResponses = numResponses;
        }
        _question = "";
        _responses = new ArrayList<String>(numResponses);
        for(int i = 0; i < numResponses; i++) {
            _responses.add("");
        }
    }

    public SurveyItem(String question, String[] responses) {
        _responses = new ArrayList<String>();

        if(responses.length < 1) {
            _responses.add("");
        } else {
            for (int i = 0; i < responses.length && i < MAX_RESPONSES; i++) {
                _responses.add(responses[i]);
            }
        }
        _question = question;
        _numResponses = _responses.size();
    }

    public String getQuestion() {
        return _question;
    }

    public ArrayList<String> getResponses() {
        return _responses;
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
