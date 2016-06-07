package com.example.ekalips.vitya;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ekalips.vitya.ToDoRecyclerView.OnStartDragListener;
import com.example.ekalips.vitya.ToDoRecyclerView.SimpleItemTouchHelperCallback;
import com.example.ekalips.vitya.ToDoRecyclerView.ToDoRecyclerViewAdapter;
import com.example.ekalips.vitya.db.TaskContract;
import com.example.ekalips.vitya.db.TaskDBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.Inflater;


public class ToDoFragment extends Fragment implements OnStartDragListener
{
    TaskDBHelper mHelper;
    List<Task> Tasks;
    RecyclerView recyclerView;
    LayoutInflater inflater;
    private ItemTouchHelper mItemTouchHelper;

    public ToDoFragment()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_to_do, container, false);

        mHelper = new TaskDBHelper(getContext());

        if (PrefsHandler.getBoolean("TableCr",false,getContext()))
        getTasks(mHelper.getReadableDatabase());
        else
        {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.close();
            PrefsHandler.setBool("TableCr",true,getContext());
        }

        ToDoRecyclerViewAdapter adapter = new ToDoRecyclerViewAdapter(Tasks,mHelper,this,(MainActivity) getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.to_do_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        Log.d("TASKS","CREATED");

        return rootView;
    }

    public void getTasks(SQLiteDatabase db)
    {
        //try{
            Tasks = new ArrayList<>();
          //  mHelper = new TaskDBHelper(getContext());
           // SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                    new String[]{TaskContract.TaskEntry._ID,
                            TaskContract.TaskEntry.COL_TASK_TITLE,
                            TaskContract.TaskEntry.COL_TASK_DATE,
                            TaskContract.TaskEntry.COL_TASK_ALARM_ID},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                int idD = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
                int idT = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
                int idI = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
                int idA = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_ALARM_ID);
                Tasks.add(new Task(
                        cursor.getString(idD),
                        cursor.getString(idT),
                        Integer.valueOf(cursor.getString(idI)),
                        cursor.getString(idA))
                );
            }
            cursor.close();
            db.close();
            //if (recyclerView!=null)recyclerView.setAdapter(new ToDoRecyclerViewAdapter(Tasks,mHelper,this,(MainActivity)getActivity()));
//        for (Task a :
//                    Tasks) {
//            Log.d("Tasks", a.toString());
//        }
        if (recyclerView!=null) {Log.d("TASKS","Need to update"); ((ToDoRecyclerViewAdapter)recyclerView.getAdapter()).updateData(Tasks);}
        else Log.d("TASKS","Don't need to update");


    }




    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
