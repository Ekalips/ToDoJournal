package com.example.ekalips.vitya;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.webkit.JsPromptResult;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ekalips on 5/25/16.
 */

public class SQLiteHelper
{
    public static JSONArray FindID(Context context, String name, String sName)
    {
        String searchQ =context.getResources().getString(R.string.id_search) + " \"" + name + "\" AND Surname = \""+ sName + "\"";
        return getResults(searchQ);
    }

    public  static JSONArray GetMarksByID(Context context,int id)
    {
        String searchQ = context.getResources().getString(R.string.get_marks) + id;
        return getResults(searchQ);
    }

    public static JSONArray GetSubjects(Context context)
    {
        String searchQ = context.getResources().getString(R.string.get_subjects);
        return getResults(searchQ);
    }

    public static JSONArray GetSubjMarksByID(int ID, int subjID)
    {
        String searchQ = " Select Mark FROM `marks` where `IDStud` = " + ID + " and `IDSubj` = " + subjID;
        return getResults(searchQ);
    }

    public static JSONArray CheckForBadMarks(int ID)
    {
        String searchQ = " Select * FROM `marks` where `IDStud` = " + ID + " and  `Mark` < 3";
        return getResults(searchQ);

    }

    private static JSONArray getResults(String seqrchQ)
    {
        String myPath = Environment.getExternalStorageDirectory() + "/Marks";// Set path to your database

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        String searchQuery = seqrchQ;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet     = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }
}
