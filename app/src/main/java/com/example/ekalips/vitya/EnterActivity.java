package com.example.ekalips.vitya;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        final Context context = this;

        //Log.d("LoggedIn", String.valueOf(sharedPreferences.getBoolean("IsLoggedIn",false)));
        //Log.d("PrefsEnter", sharedPreferences.getString("Name","noname")+ "   " + sharedPreferences.getString("SName","nosname")+ "   " + sharedPreferences.getInt("ID",-1));

        if (PrefsHandler.contains("IsLoggedIn",context) && (PrefsHandler.getInt("ID",-1,context)!=-1) && PrefsHandler.getBoolean("IsLoggedIn",false,context))
        {
                startActivity(new Intent(context,MainActivity.class));
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);
                }

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        final EditText nEditText = (EditText) findViewById(R.id.nameEditText);
        final EditText sEditText = (EditText) findViewById(R.id.surNameEditText);
        Button btn = (Button) findViewById(R.id.loginBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nEditText.getText().toString().isEmpty() && !sEditText.getText().toString().isEmpty())
                {
                    PrefsHandler.setString("Name", nEditText.getText().toString(),context);
                    PrefsHandler.setString("SName", sEditText.getText().toString(),context);
                    try {
                        Log.d("ID", String.valueOf(SQLiteHelper.FindID(context, nEditText.getText().toString().trim(), sEditText.getText().toString().trim()).getJSONObject(0).getInt("ID")));
                        PrefsHandler.setInt("ID",SQLiteHelper.FindID(context, nEditText.getText().toString().trim(), sEditText.getText().toString().trim()).getJSONObject(0).getInt("ID"),context);
                        PrefsHandler.setBool("IsLoggedIn",true,context); PrefsHandler.setBool("IsTeacher",false,context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        try {
                            PrefsHandler.setInt("ID",SQLiteHelper.FindTeacherID(context, nEditText.getText().toString().trim(), sEditText.getText().toString().trim()).getJSONObject(0).getInt("TeacherID"),context);
                            PrefsHandler.setBool("IsTeacher", true,context);
                            PrefsHandler.setInt("TeacherSubjID", SQLiteHelper.FindTeacherID(context, nEditText.getText().toString().trim(), sEditText.getText().toString().trim()).getJSONObject(0).getInt("TeacherSubjID"),context);
                            PrefsHandler.setBool("IsLoggedIn",true,context);
                            Log.d("IDTeacher", String.valueOf(SQLiteHelper.FindTeacherID(context, nEditText.getText().toString().trim(), sEditText.getText().toString().trim()).getJSONObject(0).getInt("TeacherID")));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Toast.makeText(context,"Loggin error",Toast.LENGTH_SHORT).show();
                            Log.d("Loggin","LOGGIN ERROR");
                            return;
                        } catch (SQLiteException ex)
                        {
                            PrefsHandler.setBool("IsLoggedIn",false,context);
                            Intent intent = new Intent(context,MainActivity.class);
                            intent.putExtra("Name",nEditText.getText().toString());
                            intent.putExtra("SName",sEditText.getText().toString());
                            startActivity(intent);
                        }

                    } catch (SQLiteCantOpenDatabaseException e)
                    {
                        Intent intent = new Intent(context,MainActivity.class);
                        intent.putExtra("Name",nEditText.getText().toString());
                        intent.putExtra("SName",sEditText.getText().toString());
                        PrefsHandler.setBool("IsLoggedIn",false,context);
                        startActivity(intent);
                    }
                    Log.d("PrefsEnter", PrefsHandler.getString("Name",context)+ "   " + PrefsHandler.getString("SName",context)+ "   " + PrefsHandler.getInt("ID",-1,context));
                    Intent intent = new Intent(context,MainActivity.class);
                    //intent.putExtra("Name",nEditText.getText().toString());
                    //intent.putExtra("SName",sEditText.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}
