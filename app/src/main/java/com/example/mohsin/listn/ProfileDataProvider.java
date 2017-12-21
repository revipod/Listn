package com.example.mohsin.listn;

import android.util.Log;


/**
 * Created by mabbasi on 12/6/2017.
 */

class ProfileDataProvider {

    private static final String TAG = "ProfileDataProvider";
    String username;
    String date;
    String listns;
    String audioPath;

    ProfileDataProvider(String username, String audioPath,String date)
    {
        this.username = username;
        this.audioPath = audioPath;
        this.date = date;
    }


}
