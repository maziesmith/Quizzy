package com.example.admin.quizzy;


/**
 * Created by brianmedina on 3/27/18.
 */

public class MenuItem {
    private int id;
    private String quizname;
    private int userid;
    private Boolean published;

    public MenuItem(String quizname, int surveyid, int userid, Boolean published) {
        this.quizname = quizname;
        this.id = surveyid;
        this.userid = userid;
        this.published = published;
    }

    public String getSurveyName(){
        return quizname;
    }

    public int getSurveyId() {
        return id;
    }

    public int getUserId(){
        return userid;
    }

    public Boolean isPublished() {
        return published;
    }


}

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
