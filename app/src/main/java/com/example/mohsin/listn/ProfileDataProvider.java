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
    String postText;
    String type;

    ProfileDataProvider(String username, String data, String date, String type)
    {
        this.type = type;
        if(type.contains("Audio")){
            Log.d(TAG,"added audio");
            this.audioPath = data;
        }
        else if(type.contains("Text"))
        {
            Log.d(TAG,"added text");
            this.postText = data;
        }
        this.username = username;
        this.date = date;
    }


}
