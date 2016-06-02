package com.example.ekalips.vitya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        final EditText nEditText = (EditText) findViewById(R.id.nameEditText);
        final EditText sEditText = (EditText) findViewById(R.id.surNameEditText);
        Button btn = (Button) findViewById(R.id.loginBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nEditText.getText().toString().isEmpty() && !sEditText.getText().toString().isEmpty())
                {
                    PrefsHandler.setBool("IsLoggedIn",true,context);
                    PrefsHandler.setString("Name", nEditText.getText().toString(),context);
                    PrefsHandler.setString("SName", sEditText.getText().toString(),context);
                    try {
                        Log.d("ID", String.valueOf(SQLiteHelper.FindID(context, "Viktor", "Ternoviy").getJSONObject(0).getInt("ID")));
                        PrefsHandler.setInt("ID",SQLiteHelper.FindID(context, "Viktor", "Ternoviy").getJSONObject(0).getInt("ID"),context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("PrefsEnter", PrefsHandler.getString("Name",context)+ "   " + PrefsHandler.getString("SName",context)+ "   " + PrefsHandler.getInt("ID",-1,context));

                    startActivity(new Intent(context,MainActivity.class));
                }
            }
        });
    }
}
