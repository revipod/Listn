package com.example.mohsin.listn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by Mohsin on 11/21/2017.
 */

public class ProfileTab extends Fragment{

    private static final String TAG = "PROFILETAB";
    private final int CAMERA = 1;


    private Animation fadeInAnimation;
    RippleBackground rippleBackground;
    private RelativeLayout voicePostRL;
    private RelativeLayout imagePostRL;
    private RelativeLayout textPostRL;
    private NestedScrollView mainNSV;
    private RecordAudio audioRecorder;
    private ImageView profileplayIV;
    private ImageView postplayIV;
    private ImageView stopIV;
    private ImageView pauseIV;
    private ImageView profileIV;
    private ImageView settingsIV;
    private TextView listenersTV;
    private TextView listeningTV;
    private TextView postsTV;
    private TextView usernameTV;
    private TextView fullnameTV;
    private TextView timerTV;
    private TextView bioTV;
    private TextView currentTimeTV;



    private String profileAudioPath;
    private String audioPostPath;
    private String newPostID;
    private String typeofAudio;
    private String boldText;
    private String imagePostPath;
    private SpannableString str;


    private SeekBar seekBar;

    private long MillisecondTime;
    private long StartTime;
    private long TimeBuff;
    private long UpdateTime = 0L ;

    private int Seconds;
    private int Minutes;
    private int MilliSeconds ;

    private int numofPosts;

    private double durationseconds;


    private JSONObject userObject;
    private JSONObject postObject;

    private Bitmap profilePic;

    private ProfileAsyncRequests requests;
    private MediaPlayer postmediaPlayer;
    private MediaPlayer profilemediaPlayer;

    private Handler handler;

    private DialogBox dialogBox;
    private Dialog recordAudioMenu;
    private Dialog playAudioMenu;
    private Dialog settingsMenu;
    private Dialog textPostMenu;
    private Dialog showImagePost;

    private ProfileAdapter adapter;

    private ProgressBar loadingPB;

