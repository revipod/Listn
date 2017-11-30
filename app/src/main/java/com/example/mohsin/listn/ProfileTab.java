package com.example.mohsin.listn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Mohsin on 11/21/2017.
 */

public class ProfileTab extends Fragment {

    Animation fadeInAnimation;

    RelativeLayout playbtnRL;
    ImageView playIV;
    ImageView stopIV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_tab, container, false);
        playbtnRL = rootView.findViewById(R.id.playbtnRL);
        playIV =  rootView.findViewById(R.id.playIV);
        stopIV =  rootView.findViewById(R.id.stopIV);

        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeanimation);
        fadeInAnimation.setRepeatMode(Animation.REVERSE);


        setupListeners();

        return rootView;
    }

    private void setupListeners()
    {
        playIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playIV.getVisibility() == View.VISIBLE) {
                    playIV.setVisibility(View.INVISIBLE);
                    stopIV.setVisibility(View.VISIBLE);
                    playbtnRL.startAnimation(fadeInAnimation);
                }
            }
        });
        stopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playIV.setVisibility(View.VISIBLE);
                stopIV.setVisibility(View.INVISIBLE);
                playbtnRL.setAnimation(null);
            }
        });
    }

}
