package com.example.mohsin.listn;

/**
 * Created by mabbasi on 12/20/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder>  {


    private static final String TAG = "ProfileADAPTER";
    Bitmap profilePic;
    ArrayList<String> mediaPlayerList;
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
        TextView listnsTV;
        String path;

        public MyViewHolder(View view) {
            super(view);
            playIV = itemView.findViewById(R.id.playIV);
            stopIV = itemView.findViewById(R.id.stopIV);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            listnsTV = itemView.findViewById(R.id.listnsTV);
            profileIV = itemView.findViewById(R.id.profileIV);
        }
    }


    public ProfileAdapter(Bitmap profilePic, Context CTX) {
        this.profilePic = profilePic;
        mediaPlayerList = new ArrayList<String>();
        dataProviderList = new ArrayList<ProfileDataProvider>();
        this.CTX = CTX;
        currPlaying = -1;
    }

    public void add(ProfileDataProvider object) {
        dataProviderList.add(0,object);
        mediaPlayerList.add(0,object.audioPath);
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
        if(currPlaying == position)
        {
            holder.stopIV.setVisibility(View.VISIBLE);
            holder.playIV.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.stopIV.setVisibility(View.INVISIBLE);
            holder.playIV.setVisibility(View.VISIBLE);
        }
        holder.usernameTV.setText(provider.username);
        holder.dateTV.setText(provider.date);
        holder.playIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currPlaying != -1)
                {
                    mp.stop();
                    mp.release();
                    notifyItemChanged(currPlaying);
                }
                    currPlaying = position;
                    mp = MediaPlayer.create(CTX, Uri.parse(mediaPlayerList.get(position)));
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
                if(currPlaying == position) {
                    mp.stop();
                    mp.release();
                }
                holder.stopIV.setVisibility(View.INVISIBLE);
                holder.playIV.setVisibility(View.VISIBLE);
                currPlaying = -1;
            }
        });
        holder.dateTV.setText(provider.date);
        holder.profileIV.setImageBitmap(profilePic);

    }



    @Override
    public int getItemCount() {
        return dataProviderList.size();
    }
}
