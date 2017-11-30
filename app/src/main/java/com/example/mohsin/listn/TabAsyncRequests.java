package com.example.mohsin.listn;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Webb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mohsin on 11/22/2017.
 */

class TabAsyncRequests {

    private static final String TAG = "TAB TabAsyncRequests";
    boolean foundUsername;

    //Webb is Lightweight Java HTTP-Client for calling JSON REST-Service
    final Webb webb = Webb.create();

    //Server URL to make the request to and from
    String serverURL = "http://10.155.27.245:3000";
    private TabActivityInterface listener;

    public TabAsyncRequests(TabActivityInterface listener)
    {
        this.listener = listener;
        webb.setBaseUri(serverURL);
    }


    @SuppressLint("StaticFieldLeak")
    public void test()
    {
        final JSONObject test = new JSONObject();
        new AsyncTask<JSONObject, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(JSONObject... params) {
                try {
                    JSONObject response = webb
                            .post("/test")
                            .body(test)
                            .connectTimeout(10 * 1000)
                            .asJsonObject ()
                            .getBody();
                    return response;
                } catch (Exception e) {
                    Log.d(TAG,"EXCEPTION IS = " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                if(result != null)
                {
                    try {
                        if (result.getBoolean("connected")){

                        }
                        else
                        {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }

        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void changeProfilePic(final String imagePath, final String username) throws Exception {
        Log.d(TAG,"About to upload Image");
        new AsyncTask<JSONObject, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(JSONObject... jsonObjects) {
                try {
                    JSONObject result = webb
                            .post("/setProfilePic")
                            .body(new File(imagePath))
                            .header("username",username)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
                    Log.d(TAG, "RESULT: " + result);
                    return result;
                } catch (Exception e) {
                    Log.d(TAG,"EXCEPTION IS = " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                if(result != null)
                {
                    try {
                        if (result.getBoolean("uploaded")){
                            listener.setprofileImagePath(result.getString("profilepicPath"));
                            Log.d(TAG,"profilePicPath is = " + result.getString("profilepicPath"));
                        }
                        else
                        {
                            listener.problemSettingProfilePic();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public Bitmap getImage(final String imageURL)
    {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                URL url;
                HttpURLConnection connection;
                InputStream input;
                Bitmap myBitmap;
                try {
                    url = new URL((imageURL));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    // Log exception
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                //Setting the signature only if it was found in the object
                listener.setProfilePicBitmap(result);
            }
        }.execute();
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    public void setProfileAudio(final String audioPath, final String username) throws Exception {
        Log.d(TAG,"About to upload Image");
        new AsyncTask<JSONObject, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(JSONObject... jsonObjects) {
                try {
                    JSONObject result = webb
                            .post("/setProfileAudio")
                            .body(new File(audioPath))
                            .header("username",username)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
                    Log.d(TAG, "RESULT: " + result);
                    return result;
                } catch (Exception e) {
                    Log.d(TAG,"EXCEPTION IS = " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                if(result != null)
                {
                    try {
                        if (result.getBoolean("uploaded")){
                            listener.setprofileAudioPath(result);
                            Log.d(TAG,"profileaudioPath is = " + result.getString("profileaudioPath"));
                        }
                        else
                        {
                            listener.problemSettingProfilePic();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }
        }.execute();
    }
}