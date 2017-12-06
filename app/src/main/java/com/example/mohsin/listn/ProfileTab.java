package com.example.mohsin.listn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mohsin on 11/21/2017.
 */

public class ProfileTab extends Fragment implements ProfileTabInterface{

    private static final String TAG = "PROFILETAB";
    Animation fadeInAnimation;
    RippleBackground rippleBackground;
    RelativeLayout playbtnRL;
    RelativeLayout voicePostRL;
    RelativeLayout imagePostRL;
    RelativeLayout textPostRL;
    RecordAudio audioRecorder;
    ImageView profileplayIV;
    ImageView postplayIV;
    ImageView stopIV;
    ImageView pauseIV;
    ImageView profileIV;
    ImageView settingsIV;
    TextView listenersTV;
    TextView listeningTV;
    TextView postsTV;
    TextView usernameTV;
    TextView fullnameTV;
    TextView timerTV;
    TextView currentTimeTV;
    String profileAudioPath;
    String audioPostPath;
    String newPostID;

    SeekBar seekBar;

    boolean isPlaying;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;

    int Seconds, Minutes, MilliSeconds ;

    double durationseconds;


    JSONObject userObject;
    JSONObject postObject;

    Bitmap profilePic;

    ProfileTabInterface profileTabInterface;
    TabAsyncRequests requests;
    MediaPlayer postmediaPlayer;
    MediaPlayer profilemediaPlayer;

    Handler handler;

    DialogBox dialogBox;
    Dialog recordAudioMenu;
    Dialog playAudioMenu;

