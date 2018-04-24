package com.example.admin.quizzy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by brianmedina on 3/27/18.
 */

public class MenuItemAdapter extends ArrayAdapter<MenuItem>{

    private MenuItem currentItem;
    private Context context;

    public MenuItemAdapter(Context context, ArrayList<MenuItem> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(
                    R.layout.menu_list_item, parent, false);
        }

        // bind everything
        holder = new ViewHolder(v);

        // Get the object located at this position in the list
        currentItem = getItem(position);

        // set name
        holder.titleView.setText(currentItem.getSurveyName());

        // set onclick for edit button
        holder.editButton.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               Intent intent = new Intent(context, CreateSurveyActivity.class);
               intent.putExtra("surveyid", currentItem.getSurveyId());
               intent.putExtra("surveyname", currentItem.getSurveyName());
               context.startActivity(intent);
           }
        });

        // set onclick for delete button

        return v;
    }


    @OnClick(R.id.menuEditButton)
    public void edit(){
    }

    // holds all the views to bind with butterknife
    static class ViewHolder{
        @BindView(R.id.menuItemTitle) TextView titleView;
        @BindView(R.id.menuEditButton) ImageButton editButton;
        @BindView(R.id.menuDeleteButton) ImageButton deleteButton;

        // constructor binds them immediately
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
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
