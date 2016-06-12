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
        return getResults(searchQ,context);
    }

    public static JSONArray FindTeacherID(Context context,String name, String sName)
    {
        String searchQ ="SELECT TeacherID,TeacherSubjID FROM teachers WHERE TeacherName = " + " \"" + name + "\" AND TeacherSName = \""+ sName + "\"";
        return getResults(searchQ,context);
    }

    public static JSONArray GetStudents(Context context)
    {
        String searchQ ="SELECT * FROM students";
        return getResults(searchQ,context);
    }

    public  static JSONArray GetMarksByID(Context context,int id)
    {
        String searchQ = context.getResources().getString(R.string.get_marks) + id;
        return getResults(searchQ,context);
    }

    public static JSONArray GetSubjects(Context context)
    {
        String searchQ = context.getResources().getString(R.string.get_subjects);
        return getResults(searchQ,context);
    }

    public static JSONArray GetSubjMarksByID(int ID, int subjID, Context context)
    {
        String searchQ = " Select Mark,Theme,Date FROM `marks` where `IDStud` = " + ID + " and `IDSubj` = " + subjID;
        return getResults(searchQ,context);
    }

    public static JSONArray CheckForBadMarks(int ID, Context context)
    {
        String searchQ = " Select * FROM `marks` where `IDStud` = " + ID + " and  `Mark` < 3";
        return getResults(searchQ,context);

    }

    public static void AddMark(int StudentID,int Mark, int SubjectID, String Theme, String Date, Context context)
    {
        String requestQ = "INSERT INTO marks (IDStud,IDSubj,Mark,Theme,'Date') VALUES (" + StudentID + ", " + SubjectID + ", " + Mark+ ", '" + Theme+ "', '" + Date +"' );";
        executeSQL(requestQ,context);
    }

    public static void executeSQL(String reqestQ,Context context)
    {
        String myPath = Environment.getExternalStorageDirectory() + "/SECRETDOCUMENTS";// Set path to your database

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        myDataBase.execSQL(reqestQ);
    }


    public static JSONArray getResults(String seqrchQ, Context context)
    {
        String myPath = Environment.getExternalStorageDirectory() + "/SECRETDOCUMENTS";// Set path to your database

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
                            Log.d("JSON_RESSULTS", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("JSON_RESSULTS_ERROR", e.getMessage()  );
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


    public static JSONArray getResults(String seqrchQ, SQLiteDatabase myDataBase)
    {
        String myPath = Environment.getExternalStorageDirectory() + "/databs";// Set path to your database

        //SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

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
