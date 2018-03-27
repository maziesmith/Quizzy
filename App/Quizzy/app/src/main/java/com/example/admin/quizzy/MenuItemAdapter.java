package com.example.admin.quizzy;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by brianmedina on 3/27/18.
 */

public class MenuItemAdapter extends ArrayAdapter<MenuItem>{

    public MenuItemAdapter(Context context, ArrayList<MenuItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.menu_list_item, parent, false);
        }

        // Get the {@link Song} object located at this position in the list
        MenuItem currentItem = getItem(position);

        // Handle song name
        TextView mmTextView = (TextView) listItemView.findViewById(R.id.menuItemTitle);
        mmTextView.setText(currentItem.getSurveyId());

        // Handle edit button
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);

        //handle delete button

        return listItemView;
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
