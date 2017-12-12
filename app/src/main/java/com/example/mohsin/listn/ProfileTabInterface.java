package com.example.mohsin.listn;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mabbasi on 11/30/2017.
 */

public interface ProfileTabInterface
{
    void setProfileTabProfilePic(Bitmap result);
    void gotProfileAudio(String profile);
    void loadListView(ArrayList<String> audioFileList);
    void setprofileImagePath(JSONObject user);
    void problemSettingProfilePic();
    void setSettingProfilePic(Bitmap result);
    void changeSettings(JSONObject result) throws JSONException;
    void problemChangingSettings();
    void setProfileAudioPath(JSONObject user);
    void problemProfileAudioPath();
}
