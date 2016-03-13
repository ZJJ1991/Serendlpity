package com.example.jzhou.serendlpity;

import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Bel on 22.02.2016.
 */
public class Menu extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView listView;
    private RelativeLayout rlMainMenu;

    private DrawerListAdapter adapter;

    ArrayList<NavItem> aNavItems;
    String[] aTitles;
    String[] aDescriptions;
    int mCurCheckPosition = 0;

    int mPrevItem = 0;

    UserLocalStore userLocalStore;

    Fragment menuFragment;
    FragmentManager menuFragmentManager;
    FragmentTransaction menuFragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMenu);
        listView = (ListView) findViewById(R.id.listViewMenu);
        rlMainMenu = (RelativeLayout) findViewById(R.id.rlMainMenu);
        //getting arrays
        aTitles = getResources().getStringArray(R.array.itemTitles);
        aDescriptions = getResources().getStringArray(R.array.itemDescriptions);

        //get reference to local store
        userLocalStore = new UserLocalStore(this);

        if(savedInstanceState == null){
            menuFragmentManager = getSupportFragmentManager();
            menuFragmentManager.beginTransaction().add(R.id.frameLayoutMainContent, new Map()).commit();
        }

        fillMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.menu_overflow, menu);
        return true;
    }

    public void fillMenu(){
        aNavItems = new ArrayList<NavItem>();
        aNavItems.add(new NavItem(aTitles[0], aDescriptions[0], R.mipmap.ic_map_marker, 0));
        aNavItems.add(new NavItem(aTitles[1], aDescriptions[1], R.mipmap.ic_microphone, 1));
        aNavItems.add(new NavItem(aTitles[2], aDescriptions[2], R.mipmap.ic_note, 2));
        //aNavItems.add(new NavItem(aTitles[3], aDescriptions[3], R.mipmap.ic_random_place, 3));
        aNavItems.add(new NavItem(aTitles[4], aDescriptions[4], R.mipmap.ic_settings, 4));

        listView.setOnItemClickListener(this);
        adapter = new DrawerListAdapter(this, aNavItems);
        listView.setAdapter(adapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                //Toast.makeText(MainActivity.this, "Drawer closed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Toast.makeText(MainActivity.this, "Drawer opened", Toast.LENGTH_SHORT).show();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.setHomeAsUpIndicator(R.mipmap.ic_menu);
        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.mipmap.ic_logo_circle);


        if(userLocalStore.isUserLoggedIn())
            createUserProfileLayout();
    }

    public void createUserProfileLayout(){
        RelativeLayout relativeLayout = new RelativeLayout(this);
        ImageView ivUserPhoto = new ImageView(this);
        TextView tvUserName = new TextView(this);

        //SET PARAMS TO LinearLayout WHERE USER PROFILE INFO is situated
        //set width and height
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //set id to Header in the menu
        //relativeLayout.setId(R.id.layoutProfileHeader);
        relativeLayout.setBackgroundColor(Color.BLACK);
        relativeLayout.setGravity(Gravity.CENTER_VERTICAL);
        relativeLayout.setLayoutParams(layoutParams);

        //PUSH List View below created layout
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //p.addRule(RelativeLayout.BELOW, R.id.layoutProfileHeader);
        listView.setLayoutParams(p);

        //SET PARAMS FOR TEXT VIEW with USER PROFILE INFO
        tvUserName.setText(userLocalStore.getUsername());
        tvUserName.setTextSize(20);
        tvUserName.setTextColor(Color.WHITE);
        tvUserName.setHeight(150);
        RelativeLayout.LayoutParams paramsUserText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsUserText.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsUserText.setMarginStart(50);
        tvUserName.setLayoutParams(paramsUserText);
        relativeLayout.addView(tvUserName);

        //SET PARAMS for IMAGE VIEW with USER PROFILE INFO
        ivUserPhoto.setImageResource(R.mipmap.ic_user);
        ivUserPhoto.setContentDescription("Profile photo");
        RelativeLayout.LayoutParams paramsUserPhoto = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsUserPhoto.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsUserPhoto.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsUserPhoto.setMarginEnd(20);
        ivUserPhoto.setLayoutParams(paramsUserPhoto);
        relativeLayout.addView(ivUserPhoto);

        //setting params finished
        // ADD text view to the layout


        //Add layout to the header
        rlMainMenu.addView(relativeLayout);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //change HEADER name to the Item name
        selectItem(position);
    }

    public void selectItem(int position){
        mCurCheckPosition = position;
        NavItem currentItem;
        menuFragmentManager = getSupportFragmentManager();

        listView.setItemChecked(mCurCheckPosition, true);
        currentItem = (NavItem) listView.getItemAtPosition(mCurCheckPosition);

        if(mPrevItem != currentItem.id) {
            switch (currentItem.id) {
                case 0:
                    menuFragment = new Map();
                    menuFragmentManager.beginTransaction().replace(R.id.frameLayoutMainContent, menuFragment).commit();
                    getSupportActionBar().setTitle(currentItem.title);
                    mPrevItem = 0;
                    break;
                case 1:
                    menuFragment = new Record();

                    if (userLocalStore.isUserLoggedIn()) {
                        menuFragmentManager.beginTransaction().replace(R.id.frameLayoutMainContent, menuFragment).commit();
                        getSupportActionBar().setTitle(currentItem.title);
                        mPrevItem = 1;
                    } else {
                        showLoginRequestDialog();
                    }

                    break;
                case 2:
                    menuFragment = new RecordingList();
                    menuFragmentManager.beginTransaction().replace(R.id.frameLayoutMainContent, menuFragment).commit();
                    getSupportActionBar().setTitle(currentItem.title);
                    mPrevItem = 2;

                    break;
//                case 5:
//                    menuFragment = new RecordingList();
//                    menuFragmentManager.beginTransaction().replace(R.id.frameLayoutMainContent, menuFragment).commit();
//                    getSupportActionBar().setTitle(currentItem.title);
//                    mPrevItem = 5;
//                    break;
            }
        }


        drawerLayout.closeDrawers();
    }

    private void showLoginRequestDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("To use this function you need to Login");

        alertDialog.setPositiveButton("Go to Login", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

}
