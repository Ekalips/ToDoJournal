package com.example.ekalips.vitya;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    ToDoFragment toDoFragment;
    private int pageIndex;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    public FloatingActionsMenu floatingActionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Log.d("Prefs", PrefsHandler.getString("Name", context) + "   " + PrefsHandler.getString("SName", context) + "   " + PrefsHandler.getInt("ID", -1, context));



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);


        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.right_labels);
        FloatingActionButton floatingActionButtonToDo = (FloatingActionButton) findViewById(R.id.fab_menu_add_todo);
        FloatingActionButton floatingActionButtonToDoList = (FloatingActionButton) findViewById(R.id.fab_menu_add_todo_list);

        floatingActionButtonToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDoFragment.createEditTextAlert();
                floatingActionsMenu.collapse();
            }
        });
    }

    private void SetupViewPager(ViewPager viewPager)
    {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        toDoFragment = new ToDoFragment();
        adapter.addFragment(toDoFragment, "ToDo");
        adapter.addFragment(new MarksFragment(), "Marks");
        if (SQLiteHelper.CheckForBadMarks(PrefsHandler.getInt("ID",-1,this)).length() > 0)
        {
            adapter.addFragment(new BadMarksFragment(), "Bad marks");
        }
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Page", String.valueOf(position));
                pageIndex = position;
                if (position != 0) {
                    floatingActionsMenu.collapse();
                    floatingActionsMenu.animate().translationYBy(floatingActionsMenu.getHeight()).setDuration(200).start();}
                else floatingActionsMenu.animate().translationY(0).setDuration(200).start();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ID",-1);
        editor.putBoolean("IsLoggedIn",false);
        editor.putString("Name", null);
        editor.putString("SName", null);
        editor.apply();
        super.onBackPressed();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (pageIndex != 0) {
            floatingActionsMenu.collapse();
            floatingActionsMenu.animate().translationYBy(floatingActionsMenu.getHeight()).setDuration(200).start();}
        else floatingActionsMenu.animate().translationY(0).setDuration(200).start();


    }




    private class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }






}
