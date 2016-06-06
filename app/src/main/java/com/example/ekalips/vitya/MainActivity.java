package com.example.ekalips.vitya;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    ToDoFragment toDoFragment;
    private int pageIndex;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    GoogleApiClient mGoogleApiClient;
    public FloatingActionsMenu floatingActionsMenu;
    private DriveId mFileId;
    String ling = "0BxkYJF0YZNHbX1dXZ1ZFZldCVVU";
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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void SetupViewPager(ViewPager viewPager)
    {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        toDoFragment = new ToDoFragment();
        adapter.addFragment(toDoFragment, "ToDo");
        adapter.addFragment(new MarksFragment(), "Marks");
        if (SQLiteHelper.CheckForBadMarks(PrefsHandler.getInt("ID",-1,this),this).length() > 0)
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Connection","CONENCTED");
        if (!PrefsHandler.getString("DriveId",this).equals(""))
        {
            mFileId = DriveId.decodeFromString(PrefsHandler.getString("DriveId",this));
        }
        if (mFileId == null) {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[] {"application/octet-stream"})
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(intentSender, 13,
                        null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w("Connection", "Unable to send intent", e);
            }
        } else {
            DriveFile file = mFileId.asDriveFile();
            file.open(mGoogleApiClient,DriveFile.MODE_READ_ONLY,listener).setResultCallback(contentsOpenedCallback);
            file.getMetadata(mGoogleApiClient).setResultCallback(metadataCallback);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 14);
            } catch (IntentSender.SendIntentException e) {
                Log.d("Connection","FAILED1");
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d("DRIVE","Someresult1   " + requestCode + "    " + (requestCode == RESULT_OK));

        switch (requestCode) {
            case 14:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else Log.d("Connection", "FAILED2");
                break;
            case 13:
                if (resultCode == RESULT_OK) {


                    mFileId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    PrefsHandler.setString("DriveId",mFileId.encodeToString(),this);
                    DriveFile file = mFileId.asDriveFile();
                    file.getMetadata(mGoogleApiClient).setResultCallback(metadataCallback);
                    // PendingResult<DriveResource.MetadataResult> data2 = file.getMetadata(mGoogleApiClient);

                    Log.d("ID", String.valueOf(mFileId));
                } else {
                    finish();
                }
                break;
        }
    }



    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d("File","Isn't succes");
                        return;
                    }
                    Log.d("FILE","GetSome");

                    DriveContents contents = result.getDriveContents();
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    File file = new File(Environment.getExternalStorageDirectory(), "SECRETDOCUMENTS");
                    copyInputStreamToFile(contents.getInputStream(),file);
                    PrefsHandler.setString("Journal",file.getAbsolutePath(),context);
                    SetupViewPager(viewPager);

                    tabLayout.setupWithViewPager(viewPager);
                }
            };

    private void copyInputStreamToFile( InputStream in, File file ) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final ResultCallback<DriveResource.MetadataResult> metadataCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            Log.d("DRIVE","Someresult2");
            Log.d("SIZEREAL", String.valueOf(result.getMetadata().getFileSize()));
            if (!result.getStatus().isSuccess()) {
                Log.d("DRIVE","Problem while trying to retrieve the file metadata");
                return;
            }
            if (!result.getMetadata().isPinnable()) {
                Log.d("DRIVE","File is not pinnable");
                return;
            }
            if (result.getMetadata().isPinned()) {
                Log.d("DRIVE","File is already pinned");
                return;
            }

            DriveFile file = mFileId.asDriveFile();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
             //       .setPinned(false)
                    .build();
            file.updateMetadata(mGoogleApiClient, changeSet)
                    .setResultCallback(pinningCallback);
            file.open(mGoogleApiClient,DriveFile.MODE_READ_ONLY,listener).setResultCallback(contentsOpenedCallback);
        }
    };

    final ResultCallback<DriveResource.MetadataResult> pinningCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.d("DRIVE","Problem while trying to pin the file");
                return;
            }
            Log.d("DRIVE","File successfully pinned to the device");
        }
    };

    DriveFile.DownloadProgressListener listener = new DriveFile.DownloadProgressListener() {
        @Override
        public void onProgress(long bytesDownloaded, long bytesExpected) {
            // Update progress dialog with the latest progress.
            int progress = (int)(bytesDownloaded*100/bytesExpected);
            Log.d("PROGRESS", String.format("Loading progress: %d percent", progress));
            //mProgressBar.setProgress(progress);
        }
    };

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
