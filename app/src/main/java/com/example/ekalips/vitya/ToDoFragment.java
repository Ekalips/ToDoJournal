package com.example.ekalips.vitya;

import android.content.ContentValues;
import android.content.DialogInterface;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        //FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createEditTextAlert();
//            }
//        });
//
//
//        FloatingActionButton fab2 = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton2);
//        fab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getTasks();
//            }
//        });


        if (PrefsHandler.getBoolean("TableCr",false,getContext()))
        getTasks();
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

        return rootView;
    }

    public void getTasks()
    {
        try {
            Tasks = new ArrayList<>();
            mHelper = new TaskDBHelper(getContext());
            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                    new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_DATE},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                int idD = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
                int idT = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
                int idI = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
                Tasks.add(new Task(cursor.getString(idD), cursor.getString(idT), Integer.valueOf(cursor.getString(idI))));
            }
            cursor.close();
            db.close();
            if (recyclerView != null) {
                ((ToDoRecyclerViewAdapter) recyclerView.getAdapter()).addItem(Tasks.get(Tasks.size() - 1));
                // recyclerView.getAdapter().notifyDataSetChanged();
            }
            for (Task a :
                    Tasks) {
                Log.d("Tasks", a.toString());
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getContext(),"Creating list",Toast.LENGTH_SHORT).show();
            mHelper.createIfNotExist();
        }
    }


    public EditText createEditTextAlert() {
        final Date date = new Date();
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.to_do_alert_dialog, null);
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
                        final DatePicker picker = new DatePicker(getContext());
                        AlertDialog dialog = new AlertDialog.Builder(getContext()).
                                setTitle("Select date").
                                setView(picker).
                                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        date.setDate(picker.getDayOfMonth());
                                        date.setMonth(picker.getMonth());
                                        date.setYear(picker.getYear());
                                        spinner1.setPrompt(dateFormat.format(date));
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
                        date.setMinutes(new Date().getMinutes()+3);
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
                        final TimePicker picker = new TimePicker(getContext());
                        AlertDialog dialog = new AlertDialog.Builder(getContext()).
                                setTitle("Select date").
                                setView(picker).
                                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        date.setMinutes(picker.getCurrentMinute());
                                        date.setHours(picker.getCurrentHour());
                                        date.setSeconds(0);
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
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Add a new task")
                .setMessage("What do you want to do next?")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        Log.d("TASK", "Task to add: " + task);

                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                        if(switch1.isChecked())
                        {
                            values.put(TaskContract.TaskEntry.COL_TASK_DATE, date.toString());
                        }
                        else {
                            values.put(TaskContract.TaskEntry.COL_TASK_DATE, "");
                        }

                        //db.replace(TaskContract.TaskEntry.TABLE,)
                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        //db.replace(TaskContract.TaskEntry.TABLE,null, values);
                        db.close();
                        getTasks();

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        return taskEditText;
    }



    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
