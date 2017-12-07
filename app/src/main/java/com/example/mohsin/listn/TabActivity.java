package com.example.mohsin.listn;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;


import org.json.JSONException;
import org.json.JSONObject;



public class TabActivity extends AppCompatActivity{

    private static final String TAG = "TABACTIVITY";
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
    ProfileAsyncRequests requests;
    JSONObject userObject;
    JSONObject postObject;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLUE);
        }
        dialogBox = new DialogBox(this);

        Intent intent = getIntent();


        try {
            postObject = new JSONObject(intent.getStringExtra("postData"));
            userObject = new JSONObject(intent.getStringExtra("User"));
            username = userObject.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
            mViewPager.setOffscreenPageLimit(2);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position =  tab.getPosition();
                    switch(position){
                        case 0:
                            tabLayout.getTabAt(0).setIcon(R.mipmap.audiofeedfilled);
                            break;
                        case 1:
                            tabLayout.getTabAt(1).setIcon(R.mipmap.notificationfilled);
                            break;
                        case 2:
                            tabLayout.getTabAt(2).setIcon(R.mipmap.profilefilled);
                            break;
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    int position =  tab.getPosition();
                    switch(position){
                        case 0:
                            tabLayout.getTabAt(0).setIcon(R.mipmap.audiofeed);
                            break;
                        case 1:
                            tabLayout.getTabAt(1).setIcon(R.mipmap.notification);
                            break;
                        case 2:
                            tabLayout.getTabAt(2).setIcon(R.mipmap.profile);
                            break;
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            loadProfile();



    }

    public void loadProfile()
    {
        Log.d(TAG,"logged in " + "user = " + userObject.toString() );
        requests = null;
        tabLayout.getTabAt(2).select();
    }



    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
