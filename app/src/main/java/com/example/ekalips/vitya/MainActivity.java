package com.example.ekalips.vitya;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.ekalips.vitya.db.TaskContract;
import com.example.ekalips.vitya.db.TaskDBHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    ToDoFragment toDoFragment;
    private int pageIndex;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    public DriveContents contents;
    GoogleApiClient mGoogleApiClient;
    TaskDBHelper mHelper;
    ViewPagerAdapter viewPagerAdapter;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mHelper = new TaskDBHelper(this);


        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.right_labels);
        FloatingActionButton floatingActionButtonToDo = (FloatingActionButton) findViewById(R.id.fab_menu_add_todo);
        FloatingActionButton floatingActionButtonToDoList = (FloatingActionButton) findViewById(R.id.fab_menu_add_todo_list);

        floatingActionButtonToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEditTextAlert();
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


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        toDoFragment = new ToDoFragment();

        viewPagerAdapter.addFragment(toDoFragment, "ToDo");
        viewPagerAdapter.addFragment(new MarksFragmentHolder(), "Marks");
        viewPager.setAdapter(viewPagerAdapter);

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
        if (PrefsHandler.getBoolean("TeacherSelected",false,context)) {

            if (PrefsHandler.getBoolean("IsTeacher",false,context)) {
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStackImmediate("InitialMarksList", 0);
                PrefsHandler.setBoolean("TeacherSelected", false, context);
            }
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

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
        Log.d("DRIVE","Someresult1   " + requestCode + "    " + (resultCode == RESULT_OK));

        switch (requestCode) {
            case 14:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {Log.d("Connection", "FAILED2");
                Toast.makeText(this,"Connection error",Toast.LENGTH_LONG).show();}
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

                    contents = result.getDriveContents();
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    File file = new File(Environment.getExternalStorageDirectory(), "SECRETDOCUMENTS");
                    ParcelFileDescriptor descriptor= contents.getParcelFileDescriptor();
                    InputStream inputStream = new FileInputStream(descriptor.getFileDescriptor());
                    copyInputStreamToFile( inputStream,file);
                    PrefsHandler.setString("Journal",file.getAbsolutePath(),context);
                    if (!PrefsHandler.getBoolean("IsLoggedIn",false,context)) {
                        try {
                            //PrefsHandler.setInt("ID",SQLiteHelper.FindID(context, "Viktor", "Ternoviy").getJSONObject(0).getInt("ID"),context);
                            PrefsHandler.setInt("ID",SQLiteHelper.FindID(context, getIntent().getStringExtra("Name").trim(), getIntent().getStringExtra("SName").trim()).getJSONObject(0).getInt("ID"),context);
                            PrefsHandler.setBoolean("IsLoggedIn",true,context); PrefsHandler.setBoolean("IsTeacher",false,context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                PrefsHandler.setInt("ID", SQLiteHelper.FindTeacherID(context, getIntent().getStringExtra("TeacherName").trim(), getIntent().getStringExtra("TeacherSName").trim()).getJSONObject(0).getInt("TeacherID"), context);
                                PrefsHandler.setBoolean("IsTeacher", true, context);
                                PrefsHandler.setInt("TeacherSubjID", SQLiteHelper.FindTeacherID(context, getIntent().getStringExtra("TeacherName").trim(), getIntent().getStringExtra("TeacherSName").trim()).getJSONObject(0).getInt("TeacherSubjID"), context);
                                PrefsHandler.setBoolean("IsLoggedIn", true, context);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                Toast.makeText(context, "Error logging in", Toast.LENGTH_LONG).show();
                                PrefsHandler.setBoolean("IsLoggedIn",false,context);
                            }
                        }
                    }
                    if (viewPagerAdapter==null) {SetupViewPager(viewPager);tabLayout.setupWithViewPager(viewPager);}

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
            if (PrefsHandler.getBoolean("IsTeacher",false,context) && result.getMetadata().isEditable()) Toast.makeText(context,"Hello,teacher!",Toast.LENGTH_LONG).show();
            {

            }
            DriveFile file = mFileId.asDriveFile();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
             //       .setPinned(false)
                    .build();
            file.updateMetadata(mGoogleApiClient, changeSet)
                    .setResultCallback(pinningCallback);
            file.open(mGoogleApiClient,DriveFile.MODE_READ_WRITE,listener).setResultCallback(contentsOpenedCallback);
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

    //Меню создания напоминания
    public void createEditTextAlert() {
        final Date date = new Date();
        ConstraintLayout view = (ConstraintLayout) getLayoutInflater().inflate(R.layout.to_do_alert_dialog, null);
        final Switch switch1 = (Switch) view.findViewById(R.id.switch1);
        final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinner);
        final EditText taskEditText = (EditText) view.findViewById(R.id.edit_text_to_do_alert);




        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0: break;
                    case 1: date.setDate(date.getDay()+1);break;
                    case 2: date.setDate(date.getDay()+3);break;
                    case 3:
                    {
                        final DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", Locale.US);
                        final DatePicker picker = new DatePicker(context);
                        picker.setMinDate(System.currentTimeMillis() - 1000);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select date");
                        builder.setView(picker);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                date.setDate(picker.getDayOfMonth());
                                date.setMonth(picker.getMonth());
                                date.setYear(picker.getYear() - 1900);
                                if (Calendar.getInstance().getTime().compareTo(date) > 0) {
                                    Toast.makeText(context, "WRONG DATE", Toast.LENGTH_LONG).show();
                                    spinner1.setSelection(0,true);
                                    Log.d("Date", "WRONG DATE");
                                    date.setDate(Calendar.getInstance().getTime().getDay());
                                    date.setMonth(Calendar.getInstance().getTime().getMonth());
                                    date.setYear(Calendar.getInstance().getTime().getYear() - 1900);
                                    return;
                                }
                                spinner1.setPrompt(dateFormat.format(date));

                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {

                    case 0: {
                        date.setHours(new Date().getHours());
                        date.setMinutes(new Date().getMinutes()+5);
                        date.setSeconds(new Date().getSeconds());
                        break;
                    }
                    case 1: {
                        date.setHours(new Date().getHours());
                        date.setMinutes(new Date().getMinutes()+15);
                        date.setSeconds(new Date().getSeconds());
                        break;
                    }
                    case 2: {
                        date.setHours(new Date().getHours()+3);
                        date.setMinutes(new Date().getMinutes());
                        date.setSeconds(new Date().getSeconds());
                        break;
                    }
                    case 3:
                    {
                        final DateFormat dateFormat = new SimpleDateFormat("HH:MM", Locale.US);
                        final TimePicker picker = new TimePicker(context);
                        picker.setIs24HourView(true);
                        AlertDialog dialog = new AlertDialog.Builder(context).
                                setTitle("Select date").
                                setView(picker).
                                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        date.setMinutes(picker.getCurrentMinute());
                                        date.setHours(picker.getCurrentHour());
                                        date.setSeconds(0);
                                        if (Calendar.getInstance().getTime().compareTo(date) > 0)
                                        {
                                            Toast.makeText(context,"WRONG TIME",Toast.LENGTH_LONG).show();
                                            date.setMinutes(Calendar.getInstance().getTime().getMinutes());
                                            date.setHours(Calendar.getInstance().getTime().getHours());
                                            spinner2.setSelection(0,true);
                                        }
                                        spinner2.setPrompt(dateFormat.format(date));
                                    }
                                }).setNegativeButton("Cancel", null)
                                .create();
                        dialog.show();
                    }
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinner1.setVisibility(View.VISIBLE);
                    spinner2.setVisibility(View.VISIBLE);
                } else {
                    spinner1.setVisibility(View.GONE);
                    spinner2.setVisibility(View.GONE);
                }
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Add a new task")
                .setMessage("What do you want to do next?")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        Log.d("TASK", "Task to add: " + task);

                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                        if(switch1.isChecked())
                        {
                            values.put(TaskContract.TaskEntry.COL_TASK_DATE, date.toString());
                            int a =  new Random().nextInt(Integer.SIZE - 1);
                            Intent intent = createIntent("Alarm:" + a, task + ";" + date.toString());
                            PendingIntent pendIntent = PendingIntent.getBroadcast(context, a,intent, 0);
                            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            am.set(AlarmManager.RTC_WAKEUP,date.getTime(), pendIntent);
                            values.put(TaskContract.TaskEntry.COL_TASK_ALARM_ID, String.valueOf(a));
                        }
                        else {
                            values.put(TaskContract.TaskEntry.COL_TASK_DATE, "");
                            values.put(TaskContract.TaskEntry.COL_TASK_ALARM_ID, "");
                        }

                        //db.replace(TaskContract.TaskEntry.TABLE,)
                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        //db.replace(TaskContract.TaskEntry.TABLE,null, values);
                        db.close();
                        toDoFragment.getTasks(mHelper.getReadableDatabase());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    Intent createIntent(String action, String extra) {
        Intent intent = new Intent(context,Receiver.class);
        intent.setAction(action);
        intent.putExtra("extra", extra);
        return intent;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                DriveFile file = mFileId.asDriveFile();
                file.open(mGoogleApiClient,DriveFile.MODE_READ_ONLY,listener).setResultCallback(contentsOpenedCallback);

                return true;

            case R.id.action_exit:
                PrefsHandler.setBoolean("IsLoggedIn", false, context);
                PrefsHandler.setInt("ID", -1, context);
                PrefsHandler.setString("Name", "", context);
                PrefsHandler.setString("SName", "", context);
                super.onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
