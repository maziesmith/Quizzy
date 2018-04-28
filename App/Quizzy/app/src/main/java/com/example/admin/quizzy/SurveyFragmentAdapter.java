package com.example.admin.quizzy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SurveyFragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;
    public SurveyFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1) {
            return new PublishedSurveysFragment();
        } else {
            return new MySurveysFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 1) {
            return mContext.getString(R.string.published_surveys);
        } else {
            return mContext.getString(R.string.my_surveys);
        }
    }
}
