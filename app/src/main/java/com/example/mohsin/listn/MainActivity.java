package com.example.mohsin.listn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private Button loginBTN;
    private Button registerBTN;
    private EditText usernameET;
    private EditText passwordET;

    private TextView timerTV;
    private TextView currentTimeTV;
    private TextView additionalinfoTV;

    private MainAsyncRequests requests;
    private DialogBox dialogBox;

    private SeekBar seekBar;

    private ProgressBar loadingPB;

    private Dialog newUserDialog;
    private Dialog additionalregistermenuDialog;

    private Dialog firsttimeCameraMenu;
    private Dialog firsttimeAudioMenu;
    private Dialog recordAudioMenu;
    private Dialog playAudioMenu;

    private Handler handler;

    private RecordAudio audioRecorder;
    private MediaPlayer mediaPlayer;

    private Bitmap profilePic;

    private static final int INITIAL_REQUEST = 1337;
    private static final int WRITE_EXTERNAL_REQUEST = INITIAL_REQUEST + 3;
    static final int ACCESS_COARSE_LOCATION = INITIAL_REQUEST + 4;
    static final int ACCESS_WIFI = INITIAL_REQUEST + 5;
    private static final int ACCESS_INTERNET = INITIAL_REQUEST + 6;
    private static final int ACCESS_MICROPHONE = INITIAL_REQUEST + 7;
    private final int CAMERA = 1;

    private long MillisecondTime;
    private long StartTime;
    private long TimeBuff;
    private long UpdateTime = 0L ;

    private int Seconds;
    private int Minutes;
    private int MilliSeconds ;

    private double durationseconds;

    private static final String[] WRITE_EXTERNAL_PREMS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static final String[] ACCESS_WIFI_PREMS = {
            Manifest.permission.ACCESS_WIFI_STATE
    };

    private static final String[] ACCESS_INTERNET_PREMS = {
            Manifest.permission.INTERNET
    };

    private static final String[] ACCESS_MICROPHONE_PREMS = {
            Manifest.permission.RECORD_AUDIO
    };

    private String profileAudioPath = "blank";
    private String profilePicPath = "blank";
    private final String usernameregexp = "^[a-zA-Z0-9._-]{5,20}$";
    private final String passwordregex = "^[a-zA-Z0-9._!@#$%^&+=-]{5,20}$";
    private final String fullnameregexp = "^[\\p{L} .'-]{2,25}+$";
    private String email;
    private String password;
    private String fullname;

    private JSONObject userData;
    private JSONObject postData;

    private ImageView playIV;
    private ImageView pauseIV;

    private RelativeLayout rootView;

    private final Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timerTV.setText(String.format("%02d", Seconds) + ":"
                    + String.format("%02d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginBTN = (Button) findViewById(R.id.loginBTN);
        registerBTN = (Button) findViewById(R.id.registerBTN);
        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        View centerView = (View) findViewById(R.id.centerdot);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        loadingPB = (ProgressBar) findViewById(R.id.progressbar);
        MainActivityInterface mainActivityInterface = new MainActivityInterface() {
            @Override
            public void loginUserI(JSONObject result) throws JSONException {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.GONE);
                if (result.getBoolean("loggedin")) {
                    userData = result.getJSONObject("user");
                    postData = result.getJSONObject("posts");
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    intent.putExtra("User", userData.toString());
                    intent.putExtra("postData", postData.toString());
                    if (postData != null) intent.putExtra("postData", postData.toString());
                    intent.putExtra("loginorRegister", "login");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    dialogBox.createDialog("Not found", "Either the username of password you inserted is in correct. Please try again.", "bad");
                }

            }

            @Override
            public void getNameI(Boolean found, String username) {
                getName(found, username);
            }

            @Override
            public void setNewUserI(JSONObject result) throws JSONException {
                setNewUser(result);
            }

            @Override
            public void setprofileImagePath(JSONObject user) throws JSONException {

                userData = user;
                profilePicPath = user.getString("profilepic");
                if (profilePicPath.contains("NoPicture")) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingPB.setVisibility(View.GONE);
                    profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.noprofilepic);
                } else {
                    requests.getImage(profilePicPath);
                }
                firsttimeAudioMenu.show();
                firsttimeCameraMenu.dismiss();
            }

            @Override
            public void problemSettingProfilePic() {

            }

            @Override
            public void profileCreated(JSONObject result) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.GONE);
                firsttimeAudioMenu.dismiss();
                recordAudioMenu.dismiss();
                playAudioMenu.dismiss();
                userData = result;
                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                intent.putExtra("User", userData.toString());
                intent.putExtra("postData", postData.toString());
                intent.putExtra("loginorRegister", "login");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void setMainActivityProfilePicBitmap(Bitmap result) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.GONE);
                profilePic = result;
            }
        };
        requests = new MainAsyncRequests(mainActivityInterface);
        dialogBox = new DialogBox(this);
        profileAudioPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/profileaudio.3gp");
        callClickListeners();





    }

    private void callClickListeners()
    {

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    registerBTN.setVisibility(View.GONE);
                }
                else {
                    // keyboard is closed
                    registerBTN.setVisibility(View.VISIBLE);
                }
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCameraPermission() && checkMicPermission()) {
                    try {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        loadingPB.setVisibility(View.VISIBLE);
                        checkUser(usernameET.getText().toString(), passwordET.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayUserNameMenu();
            }
        });
    }


    private void setNewUser(final JSONObject result) throws JSONException {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingPB.setVisibility(View.GONE);
        if (result.getBoolean("found"))
        {
            dialogBox.createDialog("Email exists","Sorry but a user with this email already exists. Please try again or reset your password.","bad");
        }
        else
        {
            postData = result.getJSONObject("postData");
            userData = result.getJSONObject("user");
            additionalregistermenuDialog.dismiss();
            loadFirstUser();
            loadFirstAudio();
        }
    }


    private void displayUserNameMenu() {
        newUserDialog = new Dialog(this, R.style.CustomDialog);
        newUserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newUserDialog.setCancelable(true);
        newUserDialog.setContentView(R.layout.username_menu);
        newUserDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        newUserDialog.show();

        Button continueBTN = (Button) newUserDialog.findViewById(R.id.continueBTN);
        final EditText usernameET = (EditText) newUserDialog.findViewById(R.id.usernameET);
        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameET.length() < 1)
                {
                    dialogBox.createDialog("Empty Username","Please insert a username","bad");
                }
                else
                {
                    if(usernameET.getText().toString().matches(usernameregexp)) {
                        JSONObject usernameParams = new JSONObject();
                        try {
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            loadingPB.setVisibility(View.VISIBLE);
                            usernameParams.put("username", usernameET.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            requests.checkUsername(usernameParams);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        dialogBox.createDialog("Invalid Username","Please make sure your username is at least 5 characters, no spaces and contains only the following values a-z, A-Z, 0-9, period, dash and underscores.","bad");
                    }
                }
            }
        });
    }

    private void getName(Boolean found, final String username){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingPB.setVisibility(View.GONE);
        if(found)
        {
            dialogBox.createDialog("Username exists","Sorry but a user with this username already exists. Please try again.","bad");
        }
        else
        {
            additionalregistermenuDialog = new Dialog(this, R.style.DialogStyle);
            additionalregistermenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            additionalregistermenuDialog.setCancelable(true);
            additionalregistermenuDialog.setContentView(R.layout.additionalregistermenu);
            additionalregistermenuDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            additionalregistermenuDialog.show();
            dialogBox.createDialog("Sucess!","Welcome to Listn " + username + "! Let's get you started!","good");
            newUserDialog.dismiss();

            Button nextBTN = (Button) additionalregistermenuDialog.findViewById(R.id.nextBTN);
            additionalinfoTV = additionalregistermenuDialog.findViewById(R.id.additionalinfoTV);
            final EditText emailET = (EditText) additionalregistermenuDialog.findViewById(R.id.emailET);
            final EditText fullnameET = (EditText) additionalregistermenuDialog.findViewById(R.id.fullnameET);
            final EditText regpasswordET = (EditText) additionalregistermenuDialog.findViewById(R.id.passwordET);
            final ImageView backIV = (ImageView) additionalregistermenuDialog.findViewById(R.id.backIV);

            fullnameET.requestFocus();


            nextBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fullnameET.getVisibility() == View.VISIBLE)
                    {
                            if(fullnameET.getText().toString().matches(fullnameregexp)) {
                                fullname = fullnameET.getText().toString().trim();
                                fullnameET.setVisibility(View.GONE);
                                regpasswordET.setVisibility(View.VISIBLE);
                                additionalinfoTV.setText("Password");
                                regpasswordET.requestFocus();
                            }
                            else
                            {
                                dialogBox.createDialog("Invalid Name","Please make sure you've inserted a valid Name.","bad");
                            }
                    }
                    else if(regpasswordET.getVisibility() == View.VISIBLE)
                    {
                            if(regpasswordET.getText().toString().matches(passwordregex))
                            {
                            password = regpasswordET.getText().toString().trim();
                            regpasswordET.setVisibility(View.GONE);
                            emailET.setVisibility(View.VISIBLE);
                            additionalinfoTV.setText("Email");
                            emailET.requestFocus();
                        }
                        else
                            {
                                dialogBox.createDialog("Invalid password","Please make sure your password is between 5-20 characters and includes only letters from A-Z or numbers from 0-9 and no spaces. Only the following special characters are allowed ._!@#$%^&+=- .","bad");
                            }
                    }
                    else if(emailET.getVisibility() == View.VISIBLE)
                    {
                        if(emailET.length() < 5 || !emailET.getText().toString().contains("@"))
                        {
                            dialogBox.createDialog("Email", "Please make sure you've inserted a valid email address.", "bad");
                        }
                        else
                        {
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            loadingPB.setVisibility(View.VISIBLE);
                            email = emailET.getText().toString();
                            JSONObject nameandemailParams = new JSONObject();
                            try {
                                nameandemailParams.put("username",username);
                                nameandemailParams.put("fullname", fullname);
                                nameandemailParams.put("password", password);
                                nameandemailParams.put("email", email);
                                requests.checkEmail(nameandemailParams);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            backIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fullnameET.getVisibility() == View.VISIBLE)
                    {
                        additionalregistermenuDialog.dismiss();
                    }
                    else if (regpasswordET.getVisibility() == View.VISIBLE)
                    {
                        fullnameET.requestFocus();
                        additionalinfoTV.setText("Name");
                        fullnameET.setVisibility(View.VISIBLE);
                        regpasswordET.setVisibility(View.GONE);
                    }
                    else if(emailET.getVisibility() == View.VISIBLE)
                    {
                        regpasswordET.requestFocus();
                        additionalinfoTV.setText("Password");
                        emailET.setVisibility(View.INVISIBLE);
                        regpasswordET.setVisibility(View.VISIBLE);
                    }
                }
            });






        }


    }



    private void loadFirstUser() throws JSONException {
        firsttimeCameraMenu = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        firsttimeCameraMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        firsttimeCameraMenu.setCancelable(false);
        firsttimeCameraMenu.setContentView(R.layout.firsttime_cameramenu);
        firsttimeCameraMenu.show();
        dialogBox.createDialog("Sucess!","Welcome to Listn " + userData.get("username") + ". You have sucessfully created a Listn Account! Let's get your profile started!","good");
        Button addPhotoBTN = (Button) firsttimeCameraMenu.findViewById(R.id.addphotoBTN);
        TextView skipPhotoTV = (TextView) firsttimeCameraMenu.findViewById(R.id.skipPhotoTV);
        addPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(checkCameraPermission())
                {
                    setProfilePic();
                }
            }
        });
        skipPhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCameraPermission()) {
                    firsttimeAudioMenu.show();
                    firsttimeCameraMenu.dismiss();
                }
            }
        });
    }



    private void setProfilePic() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loadingPB.setVisibility(View.VISIBLE);
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = saveImage(thumbnail);
            thumbnail.recycle();
                try {
                    Log.d(TAG,"calling async upload");
                    requests.changeProfilePic(path,userData.getString("username"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String IMAGE_DIRECTORY = "/demonuts_upload_camera";
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            Log.d(TAG,"file f = " + f.getAbsoluteFile());
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d(TAG, "File Saved::--->" + f.getAbsolutePath());
            myBitmap.recycle();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void loadFirstAudio() {
        firsttimeAudioMenu = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        firsttimeAudioMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        firsttimeAudioMenu.setCancelable(false);
        firsttimeAudioMenu.setContentView(R.layout.firsttime_profileaudio);
        Button recordaudioBTN = firsttimeAudioMenu.findViewById(R.id.recordaudioBTN);
        TextView skipAudioTV = firsttimeAudioMenu.findViewById(R.id.skipAudioTV);
        recordaudioBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "in record on click listner");
                if (checkMicPermission()) {
                    showRecordAudioMenu();
                    firsttimeAudioMenu.hide();
                }
            }
        });
        skipAudioTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMicPermission()) {
                    firsttimeAudioMenu.dismiss();
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    intent.putExtra("User", userData.toString());
                    intent.putExtra("postData", postData.toString());
                    intent.putExtra("loginorRegister", "login");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    @SuppressLint("ClickableViewAccessibility")
    private void showRecordAudioMenu()
    {

        recordAudioMenu = new Dialog(this, R.style.CustomDialog);
        recordAudioMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        recordAudioMenu.setCancelable(true);
        recordAudioMenu.setContentView(R.layout.recordaudio_menu);
        recordAudioMenu.show();
        handler = new Handler() ;

        ImageView profileIV = recordAudioMenu.findViewById(R.id.profileIV);
        timerTV = recordAudioMenu.findViewById(R.id.timerTV);
        final ImageView recordaudioIV = recordAudioMenu.findViewById(R.id.recordaudioIV);
        final ImageView stopaudioIV = recordAudioMenu.findViewById(R.id.recordstopIV);
        final ImageView recordingcircle = recordAudioMenu.findViewById(R.id.recordingcircleIV);
        final ImageView closeIV = recordAudioMenu.findViewById(R.id.closeIV);

        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;

        timerTV.setText("00:00");

        profileIV.setImageBitmap(profilePic);

        final Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeanimation);
        fadeInAnimation.setRepeatMode(Animation.REVERSE);



        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firsttimeAudioMenu.show();
                recordAudioMenu.dismiss();
                timerTV.setText("00:00");
            }
        });

        recordaudioIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    audioRecorder = new RecordAudio(profileAudioPath);
                try {
                    audioRecorder.startRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timerTV.setText("00:00");
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                recordingcircle.setAnimation(fadeInAnimation);
                stopaudioIV.setVisibility(View.VISIBLE);
                recordaudioIV.setVisibility(View.INVISIBLE);
                closeIV.setVisibility(View.INVISIBLE);
            }
        });

        stopaudioIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"seconds = " + Seconds);
                audioRecorder.stopRecord();
                if(Seconds >= 1) {
                    timerTV.setText("00:00");
                    TimeBuff += MillisecondTime;
                    MillisecondTime = 0L;
                    StartTime = 0L;
                    TimeBuff = 0L;
                    UpdateTime = 0L;
                    Seconds = 0;
                    Minutes = 0;
                    MilliSeconds = 0;
                    handler.removeCallbacks(runnable);
                    recordaudioIV.setVisibility(View.VISIBLE);
                    recordingcircle.setAnimation(null);
                    stopaudioIV.setVisibility(View.INVISIBLE);
                    closeIV.setVisibility(View.VISIBLE);
                    playAudioMenu();
                }
                else
                {
                    timerTV.setText("00:00");
                    dialogBox.createDialog("Too Fast!","Please record a note at least one second long.","bad");
                    TimeBuff += MillisecondTime;
                    MillisecondTime = 0L;
                    StartTime = 0L;
                    TimeBuff = 0L;
                    UpdateTime = 0L;
                    Seconds = 0;
                    Minutes = 0;
                    MilliSeconds = 0;
                    handler.removeCallbacks(runnable);
                    recordaudioIV.setVisibility(View.VISIBLE);
                    recordingcircle.setAnimation(null);
                    stopaudioIV.setVisibility(View.INVISIBLE);
                    closeIV.setVisibility(View.VISIBLE);
                }
            }
        });


        recordAudioMenu.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                firsttimeAudioMenu.show();
                recordAudioMenu.dismiss();
                timerTV.setText("00:00:00");

            }
        });
    }

    private void playAudioMenu()
    {
        playAudioMenu = new Dialog(this, R.style.CustomDialog);
        playAudioMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        playAudioMenu.setCancelable(true);
        playAudioMenu.setContentView(R.layout.audio_playback);
        playAudioMenu.show();

        seekBar = playAudioMenu.findViewById(R.id.seekBar);
        playIV = playAudioMenu.findViewById(R.id.playIV);
        pauseIV = playAudioMenu.findViewById(R.id.pauseIV);
        currentTimeTV = playAudioMenu.findViewById(R.id.currentTimeTV);
        TextView endtimeTV = playAudioMenu.findViewById(R.id.endTimeTV);
        ImageView closeIV = playAudioMenu.findViewById(R.id.closeIV);
        ImageView micIV = playAudioMenu.findViewById(R.id.micIV);
        ImageView profileIV = playAudioMenu.findViewById(R.id.profileIV);
        ImageView postIV = playAudioMenu.findViewById(R.id.postIV);

        profileIV.setImageBitmap(profilePic);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(profileAudioPath));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp");
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        durationseconds = Double.parseDouble(duration) / 1000.0;
        seekBar.setMax(mediaPlayer.getDuration());
        endtimeTV.setText(String.valueOf(durationseconds));
        seekChange(seekBar);


        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                seekChange(view);
                return false;
            }
        });

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudioMenu.dismiss();
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
            }
        });

        playIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mediaPlayer.start();
                    startPlayProgressUpdater();
                    playIV.setVisibility(View.INVISIBLE);
                    pauseIV.setVisibility(View.VISIBLE);
                }catch (IllegalStateException e) {
                    mediaPlayer.pause();
                }
            }
        });

        pauseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                playIV.setVisibility(View.VISIBLE);
                pauseIV.setVisibility(View.INVISIBLE);

            }
        });

        playAudioMenu.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                recordAudioMenu.show();
                playAudioMenu.dismiss();
                timerTV.setText("00:00");

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG,"in completion duration seconds = " + String.valueOf(durationseconds));
                currentTimeTV.setText(String.valueOf(durationseconds));
            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                recordAudioMenu.show();
                playAudioMenu.dismiss();
                timerTV.setText("00:00");
            }
        });

        postIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingPB.setVisibility(View.VISIBLE);
                    requests.setProfileAudio(profileAudioPath,userData.getString("username"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void startPlayProgressUpdater() {

        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            currentTimeTV.setText(String.valueOf((double)(mediaPlayer.getCurrentPosition()) /1000));
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,10);
        }else{
            pauseIV.setVisibility(View.INVISIBLE);
            playIV.setVisibility(View.VISIBLE);
        }

    }

    private void seekChange(View v) {
        SeekBar sb = (SeekBar) v;
        mediaPlayer.seekTo(sb.getProgress());
        currentTimeTV.setText(String.valueOf((double)(mediaPlayer.getCurrentPosition()) /1000));
    }




    private void checkUser(String username, String password) throws JSONException {
        JSONObject loginParams = new JSONObject();
        loginParams.put("username",username);
        loginParams.put("password",password);
        requests.login(loginParams);
    }


    private boolean checkMicPermission() {
        Log.d(TAG,"in check mic permission");
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Permission is granted");
                return true;
            }
            else
            {
                Log.d(TAG, "Permission is revoked");
                requestPermissions(ACCESS_MICROPHONE_PREMS, ACCESS_MICROPHONE);
                return false;
            }
        }
        else
        {
            //permission is automatically granted on sdk<23 upon installation
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }

    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Permission is granted");
                return true;
            }
            else
            {
                Log.d(TAG, "Permission is revoked");
                requestPermissions(WRITE_EXTERNAL_PREMS, WRITE_EXTERNAL_REQUEST);
                return false;
            }
        }
        else
        {
            //permission is automatically granted on sdk<23 upon installation
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }


    public boolean checkWifiPermission()  {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) ==
                    PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Permission is granted");
                return true;
            }
            else
            {
                Log.d(TAG, "Permission is revoked");
                requestPermissions(WRITE_EXTERNAL_PREMS, WRITE_EXTERNAL_REQUEST);
                return false;
            }
        }
        else
        {
            //permission is automatically granted on sdk<23 upon installation
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }


    public boolean checkInternetPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.INTERNET) ==
                    PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Permission is granted");
                return true;
            }
            else
            {
                Log.d(TAG, "Permission is revoked");
                requestPermissions(ACCESS_INTERNET_PREMS, ACCESS_INTERNET);
                return false;
            }
        }
        else
        {
            //permission is automatically granted on sdk<23 upon installation
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }

}
