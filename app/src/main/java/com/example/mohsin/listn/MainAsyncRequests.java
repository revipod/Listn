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
import java.util.concurrent.ExecutionException;

/**
 * Created by Mohsin on 11/19/2017.
 */

class MainAsyncRequests {

    private static final String TAG = "MAIN MainAsyncRequests";
    private boolean foundUsername;

    //Webb is Lightweight Java HTTP-Client for calling JSON REST-Service
    private final Webb webb = Webb.create();

    //String serverURL = "https://pptis-revipod.c9users.io";
    private final MainActivityInterface listener;

    public MainAsyncRequests(MainActivityInterface listener)
    {
        this.listener = listener;
        String serverURL = "http://10.155.27.245:3000";
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
                    return webb
                            .post("/test")
                            .body(test)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
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
    public void login(final JSONObject data)
    {
        new AsyncTask<JSONObject, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(JSONObject... params) {
                try {
                    return webb
                            .post("/login")
                            .body(data)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
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
                        if (result.getBoolean("loggedin")){
                            Log.d(TAG,"result = " + result.toString());
                            listener.loginUserI(result);
                        }
                        else
                        {
                            listener.loginUserI(result);
                            Log.d(TAG,"result = " + result.toString());
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

    public void checkUsername(final JSONObject data) throws ExecutionException, InterruptedException {
        foundUsername = false;
        new AsyncTask<JSONObject, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(JSONObject... params) {
                try {
                    return webb
                            .post("/checkUsername")
                            .body(data)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
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
                        if (result.getBoolean("found")){
                            listener.getNameI(true,"");
                            Log.d(TAG,"result = " + result.toString());

                        }
                        else
                        {
                            Log.d(TAG,"result = " + result.toString());
                            listener.getNameI(false,result.getString("username"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }

        }.execute().get();
    }

    @SuppressLint("StaticFieldLeak")

    public void checkEmail(final JSONObject data) throws ExecutionException, InterruptedException {
        foundUsername = false;
        new AsyncTask<JSONObject, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(JSONObject... params) {
                try {
                    return webb
                            .post("/checkEmail")
                            .body(data)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
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
                        if (result.getBoolean("found")){
                            Log.d(TAG,"result = " + result.toString());
                            listener.setNewUserI(result);
                        }
                        else
                        {
                            Log.d(TAG,"result = " + result.toString());
                            listener.setNewUserI(result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }

        }.execute().get();
    }


    @SuppressLint("StaticFieldLeak")
    public void changeProfilePic(final String imagePath, final String username) {
        Log.d(TAG, "About to upload Image");
        new AsyncTask<JSONObject, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(JSONObject... jsonObjects) {
                try {
                    JSONObject result = webb
                            .post("/setProfilePic")
                            .body(new File(imagePath))
                            .header("username", username)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
                    Log.d(TAG, "RESULT: " + result);
                    return result;
                } catch (Exception e) {
                    Log.d(TAG, "EXCEPTION IS = " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (result != null) {
                    try {
                        if (result.getBoolean("uploaded")) {
                            listener.setprofileImagePath(result.getJSONObject("user"));
                            Log.d(TAG, "profilePicPath is = " + result.getJSONObject("user").getString("profilepic"));
                        } else {
                            listener.problemSettingProfilePic();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getImage(final String imageURL) {
        Log.d(TAG, "imgurl = " + imageURL);
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
                    listener.setMainActivityProfilePicBitmap(result);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void setProfileAudio(final String audioPath, final String username) {
        Log.d(TAG, "About to upload Image");
        new AsyncTask<JSONObject, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(JSONObject... jsonObjects) {
                try {
                    JSONObject result = webb
                            .post("/setProfileAudio")
                            .body(new File(audioPath))
                            .header("username", username)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
                    Log.d(TAG, "RESULT: " + result);
                    return result;
                } catch (Exception e) {
                    Log.d(TAG, "EXCEPTION IS = " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (result != null) {
                    try {
                        if (result.getBoolean("uploaded")) {
                            listener.profileCreated(result.getJSONObject("user"));
                            Log.d(TAG, "profileaudioPath is = " + result.getJSONObject("user").getString("profileaudioPath"));
                        } else {
                            listener.problemSettingProfilePic();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }.execute();
    }



}
