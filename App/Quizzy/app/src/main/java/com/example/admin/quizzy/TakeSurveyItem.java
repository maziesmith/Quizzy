package com.example.admin.quizzy;

public class TakeSurveyItem {

    private SurveyItem item;
    private int selectedResponseId;
    private int selectedResponseIndex;

    public TakeSurveyItem(SurveyItem item, int responseButtonId) {
        this.item = item;
        selectedResponseId = responseButtonId;
        selectedResponseIndex = 0;
    }

    public SurveyItem getSurveyItem() {
        return item;
    }

    public int getSelectedId() {
        return selectedResponseId;
    }

    public void setSelectedId(int id) {
        selectedResponseId = id;
    }

    public int getSelectedResponseIndex() {
        return selectedResponseIndex;
    }

    public void setSelectedResponseIndex(int index) {
        selectedResponseIndex = index;
    }

}
