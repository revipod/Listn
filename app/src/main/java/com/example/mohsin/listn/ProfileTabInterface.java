package com.example.mohsin.listn;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by mabbasi on 11/30/2017.
 */

public interface ProfileTabInterface
{
   public void setProfileTabProfilePic(Bitmap result);
   public void gotProfileAudio();
   public void loadListView(ArrayList<String> audioFileList);
}
