package com.example.mohsin.listn;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mohsin on 11/22/2017.
 */

public interface MainActivityInterface
{
        public void loginUserI(JSONObject result) throws JSONException;

        public void getNameI(Boolean found, final String username);

        public void setNewUserI(final JSONObject result) throws JSONException;
}
