package com.example.admin.quizzy;

import java.util.ArrayList;

/**
 * Created by Admin on 3/21/2018.
 *
 * Model to represent one item in a survey - i.e. a question and its associated answers
 */

public class SurveyItem {

    private String _question;
    private int _questionId;
    private int _numResponses;  // Must guarantee this to be 1 at the minimum
    private ArrayList<String> _responses;
    private int[] _responseIds;
    static public final int MAX_RESPONSES = 6;
    static public final int DEFAULT_ID = -1;

    public SurveyItem(int numResponses) {
        if(numResponses < 1) {
            _numResponses = 1;
        } else if(numResponses > MAX_RESPONSES) {
            _numResponses = MAX_RESPONSES;
        } else {
            _numResponses = numResponses;
        }
        _question = "";
        _questionId = DEFAULT_ID;
        _responses = new ArrayList<String>(_numResponses);
        _responseIds = new int[_numResponses];
        for(int i = 0; i < _numResponses; i++) {
            _responses.add("");
            _responseIds[i] = DEFAULT_ID;
        }
    }

    public SurveyItem(String question, String[] responses, int questionId, int[] responseIds) throws IllegalArgumentException{

        if(responses.length != responseIds.length) {
            throw new IllegalArgumentException("SurveyItem: Array arguments must contain the same number of items");
        }

        _responses = new ArrayList<String>();

        if(responses.length < 1) {
            _responses.add("");
        } else {
            for (int i = 0; i < responses.length && i < MAX_RESPONSES; i++) {
                _responses.add(responses[i]);
            }
        }
        _question = question;
        _questionId = questionId;
        _numResponses = _responses.size();

        _responseIds = new int[_numResponses];
        for (int i = 0; i < _responseIds.length; i++) {
            _responseIds[i] = responseIds[i];
        }
    }

    public int getQuestionId() { return _questionId; }

    public int getNumResponses() { return  _numResponses; }

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

    public String toJson() {
        String jsonString = "{";

        if (_questionId != DEFAULT_ID) {  // omit id field if default value
            jsonString += "\"id\": " + _questionId + ",";
        }
        jsonString += "\"text\": " + "\"" + _question + "\",";
        jsonString += "\"answers\": [";
        for (int i = 0; i < _responses.size(); i++) {
            jsonString += "{";

            if (_responseIds[i] != DEFAULT_ID) {  // omit id field if default value
                jsonString += "\"id\": " + _responseIds[i] + ",";
            }
            if (_questionId != DEFAULT_ID) {
                jsonString += "\"questiontextid\": " + _questionId + ",";
            }
            jsonString += "\"text\": " + "\"" + _responses.get(i) + "\"";

            jsonString += "}";
            if(i < (_responses.size() - 1)) {
                jsonString += ",";
            }
        }
        jsonString += "]";

        jsonString += "}";
        return jsonString;
    }

    // Only meant to be called in TakeSurveyActivity, where all questions and
    //   responses have valid IDs
    public String toJsonForSubmit(int responseIndex, int userId, int quizId) {

        String responseText = _responses.get(responseIndex);
        int responseId = _responseIds[responseIndex];

        String jsonString = "{";

        jsonString += "\"id\": " + _questionId + ",";
        jsonString += "\"text\": " + "\"" + _question + "\",";
        jsonString += "\"quizid\": " + quizId + ",";
        jsonString += "\"answers\": [";

        jsonString += "{";
        jsonString += "\"id\": " + responseId + ",";
        jsonString += "\"text\": " + "\"" + responseText + "\",";
        jsonString += "\"user\": " + userId + ",";
        jsonString += "\"questiontextid\": " + _questionId;
        jsonString += "}";

        jsonString += "]";
        jsonString += "}";
        return jsonString;
    }
}