    private RecyclerView recyclerView;
    private String imageType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_tab, container, false);
        RelativeLayout playbtnRL = rootView.findViewById(R.id.profileRL);
        voicePostRL = rootView.findViewById(R.id.micpostRL);
        imagePostRL = rootView.findViewById(R.id.imagepostRL);
        textPostRL = rootView.findViewById(R.id.textpostRL);
        RelativeLayout topholderRL = rootView.findViewById(R.id.topholderRL);
        mainNSV = rootView.findViewById(R.id.mainNSV);
        profileplayIV =  rootView.findViewById(R.id.playIV);
        stopIV =  rootView.findViewById(R.id.stopIV);
        profileIV = rootView.findViewById(R.id.profileIV);
        settingsIV = rootView.findViewById(R.id.settingsIV);
        listenersTV = rootView.findViewById(R.id.listenersTV);
        listeningTV = rootView.findViewById(R.id.listeningTV);
        usernameTV = rootView.findViewById(R.id.usernameTV);
        fullnameTV = rootView.findViewById(R.id.fullnameTV);
        bioTV = rootView.findViewById(R.id.bioTV);
        postsTV = rootView.findViewById(R.id.postsTV);
        dialogBox = new DialogBox(getContext());
        boolean isPlaying = false;
        loadingPB = (ProgressBar) rootView.findViewById(R.id.progressbar);
        loadingPB.setVisibility(View.VISIBLE);
        userObject = ((TabActivity) getActivity()).userObject;
        postObject = ((TabActivity) getActivity()).postObject;
        recordAudioMenu = new Dialog(getContext(), R.style.CustomDialog);
        playAudioMenu = new Dialog(getContext(), R.style.CustomDialog);
        settingsMenu = new Dialog(getContext(), R.style.CustomDialog);
        recyclerView = rootView.findViewById(R.id.profileRV);
        ProfileTabInterface profileTabInterface = new ProfileTabInterface() {
            @Override
            public void setProfileTabProfilePic(Bitmap result) {
                adapter.profilePic = result;
                profilePic = result;
                profileIV.setImageBitmap(profilePic);
            }

            @Override
            public void gotProfileAudio(JSONObject user) {
                userObject = user;
                profileAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/profileaudio.3gp";
                profilemediaPlayer = MediaPlayer.create(getContext(), Uri.parse(profileAudioPath));
                profilemediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        profileIV.setAnimation(null);
                        profileplayIV.setVisibility(View.VISIBLE);
                        stopIV.setVisibility(View.INVISIBLE);
                    }
                });
                if (playAudioMenu.isShowing()) {
                    recordAudioMenu.dismiss();
                    playAudioMenu.dismiss();
                    recordAudioMenu = null;
                    playAudioMenu = null;
                    dialogBox.createDialog("Changed Audio", "You have sucessfully updated your Listn Audio Profile", "good");
                }
                if (typeofAudio.contains("Profile")) {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingPB.setVisibility(View.GONE);
                }

            }

            @Override
            public void loadListView(ArrayList<String> dataFileList) throws JSONException {

                JSONArray postType = postObject.getJSONArray("postType");
                for (int i = 0; i < postType.length(); i++) {
                    if (postType.getString(i).contains("Audio")) {
                        Log.d(TAG, "adding audio " + dataFileList.get(i));
                        adapter.add(new ProfileDataProvider(userObject.getString("username"), dataFileList.get(i), (String) postObject.getJSONArray("postDate").getString(i), "Audio"));
                    } else if (postType.getString(i).contains("Text")) {
                        Log.d(TAG, "adding text " + dataFileList.get(i));
                        adapter.add(new ProfileDataProvider(userObject.getString("username"), dataFileList.get(i), postObject.getJSONArray("postDate").getString(i), "Text"));
                    }
                }
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.GONE);
                mainNSV.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void setprofileImagePath(JSONObject user) {
                userObject = user;
                try {
                    requests.getImage(user.getString("profilepic"), "Settings");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void problemSettingProfilePic() {

            }

            @Override
            public void setSettingProfilePic(Bitmap result) {
                profilePic = result;
                profileIV.setImageBitmap(result);
                adapter.profilePic = result;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void changeSettings(JSONObject result) throws JSONException {
                JSONObject user = result.getJSONObject("user");
                userObject = user;
                if (result.has("existed")) {
                    if (result.getBoolean("existed")) {
                        dialogBox.createDialog("Already Exists", "Sorry but a person is already using that username", "bad");
                    } else
                        dialogBox.createDialog("New username", "Your new username is " + user.getString("username"), "good");

                    usernameTV.setText(user.getString("username"));
                }
                fullnameTV.setText(user.getString("fullname"));
                bioTV.setText(user.getString("bio"));
                View settingsView = settingsMenu.getCurrentFocus();
                if (settingsView != null) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(settingsView.getWindowToken(), 0);
                }
                settingsMenu.dismiss();
                settingsMenu = null;
            }

            @Override
            public void problemChangingSettings() {

            }

            @Override
            public void setProfileAudioPath(JSONObject user) {

            }

            @Override
            public void problemProfileAudioPath() {

            }

            @Override
            public void gotPostAudio(JSONObject post) throws JSONException {
                Log.d(TAG, "audio being added to listview path is = " + post.getString("audioPath"));
                adapter.add(new ProfileDataProvider(post.getString("username"), post.getString("audioPath"), post.getString("postDate"), "Audio"));
                recordAudioMenu.dismiss();
                playAudioMenu.dismiss();
                recordAudioMenu = null;
                playAudioMenu = null;
                adapter.notifyDataSetChanged();
                numofPosts++;
                boldText = numofPosts + "\n";
                str = new SpannableString(boldText + "Posts");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                postsTV.setText(str);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.GONE);
                dialogBox.createDialog("Posted!", "Your new voice post was sucessfully uploaded!", "good");


            }

            @Override
            public void gotTextPost(JSONObject post) throws JSONException {
                Log.d(TAG, "text being added to listview path is = " + post.getString("postText"));
                adapter.add(new ProfileDataProvider(post.getString("username"), post.getString("postText"), post.getString("postDate"), "Text"));
                recyclerView.smoothScrollToPosition(0);
                adapter.notifyDataSetChanged();
                textPostMenu.dismiss();
                textPostMenu = null;
                View textPostView = textPostMenu.getCurrentFocus();
                if (textPostView != null) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textPostView.getWindowToken(), 0);
                }
                numofPosts++;
                boldText = numofPosts + "\n";
                str = new SpannableString(boldText + "Posts");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                postsTV.setText(str);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.GONE);
                dialogBox.createDialog("Posted!", "Your new text post was sucessfully uploaded!", "good");

            }

        };
        typeofAudio = "Nothing";
        profilePic = BitmapFactory.decodeResource(getContext().getResources(),
                R.mipmap.noprofilepic);
        requests = new ProfileAsyncRequests(profileTabInterface);
        adapter = new ProfileAdapter(profilePic,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        profileAudioPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/profileaudio.3gp");
        audioPostPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + generateID() + ".3gp";
        Log.d(TAG,"userobject = " + userObject.toString());
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeanimation);
        fadeInAnimation.setRepeatMode(Animation.REVERSE);


        setupListeners();
        setupProfile();

        return rootView;
    }

    private void setupProfile() {
        try {
            if(userObject.getString("profilepic").contains("NoPicture"))
            {
                profilePic = BitmapFactory.decodeResource(getContext().getResources(),
                        R.mipmap.noprofilepic);
                profileIV.setImageBitmap(profilePic);
            }
            else
            {
                mainNSV.setVisibility(View.INVISIBLE);
               getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
               loadingPB.setVisibility(View.VISIBLE);
                requests.getImage(userObject.getString("profilepic"),"ProfileTab");
            }
            if(userObject.getString("profileaudio").contains("NoAudio"))
            {

            }
            else
            {
                mainNSV.setVisibility(View.INVISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingPB.setVisibility(View.VISIBLE);
                requests.downloadAudio(userObject,"profile");
            }
            try {
                if(postObject.getJSONArray("postSource").length() > 0)
                {
                    mainNSV.setVisibility(View.INVISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingPB.setVisibility(View.VISIBLE);
                    requests.downloadAudioArray(postObject);
                }
                else
                {
                    mainNSV.setVisibility(View.VISIBLE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingPB.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            usernameTV.setText( "@" + userObject.getString("username"));
            fullnameTV.setText(userObject.getString("fullname"));
            bioTV.setText(userObject.getString("bio"));
            boldText  = String.valueOf(userObject.getJSONArray("followers").length()) + "\n";
            String normalText = "Listeners";
            str = new SpannableString(boldText + normalText);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            listenersTV.setText(str);
            boldText = String.valueOf(userObject.getJSONArray("following").length()) + "\n";
            normalText = "Listening";
            str = new SpannableString(boldText + normalText );
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            listeningTV.setText(str);
            boldText = String.valueOf(postObject.getJSONArray("postSource").length()) + "\n";
            numofPosts = postObject.getJSONArray("postSource").length();
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
                typeofAudio = "Post";
                showRecordAudioMenu();
            }
        });

        imagePostRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePostMenu();
            }
        });

        textPostRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextPostMenu();
            }
        });

        settingsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation spinAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.spinanimation);
                settingsIV.startAnimation(spinAnimation);
                try {
                    loadSettings();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        profileplayIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!userObject.getString("profileaudio").contains("NoAudio")) {
                        if (!profilemediaPlayer.isPlaying()) {
                            profileplayIV.setVisibility(View.INVISIBLE);
                            stopIV.setVisibility(View.VISIBLE);
                            profilemediaPlayer.start();
                            profileIV.startAnimation(fadeInAnimation);
                            //    rippleBackground.startRippleAnimation();
                        }
                    }
                    else
                    {
                        dialogBox.createDialog("No Audio","It seems as tho you have not recorded a profile audio clip. Press the settings button on the top right to do so","bad");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        stopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profilemediaPlayer.isPlaying()) {
                    profileplayIV.setVisibility(View.VISIBLE);
                    stopIV.setVisibility(View.INVISIBLE);
                    profilemediaPlayer.stop();
                    profilemediaPlayer.reset();
                    profileIV.setAnimation(null);
                    gotProfileAudio();
                }
            }
        });

    }

    private void showImagePostMenu() {
        imageType = "Post";
        loadCamera();
    }

    private void showTextPostMenu() {
        textPostMenu = new Dialog(getActivity(),R.style.CustomDialog);
        textPostMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        textPostMenu.setCancelable(true);
        textPostMenu.setContentView(R.layout.text_post);
        textPostMenu.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        textPostMenu.show();
        ImageView closeIV = textPostMenu.findViewById(R.id.closeIV);
        ImageView postIV = textPostMenu.findViewById(R.id.saveIV);
        final RelativeLayout textpostRL = textPostMenu.findViewById(R.id.textpostRL);
        final EditText newPostET = textPostMenu.findViewById(R.id.textpostET);
        postIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newPostET.getText().toString().trim().length() < 1) dialogBox.createDialog("Empty","Your post seems to be empty.","bad");
                else
                {
                    try {
                        String postText = newPostET.getText().toString().trim();
                        JSONObject data = new JSONObject();
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        data.put("postDate", currentDateTimeString);
                        data.put("username", userObject.getString("username"));
                        data.put("postTextName", newPostID);
                        data.put("postText",postText);
                        Log.d(TAG, "postDate = " + currentDateTimeString);
                        requests.uploadTextPost(data);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View textPostView = textPostMenu.getCurrentFocus();
                if (textPostView != null) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textPostView.getWindowToken(), 0);
                }
                textPostMenu.dismiss();
                textPostMenu = null;
            }
        });

        textpostRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPostET.requestFocus();
            }
        });

    }

    private void loadSettings() throws JSONException {
        settingsMenu = new Dialog(getContext(),R.style.CustomDialog);
        settingsMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsMenu.setCancelable(true);
        settingsMenu.setContentView(R.layout.editprofile);
        settingsMenu.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        settingsMenu.show();
        final ImageView closeIV = settingsMenu.findViewById(R.id.backIV);
        final ImageView changeIV = settingsMenu.findViewById(R.id.saveIV);
        final ImageView changeProfileListnLV = settingsMenu.findViewById(R.id.changeprofileaudioIV);
        final Button changePicBTN = settingsMenu.findViewById(R.id.changepicBTN);
        final RelativeLayout settingsRootView = settingsMenu.findViewById(R.id.settingsmainholderRL);
        final Button logoutBTN = settingsMenu.findViewById(R.id.logoutBTN);
        final EditText fullnameET = settingsMenu.findViewById(R.id.fullnameET);
        final EditText usernameET = settingsMenu.findViewById(R.id.usernameET);
        final EditText aboutmeET = settingsMenu.findViewById(R.id.aboutmeET);
        final ImageView settingprofileIV = settingsMenu.findViewById(R.id.settingprofileIV);

        settingprofileIV.setImageBitmap(profilePic);

        fullnameET.setText(userObject.getString("fullname"));
        aboutmeET.setText(userObject.getString("bio"));
        usernameET.setText(userObject.getString("username"));

        changeProfileListnLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeofAudio = "Profile";
                showRecordAudioMenu();
            }
        });

        changeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fullnameET.length() > 0 && usernameET.length() > 0)
                {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("username",userObject.getString("username"));
                        params.put("fullname",fullnameET.getText().toString().trim());
                        params.put("newusername",usernameET.getText().toString().trim());
                        params.put("bio",aboutmeET.getText().toString().trim());
                        requests.changeProfileSettings(params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    dialogBox.createDialog("Empty Fields","Please make sure you have not left the Name, Username and email field empty.","bad");
                }
            }
        });

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsMenu.dismiss();
                settingsMenu = null;
            }
        });

        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsMenu.dismiss();
                settingsMenu = null;
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        changePicBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType = "Profile";
                loadCamera();
            }
        });

        settingsRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                settingsRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = settingsRootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    logoutBTN.setVisibility(View.GONE);
                }
                else {
                    // keyboard is closed
                    logoutBTN.setVisibility(View.VISIBLE);
                }
            }
        });

    }



    private void loadCamera() {
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
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            final String path = saveImage(thumbnail);
            thumbnail.recycle();
            if(imageType.contains("Profile")) {
                try {
                    Log.d(TAG, "calling async upload");
                    requests.changeProfilePic(path, userObject.getString("username"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (imageType.contains("Post")) {
                imagePostPath = path;
                final AlertDialog builder;
                builder = new AlertDialog.Builder(getContext()).create();
                builder.setTitle("Add Audio");
                builder.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        typeofAudio = "Image";
                        builder.dismiss();
                        showRecordAudioMenu();
                    }
                });
                builder.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.dismiss();
                        showImagePost(false);
                    }
                });
                builder.setMessage("Do you want to add a audio description to this picture?");
                builder.setCancelable(false);
                builder.show();

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
            MediaScannerConnection.scanFile(getActivity(),
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


    private void gotProfileAudio() {
        profilemediaPlayer = MediaPlayer.create(getContext(), Uri.parse(profileAudioPath));
        profilemediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                profileIV.setAnimation(null);
                profileplayIV.setVisibility(View.VISIBLE);
                stopIV.setVisibility(View.INVISIBLE);
            }
        });

    }


    @SuppressLint("ClickableViewAccessibility")
    private void showRecordAudioMenu()
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
                recordAudioMenu = null;
                timerTV.setText("00:00");
            }
        });

        recordaudioIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    newPostID = generateID();
                    audioPostPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + newPostID + ".3gp";
                    audioRecorder = new RecordAudio(audioPostPath);
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
                if(typeofAudio.contains("Image"))
                {

                }
                recordAudioMenu.dismiss();
                recordAudioMenu = null;
                timerTV.setText("00:00:00");

            }
        });
    }

    private void playAudioMenu()
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
                recordAudioMenu = null;
                playAudioMenu = null;
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
                playAudioMenu = null;
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
                playAudioMenu = null;
                timerTV.setText("00:00");
            }
        });

        postIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              try {
                  JSONObject headers = new JSONObject();
                  String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                  headers.put("postDate",currentDateTimeString);
                  headers.put("username",userObject.getString("username"));
                  headers.put("audiofilename",newPostID);
                  Log.d(TAG,"postdate = " + currentDateTimeString);
                  if(typeofAudio.contains("Post")){
                      getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                      loadingPB.setVisibility(View.VISIBLE);
                      requests.uploadAudioPost(audioPostPath,headers.toString());
                  }
                  else if(typeofAudio.contains("Profile"))
                  {
                      getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                      loadingPB.setVisibility(View.VISIBLE);
                      requests.setProfileAudio(audioPostPath,userObject.getString("username"));
                  }
                  else if(typeofAudio.contains("Image"))
                  {
                      playAudioMenu.dismiss();
                      recordAudioMenu.dismiss();
                      recordAudioMenu = null;
                      playAudioMenu = null;
                      showImagePost(true);
                      requests.uploadImagePost();
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
            }
        });

    }

    private void showImagePost(boolean b) {
        showImagePost = new Dialog(getContext(), R.style.CustomDialog);
        showImagePost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showImagePost.setCancelable(true);
        showImagePost.setContentView(R.layout.imagepost_view);
        showImagePost.show();
        ImageView closeIV = showImagePost.findViewById(R.id.closeIV);
        ImageView postIV = showImagePost.findViewById(R.id.saveIV);
        ImageView imageholderIV = showImagePost.findViewById(R.id.imageholderIV);
        final ImageView playIV = showImagePost.findViewById(R.id.playIV);
        final ImageView stopIV = showImagePost.findViewById(R.id.stopIV);

        if(b) {
            final MediaPlayer imageAudioPlayer = MediaPlayer.create(getContext(), Uri.parse(audioPostPath));
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(audioPostPath);


            imageAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playIV.setVisibility(View.VISIBLE);
                    stopIV.setVisibility(View.INVISIBLE);
                }
            });

            playIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        imageAudioPlayer.start();
                        playIV.setVisibility(View.INVISIBLE);
                        stopIV.setVisibility(View.VISIBLE);
                    }catch (IllegalStateException e) {
                        postmediaPlayer.pause();
                    }
                }
            });

            stopIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageAudioPlayer.pause();
                    playIV.setVisibility(View.VISIBLE);
                    stopIV.setVisibility(View.INVISIBLE);

                }
            });

        }
        else
        {
            playIV.setVisibility(View.INVISIBLE);
            stopIV.setVisibility(View.INVISIBLE);
        }

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePost.dismiss();
                showImagePost = null;
            }
        });

        Bitmap picReviewBitmap = BitmapFactory.decodeFile(imagePostPath);
        imageholderIV.setImageBitmap(picReviewBitmap);




    }

    private void seekChange(View v) {
        SeekBar sb = (SeekBar) v;
        postmediaPlayer.seekTo(sb.getProgress());
        currentTimeTV.setText(String.valueOf((double)(postmediaPlayer.getCurrentPosition()) /1000));
    }

    private void startPlayProgressUpdater() {

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



        private String generateID()
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
            return currentDay.substring(0, 3) + Month + dayOfMonth + year + milliseconds.substring(milliseconds.length() - 5, milliseconds.length());
        }



}