package com.example.mohsin.listn;

/**
 * Created by mabbasi on 12/20/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = "ProfileADAPTER";
    Bitmap profilePic;
    ArrayList<String> dataFileList;
    ArrayList<ProfileDataProvider> dataProviderList;
    Context CTX;
    MediaPlayer mp;
    int currPlaying;



    public class audioViewHolder extends RecyclerView.ViewHolder {
        ImageView playIV;
        ImageView stopIV;
        ImageView profileIV;
        TextView usernameTV;
        TextView dateTV;

        public audioViewHolder (View view) {
            super(view);
            playIV = itemView.findViewById(R.id.playIV);
            stopIV = itemView.findViewById(R.id.stopIV);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            profileIV = itemView.findViewById(R.id.profileIV);
        }
    }

    public class textViewHolder  extends RecyclerView.ViewHolder {

        ImageView profileIV;
        TextView usernameTV;
        TextView dateTV;
        TextView textTV;

        public textViewHolder(View view) {
            super(view);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            profileIV = itemView.findViewById(R.id.profileIV);
            textTV = itemView.findViewById(R.id.textpostTV);
        }
    }

    public ProfileAdapter(Bitmap profilePic, Context CTX) {
        this.profilePic = profilePic;
        dataFileList = new ArrayList<String>();
        dataProviderList = new ArrayList<ProfileDataProvider>();
        this.CTX = CTX;
        currPlaying = -1;
    }


    @Override
    public int getItemViewType(int position) {

        if(dataProviderList.get(position).type.contains("Audio")){
            return 1;
        }
        else if (dataProviderList.get(position).type.contains("Text")){
            return 2;
        }
        return 0;
    }


    public void add(ProfileDataProvider object) {
        dataProviderList.add(0,object);
        if(object.type.contains("Audio")) dataFileList.add(0,object.audioPath);
        else if(object.type.contains("Text")) dataFileList.add(0,object.postText);
        Log.d(TAG,"dataFileList = " + dataFileList);
        notifyItemInserted(0);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                {
                    View itemView = LayoutInflater.from(CTX)
                            .inflate(R.layout.single_audiopost, parent, false);
                return new audioViewHolder(itemView);
            }
            case 2: {
                View itemView = LayoutInflater.from(CTX)
                        .inflate(R.layout.single_textpost, parent, false);
                return new textViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ProfileDataProvider provider = dataProviderList.get(position);
        switch (holder.getItemViewType()) {
            case 1:
                final int currPosition = position;
                final audioViewHolder audioPostView = (audioViewHolder)holder;
                audioPostView.dateTV.setText(provider.date);
                audioPostView.profileIV.setImageBitmap(profilePic);
                audioPostView.usernameTV.setText(provider.username);
                audioPostView.playIV.setVisibility(View.VISIBLE);
                audioPostView.stopIV.setVisibility(View.INVISIBLE);
                if (currPlaying == currPosition) {
                    audioPostView.stopIV.setVisibility(View.VISIBLE);
                    audioPostView.playIV.setVisibility(View.INVISIBLE);
                } else {
                    audioPostView.stopIV.setVisibility(View.INVISIBLE);
                    audioPostView.playIV.setVisibility(View.VISIBLE);
                }
                audioPostView.playIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,"datafilelist is = " + dataFileList.get(currPosition));
                        if (currPlaying != -1) {
                            mp.stop();
                            mp.release();
                            notifyItemChanged(currPlaying);
                        }
                        currPlaying = currPosition;
                        mp = MediaPlayer.create(CTX, Uri.parse(dataFileList.get(currPosition)));
                        mp.start();
                        audioPostView.stopIV.setVisibility(View.VISIBLE);
                        audioPostView.playIV.setVisibility(View.INVISIBLE);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                audioPostView.stopIV.setVisibility(View.INVISIBLE);
                                audioPostView.playIV.setVisibility(View.VISIBLE);
                                mp.release();
                                currPlaying = -1;
                            }
                        });

                    }
                });

                audioPostView.stopIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currPlaying == currPosition) {
                            mp.stop();
                            mp.release();
                        }
                        audioPostView.stopIV.setVisibility(View.INVISIBLE);
                        audioPostView.playIV.setVisibility(View.VISIBLE);
                        currPlaying = -1;
                    }
                });
                break;

            case 2:
                textViewHolder textPostView = (textViewHolder)holder;
                textPostView.dateTV.setText(provider.date);
                textPostView.profileIV.setImageBitmap(profilePic);
                textPostView.usernameTV.setText(provider.username);
                textPostView.textTV.setVisibility(View.VISIBLE);
                textPostView.textTV.setText(dataFileList.get(position));
                break;
        }



    }



    @Override
    public int getItemCount() {
        return dataProviderList.size();
    }
}