    ListView profileLV;
    ProfileAdapter adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_tab, container, false);
        playbtnRL = rootView.findViewById(R.id.profileRL);
        voicePostRL = rootView.findViewById(R.id.micpostRL);
        imagePostRL = rootView.findViewById(R.id.imagepostRL);
        textPostRL = rootView.findViewById(R.id.textpostRL);
        profileplayIV =  rootView.findViewById(R.id.playIV);
        stopIV =  rootView.findViewById(R.id.stopIV);
        profileIV = rootView.findViewById(R.id.profileIV);
        settingsIV = rootView.findViewById(R.id.settingsIV);
        listenersTV = rootView.findViewById(R.id.listenersTV);
        listeningTV = rootView.findViewById(R.id.listeningTV);
        usernameTV = rootView.findViewById(R.id.usernameTV);
        fullnameTV = rootView.findViewById(R.id.fullnameTV);
        postsTV = rootView.findViewById(R.id.postsTV);
        profileLV = rootView.findViewById(R.id.profileLV);
        dialogBox = new DialogBox(getContext());
        isPlaying = false;
        //    rippleBackground= rootView.findViewById(R.id.rippleview);
        dialogBox.loadingDialog();


        userObject = ((TabActivity) getActivity()).userObject;
        postObject = ((TabActivity) getActivity()).postObject;
        profileTabInterface = new ProfileTabInterface() {
            @Override
            public void setProfileTabProfilePic(Bitmap result) {
                profilePic = result;
                profileIV.setImageBitmap(profilePic);
                dialogBox.recordAudioMenu.dismiss();
                adapter = new ProfileAdapter(getContext(),R.layout.single_postview,profilePic);
                profileLV.setAdapter(adapter);
            }

            @Override
            public void gotProfileAudio()
            {
                profilemediaPlayer = MediaPlayer.create(getContext(), Uri.parse(profileAudioPath));
                profilemediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //   rippleBackground.stopRippleAnimation();
                        profileIV.setAnimation(null);
                        profileplayIV.setVisibility(View.VISIBLE);
                        stopIV.setVisibility(View.INVISIBLE);
                    }
                });
                try {
                    if(postObject.getJSONArray("postSource").length() > 0)
                    {
                        requests.downloadAudioArray(postObject.getJSONArray("postSource"),postObject.getJSONArray("postType"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void loadListView(ArrayList<String> audioFileList) {
              for(int i = 0; i < audioFileList.size(); i++)
                  try {
                      adapter.add(new ProfileDataProvider(userObject.getString("username"),audioFileList.get(i), (String) postObject.getJSONArray("postDate").get(i)));
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
            }

        };

        requests = new TabAsyncRequests(profileTabInterface);
        profileAudioPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/profileaudio.3gp");
        audioPostPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + generateID() + ".3gp";


        Log.d(TAG,"userobject = " + userObject.toString());
        Log.d(TAG,"postobject = " + postObject.toString());

        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeanimation);
        fadeInAnimation.setRepeatMode(Animation.REVERSE);


        setupListeners();
        setupProfile();

        return rootView;
    }

    private void setupProfile() {
        try {
            if(userObject.getString("profilepic").contains("Nopicture"))
            {

                profilePic = BitmapFactory.decodeResource(getContext().getResources(),
                        R.mipmap.noprofilepic);
                profileIV.setImageBitmap(profilePic);
            }
            else
            {
                dialogBox.recordAudioMenu.show();
                requests.getImage(userObject.getString("profilepic"),"ProfileTab");
            }
            if(userObject.getString("profileaudio").contains("Noaudio"))
            {

            }
            else
            {
                dialogBox.recordAudioMenu.show();
                requests.downloadAudio(userObject.getString("profileaudio"));
            }
            usernameTV.setText( "@" + userObject.getString("username"));
            fullnameTV.setText(userObject.getString("fullname"));
            String boldText  = String.valueOf(userObject.getJSONArray("followers").length()) + "\n";
            String normalText = "Listeners";
            SpannableString str = new SpannableString(boldText + normalText);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            listenersTV.setText(str);
            boldText = String.valueOf(userObject.getJSONArray("following").length()) + "\n";
            normalText = "Listening";
            str = new SpannableString(boldText + normalText );
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            listeningTV.setText(str);
            str = new SpannableString(boldText + "Posts" );
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            postsTV.setText(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void setupListeners()
    {

        voicePostRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordAudioMenu();
            }
        });

        settingsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation spinAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.spinanimation);
                settingsIV.startAnimation(spinAnimation);
            }
        });

        profileplayIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!profilemediaPlayer.isPlaying()) {
                    profileplayIV.setVisibility(View.INVISIBLE);
                    stopIV.setVisibility(View.VISIBLE);
                    profilemediaPlayer.start();
                    profileIV.startAnimation(fadeInAnimation);
                    //    rippleBackground.startRippleAnimation();
                }
            }
        });
        stopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    profileplayIV.setVisibility(View.VISIBLE);
                    stopIV.setVisibility(View.INVISIBLE);
                    profilemediaPlayer.stop();
                    profilemediaPlayer.reset();
                    profileIV.setAnimation(null);
                    gotProfileAudio();
                    //      rippleBackground.stopRippleAnimation();


            }
        });

    }

    public void setProfileTabProfilePic(Bitmap result) {

    }

    public void gotProfileAudio() {
        profilemediaPlayer = MediaPlayer.create(getContext(), Uri.parse(profileAudioPath));
        profilemediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //   rippleBackground.stopRippleAnimation();
                profileIV.setAnimation(null);
                profileplayIV.setVisibility(View.VISIBLE);
                stopIV.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void loadListView(ArrayList<String> audioFileList) {

    }

    @SuppressLint("ClickableViewAccessibility")
    public void showRecordAudioMenu()
    {

        recordAudioMenu = new Dialog(getContext(), R.style.CustomDialog);
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

        final Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeanimation);
        fadeInAnimation.setRepeatMode(Animation.REVERSE);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAudioMenu.dismiss();
                timerTV.setText("00:00");
            }
        });

        recordaudioIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    newPostID = generateID();
                    audioPostPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + newPostID + ".3gp";
                    audioRecorder = new RecordAudio(audioPostPath);
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
                recordAudioMenu.dismiss();
                timerTV.setText("00:00:00");

            }
        });
    }

    public void playAudioMenu()
    {
        playAudioMenu = new Dialog(getContext(), R.style.CustomDialog);
        playAudioMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        playAudioMenu.setCancelable(true);
        playAudioMenu.setContentView(R.layout.audio_playback);
        playAudioMenu.show();

        seekBar = playAudioMenu.findViewById(R.id.seekBar);
        postplayIV = playAudioMenu.findViewById(R.id.playIV);
        pauseIV = playAudioMenu.findViewById(R.id.pauseIV);
        currentTimeTV = playAudioMenu.findViewById(R.id.currentTimeTV);
        TextView endtimeTV = playAudioMenu.findViewById(R.id.endTimeTV);
        ImageView closeIV = playAudioMenu.findViewById(R.id.closeIV);
        ImageView micIV = playAudioMenu.findViewById(R.id.micIV);
        ImageView profileIV = playAudioMenu.findViewById(R.id.profileIV);
        ImageView postIV = playAudioMenu.findViewById(R.id.postIV);


        profileIV.setImageBitmap(profilePic);
        postmediaPlayer = MediaPlayer.create(getContext(), Uri.parse(audioPostPath));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(audioPostPath);
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        durationseconds = Double.parseDouble(duration) / 1000.0;
        seekBar.setMax(postmediaPlayer.getDuration());
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
                recordAudioMenu.dismiss();
                playAudioMenu.dismiss();
                if(postmediaPlayer.isPlaying())
                {
                    postmediaPlayer.stop();
                }
            }
        });

        postplayIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    postmediaPlayer.start();
                    startPlayProgressUpdater();
                    postplayIV.setVisibility(View.INVISIBLE);
                    pauseIV.setVisibility(View.VISIBLE);
                }catch (IllegalStateException e) {
                    postmediaPlayer.pause();
                }
            }
        });

        pauseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postmediaPlayer.pause();
                postplayIV.setVisibility(View.VISIBLE);
                pauseIV.setVisibility(View.INVISIBLE);

            }
        });

        playAudioMenu.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if(postmediaPlayer.isPlaying())
                {
                    postmediaPlayer.stop();
                }
                recordAudioMenu.show();
                playAudioMenu.dismiss();
                timerTV.setText("00:00");

            }
        });

        postmediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG,"in completion duration seconds = " + String.valueOf(durationseconds));
                currentTimeTV.setText(String.valueOf(durationseconds));
            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postmediaPlayer.isPlaying())
                {
                    postmediaPlayer.stop();
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
                  JSONObject headers = new JSONObject();
                  String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                  headers.put("date",currentDateTimeString);
                  headers.put("username",userObject.getString("username"));
                  headers.put("audiofilename",newPostID);
                    requests.uploadAudioPost(audioPostPath,headers.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void seekChange(View v) {
        SeekBar sb = (SeekBar) v;
        postmediaPlayer.seekTo(sb.getProgress());
        currentTimeTV.setText(String.valueOf((double)(postmediaPlayer.getCurrentPosition()) /1000));
    }

    public void startPlayProgressUpdater() {

        seekBar.setProgress(postmediaPlayer.getCurrentPosition());
        if (postmediaPlayer.isPlaying()) {
            currentTimeTV.setText(String.valueOf((double)(postmediaPlayer.getCurrentPosition()) /1000));
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,10);
        }else{
            pauseIV.setVisibility(View.INVISIBLE);
            postplayIV.setVisibility(View.VISIBLE);
        }

    }

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



        public String generateID()
        {
            String daysArray[] = {
                    "Sunday",
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday"
            };
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int Month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            long time = System.currentTimeMillis();
            String milliseconds = String.valueOf(time);
            // Log.d(TAG, "day = " + day + " day of month = " + dayOfMonth + " Month = " + Month + " year = " + year + " milliseconds = " + time);
            String currentDay = daysArray[day - 1];
            String generatedID = currentDay.substring(0, 3) + Month + dayOfMonth + year + milliseconds.substring(milliseconds.length() - 5, milliseconds.length());
            return generatedID;
        }

}