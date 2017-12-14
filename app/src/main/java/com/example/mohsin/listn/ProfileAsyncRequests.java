package com.example.mohsin.listn;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Mohsin on 11/22/2017.
 */

class ProfileAsyncRequests {

    ProgressDialog loadingScreen;

    private static final String TAG = "TAB ProfileRequests";

    //Webb is Lightweight Java HTTP-Client for calling JSON REST-Service
    final Webb webb = Webb.create();

    //Server URL to make the request to and from
    String serverURL = "http://10.155.27.245:3000";
    private ProfileTabInterface profileTabInterface;


    public ProfileAsyncRequests(ProfileTabInterface listener) {
        this.profileTabInterface = listener;
        webb.setBaseUri(serverURL);
    }


    @SuppressLint("StaticFieldLeak")
    public void test() {
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
                    Log.d(TAG, "EXCEPTION IS = " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (result != null) {
                    try {
                        if (result.getBoolean("connected")) {

                        } else {

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
    public void changeProfilePic(final String imagePath, final String username) throws Exception {
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
                            profileTabInterface.setprofileImagePath(result.getJSONObject("user"));
                        } else {
                            profileTabInterface.problemSettingProfilePic();
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
    public Bitmap getImage(final String imageURL, final String type) {
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
                if (type.contains("ProfileTab"))
                    profileTabInterface.setProfileTabProfilePic(result);
                else if(type.contains("Settings"))
                    profileTabInterface.setSettingProfilePic(result);
            }
        }.execute();
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    public void uploadAudioPost(final String audioPath, final String headers) throws Exception {
        Log.d(TAG, "About to upload Image");
        new AsyncTask<JSONObject, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(JSONObject... jsonObjects) {
                try {
                    JSONObject result = webb
                            .post("/uploadAudioPost")
                            .body(new File(audioPath))
                            .header("header", headers)
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
                        if (result.getBoolean("posted")) {
                        //    tabActivityInterface.setprofileAudioPath(result);
                            Log.d(TAG, "audiopostpathis is = " + result.getString("audioPostPath"));
                        } else {
                       //     tabActivityInterface.problemSettingProfilePic();
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
    public void downloadAudio(final JSONObject user, final String type)
    {
        try {
            Log.d(TAG,"path is = " + user.getString("profileaudio"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                int count;
                try {
                    URL url = new URL(user.getString("profileaudio"));
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    // this will be useful so that you can show a tipical 0-100% progress bar
                    int lenghtOfFile = conexion.getContentLength();
                    // downlod the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/profileaudio.3gp");
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {}
                return null;
            }

            @Override
            protected void onPostExecute(String result)
            {
                profileTabInterface.gotProfileAudio(type,user);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void downloadAudioArray(final JSONArray postSource, final JSONArray postType)
    {
        final ArrayList<String> audioFileList = new ArrayList<>();
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                int count;
                Log.d(TAG,"about to start getting audio");
                for(int i = 0; i < postSource.length(); i++) {
                    try {
                        Log.d(TAG,"about to start getting audio try posttype =  " + postType.toString());
                        if(postType.getString(i).contains("Audio")) {
                            Log.d(TAG,"about to start getting audio try audiosource ");
                            String filename = postSource.getString(i).substring(postSource.getString(i).length() - 19, postSource.getString(i).length() - 4) + ".3gp";
                            Log.d(TAG,"filename is = " + filename);
                            filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
                            audioFileList.add(filename);
                            try {
                                Log.d(TAG,"url = " + postSource.getString(i));
                                URL url = new URL(postSource.getString(i));
                                URLConnection conexion = url.openConnection();
                                conexion.connect();
                                // this will be useful so that you can show a tipical 0-100% progress bar
                                int lenghtOfFile = conexion.getContentLength();
                                // downlod the file
                                InputStream input = new BufferedInputStream(url.openStream());
                                OutputStream output = new FileOutputStream(filename);
                                byte data[] = new byte[1024];
                                long total = 0;
                                while ((count = input.read(data)) != -1) {
                                    total += count;
                                    // publishing the progress....
                                    output.write(data, 0, count);
                                }
                                output.flush();
                                output.close();
                                input.close();

                            } catch (Exception e) {
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result)
            {
                profileTabInterface.loadListView(audioFileList);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void changeProfileSettings(final JSONObject data)
        {
            new AsyncTask<JSONObject, Void, JSONObject>() {
                @Override
                protected JSONObject doInBackground(JSONObject... params) {
                    try {
                        JSONObject response = webb
                                .post("/changeProfileSettings")
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
                            Log.d(TAG,"settings result is = " + result.toString());
                            profileTabInterface.changeSettings(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        {
                            profileTabInterface.problemChangingSettings();
                    }
                }

            }.execute();
        }

    @SuppressLint("StaticFieldLeak")
    public void setProfileAudio(final String audioPath, final String username) throws Exception {
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
                            downloadAudio(result.getJSONObject("user"),"profile");
                            Log.d(TAG, "profileaudioPath is = " + result.getJSONObject("user").getString("profileaudioPath"));
                        } else {
                            profileTabInterface.problemProfileAudioPath();
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
