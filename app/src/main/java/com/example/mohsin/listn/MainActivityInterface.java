package com.example.mohsin.listn;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mohsin on 11/22/2017.
 */

interface MainActivityInterface
{
        void loginUserI(JSONObject result) throws JSONException;

        void getNameI(Boolean found, final String username);

        void setNewUserI(final JSONObject result) throws JSONException;

        void setprofileImagePath(JSONObject profilepicPath) throws JSONException;

        void problemSettingProfilePic();

        void profileCreated(JSONObject result);

        void setMainActivityProfilePicBitmap(Bitmap result);
}
