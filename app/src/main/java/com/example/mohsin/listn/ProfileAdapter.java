package com.example.mohsin.listn;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mabbasi on 12/6/2017.
 */


class ProfileAdapter extends ArrayAdapter<ProfileDataProvider> {

    Context CTX;
    Bitmap profilePic;
    ArrayList<MediaPlayer> mediaPlayerList;

    private static class ViewHolder{
        ImageView playIV;
        ImageView stopIV;
        ImageView profileIV;
        TextView usernameTV;
        TextView dateTV;
        TextView listnsTV;
        RippleBackground rippleBackground;

    }

    public List<ProfileDataProvider> post_list = new ArrayList<ProfileDataProvider>();

    public ProfileAdapter(Context context,int resource,Bitmap profilePic) {
        super(context, resource);
        CTX = context;
        this.profilePic = profilePic;
        mediaPlayerList = new ArrayList<MediaPlayer>();
    }

    @Override
    public void add(ProfileDataProvider object) {
        post_list.add(object);
        super.add(object);
    }

    @Override
    public ProfileDataProvider getItem(int position) {

        return post_list.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) CTX.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.single_postview, parent, false);
            holder = new ViewHolder();
            holder.playIV = convertView.findViewById(R.id.playIV);
            holder.stopIV = convertView.findViewById(R.id.stopIV);
            holder.usernameTV = convertView.findViewById(R.id.usernameTV);
            holder.dateTV = convertView.findViewById(R.id.dateTV);
            holder.listnsTV = convertView.findViewById(R.id.listnsTV);
            holder.profileIV = convertView.findViewById(R.id.profileIV);
            convertView.setTag(holder);
        }
        else
            {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfileDataProvider provider = getItem(position);
        if(provider!=null)
        {
            holder.usernameTV.setText(provider.username);
            holder.dateTV.setText(provider.date);
        }
        holder.playIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerList.get(position).start();
            }
        });
        holder.stopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerList.get(position).stop();
            }
        });
        holder.dateTV.setText(provider.date);
        holder.profileIV.setImageBitmap(profilePic);
        MediaPlayer temp = MediaPlayer.create(CTX, Uri.parse(provider.audioPath));
        mediaPlayerList.add(temp);
        return convertView;

    }


}
