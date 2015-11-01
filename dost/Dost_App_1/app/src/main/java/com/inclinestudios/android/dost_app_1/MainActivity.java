package com.inclinestudios.android.dost_app_1;

import android.app.Activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


//import com.squareup.picasso.Picasso;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Communicator {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    public Fragment fragHome = new Home();
    public Fragment fragExpertArea = new ExpertArea();


    public String[] data_user = new String[3];
    /*
    public Fragment fragGrid1 = new Fragment_Recipe_Grid();
    public Fragment fragCuisine1 = new Fragment_Cuisine();
    public Fragment fragRecipe1 = new Fragment_Recipes();
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
       /*if (extras != null) {
            data_user = extras.getStringArray("string12");
        }
        for (int i = 0; i < 3; i++) {
            Log.d("Data_User =", data_user[i]);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar bar = getSupportActionBar();

        bar.setBackgroundDrawable(new ColorDrawable(0xFFDA4541));
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        ImageView iv_image = (ImageView) findViewById(R.id.image_restro);
        TextView tv_name = (TextView) findViewById(R.id.textView);
        //Picasso.with(getApplicationContext()).load(data_user[1]).into(iv_image);
        //tv_name.setText(data_user[0]);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.container, fragHome).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.container, fragExpertArea).commit();
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.container, fragHome).commit();
                break;

        }
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            /*case 2:
                mTitle = getString(R.string.title_section2);
                break;/*
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        */
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(mTitle);
    }



    @Override
    public void respond(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        Fragment fragSingleQuestion = new SingleQuestion(str, str2, str3, str4, str5, str6, str7);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().hide(fragHome).add(R.id.container, fragSingleQuestion).addToBackStack(null).commit();


    }

    public static class PlaceholderFragment extends Fragment {
        public static int section;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            section = sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = null;

            switch (section) {
                /*case 1:
                    rootView = inflater.inflate(R.layout.fragment_3, container, false);
                    TextView myText1 = (TextView) rootView.findViewById(R.id.textView);
                    int x = R.id.container;
                    myText1.setText(x);
                    break;*/
                /*case 2:
                    rootView = inflater.inflate(R.layout.fragment_3, container, false);
                    break;*/
                //case 3:
                // rootView = inflater.inflate(R.layout.fragment_main, container, false);
                //break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
