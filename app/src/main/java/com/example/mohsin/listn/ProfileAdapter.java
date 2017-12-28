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

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder>  {


    private static final String TAG = "ProfileADAPTER";
    Bitmap profilePic;
    ArrayList<String> dataFileList;
    ArrayList<ProfileDataProvider> dataProviderList;
    Context CTX;
    MediaPlayer mp;
    int currPlaying;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView playIV;
        ImageView stopIV;
        ImageView profileIV;
        TextView usernameTV;
        TextView dateTV;
        TextView textTV;

        public MyViewHolder(View view) {
            super(view);
            playIV = itemView.findViewById(R.id.playIV);
            stopIV = itemView.findViewById(R.id.stopIV);
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

    public void add(ProfileDataProvider object) {
        dataProviderList.add(0,object);
        if(object.type.contains("Audio")) dataFileList.add(0,object.audioPath);
        else if(object.type.contains("Text")) dataFileList.add(0,object.postText);
        Log.d(TAG,"dataFileList = " + dataFileList);
        notifyItemInserted(0);
    }

    @Override
    public ProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(CTX)
                .inflate(R.layout.single_postview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.MyViewHolder holder,final int position) {
        final ProfileDataProvider provider = dataProviderList.get(position);
        holder.dateTV.setText(provider.date);
        holder.profileIV.setImageBitmap(profilePic);
        if(provider.type.contains("Audio")) {
            holder.playIV.setVisibility(View.VISIBLE);
            holder.stopIV.setVisibility(View.INVISIBLE);
            holder.textTV.setVisibility(View.INVISIBLE);
            if (currPlaying == position) {
                holder.stopIV.setVisibility(View.VISIBLE);
                holder.playIV.setVisibility(View.INVISIBLE);
            } else {
                holder.stopIV.setVisibility(View.INVISIBLE);
                holder.playIV.setVisibility(View.VISIBLE);
            }
            holder.usernameTV.setText(provider.username);
            holder.playIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"datafilelist is = " + dataFileList.get(position));
                    if (currPlaying != -1) {
                        mp.stop();
                        mp.release();
                        notifyItemChanged(currPlaying);
                    }
                    currPlaying = position;
                    mp = MediaPlayer.create(CTX, Uri.parse(dataFileList.get(position)));
                    mp.start();
                    holder.stopIV.setVisibility(View.VISIBLE);
                    holder.playIV.setVisibility(View.INVISIBLE);
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            holder.stopIV.setVisibility(View.INVISIBLE);
                            holder.playIV.setVisibility(View.VISIBLE);
                            mp.release();
                            currPlaying = -1;
                        }
                    });

                }
            });

            holder.stopIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currPlaying == position) {
                        mp.stop();
                        mp.release();
                    }
                    holder.stopIV.setVisibility(View.INVISIBLE);
                    holder.playIV.setVisibility(View.VISIBLE);
                    currPlaying = -1;
                }
            });
            holder.dateTV.setText(provider.date);
        }
        else if(provider.type.contains("Text"))
        {
            holder.playIV.setVisibility(View.INVISIBLE);
            holder.stopIV.setVisibility(View.INVISIBLE);
            holder.textTV.setVisibility(View.VISIBLE);
            holder.textTV.setText(dataFileList.get(position));

        }
    }



    @Override
    public int getItemCount() {
        return dataProviderList.size();
    }
}
