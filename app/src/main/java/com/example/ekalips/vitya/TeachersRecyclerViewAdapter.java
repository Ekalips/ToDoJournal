package com.example.ekalips.vitya;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ekalips on 6/11/16.
 */

public class TeachersRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Student> mStudents;
    MainActivity parentActivity;
    String Date;
    String Theme;
    int SubjID;
    EditText ThemeEditText;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;


    // Pass in the contact array into the constructor
    public TeachersRecyclerViewAdapter(List<Student> subjects, MainActivity parentActivity,int subjID) {
        mStudents = subjects;
        this.parentActivity = parentActivity;
        SubjID = subjID;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.teacher_marks_list_item, parent, false);
            return new VHItem(contactView);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.teachers_marks_list_header, parent, false);
            return new VHHeader(contactView);
        }
        else if (viewType == TYPE_FOOTER)
        {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.teachers_marks_list_footer, parent, false);
            return new VHFooter(contactView);
        }




        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            Log.d("POSITIONS", String.valueOf(position));
            final Student subject = getItem(holder.getAdapterPosition());
            ((VHItem) holder).nameTextView.setText(subject.Name + " " + subject.SName);
            ((VHItem) holder).marksList.setPrompt("Select mark");
            ((VHItem) holder).marksList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!parent.getItemAtPosition(position).equals("Choose mark")) {
                        if (position != 6 && position != 0) subject.setMark(position);
                        else subject.setMark(-1);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else if(holder instanceof VHFooter){
            ((VHFooter) holder).ApplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Theme = ThemeEditText.getText().toString().trim();
                    boolean temp = false;
                    for (Student st :
                            mStudents) {
                        if (st.haveMark()) {
                            SQLiteHelper.AddMark(st.ID, st.Mark, SubjID, Theme, Date, parentActivity);
                            temp = true;
                        }
                    }

                    if (temp) {
                        DriveId mFileId = DriveId.decodeFromString(PrefsHandler.getString("DriveId",parentActivity));
                        DriveFile file = mFileId.asDriveFile();
                        EditContentsAsyncTask task = new EditContentsAsyncTask(parentActivity);
                        task.execute(file);



                    }




                    ((VHFooter)holder).CancelBtn.callOnClick();
                    Toast.makeText(parentActivity,"Succes",Toast.LENGTH_LONG).show();

                }
            });
        }

        else if (holder instanceof VHHeader)
        {
            final EditText DateEdText = ((VHHeader) holder).DateEditText;
            ThemeEditText = ((VHHeader)holder).ThemeEditText;

            final Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(DateEdText,myCalendar);
                }

            };

            DateEdText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(parentActivity, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });


        }

        }

    private void updateLabel(EditText edittext, Calendar myCalendar) {

        String myFormat = "dd.MM.yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date = sdf.format(myCalendar.getTime());
        edittext.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public int getItemCount() {
        return mStudents.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        else if (isPositionFooter(position)) return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
    private boolean isPositionFooter(int position)
    {
        return position==mStudents.size()+1;
    }

    private Student getItem(int position) {

        return mStudents.get(position - 1);

    }
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    private class VHItem extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView nameTextView;
        Spinner marksList;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        VHItem(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.teachers_list_name);
            marksList = (Spinner) itemView.findViewById(R.id.teachers_list_mark_spinner);
        }
    }
    private class VHHeader extends RecyclerView.ViewHolder {

        EditText ThemeEditText;
        EditText DateEditText;
        VHHeader(View itemView) {
            super(itemView);
            ThemeEditText = (EditText) itemView.findViewById(R.id.teachers_marks_list_header_theme);
            DateEditText = (EditText) itemView.findViewById(R.id.teachers_marks_list_header_date);
        }
    }
    private class VHFooter extends RecyclerView.ViewHolder
    {
        Button CancelBtn,ApplyBtn;
        VHFooter(View itemView)
        {
            super(itemView);
            CancelBtn = (Button) itemView.findViewById(R.id.teachers_cancel_btn);
            CancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = parentActivity.getSupportFragmentManager();
                    manager.popBackStackImmediate("InitialMarksList",0);
                    PrefsHandler.setBoolean("TeacherSelected",false, parentActivity);
                }
            });
            ApplyBtn = (Button) itemView.findViewById(R.id.teachers_apply_btn);
        }
    }

}

