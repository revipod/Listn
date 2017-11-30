package com.example.mohsin.listn;

import android.Manifest;
import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by Mohsin on 11/22/2017.
 */

public interface TabActivityInterface {

    public void setprofileImagePath(String profilepicPath);
    public void problemSettingProfilePic();
    public void setTabActivityProfilePicBitmap(Bitmap result);
    public void setprofileAudioPath(JSONObject profileaudioPath);
}
