package com.example.mohsin.listn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Webb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mohsin on 11/19/2017.
 */

public class MainAsyncRequests {

    private static final String TAG = "MAIN MainAsyncRequests";
    boolean foundUsername;

    //Webb is Lightweight Java HTTP-Client for calling JSON REST-Service
    final Webb webb = Webb.create();

    //Server URL to make the request to and from
    String serverURL = "http://10.155.27.245:3000";
    //String serverURL = "https://pptis-revipod.c9users.io";
    private MainActivityInterface listener;

    public MainAsyncRequests(MainActivityInterface listener)
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
                            .asJsonObject()
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
    public void login(final JSONObject data)
    {
        new AsyncTask<JSONObject, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(JSONObject... params) {
                try {
                    JSONObject response = webb
                            .post("/login")
                            .body(data)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
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
                    JSONObject response = webb
                            .post("/checkUsername")
                            .body(data)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
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
                    JSONObject response = webb
                            .post("/checkEmail")
                            .body(data)
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
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



}
