package com.example.mohsin.listn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class TabActivity extends AppCompatActivity implements TabActivityInterface {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    DialogBox dialogBox;

    TabLayout tabLayout;
    TabAsyncRequests requests;
    TabActivityInterface tabActivityInterface;
    Handler handler;
    RecordAudio audioRecorder;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;

    static final int INITIAL_REQUEST = 1337;
    static final int WRITE_EXTERNAL_REQUEST = INITIAL_REQUEST + 3;
    static final int ACCESS_COARSE_LOCATION = INITIAL_REQUEST + 4;
    static final int ACCESS_WIFI = INITIAL_REQUEST + 5;
    static final int ACCESS_INTERNET = INITIAL_REQUEST + 6;
    static final int ACCESS_MICROPHONE = INITIAL_REQUEST + 7;
    private static final String TAG = "CameraClass";
    private final int CAMERA = 1;
    private final String IMAGE_DIRECTORY = "/demonuts_upload_camera";

    static final String[] WRITE_EXTERNAL_PREMS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static final String[] ACCESS_WIFI_PREMS = {
            Manifest.permission.ACCESS_WIFI_STATE
    };

    static final String[] ACCESS_INTERNET_PREMS = {
            Manifest.permission.INTERNET
    };

    static final String[] ACCESS_MICROPHONE_PREMS = {
            Manifest.permission.RECORD_AUDIO
    };



    String profilePicPath = "blank";
    String profileaudioPath = "blank";
    String audioPath;
    String username;


    int currentPicType = 0;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;

    int Seconds, Minutes, MilliSeconds ;

    double durationseconds;

    Bitmap profilePic;

    Dialog firsttimeCameraMenu;
    Dialog firsttimeAudioMenu;
    Dialog recordAudioMenu;
    Dialog playAudioMenu;

    TextView timerTV;
    TextView currentTimeTV;

    ImageView playIV;
    ImageView pauseIV;

    JSONObject userObject;

    public Runnable runnable = new Runnable() {

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

    boolean firstime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);


        dialogBox = new DialogBox(this);

        tabActivityInterface = new TabActivityInterface() {
            @Override
            public void setprofileImagePath(String foundprofilepicPath) {
                profilePicPath = foundprofilepicPath;
                try {
                    userObject.put("profilepic",profilePicPath);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(profilePicPath.contains("NoPicture"))
                {
                    profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.noprofilepic);
                }
                else
                {
                    requests.getImage(profilePicPath);
                }

                if(firstime) {
                    firsttimeAudioMenu.show();
                    firsttimeCameraMenu.dismiss();
                }
            }

            @Override
            public void problemSettingProfilePic() {
                dialogBox.createDialog("Error","Sorry, we couldn't upload your picture. Please confirm that you have an internet connection and try again.","bad");
            }

            @Override
            public void setProfilePicBitmap(Bitmap result) {
                profilePic = result;
            }

            @Override
            public void setprofileAudioPath(JSONObject result)
            {
                if(firstime) {
                    firsttimeAudioMenu.dismiss();
                    recordAudioMenu.dismiss();
                    playAudioMenu.dismiss();
                    JSONObject newUser = new JSONObject();
                    try {
                        newUser = result.getJSONObject("user");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    intent.putExtra("User",newUser.toString());
                    intent.putExtra("loginorRegister","login");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    recordAudioMenu.dismiss();
                    playAudioMenu.dismiss();
                }
            }
        };

        requests = new TabAsyncRequests(tabActivityInterface);
        Intent intent = getIntent();
        audioPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp");

        try {
            userObject = new JSONObject(intent.getStringExtra("User"));
            username = userObject.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(intent.getStringExtra("loginorRegister").contains("register"))
        {
            firstime = true;
            try {
                loadFirstUser(userObject);
                loadFirstAudio();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.getTabAt(0).setIcon(R.mipmap.audiofeed);
            tabLayout.getTabAt(1).setIcon(R.mipmap.notification);
            tabLayout.getTabAt(2).setIcon(R.mipmap.profile);
            firstime = false;
            loadProfile();
        }


    }

    private void loadProfile()
    {
        Log.d(TAG,"logged in " + "user = " + userObject.toString() );
        tabLayout.getTabAt(2).select();
    }

    private void loadFirstUser(JSONObject user) throws JSONException {
        firsttimeCameraMenu = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        firsttimeCameraMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        firsttimeCameraMenu.setCancelable(true);
        firsttimeCameraMenu.setContentView(R.layout.firsttime_cameramenu);
        firsttimeCameraMenu.show();
        dialogBox.createDialog("Sucess!","Welcome to Listn " + user.get("username") + " You have sucessfully created a Listn Account! Let's get your profile started!","good");
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

                profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.noprofilepic);
                firsttimeAudioMenu.show();
                firsttimeCameraMenu.dismiss();

            }
        });
    }

    private void loadFirstAudio() {
        firsttimeAudioMenu = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        firsttimeAudioMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        firsttimeAudioMenu.setCancelable(true);
        firsttimeAudioMenu.setContentView(R.layout.firsttime_profileaudio);
        Button recordaudioBTN = firsttimeAudioMenu.findViewById(R.id.recordaudioBTN);
        TextView skipAudioTV =  firsttimeAudioMenu.findViewById(R.id.skipAudioTV);
        recordaudioBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"in record on click listner");
                if(checkMicPermission()) {
                    showRecordAudioMenu();
                    firsttimeAudioMenu.hide();
                }
            }
        });
        skipAudioTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    firsttimeAudioMenu.dismiss();
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    intent.putExtra("User",userObject.toString());
                    intent.putExtra("loginorRegister","login");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showRecordAudioMenu()
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
                if(firstime) firsttimeAudioMenu.show();
                recordAudioMenu.dismiss();
                timerTV.setText("00:00");
            }
        });

        recordaudioIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp";
                try {
                    audioRecorder = new RecordAudio(audioPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    audioRecorder.startRecord();
                    timerTV.setText("00:00");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                if(firstime) firsttimeAudioMenu.show();
                recordAudioMenu.dismiss();
                timerTV.setText("00:00:00");

            }
        });
    }

    public void playAudioMenu()
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
        mediaPlayer = MediaPlayer.create(this, Uri.parse(audioPath));
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
                    requests.setProfileAudio(audioPath,username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void startPlayProgressUpdater() {
        double seconds = (double)(mediaPlayer.getCurrentPosition()) /1000;

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





    private void setProfilePic() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        currentPicType = 1;
        this.startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = saveImage(thumbnail);
            Bundle extras = data.getExtras();
            thumbnail.recycle();
            if(currentPicType == 1) {
                try {
                    Log.d(TAG,"calling async upload");
                    requests.changeProfilePic(path,username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setprofileImagePath(String profilepicPath) {

    }

    public void problemSettingProfilePic() {

    }

    public void setProfilePicBitmap(Bitmap result) {

    }

    public void setprofileAudioPath(JSONObject result) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FeedTab feedTab = new FeedTab();
                    return feedTab;
                case 1:
                    NotificationTab notificationTab = new NotificationTab();
                    return notificationTab;
                case 2:
                    ProfileTab profileTab = new ProfileTab();
                    return profileTab;
                default:
                    return  null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }
    }
    public boolean checkMicPermission() {
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

    public boolean checkCameraPermission() {
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


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
