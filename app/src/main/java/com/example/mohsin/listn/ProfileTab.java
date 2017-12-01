package com.example.mohsin.listn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mohsin on 11/21/2017.
 */

public class ProfileTab extends Fragment implements ProfileTabInterface{

    private static final String TAG = "PROFILETAB";
    Animation fadeInAnimation;
    RippleBackground rippleBackground;
    RelativeLayout playbtnRL;
    ImageView playIV;
    ImageView stopIV;
    ImageView profileIV;
    TextView listenersTV;
    TextView listeningTV;
    TextView postsTV;
    TextView usernameTV;
    TextView fullnameTV;
    String audioPath;


    JSONObject userObject;

    Bitmap profilePic;

    ProfileTabInterface profileTabInterface;
    TabAsyncRequests requests;
    MediaPlayer mediaPlayer;

    DialogBox dialogBox;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_tab, container, false);
        playbtnRL = rootView.findViewById(R.id.profileRL);
        playIV =  rootView.findViewById(R.id.playIV);
        stopIV =  rootView.findViewById(R.id.stopIV);
        profileIV = rootView.findViewById(R.id.profileIV);
        listenersTV = rootView.findViewById(R.id.listenersTV);
        listeningTV = rootView.findViewById(R.id.listeningTV);
        usernameTV = rootView.findViewById(R.id.usernameTV);
        fullnameTV = rootView.findViewById(R.id.fullnameTV);
        postsTV = rootView.findViewById(R.id.postsTV);
        dialogBox = new DialogBox(getContext());
    //    rippleBackground= rootView.findViewById(R.id.rippleview);
        dialogBox.loadingDialog();


        userObject = ((TabActivity) getActivity()).userObject;
        profileTabInterface = new ProfileTabInterface() {
            @Override
            public void setProfileTabProfilePic(Bitmap result) {
                profilePic = result;
                profileIV.setImageBitmap(profilePic);
                dialogBox.recordAudioMenu.dismiss();
            }

            @Override
            public void gotProfileAudio()
            {
                mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(audioPath));
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp");
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                     //   rippleBackground.stopRippleAnimation();
                        profileIV.setAnimation(null);
                        playIV.setVisibility(View.VISIBLE);
                        stopIV.setVisibility(View.INVISIBLE);
                    }
                });
                dialogBox.recordAudioMenu.dismiss();
            }

        };
        mediaPlayer = new MediaPlayer();
        requests = new TabAsyncRequests(profileTabInterface);
        audioPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp");


        Log.d(TAG,"Profile Tab userobject = " + userObject.toString());

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
            listenersTV.setText(userObject.getJSONArray("followers").length() + "\n Listeners");
            listeningTV.setText(userObject.getJSONArray("following").length() + "\n Listening");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setupListeners()
    {
        playIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playIV.getVisibility() == View.VISIBLE) {
                    mediaPlayer.start();
                    playIV.setVisibility(View.INVISIBLE);
                    stopIV.setVisibility(View.VISIBLE);
                //    rippleBackground.startRippleAnimation();
                    profileIV.startAnimation(fadeInAnimation);
                }
            }
        });
        stopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playIV.setVisibility(View.VISIBLE);
                stopIV.setVisibility(View.INVISIBLE);
          //      rippleBackground.stopRippleAnimation();
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                profileIV.setAnimation(null);
            }
        });

    }

    public void setProfileTabProfilePic(Bitmap result) {

    }

    public void gotProfileAudio() {

    }
}
